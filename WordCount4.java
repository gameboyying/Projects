//yinning liu final task4-bonus
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.util.StringUtils;

public class WordCount4 {

  public static class TokenizerMapper
       extends Mapper<Object, Text, Text,Text>{

    static enum CountersEnum { INPUT_WORDS }

    private final static IntWritable one = new IntWritable(1);
    private Text word = new Text();

    @Override
    public void map(Object key, Text value, Context context
                    ) throws IOException, InterruptedException {
      //get the single line
      String line = value.toString();
      //split line into word and count
      String[] str = line.split("\t");
      str[0] = str[0].trim();
      //if words is a single word
      if(str[0].indexOf(" ")<=0){

    	  //sent the word and word:key to parse
    	  // for example: apple 500, thus a as key pple:500 as value, ap as key ple:500 as value and so on
    	  
    	  String res = "";
    	  String other ="";
    	  for(int i=0;i<str[0].length()-1;i++){
    		  res = res + str[0].substring(i, i+1);
    	      other = str[0].substring(i+1);
    	  	  word.set(res);
    	  	  context.write(word, new Text(other + ":" + str[1]));
      }
    }
  }
 }

  public static class IntSumReducer
       extends TableReducer<Text,Text,ImmutableBytesWritable>{
	  

 
    public void reduce(Text key, Iterable<Text> values,
                       Context context
                       ) throws IOException, InterruptedException {  	
    	
      List<String> arrayList = new ArrayList<String>();
      for (Text val : values) {
         String s = val.toString();
         arrayList.add(s);
      }  
       
      //sort arraylist, arraylist's format is word:count. Thus, sort count first Then sort word secondly.
      Collections.sort(arrayList, new Comparator<String>(){
          
          public int compare(String s1, String s2) {
              String[] bk1 = s1.split(":");
              String[] bk2 = s2.split(":");
              int res = Integer.parseInt(bk2[1]) -Integer.parseInt(bk1[1]);
              if(res==0){
            	  return bk1[0].compareTo(bk2[0]);
              }
              
              return res;
          }
      });
      
      
      // output probability, and the maximum times is only 5;     
	     for(int i=0;i<arrayList.size() && i<5;i++){
	    	 	 String[] ttt = arrayList.get(i).split(":");
	    	 	 	    		 
	    	//put into the database     
	     		Put put = new Put(key.toString().getBytes());
	    		put.add(Bytes.toBytes("data"),Bytes.toBytes(key.toString()+ttt[0]),Bytes.toBytes(ttt[1]));
	    		context.write(null, put);
	      }
    }
  }

  public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
    GenericOptionsParser optionParser = new GenericOptionsParser(conf, args);
    String[] remainingArgs = optionParser.getRemainingArgs();
    if (!(remainingArgs.length != 2 || remainingArgs.length != 4)) {
      System.err.println("Usage: wordcount <in> <out> [-skip skipPatternFile]");
      System.exit(2);
    }
    Job job = Job.getInstance(conf, "word count");
    job.setJarByClass(WordCount4.class);
    job.setMapperClass(TokenizerMapper.class);
    
    //setup the connection between the hadoop and hbase
    TableMapReduceUtil.initTableReducerJob("task4", WordCount4.IntSumReducer.class, job);
    job.setReducerClass(IntSumReducer.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(Text.class);

    List<String> otherArgs = new ArrayList<String>();
    for (int i=0; i < remainingArgs.length; ++i) {
      if ("-skip".equals(remainingArgs[i])) {
        job.addCacheFile(new Path(remainingArgs[++i]).toUri());
        job.getConfiguration().setBoolean("wordcount.skip.patterns", true);
      } else {
        otherArgs.add(remainingArgs[i]);
      }
    }
    FileInputFormat.addInputPath(job, new Path(otherArgs.get(0)));
    FileOutputFormat.setOutputPath(job, new Path(otherArgs.get(1)));

    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}