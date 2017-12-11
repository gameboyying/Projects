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


public class FollowerServlet extends HttpServlet {

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
    
    //initialize the Hbase
    public FollowerServlet() throws IOException {
        /*
            Your initialization code goes here
        */
    	
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
    }

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {

        String id = request.getParameter("id");
        
        /*
        Task 2:
        Implement your logic to retrive the followers of this user. 
        You need to send back the Name and Profile Image URL of his/her Followers.

        You should sort the followers alphabetically in ascending order by Name. 
        If there is a tie in the followers name, 
    	sort alphabetically by their Profile Image URL in ascending order. 
         */
        
        //hbase search to search rowkey. my rowkey is "followee,follower"
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
        // use treemap to sort
        for (Result r = rs.next(); r != null; r = rs.next()) {   
            map.put(Bytes.toString(r.getValue(bColFamily, bCol1)), Bytes.toString(r.getValue(bColFamily, bCol2)));
        }
        
        //putinto json array
        for(Entry<String,String> mapping:map.entrySet()){ 
            JSONObject follower = new JSONObject();
            follower.put("name", mapping.getKey());
            follower.put("profile",  mapping.getValue());
            followers.put(follower);
       } 
        
        
        	
        rs.close();
        //putinto json object
        JSONObject res = new JSONObject();
        res.put("followers", followers);

        PrintWriter writer = response.getWriter();
        writer.write(String.format("returnRes(%s)", res.toString()));
        writer.close();
    }

    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }   
    
}


