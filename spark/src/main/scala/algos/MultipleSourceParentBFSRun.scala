package algos

import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import java.io.{File, PrintWriter}

object MultipleSourceParentBFSRun {
  def main(args: Array[String]): Unit = {
    if (args.length != 3) {
      System.err.println("Usage: MultipleSourceParentBFSRun <inputFile> <outputFile> <coresNum>")
      System.exit(1)
    }

    val inputFile = args(0)
    val outputFile = args(1)
    val coresNum = args(2).toInt

    val conf = new SparkConf()
      .setAppName("MultipleSourceParentBFSRun")
      .setMaster(s"local[$coresNum]")
//      .set("spark.eventLog.enabled", "true")
//      .set("spark.eventLog.dir", "file:///home/julia/spark-events")

    val sc = new SparkContext(conf)
    sc.setLogLevel("WARN")

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

    val results = MultipleSourceParentBFS.run(sourceVertices, graph, sc)

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

    val (computationTimeMs, communicationTimeMs) = MultipleSourceParentBFS.getMetrics
    val outputFileName = new File(outputFile).getName.stripSuffix(".txt")

    val timeWriter = new PrintWriter(new File(s"src/main/resources/metrics/${outputFileName}_msbfs_algorithm_timing.txt"))
    try {
      timeWriter.println(s"ComputationTime(ms): $computationTimeMs")
      timeWriter.println(s"CommunicationTime(ms): $communicationTimeMs")
    } finally {
      timeWriter.close()
    }

    sc.stop()
  }
}
