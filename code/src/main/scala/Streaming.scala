import java.io.PrintWriter
import java.net.ServerSocket
import java.text.{SimpleDateFormat, DateFormat}
import java.util.Date
import org.apache.spark.SparkContext._
import org.apache.spark.streaming.{Seconds, StreamingContext}

import scala.util.Random

object StreamingProducer {
  def main(atrgs: Array[String]) {
    val random = new Random()
    val MaxEvents = 6

    val namesResource = this.getClass.getResourceAsStream("/names.csv")
    val names = scala.io.Source.fromInputStream(namesResource)
      .getLines()
      .toList
      .head
      .split(",")
      .toSeq

    val products = Seq(
      "iPhone Cover" -> 9.99,
      "Headphones" -> 5.49,
      "Samsung Galaxy" -> 8.95,
      "iPad Cover" -> 7.49
    )

    def generateProductEvents(n: Int) = {
      (1 to n).map { i =>
        val (product, price) = products(random.nextInt(products.size))
        val user = random.shuffle(names).head
        (user, product, price)
      }
    }

    val listener = new ServerSocket(9999)
    println("Listening on port: 9999")

    while(true) {
      val socket = listener.accept()
      new Thread() {
        override def run = {
          println("Got client connected from: " + socket.getInetAddress)
          val out = new PrintWriter(socket.getOutputStream(), true)

          while(true) {
            Thread.sleep(1000)
            val num = random.nextInt(MaxEvents)
            val productEvents = generateProductEvents(num)
            productEvents.foreach{ event =>
              out.write(event.productIterator.mkString(","))
              out.write("\n")
            }
            out.flush()
            println(s"created $num events...")
          }
          socket.close()
        }
      }.start()
    }
  }
}

object SimpleStreamingApp {
  def main(args: Array[String]) {
    val ssc = new StreamingContext("local[2]", "First streaming app", Seconds(10))
    val stream  =ssc.socketTextStream("localhost", 9999)

    stream.print()
    ssc.start()
    ssc.awaitTermination()
  }
}


object StreamingAnalyticsApp {
  def main(args: Array[String]) {
    val ssc = new StreamingContext("local[2]", "first streaming app", Seconds(10))
    val stream = ssc.socketTextStream("localhost", 9999)

    val events = stream.map { record =>
      val event = record.split(",")
      (event(0), event(1), event(2))
    }

    events.foreachRDD { (rdd, time) =>
      val numPurchases = rdd.count()
      val uniqueUsers = rdd.map { case (user, _, _) => user }.distinct().count()
      val totalRevenue = rdd.map { case (_, _, price) => price.toDouble }.sum()
      val productByPopularity = rdd
        .map { case (user, product, price) => (product, 1) }
        .reduceByKey(_ + _)
        .collect()
        .sortBy(-_._2)
      val mostPopular = productByPopularity(0)

      val formatter = new SimpleDateFormat
      val dateStr = formatter.format(new Date(time.milliseconds))
      println(s"== Batch start time: $dateStr ==")
      println("total purchases: " + numPurchases)
      println("unique users: " + numPurchases)
      println("total revenues: " + uniqueUsers)
      println("most popular product: %s with %d purchases".format(mostPopular._1, mostPopular._2))
    }

    ssc.start()
    ssc.awaitTermination()
  }
}
