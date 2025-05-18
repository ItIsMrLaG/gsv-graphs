package algos

import org.apache.spark.graphx.{Edge, Graph, VertexId}
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object MultipleSourceParentBFSTest extends App {
  val conf = new SparkConf().setAppName("MultipleSourceParentBFSTest").setMaster("local[*]")
  val sc = new SparkContext(conf)

  //input
  val sourceVertices = Set(1L, 4L)
  val edges: RDD[Edge[Int]] = sc.parallelize(Seq(
    Edge(1L, 3L, 1),
    Edge(3L, 1L, 1),
    Edge(1L, 2L, 1),
    Edge(2L, 1L, 1),
    Edge(2L, 4L, 1),
    Edge(4L, 2L, 1),
    Edge(3L, 4L, 1),
    Edge(4L, 3L, 1)
  ))
  val vertices: RDD[(VertexId, Int)] = edges.flatMap(e => Seq(e.srcId, e.dstId)).distinct().map(v => (v, 0))
  val graph = Graph(vertices, edges)

  val results = MultipleSourceParentBFS.run(sourceVertices, graph, sc)
  results.foreach { case (root, parentArray) =>
    println(root, parentArray.mkString(" "))
  }
  sc.stop()

  // output:
  // (1,-1 1 1 2)
  // (4,2 4 4 -1)
}
