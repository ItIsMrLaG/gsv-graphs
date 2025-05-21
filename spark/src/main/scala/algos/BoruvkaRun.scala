package algos

import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import java.io.{File, PrintWriter}

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
    sc.setLogLevel("WARN")

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

    val (computationTimeMs, communicationTimeMs) = Boruvka.getMetrics
    val inputFileName = new File(inputFile).getName.stripSuffix(".txt")

    val timeWriter = new PrintWriter(new File(s"src/main/resources/metrics/${inputFileName}_boruvka_timing.txt"))
    try {
      timeWriter.println(s"ComputationTime(ms): $computationTimeMs")
      timeWriter.println(s"CommunicationTime(ms): $communicationTimeMs")
    } finally {
      timeWriter.close()
    }

    sc.stop()
  }
}
