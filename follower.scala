import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf

object Follower {
  def main(args: Array[String]) {
        val conf = new SparkConf().setAppName("task2")
        val sc = new SparkContext(conf)
        val file = sc.textFile("hdfs:///input/TwitterGraph.txt")
        val lines = file.distinct().map(line=>line.split("\t")(0)).map(word=>(word,0)).reduceByKey(_+_)
        val lines2= file.distinct().map(line=>line.split("\t")(1)).map(word=>(word,1)).reduceByKey(_+_)
        val lines3= lines++lines2
        lines3.map(words=>s"${words._1}\t${words._2}").saveAsTextFile("hdfs:///follower-output")
 }
}
