import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.TimeZone;

import javax.swing.text.Keymap;

import java.util.Iterator;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.sql.Timestamp;

import org.vertx.java.core.Handler;
import org.vertx.java.core.MultiMap;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.platform.Verticle;

public class Coordinator extends Verticle {

	//Default mode: sharding. Possible string values are "replication" and "sharding"
	private static String storageType; //= "sharding";
	private static PriorityQueue<String> operations = new PriorityQueue<String>();
	private static Map<String, PriorityQueue<String>> keyMap = new HashMap<String, PriorityQueue<String>>();


	/**
	 * TODO: Set the values of the following variables to the DNS names of your
	 * three dataCenter instances
	 */
	private static final String dataCenter1 = "ec2-52-91-4-110.compute-1.amazonaws.com";
	private static final String dataCenter2 = "ec2-54-84-159-194.compute-1.amazonaws.com";
	private static final String dataCenter3 = "ec2-52-90-128-93.compute-1.amazonaws.com";

	
	private void helperGetReplication(final HttpServerRequest req, final String key, final String loc, final String timestamp){
		try{
			
			/* strict ordering and Controlled Access
			 * Ordering control by priorityqueue and access control by locking key's priorityqueue
			 * 
			 * create Map(key,timestamp(priorityQueue))
			 * if request for one certain key, just lock that key's timestamp. Thus, all datacenters for this key will be locked
			 * Timestamp will control the request sequence(ordering)
			 * in here, after get is done for the certain key, just release that key's priorityQueue
			 */
			// key's value as object (priorityQueue)
			synchronized(keyMap.get(key)){	
					//if q size >0 and current timestamp is not equal to q's miminum timestamp. Lock this key's priority.
					while(keyMap.get(key).size()!=0 && !keyMap.get(key).peek().equals(timestamp))
					{
						try{
							keyMap.get(key).wait();
						}
						catch(Exception e){
							e.printStackTrace();
						}
					}
					
					// get key's value from datacenter. if one datacenter is down, i will get value from other datacenter
					String res=KeyValueLib.GET(dataCenter1, key);
					res = res.equals(null)?KeyValueLib.GET(dataCenter2, key):res;
					res = res.equals(null)?KeyValueLib.GET(dataCenter3, key):res;
					// value is null, output 0
					if(res.equals(null)){
							req.response().end("0"); //Default response = 0
					}
					else{
						req.response().end(res);
					}
					// remove this key's minimum timestamp. release all the locks.
					keyMap.get(key).poll();
					keyMap.get(key).notifyAll();
					}
				}
				catch(Exception ex)
				{
					System.out.print(ex.getMessage());
				}		
		}	

	
	private void helperPutReplication(final HttpServerRequest req, final String key, final String value,final String timestamp){
			try{		
				/* strict ordering and Controlled Access
				 * Ordering control by priorityqueue and access control by locking key's priorityqueue
				 * 
				 * create Map(key,timestamp(priorityQueue))
				 * if request for one certain key, just lock that key's timestamp. Thus, all datacenters for this key will be locked
				 * Timestamp will control the request sequence(ordering)
				 * in here, after get is done for the certain key, just release that key's priorityQueue
				 */
				// key's value as object (priorityQueue)
				synchronized(keyMap.get(key)){	
					
					//if q size >0 and current timestamp is not equal to q's miminum timestamp. Lock this key's priority.
					while(keyMap.get(key).size()!=0 && !keyMap.get(key).peek().equals(timestamp))
					{
						try{
							keyMap.get(key).wait();
						}
						catch(Exception e){
							e.printStackTrace();
						}
					}
					
							//Replication Strict Consistency. Keep same key/value into the 3 datacenters
							KeyValueLib.PUT(dataCenter1,key,value);										
							KeyValueLib.PUT(dataCenter2,key,value);
							KeyValueLib.PUT(dataCenter3,key,value);
							
							// remove this key's minimum timestamp. release all the locks.
							keyMap.get(key).poll();
							keyMap.get(key).notifyAll();								
				}
	
			}
			catch(Exception ex)
			{
				System.out.print(ex.getMessage());
			}	
	}
	
