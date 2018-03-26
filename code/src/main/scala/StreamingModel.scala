import java.io.PrintWriter
import java.net.ServerSocket

import breeze.linalg.DenseVector
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.{StreamingLinearRegressionWithSGD, LabeledPoint}
import org.apache.spark.streaming.{Seconds, StreamingContext}

import scala.util.Random

object StreamingModelProducer {
  import breeze.linalg._

  def main(args: Array[String]) {
    val MaxEvents = 100
    val NumFeatures = 100
    val random = new Random()

    def generateRandomArray(n: Int) = Array.tabulate(n)(_ => random.nextGaussian())

    val w = new DenseVector(generateRandomArray(NumFeatures))
    val intercept = random.nextGaussian() * 10

    def generateNoisyData(n: Int) = {
      (1 to n).map { i =>
        val x = new DenseVector(generateRandomArray(NumFeatures))
        val y: Double = w.dot(x)
        val noisy = y + intercept
        (noisy, x)
      }
    }

    val listener = new ServerSocket(9999)
    println("Listening on port: 9999")

    while(true) {
      val socket = listener.accept()
      new Thread() {
        override def run = {
          println("got client connected from: " + socket.getInetAddress)
          val out = new PrintWriter(socket.getOutputStream(), true)

          while(true) {
            Thread.sleep(1000)
            val num = random.nextInt(MaxEvents)
            val data = generateNoisyData(num)
            data.foreach { case (y, x) =>
              val xStr = x.data.mkString(",")
              val eventStr = s"$y\t$xStr"
              out.write(eventStr)
              out.write("\n")
            }

            out.flush()
            println(s"Created $num events")
          }
          socket.close()
        }
      }.start()
    }
  }
}

object SimpleStreamingModel {
  def main(args: Array[String]) {
    val ssc = new StreamingContext("local[2]", "first streaming app", Seconds(10))
    val stream = ssc.socketTextStream("localhost", 9999)

    val NumFeatures = 100
    val zeroVector = DenseVector.zeros[Double](NumFeatures)
    val model = new StreamingLinearRegressionWithSGD()
      .setInitialWeights(Vectors.dense(zeroVector.data))
      .setNumIterations(1)
      .setStepSize(0.01)

    val labeledStream = stream.map { event =>
      val split = event.split("\t")
      val y = split(0).toDouble
      val features = split(1).split(",").map(_.toDouble)
      LabeledPoint(label = y, features = Vectors.dense(features))
    }

    model.trainOn(labeledStream)
    model.predictOnValues(labeledStream.map(lp => (lp.label, lp.features))).print()
    //model.predictOn(labeledStream).print()

    ssc.start()
    ssc.awaitTermination()
  }
}
