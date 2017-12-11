package cc.cmu.edu.minisite;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.json.JSONArray;

public class ProfileServlet extends HttpServlet {
	
	
    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String DB_NAME = "project34";
    private static final String URL = "jdbc:mysql://mysql.clzo5f5ipit4.us-east-1.rds.amazonaws.com:3306/" + DB_NAME;
    // this is db user id
    private static final String DB_USER = "yinningl";
    //this is db password
    private static final String DB_PWD = "shuishui0829";
    private static Connection conn;

    //initialized the mysql
    public ProfileServlet() throws ClassNotFoundException, SQLException{
        /*
            Your initialization code goes here
        */
    	super();
    	Class.forName(JDBC_DRIVER);
        conn = DriverManager.getConnection(URL, DB_USER, DB_PWD);
    }

    
    //get name and profile from mysql
    private String[] getValues(String id, String password){
    	String[] res = null;
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            String tableName = "task1";
            String sql = "select name,profile from " +tableName+ " where id ='" + id + "' and password ='"+ password +"'";
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
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) 
            throws ServletException, IOException {

        /*
            Task 1:
            This query simulates the login process of a user, 
            and tests whether your backend system is functioning properly. 
            Your web application will receive a pair of UserID and Password, 
            and you need to check in your backend database to see if the 
	    UserID and Password is a valid pair. 
            You should construct your response accordingly:

            If YES, send back the user's Name and Profile Image URL.
            If NOT, set Name as "Unauthorized" and Profile Image URL as "#".
        */

    	//get name and profile from mysql
        String id = request.getParameter("id");
        String pwd = request.getParameter("pwd");
        String[] getValues =getValues(id,pwd);
        JSONObject result = new JSONObject();
        if(getValues==null||getValues.equals(null)||getValues[0].equals("")){
        	result.put("name", "Unauthorized");
        	result.put("profile", "#");

        }else{
        	result.put("name", getValues[0]);
        	result.put("profile", getValues[1]);

        }
     
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
