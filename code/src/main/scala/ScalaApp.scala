import org.apache.spark._
import org.apache.spark.SparkContext._
import org.apache.spark.sql.SparkSession

/**
  * a simple spark app in Scala
  */

object ScalaApp {
  def main(args: Array[String]) {
    val configuration = new SparkConf()
      .setAppName("simple app")
      .setMaster("local")
    val sc = new SparkContext(configuration)
    val logFile = "/Users/soichi/Projects/spark-environment/README.md"
    val spark = SparkSession.builder.appName("Simple Application").getOrCreate()
    val logData = spark.read.textFile(logFile).cache()
    val numAs = logData.filter(line => line.contains("a")).count()
    val numBs = logData.filter(line => line.contains("b")).count()
    println(s"Lines with a $numAs, Lines with b: $numBs")
    spark.stop()
  }
}
