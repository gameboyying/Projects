//yinningliu final Task1
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.IntWritable;

public class WordCount {
	
	public static class Map extends Mapper<LongWritable,Text,Text,IntWritable>{
		private final static IntWritable one = new IntWritable(1);
		private Text word = new Text();
		
		//parse to get very clean result
		public String parse(String original){
			
			if(original.equals("")||original==null){
				return "";
			}
			
			//translate the sentence from uppercase to lowercase
			String original2 = original.toLowerCase();
			
			//remove <ref>, ect from the sentence
			StringBuffer buffer2 = new StringBuffer();
			Pattern pattern2 = Pattern.compile("</ref>|<ref.*?>");
			Matcher matcher2 = pattern2.matcher(original2);
			
	        while(matcher2.find()){              
	            buffer2.append(matcher2.replaceAll(" "));        
	            buffer2.append(" ");              
	            original2=buffer2.toString(); 	
	        }
			
			
			//remove http https ftp of url from the sentence
			//pattern2 = Pattern.compile("(http://|https://|ftp://){1}[\\w\\.\\-/:#]+"); 
	        
	        pattern2 = Pattern.compile("(https?|ftp):\\/\\/[^\\s/$.?#][^\\s]*"); 
	        matcher2 = pattern2.matcher(original2);
			StringBuilder buffer3 = new StringBuilder();
	        while(matcher2.find()){              
	            buffer3.append(matcher2.replaceAll(" "));        
	            buffer3.append(" ");              
	            original2 = buffer3.toString();
	        }
	        
			
			
			//remove all characters except a-z and single quote
			pattern2 = Pattern.compile("[^a-z' ]+"); 
			matcher2 = pattern2.matcher(original2);		
			StringBuffer buffer4 = new StringBuffer();
	        while(matcher2.find()){              
	            buffer4.append(matcher2.replaceAll(" "));        
	            buffer4.append(" ");              
	            original2 = buffer4.toString();
	        }
			
	        
	        String[] str = original2.split(" ");
			
			StringBuffer buffer = new StringBuffer();
			
			// remove single quotes before the words and after the words
			for(int i=0;i<str.length;i++){        

				//remove single quotes before the single word
				while(str[i].indexOf("'")==0){
					str[i]=str[i].substring(1);
				}
				
				//remove single quotes after the single word
				while(!str[i].equals("")&&str[i].charAt(str[i].length()-1)=='\''){
					int k = str[i].length();
					str[i]=str[i].substring(0,k-1);
				}
		             buffer.append(str[i]);  
		             buffer.append(" ");  
		             original2= buffer.toString();
			}

			//remove duplication space between the words
			while(original2.indexOf("  ")>=0){
				original2 = original2.replace("  ", " ");
			}
			
			return original2.trim();
		}
		
		//Ngram program
		public String nGram(String original){
			if(original.equals("")||original==null)
				return "";
			
			String res ="";
			String[] gram = original.split(" ");
			// 1 gram
			
			for(int i=0;i<gram.length;i++){
				res = res + gram[i]+"\t";
			}
			
			//2 gram:

			for(int i =0;i<gram.length-1;i++){
				res = res + gram[i] + " " + gram[i+1]+ "\t";
			}
			
			//3 gram:
			for(int i=0;i<gram.length-2;i++){
				res= res+ gram[i] + " " + gram[i+1] +" "+gram[i+2] +"\t";
			}
			
			//4 gram:
			for(int i=0;i<gram.length-3;i++){
				res =res + gram[i] + " " + gram[i+1]+" " + gram[i+2] + " " + gram[i+3] +"\t";
			}
			
			//5 gram:
			for(int i=0;i<gram.length-4;i++){
				res =res + gram[i] + " " + gram[i+1]+" " + gram[i+2] + " " + gram[i+3] +" " + gram[i+4] +"\t";
			}
					
			return res;
		}
		
		public void map(LongWritable key, Text value, Context context) throws IOException,InterruptedException{
			//get line after parse and ngram
			String line = nGram(parse(value.toString()));
			StringTokenizer tokenizer = new StringTokenizer(line,"\t");
			while(tokenizer.hasMoreTokens()){
				word.set(tokenizer.nextToken());
				context.write(word, one);
			}	
		}	
	}
	public static class Reduce extends Reducer<Text,IntWritable,Text,IntWritable>{
		public void reduce(Text key,Iterable<IntWritable> values, Context context)
			throws IOException,InterruptedException{
			int sum=0;
			for(IntWritable val:values){
				sum +=val.get();
			}
			//write the result to output file
			context.write(key, new IntWritable(sum));
		}
	}
	
	

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
	       Configuration conf = new Configuration();
	       //set up timeout
			long milliSeconds = 1000*60*60;
			conf.setLong("mapred.task.timeout", milliSeconds);
	        Job job = Job.getInstance(conf, "word count");
	        job.setJarByClass(WordCount.class);
	        job.setMapperClass(Map.class);
	        job.setCombinerClass(Reduce.class);
	        job.setReducerClass(Reduce.class);
	        job.setOutputKeyClass(Text.class);
	        job.setOutputValueClass(IntWritable.class);
	        FileInputFormat.addInputPath(job, new Path(args[0]));
	        FileOutputFormat.setOutputPath(job, new Path(args[1]));
	        System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
