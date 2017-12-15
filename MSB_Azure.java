import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.platform.Verticle;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

//New class, override the removeEldestEntry in order to control size;
class SizeControlMap<K,V> extends LinkedHashMap<K,V>{
	private int capacity;
	private static final long serialVersionUID= 1L;
	
	//initial capacity
	SizeControlMap(int capacity){
		super(20,0.75f,true);
		this.capacity=capacity;
	}
	
	//if the size is greater than capacity, just remove the edlest one
	public boolean removeEldestEntry(Map.Entry<K, V> edlest){
		return size()>capacity;
	}	
}

public class MSB extends Verticle {
		
	private String[] databaseInstances = new String[2];		
	/* 
	 * init -initializes the variables which store the 
	 *	     DNS of your database instances
	 */
	
	private static Map<String,String> cache;
	
	private void init() {
		/* Add the DNS of your database instances here */
		databaseInstances[0] = "cloud-282480vm.eastus.cloudapp.azure.com";
		databaseInstances[1] = "cloud-603366vm.eastus.cloudapp.azure.com";		
		//initial object of the SizeControlMap
		cache= new SizeControlMap<String,String>(950);
	}
	
	/*
	 * checkBackend - verifies that the DCI are running before starting this server
	 */	
	private boolean checkBackend() {
    	try{
    		if(sendRequest(generateURL(0,"1")) == null ||
            	sendRequest(generateURL(1,"1")) == null)
        		return true;
    	} catch (Exception ex) {
    		System.out.println("Exception is " + ex);
    	}

    	return false;
	}

	/*
	 * sendRequest
	 * Input: URL
	 * Action: Send a HTTP GET request for that URL and get the response
	 * Returns: The response
	 */
	private String sendRequest(String requestUrl) throws Exception {
		 
		URL url = new URL(requestUrl);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		connection.setRequestMethod("GET");
		connection.setRequestProperty("User-Agent", "Mozilla/5.0");
 
		BufferedReader in = new BufferedReader(
					new InputStreamReader(connection.getInputStream(), "UTF-8"));
		
		String responseCode = Integer.toString(connection.getResponseCode());
		if(responseCode.startsWith("2")){
			String inputLine;
			StringBuffer response = new StringBuffer();
 
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			return response.toString();
    	} else {
    		System.out.println("Unable to connect to "+requestUrl+
    		". Please check whether the instance is up and also the security group settings"); 
    		return null;
    	}   
	}
	/*
	 * generateURL
	 * Input: Instance ID of the Data Center id
	 * Returns: URL which can be used to retrieve the user's details
	 * 			from the data center instance
	 * Additional info: the user's details are cached on backend instance
	 */
	private String generateURL(Integer instanceID, String key) {
		return "http://" + databaseInstances[instanceID] + "/target?targetID=" + key;
	}
	
	/*
	 * generateRangeURL
	 * Input: 	Instance ID of the Data Center
	 * 		  	startRange - starting range (id)
	 *			endRange - ending range (id)
	 * Returns: URL which can be used to retrieve the details of all
	 * 			user in the range from the data center instance
	 * Additional info: the details of the last 1000 user are cached
	 * 					in the database instance
	 * 				
	 */
	private String generateRangeURL(Integer instanceID, String startRange, String endRange) {
		return "http://" + databaseInstances[instanceID] + "/range?start_range="
				+ (startRange) + "&end_range=" + (endRange);
	}

	/* 
	 * retrieveDetails - you have to modify this function to achieve a higher RPS value
	 * Input: the targetID
	 * Returns: The result from querying the database instance
	 */
	private String retrieveDetails(String targetID) {
		Random random = new Random();
		try{
			//find whether the key in the cache
			if(cache.containsKey(targetID)){
				return cache.get(targetID);
			}
			
			//for return;
			String res = "";
			
			/* i used partial to test part 3
			 * setup a range around the targetId
			 * setup start value is less than 20;
			 * setup end value is greater than 20
			 */
			int start = Integer.parseInt(targetID) - 20;
			if(start<=0){
				start=1;
			}
			int end =  Integer.parseInt(targetID) + 20;
			
			//just get 40 targets and saved into cache
			String temp = retrieveDetails(String.valueOf(start),String.valueOf(end));
			
			//find result again in cache
			if(cache.containsKey(targetID)){
				res= cache.get(targetID);
			}
			System.out.println("cache:" + cache.size());
			//return correct value
			return res;
			
		} catch (Exception ex){
			System.out.println(ex);
			return null;
		}
	}
	
	private int getInstanceId(){
		Random random = new Random();
		return random.nextInt(2);
	}

	private String retrieveDetails(String start, String end) {
		try{		
			int s = Integer.parseInt(start);
			int e = Integer.parseInt(end);
			String str1 =sendRequest(generateRangeURL(getInstanceId(), start, end));
			//split the target
			String[] split= str1.split(";");
			int j=0;
			// get target and value and put into the map
			for(int k=s;k<=e;k++){
				cache.put(String.valueOf(k), split[j++]);
			}	
			
			//return exception
			return  "";
		} catch (Exception ex){
			System.out.println(ex);
			return null;
		}
	}
	
	/* 
	 * processRequest - calls the retrieveDetails function with the id
	 */
	private void processRequest(String id, HttpServerRequest req) {
		String result = retrieveDetails(id);
		if(result != null)
			req.response().end(result);	
		else
			req.response().end("No resopnse received");
	}

	private void processRequest(String start, String end, HttpServerRequest req) {
		String result = retrieveDetails(start, end);
		if(result != null)
			req.response().end(result);	
		else
			req.response().end("No resopnse received");
	}
	
	/*
	 * start - starts the server
	 */
  	public void start() {
  		init();
		if(!checkBackend()){
  			vertx.createHttpServer().requestHandler(new Handler<HttpServerRequest>() {
				public void handle(HttpServerRequest req) {
				    String query_type = req.path();		
				    req.response().headers().set("Content-Type", "text/plain");
				    if(query_type.equals("/target")) {
					    String key = req.params().get("targetID");
					    processRequest(key,req);
				    } else if (query_type.equals("/range")) {
				    	String start = req.params().get("start_range");
				    	String end = req.params().get("end_range");
				    	processRequest(start, end, req);
				    }
			    }               
			}).listen(80);
		} else {
			System.out.println("Please make sure that both your DCI are up and running");
		}
	}
}
