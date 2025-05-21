package algos

import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD
import org.apache.spark.SparkContext
import java.util.concurrent.atomic.AtomicLong

object Boruvka {
  private val computationTimeNs = new AtomicLong(0)
  private val communicationTimeNs = new AtomicLong(0)

  def run(
           graph: Graph[VertexId, Double],
           sc: SparkContext
         ): Set[Edge[Double]] = {

    computationTimeNs.set(0)
    communicationTimeNs.set(0)

    var currentGraph = graph.mapVertices((id, _) => id)
    var mstEdges = Set.empty[Edge[Double]]
    var changed = true

    while (changed) {
      val commStart = System.nanoTime()
      val messages = currentGraph.aggregateMessages[(VertexId, Edge[Double])](
        triplet => {
          if (triplet.srcAttr != triplet.dstAttr) {
            val edge = Edge(triplet.srcId, triplet.dstId, triplet.attr)
            triplet.sendToSrc((triplet.srcAttr, edge))
            triplet.sendToDst((triplet.dstAttr, edge))
          }
        },
        (e1, e2) => if (e1._2.attr < e2._2.attr) e1 else e2
      )
      communicationTimeNs.addAndGet(System.nanoTime() - commStart)

      val compStart1 = System.nanoTime()
      val minEdges = messages
        .map { case (_, (componentId, edge)) => (componentId, edge) }
        .reduceByKey((e1, e2) => if (e1.attr < e2.attr) e1 else e2)
        .map(_._2)
        .distinct()
        .collect()
        .toSet
      computationTimeNs.addAndGet(System.nanoTime() - compStart1)

      if (minEdges.isEmpty) {
        changed = false
      } else {
        val compStart2 = System.nanoTime()
        val componentMap = currentGraph.vertices.collectAsMap()
        val bcComponentMap = sc.broadcast(componentMap)

        val validEdges = minEdges.filter { e =>
          bcComponentMap.value(e.srcId) != bcComponentMap.value(e.dstId)
        }
        computationTimeNs.addAndGet(System.nanoTime() - compStart2)

        if (validEdges.isEmpty) {
          changed = false
        } else {
          mstEdges = mstEdges ++ validEdges

          val compStart3 = System.nanoTime()
          val replacements: RDD[(VertexId, VertexId)] = sc.parallelize(
            validEdges.toSeq.flatMap { e =>
              val srcComp = bcComponentMap.value(e.srcId)
              val dstComp = bcComponentMap.value(e.dstId)
              val minId = math.min(srcComp, dstComp)
              Seq((e.srcId, minId), (e.dstId, minId))
            }
          )

          bcComponentMap.destroy()

          val replacementEdges: RDD[Edge[Long]] = replacements.map { case (srcId, dstId) =>
            Edge(srcId, dstId, 0L)
          }

          val newComponentIds = Graph.fromEdges(replacementEdges, 0L)
            .connectedComponents()
            .vertices

          currentGraph = currentGraph
            .joinVertices(newComponentIds) { (_, _, newComp) => newComp }
            .subgraph(triplet => triplet.srcAttr != triplet.dstAttr)
            .cache()
          computationTimeNs.addAndGet(System.nanoTime() - compStart3)
        }
      }
    }

    mstEdges
  }

  def getMetrics: (Long, Long) = {
    (
      computationTimeNs.get() / 1000000,   // ms
      communicationTimeNs.get() / 1000000  // ms
    )
  }
}
