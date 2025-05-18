package algos

import org.apache.spark.graphx.{Edge, Graph}
import org.apache.spark.rdd.RDD

object BoruvkaTest extends App {
  val spark = org.apache.spark.sql.SparkSession.builder
    .appName("BoruvkaTest")
    .master("local[*]")
    .getOrCreate()

  val sc = spark.sparkContext

  // input
  val baseEdges: RDD[Edge[Double]] = sc.parallelize(Seq(
    Edge(1L, 2L, 3.0),
    Edge(1L, 3L, 1.0),
    Edge(2L, 3L, 6.0),
    Edge(2L, 1L, 3.0),
    Edge(3L, 1L, 1.0),
    Edge(3L, 2L, 6.0)
  ))

  val edges = baseEdges.map { e =>
      val (minId, maxId) = if (e.srcId < e.dstId) (e.srcId, e.dstId) else (e.dstId, e.srcId)
      Edge(minId, maxId, e.attr)
    }
    .distinct()

  val graph = Graph.fromEdges(edges, defaultValue = 0L)

  val mst = Boruvka.run(graph, sc)

  mst.foreach { edge =>
    println(edge.srcId, edge.dstId, edge.attr)
  }

  sc.stop()

  // output
//   (1,3,1.0)
//   (1,2,3.0)
}
