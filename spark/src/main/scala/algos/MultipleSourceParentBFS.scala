package algos

import org.apache.spark.graphx._
import org.apache.spark.SparkContext
import java.util.concurrent.atomic.AtomicLong

object MultipleSourceParentBFS {

  type ParentMap = Map[VertexId, Long]

  private val computationTimeNs = new AtomicLong(0)
  private val communicationTimeNs = new AtomicLong(0)

  def multiSourceBFS(
                      sourceVertices: Set[VertexId],
                      graph: Graph[Int, Int]
                    ): Graph[ParentMap, Int] = {

    val compStart = System.nanoTime()

    val initialGraph = graph.mapVertices { case (id, _) =>
      if (sourceVertices.contains(id)) Map(id -> -1L) else Map.empty[VertexId, Long]
    }

    computationTimeNs.addAndGet(System.nanoTime() - compStart)

    val bfsGraph = initialGraph.pregel[ParentMap](
      initialMsg = Map.empty,
      activeDirection = EdgeDirection.Out
    )(
      (id, attr, msg) => {
        val compStartInner = System.nanoTime()
        val updatedAttr = attr ++ msg.filter { case (src, parentCandidate) =>
          !attr.contains(src) || parentCandidate < attr(src)
        }
        computationTimeNs.addAndGet(System.nanoTime() - compStartInner)
        updatedAttr
      },
      triplet => {
        val commStart = System.nanoTime()
        val newMsgs = triplet.srcAttr.filter { case (src, _) =>
          !triplet.dstAttr.contains(src)
        }.map { case (src, _) =>
          src -> triplet.srcId
        }
        communicationTimeNs.addAndGet(System.nanoTime() - commStart)

        if (newMsgs.nonEmpty) {
          Iterator((triplet.dstId, newMsgs))
        } else {
          Iterator.empty
        }
      },
      (a, b) => {
        val compStartInner = System.nanoTime()
        val merged = (a.keySet ++ b.keySet).map { k =>
          val parentA = a.getOrElse(k, Long.MaxValue)
          val parentB = b.getOrElse(k, Long.MaxValue)
          k -> math.min(parentA, parentB)
        }.toMap
        computationTimeNs.addAndGet(System.nanoTime() - compStartInner)
        merged
      }
    )

    bfsGraph
  }

  def run(
           sourceVertices: Set[VertexId],
           graph: Graph[Int, Int],
           sc: SparkContext
         ): Seq[(VertexId, Array[Long])] = {

    computationTimeNs.set(0)
    communicationTimeNs.set(0)

    val bfsResult = multiSourceBFS(sourceVertices, graph)

    val compStart = System.nanoTime()
    val allVertexIds = graph.vertices.map(_._1).collect().sorted
    val resultsMap = bfsResult.vertices.collect().toMap
    computationTimeNs.addAndGet(System.nanoTime() - compStart)

    val finalResult = sc.parallelize(sourceVertices.toSeq).map { root =>
      val parentArray = allVertexIds.map { vid =>
        resultsMap.get(vid).flatMap(_.get(root)).getOrElse(-2L) match {
          case -1L => -1L
          case x => x
        }
      }
      (root, parentArray)
    }.collect().toSeq

    finalResult
  }

  def getMetrics: (Long, Long) = {
    (
      computationTimeNs.get() / 1000000,   // ComputationTime(ms)
      communicationTimeNs.get() / 1000000  // CommunicationTime(ms)
    )
  }
}
