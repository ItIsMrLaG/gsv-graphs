package algos

import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

import java.io.{File, PrintWriter}

object Boruvka {
  def run(
           graph: Graph[VertexId, Double],
           sc: SparkContext
         ): Set[Edge[Double]] = {

    var currentGraph = graph.mapVertices((id, _) => id) // Изначально каждая вершина — своя компонента
    var mstEdges = Set.empty[Edge[Double]]
    var changed = true

    while (changed) {
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

      val minEdges = messages
        .map { case (_, (componentId, edge)) => (componentId, edge) }
        .reduceByKey((e1, e2) => if (e1.attr < e2.attr) e1 else e2)
        .map(_._2)
        .distinct()
        .collect()
        .toSet


      if (minEdges.isEmpty) {
        changed = false
      } else {
        val validEdges = minEdges.filter(e => currentGraph.vertices.lookup(e.srcId).head != currentGraph.vertices.lookup(e.dstId).head)

        if (validEdges.isEmpty) {
          changed = false
        } else {
          mstEdges = mstEdges ++ validEdges

          val replacements: RDD[(VertexId, VertexId)] = sc.parallelize(
            validEdges.toSeq.flatMap { e =>
              val minId = math.min(currentGraph.vertices.lookup(e.srcId).head, currentGraph.vertices.lookup(e.dstId).head)
              Seq((e.srcId, minId), (e.dstId, minId))
            }
          )

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
        }
      }
    }

    mstEdges
  }
}

object BoruvkaRun {
  def main(args: Array[String]): Unit = {
    if (args.length != 3) {
      System.err.println("Usage: BoruvkaRun <inputFile> <outputFile> <coresNum>")
      System.exit(1)
    }

    val inputFile = args(0)
    val outputFile = args(1)
    val coresNum = args(2).toInt

    val conf = new SparkConf().setAppName("BoruvkaRun").setMaster(s"local[${coresNum}]")
    val sc = new SparkContext(conf)

    val lines: RDD[String] = sc.textFile(inputFile)

    val baseEdges: RDD[Edge[Double]] = lines.map { line =>
      val tokens = line.trim.split("\\s+")
      val _ = tokens(0)
      val srcId = tokens(1).toLong
      val dstId = tokens(2).toLong
      val weight = tokens(3).toDouble
      Edge(srcId, dstId, weight)
    }

    val edges = baseEdges.map { e =>
        val (minId, maxId) = if (e.srcId < e.dstId) (e.srcId, e.dstId) else (e.dstId, e.srcId)
        Edge(minId, maxId, e.attr)
      }
      .distinct()

    val graph = Graph.fromEdges(edges, defaultValue = 0L)

    val mst: Set[Edge[Double]] = Boruvka.run(graph, sc)

    val formattedResults = mst.map { edge =>
      s"${edge.srcId} ${edge.dstId} ${edge.attr}"
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
