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
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.*;
import org.json.JSONObject;
import org.json.JSONArray;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.RegexStringComparator;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import java.util.*;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.filter.*;
import java.util.TreeMap;
import java.util.Map.Entry;
import org.apache.hadoop.hbase.filter.RegexStringComparator;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.SubstringComparator;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.RegexStringComparator;


public class TimelineServlet extends HttpServlet {
	
    /**
     * The private IP address of HBase master node.
     */
    private static String zkAddr = "172.31.5.191";
    /**
     * The name of your HBase table.
     */
    private static String tableName = "task2";
    /**
     * HTable handler.
     */
    private static HTableInterface task2Table;
    /**
     * HBase connection.
     */
    private static HConnection conn;
    /**
     * Byte representation of column family.
     */
    private static byte[] bColFamily = Bytes.toBytes("data");
    /**
     * Logger.
     */
    private final static Logger logger = Logger.getRootLogger();
    
    
    
    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String DB_NAME = "project34";
    private static final String URL = "jdbc:mysql://mysql.clzo5f5ipit4.us-east-1.rds.amazonaws.com:3306/" + DB_NAME;
    //DB username
    private static final String DB_USER = "yinningl";
    //DB password
    private static final String DB_PWD = "shuishui0829";
    private static Connection conn2;
    
    
	MongoClient mongoClient = new MongoClient("172.31.1.42", 27017);
	DB db = mongoClient.getDB("task3");

	//initialized the mysql, hbase and mongodb
    public TimelineServlet() throws Exception {
        /*
            Your initialization code goes here
        */
    	super();
        logger.setLevel(Level.ERROR);
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.master", zkAddr + ":60000");
        conf.set("hbase.zookeeper.quorum", zkAddr);
        conf.set("hbase.zookeeper.property.clientport", "2181");
	    if (!zkAddr.matches("\\d+.\\d+.\\d+.\\d+")) {
		    System.out.print("HBase not configured!");
		    return;
	    }
        conn = HConnectionManager.createConnection(conf);
        task2Table = conn.getTable(Bytes.toBytes(tableName));
        
    	
    	Class.forName(JDBC_DRIVER);
        conn2 = DriverManager.getConnection(URL, DB_USER, DB_PWD);
           		
    }
    
    // get id's name and profile from mysql
    private String[] getValues(String id){
    	String[] res = null;
        Statement stmt = null;
        try {
            stmt = conn2.createStatement();
            String tableName = "task1";
            String sql = "select name,profile from " +tableName+ " where id ='" + id + "'";
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
            	res = new String[2];
            	res[0]=rs.getString("name");
            	res[1]=rs.getString("profile");
            }
            
            return res;
           
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            
        }

