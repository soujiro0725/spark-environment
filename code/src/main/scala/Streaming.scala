import java.io.PrintWriter
import java.net.ServerSocket
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
