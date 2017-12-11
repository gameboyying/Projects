package cc.cmu.edu.minisite;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.json.JSONArray;
import org.bson.Document;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DB;
import com.mongodb.*;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Sorts.ascending;
import static java.util.Arrays.asList;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonObject;

public class HomepageServlet extends HttpServlet {
	
	MongoClient mongoClient = new MongoClient("172.31.1.42", 27017);
	DB db = mongoClient.getDB("task3");
	
	

    public HomepageServlet() {
        /*
            Your initialization code goes here
        */
    }

    @Override
    protected void doGet(final HttpServletRequest request, 
            final HttpServletResponse response) throws ServletException, IOException {

        String id = request.getParameter("id");
        final JSONObject result = new JSONObject();

        /*
            Task 3:
            Implement your logic to return all the posts authored by this user.
            Return this posts as-is, but be cautious with the order.

            You will need to sort the posts by Timestamp in ascending order
	     (from the oldest to the latest one). 
        */
        
 
        JsonParser jsonParser = new JsonParser();
        DBCollection collection = db.getCollection("posts"); 
        
        //set up search target
        BasicDBObject keys = new BasicDBObject();
        keys.put("uid", Integer.valueOf(id));
        
        //setup sort
        BasicDBObject keys2 = new BasicDBObject();
        keys2.put("timestamp", 1);
        

        //my main idea is to get all the documents. Then parse them and re-group.
        DBCursor cursor = collection.find(keys).sort(keys2);
        JSONArray all = new JSONArray();   
        while(cursor.hasNext()) {
        	JSONObject k = new JSONObject();
            JsonObject tObj = jsonParser.parse(String.valueOf(cursor.next())).getAsJsonObject();
            k.put("content",tObj.get("content").toString().replace("\"", ""));
            k.put("timestamp",tObj.get("timestamp").toString().replace("\"", ""));
            k.put("uid",Integer.valueOf(tObj.get("uid").toString().replace("\"", "")));
            k.put("name",tObj.get("name").toString().replace("\"", ""));
            k.put("image",tObj.get("image").toString().replace("\"", ""));
            k.put("pid",Integer.valueOf(tObj.get("pid").toString().replace("\"", "")));
            k.put("profile",tObj.get("profile").toString().replace("\"", ""));

            JSONArray comments = new JSONArray(); 
            JsonArray commentObj = tObj.getAsJsonArray("comments");
            
			for (JsonElement tag : commentObj) {
				JSONObject o = new JSONObject();
				String uid = tag.getAsJsonObject().get("uid").getAsString();
				o.put("uid", Integer.parseInt(uid));
				String timestamp = tag.getAsJsonObject().get("timestamp").getAsString();
				o.put("timestamp", timestamp);
				String content = tag.getAsJsonObject().get("content").getAsString();
				o.put("content", content);
				String name = tag.getAsJsonObject().get("name").getAsString();
				o.put("name", name);
				String profile = tag.getAsJsonObject().get("profile").getAsString();
				o.put("profile", profile);
				comments.put(o);
			}
            k.put("comments",comments);
            all.put(k);
        }
        
        result.put("posts", all);

        PrintWriter writer = response.getWriter();           
        writer.write(String.format("returnRes(%s)", result.toString()));
        writer.close();
    }

    @Override
    protected void doPost(final HttpServletRequest request, 
            final HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}