		return res;
    }

    @Override
    protected void doGet(final HttpServletRequest request,
            final HttpServletResponse response) throws ServletException, IOException {

        final JSONObject result = new JSONObject();
        String id = request.getParameter("id");



        /*
            Task 4 (2);
            Get the follower name and profiles as you did in Task 2
            Put them in the result JSON object as one array
        */
    
        // like task2, get followers.
        byte[] bRowKey = Bytes.toBytes(id+",");
        byte[] prefix = Bytes.toBytes(id+",");
        Filter prefixFilter = new PrefixFilter(prefix);
    	Scan scan = new Scan(bRowKey,prefixFilter);
        byte[] bCol1 = Bytes.toBytes("name");
        scan.addColumn(bColFamily, bCol1);
        byte[] bCol2 = Bytes.toBytes("profile");
        scan.addColumn(bColFamily, bCol2);
        ResultScanner rs = task2Table.getScanner(scan);     
        JSONArray followers = new JSONArray();      
        Map<String,String> map = new TreeMap<String,String>();
        for (Result r = rs.next(); r != null; r = rs.next()) {   
            map.put(Bytes.toString(r.getValue(bColFamily, bCol1)), Bytes.toString(r.getValue(bColFamily, bCol2)));
        }
        
        for(Entry<String,String> mapping:map.entrySet()){ 
            JSONObject follower = new JSONObject();
            follower.put("name", mapping.getKey());
            follower.put("profile",  mapping.getValue());
            followers.put(follower);
       } 
        
        
        	
        rs.close();
        
        result.put("followers", followers);
        
        /*
        Task 4 (1):
        Get the name and profile of the user as you did in Task 1
        Put them as fields in the result JSON object
         */
        //like task1, get id's name and profile
        String[] getValues =getValues(id);
        
        if(getValues==null||getValues.equals(null)||getValues[0].equals("")){
        	result.put("name", "Unauthorized");
        	result.put("profile", "#");

        }else{
        	result.put("name", getValues[0]);
        	result.put("profile", getValues[1]);

        }

        
        /*
        Task 4 (3):
        Get the 30 LATEST followee posts and put them in the
        result JSON object as one array.

        The posts should be sorted:
        First in ascending timestamp order
        Then numerically in ascending order by their PID (PostID) 
    	if there is a tie on timestamp
         */
        
        // get id's followees
        List<Integer> list = new ArrayList<Integer>();
		Scan scan2 = new Scan();
		String reg = "^\\d*," + id + "$";
		Filter rowFilter = new RowFilter(CompareOp.EQUAL,
			    new RegexStringComparator(reg));
		scan2.setFilter(rowFilter);
        ResultScanner rs2 = task2Table.getScanner(scan2);     
        for (Result r = rs2.next(); r != null; r = rs2.next()) {   
        	 String key = Bytes.toString(r.getRow());
        	 list.add(Integer.parseInt(key.replace(","+id, "")));
        }
        rs2.close();
        

        // search mongodb by uids. get uids from above. Then use "in" to get real list
        JsonParser jsonParser = new JsonParser();
        DBCollection collection = db.getCollection("posts"); 
   
        BasicDBObject keys = new BasicDBObject();     
        keys.put("uid", new BasicDBObject("$in", list));
        
        BasicDBObject keys2 = new BasicDBObject();
        keys2.put("timestamp", -1);
        keys2.put("pid",-1);

       

        DBCursor cursor = collection.find(keys).sort(keys2).limit(30);
        JSONArray all = new JSONArray();   
        List<DBObject> list2= cursor.toArray();
        Collections.reverse(list2);
        for(DBObject l:list2) {
        	JSONObject k = new JSONObject();
            JsonObject tObj = jsonParser.parse(String.valueOf(l.toString())).getAsJsonObject();
            k.put("content",tObj.get("content").toString().replace("\"", ""));
            k.put("timestamp",tObj.get("timestamp").toString().replace("\"", ""));
            k.put("_id",new JSONObject().put("$oid",tObj.getAsJsonObject("_id").get("$oid").toString().replace("\"", "")));
            k.put("uid",Integer.valueOf(tObj.get("uid").toString().replace("\"", "")));
            k.put("name",tObj.get("name").toString().replace("\"", ""));
            k.put("image",tObj.get("image").toString().replace("\"", ""));
            k.put("pid",Integer.valueOf(tObj.get("pid").toString().replace("\"", "")));
            k.put("profile",tObj.get("profile").toString().replace("\"", ""));

            JSONArray comments = new JSONArray(); 
            JsonArray commentObj = tObj.getAsJsonArray("comments");
            
			for (JsonElement tag : commentObj) {
				JSONObject o = new JSONObject();
				String content = tag.getAsJsonObject().get("content").getAsString();
				o.put("content", content);
				String timestamp = tag.getAsJsonObject().get("timestamp").getAsString();
				o.put("timestamp", timestamp);
				String uid = tag.getAsJsonObject().get("uid").getAsString();
				o.put("uid", Integer.parseInt(uid));


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

        
        PrintWriter out = response.getWriter();
        out.print(String.format("returnRes(%s)", result.toString()));
        out.close();
        
        
        

    }

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
    
}