	private void helperPutSharding(final HttpServerRequest req, final String key, final String value,final String timestamp){
		try{
			/* strict ordering and Controlled Access
			 * Ordering control by priorityqueue and access control by locking key's priorityqueue
			 * 
			 * create Map(key,timestamp(priorityQueue))
			 * if request for one certain key, just lock that key's timestamp. Thus, all datacenters for this key will be locked
			 * Timestamp will control the request sequence(ordering)
			 * in here, after get is done for the certain key, just release that key's priorityQueue
			 */
			// key's value as object (priorityQueue)
			synchronized(keyMap.get(key)){	
				//if q size >0 and current timestamp is not equal to q's miminum timestamp. Lock this key's priority.	
				while(keyMap.get(key).size()!=0 && !keyMap.get(key).peek().equals(timestamp))
				{
					try{
						keyMap.get(key).wait();
					}
					catch(Exception e){
						
					}
				}
					
				// if key is a,b,c, then load to dc1, dc2, dc3
				if(key.equals("a")){
						KeyValueLib.PUT(dataCenter1,key,value);
				}
				else if(key.equals("b")){
						KeyValueLib.PUT(dataCenter2,key,value);
				}
				else if(key.equals("c")){
						KeyValueLib.PUT(dataCenter3,key,value);
				}
				else{	
					//if key is not a,b,c. then calculate hashcode to load
					//I defined a method named hashcode();
					int hashcode=personalHashCode(key);
					if(hashcode==0){
						KeyValueLib.PUT(dataCenter1,key,value);
					}		
					else if(hashcode==1){
						KeyValueLib.PUT(dataCenter2,key,value);
					}					
					else if(hashcode==2){
						KeyValueLib.PUT(dataCenter3,key,value);
					}	
				}	
				// remove this key's minimum timestamp. release all the locks.
				keyMap.get(key).poll();
				keyMap.get(key).notifyAll();								
			}
		}
		catch(Exception ex)
		{
			System.out.print(ex.getMessage());
		}
	}
	
	private void helperGetSharding(final HttpServerRequest req, final String key, final String loc, final String timestamp){
		try{
			
			/* strict ordering and Controlled Access
			 * Ordering control by priorityqueue and access control by locking key's priorityqueue
			 * 
			 * create Map(key,timestamp(priorityQueue))
			 * if request for one certain key, just lock that key's timestamp. Thus, all datacenters for this key will be locked
			 * Timestamp will control the request sequence(ordering)
			 * in here, after get is done for the certain key, just release that key's priorityQueue
			 */
			// key's value as object (priorityQueue)
			synchronized(keyMap.get(key)){	
				
				//if q size >0 and current timestamp is not equal to q's miminum timestamp. Lock this key's priority.
				while(keyMap.get(key).size()!=0 && !keyMap.get(key).peek().equals(timestamp))
				{
					try{
						keyMap.get(key).wait();
					}
					catch(Exception e){
						
					}
				}
				
				//if loc is not datacenter, return 0;
				if(!loc.equals("1") && !loc.equals("2") && !loc.equals("3") && !loc.equals(null))
				{
					req.response().end("0");
					return;
				}
				
				
				String res=null;
				
				if(loc.equals("1")){
					res=KeyValueLib.GET(dataCenter1, key);
					
				}
				else if(loc.equals("2")){
					res=KeyValueLib.GET(dataCenter2, key);
				}
				else if(loc.equals("3")){
					res=KeyValueLib.GET(dataCenter3, key);
				}
				else{
					//loc is null, use hashcode to find location
					int hashcode = personalHashCode(key);
					if(hashcode==0){
						res=KeyValueLib.GET(dataCenter1, key);
					}
					if(hashcode==1){
						res=KeyValueLib.GET(dataCenter2, key);
					}
					if(hashcode==2){
						res=KeyValueLib.GET(dataCenter3, key);
					}
				}
				
				// key does not there
				if(res.equals(null)){
					req.response().end("0"); //Default response = 0
				}
				else{
					req.response().end(res);
				}
				
				// remove this key's minimum timestamp. release all the locks.
				keyMap.get(key).poll();
				keyMap.get(key).notifyAll();

			}
		}
		catch(Exception ex)
		{
			System.out.print(ex.getMessage());
		}	
	}
	
