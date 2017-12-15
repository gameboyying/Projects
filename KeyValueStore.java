import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap; 
import java.util.TimeZone;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.PriorityBlockingQueue;
import org.vertx.java.core.Handler;
import org.vertx.java.core.MultiMap;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.platform.Verticle;


public class KeyValueStore extends Verticle {

	/* TODO: Add code to implement your backend storage */
	
	//USING PRIORITYBLOCKINGQUEUE FOR THREAD SAFTY
	private static Map<String,PriorityBlockingQueue<String>> keyMap =
			new ConcurrentHashMap<String,PriorityBlockingQueue<String>>();
	
	//USING CONCURRENTHASHMAP FOR THREAD SAFTY
	private static Map<String, String> keyValue = 
			new ConcurrentHashMap<String,String>();
	 
	private static Map<String, Long> keyMap2 =
			new ConcurrentHashMap<String,Long>();
	
	
	private void addTimeStamps(final String key, final String timestamp){
        // load timestamp into the key's priorityBLOCKINGQueue
        if(keyMap.containsKey(key)){
        	keyMap.get(key).add(timestamp);
        }
        else{
        	keyMap.put(key,new PriorityBlockingQueue<String>());
        	keyMap.get(key).add(timestamp);
        }
	}
	
	private void helperStrongPut(final HttpServerRequest req, final Long timestamp,final String key, final String value){
		
		//IDENTIFY WHETHER RELEASE THE LOCK
		while(!timestamp.toString().equals(keyMap.get(key).peek())){

	    }	
		
		keyValue.put(key,value);				
		String response = "stored";
		req.response().putHeader("Content-Type", "text/plain");
		req.response().putHeader("Content-Length",
				String.valueOf(response.length()));
		req.response().end(response);
		req.response().close();		
	}
	
	private void helperEventualPut(final HttpServerRequest req,final Long timestamp,final String key, final String value){
		
		//TRY TO FIND THE KEY WITH TIME STAMP
		if(keyMap2.containsKey(key)){
			//IF FIND THE TIMESTAMP, UPDATE THE VALUE AND LATEST TIMESTAMP
			if(timestamp>keyMap2.get(key)){
				keyValue.put(key,value);
				keyMap2.put(key, timestamp);
			}		
		}
		else{
			//FIRST TIME TO LOAD INTO, SO CREATE KEY TIMESTAMP, AND LOAD THE KEY VALUE
			keyValue.put(key,value);
			keyMap2.put(key, timestamp);							
		}
		String response = "stored";
		req.response().putHeader("Content-Type", "text/plain");
		req.response().putHeader("Content-Length",
				String.valueOf(response.length()));
		req.response().end(response);
		req.response().close();			
	}
	
	private void helperStrongGet(final HttpServerRequest req,final Long timestamp,final String key){
		//ADD TIMESTAMP TO DATA STRUCTURE IN ORDER TO LOCK
		addTimeStamps(key,timestamp.toString());
		
		//IDENTIFY WHTHER THE TIMESTAMP HAS BEEN RELEASED OR NOT
		while(!timestamp.toString().equals(keyMap.get(key).peek())){
		}
			
		String response ="0";
		if(keyValue.containsKey(key)){				
			response = keyValue.get(key);					
		}	
			
		req.response().putHeader("Content-Type", "text/plain");
		if (response != null)
				req.response().putHeader("Content-Length",
						String.valueOf(response.length()));
				req.response().end(response);
				req.response().close();
						
	   keyMap.get(key).poll();
	}
	
	private void helperEventualGet(final HttpServerRequest req,final Long timestamp,final String key){
		String response ="0";
		//FIND WHETHER OR NOT THE TIMESTAMP IS UNDER THIS KEY;
		if(keyMap2.containsKey(key)){
			//GET CURRENT TIMESTAMP, COMPARE WITH OLD TIME STAMP, IF GREATER OR EQUAL, TRY TO GET THE VALUE
			if(timestamp>=keyMap2.get(key)){	
				//TRY TO FIND THE VALUE
				if(keyValue.containsKey(key)){		
					response = keyValue.get(key);	
				}	
			}	
		}
		req.response().putHeader("Content-Type", "text/plain");
		if (response != null)
				req.response().putHeader("Content-Length",
						String.valueOf(response.length()));
				req.response().end(response);
				req.response().close();		
	}
	
	@Override
	public void start() {
		final KeyValueStore keyValueStore = new KeyValueStore();
		final RouteMatcher routeMatcher = new RouteMatcher();
		final HttpServer server = vertx.createHttpServer();
		server.setAcceptBacklog(32767);
		server.setUsePooledBuffers(true);
		server.setReceiveBufferSize(4 * 1024);
		routeMatcher.get("/put", new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest req) {
				MultiMap map = req.params();
				String key = map.get("key");
				String value = map.get("value");
				String consistency = map.get("consistency");
				Integer region = Integer.parseInt(map.get("region"));
				Long timestamp = Long.parseLong(map.get("timestamp"));
				/* TODO: Add code here to handle the put request
					 Remember to use the explicit timestamp if needed! */
				
				
					Thread t = new Thread(new Runnable() {
						public void run() {
							if(consistency.equals("strong")){
								// utlize the helper function
								helperStrongPut(req, timestamp,key,value);
							}
							else if(consistency.equals("eventual")){
								// utlize the helper function
								helperEventualPut(req, timestamp,key,value);
							}
						}
					});
					t.start();
				}	
		});
		routeMatcher.get("/get", new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest req) {
				MultiMap map = req.params();
				final String key = map.get("key");
				String consistency = map.get("consistency");
				final Long timestamp = Long.parseLong(map.get("timestamp"));						
				/* TODO: Add code here to handle the get request
					 Remember that you may need to do some locking for this */				
				
				
					Thread t = new Thread(new Runnable() {
						public void run() {	
							
							if(consistency.equals("strong")){
								// utlize the helper function
								helperStrongGet(req,timestamp,key);			
							}
							else if(consistency.equals("eventual")){
								// utlize the helper function
								helperEventualGet(req,timestamp,key);
							}					
						}
					});
					t.start();
					
				}	
			
		});
		// Clears this stored keys. Do not change this
		routeMatcher.get("/reset", new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest req) {
				/* TODO: Add code to here to flush your datastore. This is MANDATORY */
				//CLEAN UP THREE DATA STRUCTURES
				keyMap.clear();
				keyValue.clear();
				keyMap2.clear();
				req.response().putHeader("Content-Type", "text/plain");
				req.response().end();
				req.response().close();
			}
		});
		// Handler for when the AHEAD is called
		routeMatcher.get("/ahead", new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest req) {
				MultiMap map = req.params();
				String key = map.get("key");
				final Long timestamp = Long.parseLong(map.get("timestamp"));
				/* TODO: Add code to handle the signal here if you wish */
					
					//ADD THE TIMESTAMP TO CONTROL THE LOCK
					addTimeStamps(key,timestamp.toString());
					
					req.response().putHeader("Content-Type", "text/plain");
					req.response().end();
					req.response().close();
			}
		});
		// Handler for when the COMPLETE is called
		routeMatcher.get("/complete", new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest req) {
				MultiMap map = req.params();
				String key = map.get("key");
				final Long timestamp = Long.parseLong(map.get("timestamp"));
				/* TODO: Add code to handle the signal here if you wish */		
				
				
				//RELEASE THE HEADER
				keyMap.get(key).poll();
				
				req.response().putHeader("Content-Type", "text/plain");
				req.response().end();
				req.response().close();
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
