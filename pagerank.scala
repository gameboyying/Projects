import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.{SparkConf, SparkContext, HashPartitioner}

object PageRank {
  def main(args: Array[String]) {
        val conf = new SparkConf().setAppName("task3")
        val sc = new SparkContext(conf)
        val file = sc.textFile("hdfs:///input/TwitterGraph.txt")

// get all the userid from left and right
        val all =  file.flatMap(line => line.split("\t")).distinct()

//count how many userids, get answer from task1
        val count = 2315848

        //all.count()


//get all the userid with group by followers
        val links = file.map{ s => (s.split("\t")(0), s.split("\t")(1))}.distinct().groupByKey().cache().partitionBy(new HashPartitioner(250)).persist()

//initialized the users' pagerank as 1
        var ranks = all.map(v => (v,1.0))

//get danglings user ranks
       // var danglingsuranks = ranks.subtractByKey(links)


        val reverselinks = file.map{ s => (s.split("\t")(1), s.split("\t")(0))}.distinct().groupByKey()

        var nonfollowerranks = ranks.subtractByKey(reverselinks)




      for (i <- 1 to 10) {
      val contribs = links.join(ranks).flatMap{ 
        case (pageId,(urls, rank)) =>
          val size = urls.size
          urls.map(url => (url, rank / size))      
      }
      val value = ranks.subtractByKey(links).map(pair=>pair._2).reduce(_+_)/count

  //reset ranks = followers
      ranks = contribs.reduceByKey(_ + _).mapValues(t => (0.15 + 0.85 * (t+value)))

 //reset danglingsuranks     
     // danglingsuranks = ranks.subtractByKey(links)

 //reset nonfollowerranks   

      nonfollowerranks = nonfollowerranks.mapValues(v => 0.15 + 0.85 * value)

 //ranks = followers+ nonfollowers
      ranks = ranks.union(nonfollowerranks)
    }


     ranks.map(word=>s"${word._1}\t${word._2}").saveAsTextFile("hdfs:///pagerank-output")

 }
}