	// hash algorithom
	private int personalHashCode(String key){
		int total=0;
		for(char c:key.toCharArray()){
			total+=c;
		}

		int hashcode=total %3;
		return hashcode;
	}
	
	private void addTimeStamps(final String key, final String timestamp){
        // load timestamp into the key's priorityQueue
        if(keyMap.containsKey(key)){
        	keyMap.get(key).add(timestamp);
        }
        else{
        	keyMap.put(key,new PriorityQueue<String>());
        	keyMap.get(key).add(timestamp);
        }	
	}
	
	@Override
	public void start() {
		//DO NOT MODIFY THIS
		KeyValueLib.dataCenters.put(dataCenter1, 1);
		KeyValueLib.dataCenters.put(dataCenter2, 2);
		KeyValueLib.dataCenters.put(dataCenter3, 3);
		final RouteMatcher routeMatcher = new RouteMatcher();
		final HttpServer server = vertx.createHttpServer();
		server.setAcceptBacklog(32767);
		server.setUsePooledBuffers(true);
		server.setReceiveBufferSize(4 * 1024);

		routeMatcher.get("/put", new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest req) {
				MultiMap map = req.params();
				final String key = map.get("key");
				final String value = map.get("value");
				//You may use the following timestamp for ordering requests
                final String timestamp = new Timestamp(System.currentTimeMillis() 
                                                                + TimeZone.getTimeZone("EST").getRawOffset()).toString();
				//add timestamps into queue
                addTimeStamps(key,timestamp);
                
                Thread t = new Thread(new Runnable() {
					public void run() {
						//TODO: Write code for PUT operation here.
						//Each PUT operation is handled in a different thread.
						//Highly recommended that you make use of helper functions.
						
						if(storageType.equals("replication"))
							helperPutReplication(req, key, value,timestamp);
						
						if(storageType.equals("sharding"))
							helperPutSharding(req, key, value,timestamp);
						
					}
				});
				t.start();
				req.response().end(); //Do not remove this
			}
		});

		routeMatcher.get("/get", new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest req) {
				MultiMap map = req.params();
				final String key = map.get("key");
				final String loc = map.get("loc");
				//You may use the following timestamp for ordering requests
				final String timestamp = new Timestamp(System.currentTimeMillis() 
								+ TimeZone.getTimeZone("EST").getRawOffset()).toString();
				
				//add timestamps into queue
                addTimeStamps(key,timestamp);
				
				Thread t = new Thread(new Runnable() {
					public void run() {
						//TODO: Write code for GET operation here.
                                                //Each GET operation is handled in a different thread.
                                                //Highly recommended that you make use of helper functions.
							if(storageType.equals("replication"))
								helperGetReplication(req, key,loc,timestamp);
							
							if(storageType.equals("sharding"))
								helperGetSharding(req, key,loc,timestamp);
					}
				});
				t.start();
			}
		});

		routeMatcher.get("/storage", new Handler<HttpServerRequest>() {
                        @Override
                        public void handle(final HttpServerRequest req) {
                                MultiMap map = req.params();
                                storageType = map.get("storage");
                                //This endpoint will be used by the auto-grader to set the 
				//consistency type that your key-value store has to support.
                                //You can initialize/re-initialize the required data structures here
                                req.response().end();
                        }
                });

		routeMatcher.noMatch(new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest req) {
				req.response().putHeader("Content-Type", "text/html");
				String response = "Not found.";
				req.response().putHeader("Content-Length",
						String.valueOf(response.length()));
				req.response().end(response);
				req.response().close();
			}
		});
		server.requestHandler(routeMatcher);
		server.listen(8080);
	}
}
