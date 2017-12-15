import java.net.URL;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.*;
import java.net.*;


class AwsHorizontalScaling {
	AwsHorizontalScaling(){
		
	}
	
	double getLogFile(String url) throws IOException{
		URL realUrl = new URL(url);
		URLConnection conn = realUrl.openConnection();
		conn.connect();
		
		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String readLine=null;
		String readTime=null;
		double timeCount=0.00d;
		boolean readNextLine = false;
		
		while((readLine=in.readLine())!=null){
			
			if(readNextLine==true && readLine.indexOf("amazonaws.com=")!=-1){
				readTime=readLine.split("=")[1];
				timeCount += Double.parseDouble(readTime) ;
			}
			// if next line does not include cloudapp.azure.com=, then cannot be read
			else{
				readNextLine=false;
			}
			//find minute, then start to read next line
			if(readLine.indexOf("[Minute")!=-1){
				timeCount=0;
				readNextLine=true;
			}

		}
		

		
		
		return timeCount;
		
	}
	
	
	
}
