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


public class RecommendationServlet extends HttpServlet {
	JSONObject result = new JSONObject();
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
	//this is DB user name not credential
	private static final String DB_USER = "yinningl";
	//this is DB password not credential
	private static final String DB_PWD = "shuishui0829";
	private static Connection conn2;
	
	
	//hbase and mysql connection setup
	public RecommendationServlet () throws Exception {
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
	

    
    
    private void getValues(String in, String notin){
    	String[] res = null;
        Statement stmt = null;
        try {
            stmt = conn2.createStatement();
            String tableName = "task1";
            //query is search table get 10 followee directly
            String sql = "select name,profile,id,c from task1 a inner join (select feeid, count(feeid) as c from task5 where "
            		+ "ferid in ("+in+") and feeid not in ("+notin+") group by feeid order by c desc,feeid asc limit 10) as b on a.id=b.feeid order by c desc, id asc;";
            ResultSet rs = stmt.executeQuery(sql);
            
            JSONArray all = new JSONArray();   
            while (rs.next()) {
    	          JSONObject obj = new JSONObject();
    	          obj.put("name", rs.getString("name"));
    	          obj.put("profile", rs.getString("profile"));
    	          all.put(obj);
            }
            result.put("recommendation", all);
           
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
    }

	protected void doGet(final HttpServletRequest request, final HttpServletResponse response) 
			throws ServletException, IOException {

		
	        String id = request.getParameter("id");

		/**
		 * Bonus task:
		 * 
		 * Recommend at most 10 people to the given user with simple collaborative filtering.
		 * 
		 * Store your results in the result object in the following JSON format:
		 * recommendation: [
		 * 		{name:<name_1>, profile:<profile_1>}
		 * 		{name:<name_2>, profile:<profile_2>}
		 * 		{name:<name_3>, profile:<profile_3>}
		 * 		...
		 * 		{name:<name_10>, profile:<profile_10>}
		 * ]
		 * 
		 * Notice: make sure the input has no duplicate!
		 */
	        // get id's followee from hbase like task3.
	        List<Integer> set = new ArrayList<Integer>();
			Scan scan2 = new Scan();
			String reg = "^\\d*," + id + "$";
			Filter rowFilter = new RowFilter(CompareOp.EQUAL,
				    new RegexStringComparator(reg));
			scan2.setFilter(rowFilter);
	        ResultScanner rs2 = task2Table.getScanner(scan2);   
	       
	        String in = "";
	        String notin = id+",";
	        
	        for (Result r = rs2.next(); r != null; r = rs2.next()) {  
	        	 String key = Bytes.toString(r.getRow());
	        	 in = in + key.replace(","+id, "")+",";
	        	 notin = notin + key.replace(","+id, "")+",";
	        }
	        rs2.close();

	        // i used mysql to get answer directly, setup in and notin and make search
	        // you may check my query details
	        getValues(in.substring(0,in.length()-1),notin.substring(0,notin.length()-1));
	         
    
        PrintWriter writer = response.getWriter();
        writer.write(String.format("returnRes(%s)", result.toString()));
        writer.close();

	}

	@Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}

