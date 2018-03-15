import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext._
import org.apache.spark.mllib.linalg._
import org.apache.spark.mllib.clustering._

/**
  * a simple spark app in Scala
  */

object ScalaApp {
  def main(args: Array[String]) {

    val conf = new SparkConf()
      .setMaster("local[2]")
      .setAppName("spark-app-1")
      .set("spark.executor.memory", "14g")
      .set("spark.driver.memory", "14g")
    val sc = new SparkContext(conf)

    val rawData = sc.textFile("/Users/gsx/Projects/spark-environment/data/kddcup.data")
    val labelsAndData = rawData.map{ line =>
      val buffer = line.split(',').toBuffer
      buffer.remove(1,3)
      val label = buffer.remove(buffer.length-1)
      val vector = Vectors.dense(buffer.map(_.toDouble).toArray)
      (label, vector)
    }
    val data = labelsAndData.values.cache()
    val kmeans = new KMeans()
    val model = kmeans.run(data)

    model.clusterCenters.foreach(println)

     val clusterLabelCount = labelsAndData.map { case (label, datum) =>
       val cluster = model.predict(datum)
       (cluster, label)
     }.countByValue()

    clusterLabelCount.toSeq.sorted.foreach { case ((cluster, label), count) =>
      println(f"$cluster%1s$label%18s$count%8s")
    }

    sc.stop()
  }
}
