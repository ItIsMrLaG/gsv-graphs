package algos

import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import java.io.{File, PrintWriter}


object MultipleSourceParentBFS {

  type ParentMap = Map[VertexId, Long]

  def multiSourceBFS(
                      sourceVertices: Set[VertexId],
                      graph: Graph[Int, Int]
                    ): Graph[ParentMap, Int] = {

    val initialGraph = graph.mapVertices { case (id, _) =>
      if (sourceVertices.contains(id)) Map(id -> -1L) else Map.empty[VertexId, Long]
    }

    val bfsGraph = initialGraph.pregel[ParentMap](
      initialMsg = Map.empty,
      activeDirection = EdgeDirection.Out
    )(
      (id, attr, msg) => {
        attr ++ msg.filter { case (src, parentCandidate) =>
          !attr.contains(src) || parentCandidate < attr(src)
        }
      },
      triplet => {
        val newMsgs = triplet.srcAttr.filter { case (src, _) =>
          !triplet.dstAttr.contains(src)
        }.map { case (src, _) =>
          src -> triplet.srcId
        }

        if (newMsgs.nonEmpty) {
          Iterator((triplet.dstId, newMsgs))
        } else {
          Iterator.empty
        }
      },
      (a, b) => {
        (a.keySet ++ b.keySet).map { k =>
          val parentA = a.getOrElse(k, Long.MaxValue)
          val parentB = b.getOrElse(k, Long.MaxValue)
          k -> math.min(parentA, parentB)
        }.toMap
      }
    )

    bfsGraph
  }


  def run(
           sourceVertices: Set[VertexId],
           graph: Graph[Int, Int],
           sc: SparkContext
         ): Seq[(VertexId, Array[Long])] = {
    val bfsResult = multiSourceBFS(sourceVertices, graph)

    val allVertexIds = graph.vertices.map(_._1).collect().sorted

    val resultsMap = bfsResult.vertices.collect().toMap

    sc.parallelize(sourceVertices.toSeq).map { root =>
      val parentArray = allVertexIds.map { vid =>
        resultsMap.get(vid).flatMap(_.get(root)).getOrElse(-2L) match {
          case -1L => -1L
          case x => x
        }
      }
      (root, parentArray)
    }.collect().toSeq
  }
}

object MultipleSourceParentBFSRun {
  def main(args: Array[String]): Unit = {
    if (args.length != 2) {
      System.err.println("Usage: MultipleSourceParentBFSRun <inputFile> <outputFile>")
      System.exit(1)
    }

    val inputFile = args(0)
    val outputFile = args(1)

    val conf = new SparkConf().setAppName("MultipleSourceParentBFSRun").setMaster("local[*]")
    val sc = new SparkContext(conf)

    val lines: RDD[String] = sc.textFile(inputFile)

    val firstLine = lines.first()
    val sourceVertices: Set[VertexId] = firstLine.trim.split("\\s+").map(_.toLong).toSet

    val edgeLines = lines.zipWithIndex().filter { case (_, idx) => idx != 0 }.map(_._1)

    val baseEdges: RDD[Edge[Int]] = edgeLines.map { line =>
      val tokens = line.trim.split(",")
      val srcId = tokens(0).toLong
      val dstId = tokens(1).toLong
      Edge(srcId, dstId, 1)
    }

    val reversedEdges: RDD[Edge[Int]] = baseEdges.map(e => Edge(e.dstId, e.srcId, e.attr))
    val edges: RDD[Edge[Int]] = baseEdges.union(reversedEdges)

    val vertices: RDD[(VertexId, Int)] = edges
      .flatMap(e => Seq(e.srcId, e.dstId))
      .distinct()
      .map(v => (v, 0))

    val graph = Graph(vertices, edges)

    val results: Seq[(VertexId, Array[Long])] = MultipleSourceParentBFS.run(sourceVertices, graph, sc)

    val formattedResults = results.map { case (root, parentArray) =>
      val parentArrayStr = parentArray.mkString(" ")
      s"$root $parentArrayStr"
    }

    val writer = new PrintWriter(new File(outputFile))
    try {
      formattedResults.foreach(writer.println)
    } finally {
      writer.close()
    }

    sc.stop()
  }
}
