import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Database {
	// database info
    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String DB_NAME = "twitter";
    private static final String URL = "jdbc:mysql://localhost/" + DB_NAME + "?useUnicode=true&characterEncoding=UTF-8";
    private static final String DB_USER = "root";
    private static final String DB_PWD = "root";
    // team info
    private static final String teamID = "Apollo";
    private static final String accountID = "9969-9464-1635";
    // an array of multiple connections
    private static Connection[] conns;
    // to access the connections
    private static Integer robin;
    // record current seq for a tweetid in Q4
    private static Map<String, Integer> steps;

	public Database(){	
		try {
			initializeConnection();
			// use a thread safe data structure
			steps = new ConcurrentHashMap<String, Integer>();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	// Initialize all the connections
    private static void initializeConnection() throws ClassNotFoundException, SQLException {
        Class.forName(JDBC_DRIVER);
        conns = new Connection[100];
        robin = 0;
        // one hundred connections
        for (int i = 0; i < 100; i++) {
        	conns[i] = DriverManager.getConnection(URL, DB_USER, DB_PWD);
        }   
    }
    
    // Query the MySQL database (Q2)
	public String mySQL(String userid, String hashtag){
		// combine the userid and hashtag to query
		String query = userid + ":" + hashtag;
		
		StringBuilder res = new StringBuilder();
		PreparedStatement stmt = null;
        try {
            // SQL statement to query the database
            String sql = "Select other from twitterq2 where userid=binary ?";

            // access the connections with round robin
            stmt = conns[robin].prepareStatement(sql);
            stmt.setString(1, query);
            robin++;
            if (robin >= conns.length) {
            	robin = 0;
            }
            
            ResultSet rs = stmt.executeQuery();

            // build the result
            res.append(teamID);
            res.append(",");
            res.append(accountID);
            res.append("\n");
            
            if (rs.next()) {
                // the 'other' column contains censored text
            	// sentiment score, Tweet_time and Tweet_id
                String Censored_text = rs.getString("other");
                Censored_text = Censored_text.replaceAll("\\\\n", "\n");
                Censored_text = Censored_text.replaceAll("\\\\\"", "\"");
                res.append(Censored_text);
                res.append("\n");
            }
            
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
        
		return res.toString();
	}
	
    // Query the MySQL database (Q3)
	public String mySQLQ3TwoCol (String startDate, String endDate, String startUser,
			String endUser, String words) {
		PreparedStatement stmt = null;
		
		// extract the three words
		String[] splits = words.split(",");
		String word1 = splits[0];
		String word2 = splits[1];
		String word3 = splits[2];
		
		// initialize counts as zero
        Map<String, Integer> counts = new HashMap<>();
        counts.put(word1, 0);
        counts.put(word2, 0);
        counts.put(word3, 0);
        
        StringBuilder res = new StringBuilder();

        try {
        	// query by user id
        	String sql = "select other from twitterq3 where userid between ? and ?";
        	
            // access the connections with round robin
            stmt = conns[robin].prepareStatement(sql);
            stmt.setLong(1, Long.parseLong(startUser));
            stmt.setLong(2, Long.parseLong(endUser));
            robin++;
            if (robin >= conns.length) {
            	robin = 0;
            }
            
            ResultSet rs = stmt.executeQuery();

            // go through each user
            while (rs.next()) {
                String result = rs.getString("other");
            	addCounts(counts, result, startDate, endDate, word1, word2, word3);
            }
            
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
        
        // build the result
        res.append(teamID);
        res.append(",");
        res.append(accountID);
        res.append("\n");
        
        // add each word and its corresponding count
        res.append(word1);
        res.append(":");
        res.append(counts.get(word1));
        res.append("\n");
        res.append(word2);
        res.append(":");
        res.append(counts.get(word2));
        res.append("\n");
        res.append(word3);
        res.append(":");
        res.append(counts.get(word3));
        res.append("\n");
        
		return res.toString();
	}
	
	// parse results from mysql and count each word
	private void addCounts(Map<String, Integer> counts, String result,
			String startDate, String endDate, String word1, String word2, String word3) {
		// result format:
		//  col1                             col2
		// userid       date1:word1,count1;word2,count2 date2:word3,count3
		
		String[] splits = result.split(" "); // split date
		for (int i = 0; i < splits.length; i++) {  // go through each date
			String[] parts = splits[i].split(":"); // split date and wordcounts
			String date = parts[0];
			// check whether date is in the range
			if (date.compareTo(startDate) >= 0 && date.compareTo(endDate) <= 0) {
				String[] wordCounts = parts[1].split(";"); // split words
				for (int j = 0; j < wordCounts.length; j++) { // go through each word
					// check whether the word matches the three given words
					// if match, increase the counter
					String[] current = wordCounts[j].split(",");
					if (current[0].equalsIgnoreCase(word1)) {
						Integer now = counts.get(word1);
						counts.put(word1, now + Integer.parseInt(current[1]));
					} else if (current[0].equalsIgnoreCase(word2)) {
						Integer now = counts.get(word2);
						counts.put(word2, now + Integer.parseInt(current[1]));
					} else if (current[0].equalsIgnoreCase(word3)) {
						Integer now = counts.get(word3);
						counts.put(word3, now + Integer.parseInt(current[1]));
					}
				}
			}
		}
	}
	
	// Handle Set request in Q4
	public String q4Set(String tweetid, String seq, String fields, String payloads) {
		// add to the seq map if not exist
		if (steps.get(tweetid) == null) {
			steps.put(tweetid, 1);
		}
		Integer current = Integer.parseInt(seq);
		// start time of block
		long start = System.currentTimeMillis();
		while (current > steps.get(tweetid)) {
			long end = System.currentTimeMillis();
			if (end - start > 500) { // timeout
				break;
			}
			try {
				// offer time for other Servlet threads to execute
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// servlet may parse "+" as space
		payloads = payloads.replaceAll(" ", "+");
		
		PreparedStatement stmt = null;
		// all fields to modify
		String[] field = fields.split(",");
		// all encoded payload
		String[] payload = payloads.split(",");
		
		// generate sql statement
        try {
    		String sql = "INSERT INTO twitterq4 (tweetid,";
    		for(int i=0;i<field.length;i++){
    			sql=sql+field[i]+",";
    		}
    		sql = sql.substring(0,sql.length()-1) +")";
    		sql = sql + " values (?,";
    		for(int i=0;i<field.length;i++){
    			sql=sql+"?,";
    		}
    		sql = sql.substring(0,sql.length()-1) +")";
    		// if the tweet id already exist, then only update
    		sql = sql + " ON DUPLICATE KEY UPDATE ";
    		for(int i=0;i<field.length;i++){
    			sql=sql+ field[i] + "=values("+field[i]+"),";
    		}
    		sql = sql.substring(0,sql.length()-1) +";";
    		
            if (robin >= conns.length) {
            	robin = 0;
            }
            // access the connections with round robin strategy
    		stmt = conns[robin].prepareStatement(sql);
    		stmt.setLong(1, Long.parseLong(tweetid));
    		for(int i=0;i<field.length;i++){
    			if (i < payload.length) {
    				stmt.setString(i+2, payload[i]);
    			} else {
    				// empty payload
    				stmt.setString(i+2, "");
    			}
    		}    	

            robin++;

            // execute sql statement
           int res = stmt.executeUpdate();
            
        } catch (Exception e) {
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

        // update the seq map
        steps.put(tweetid, current + 1);
			
		return "Apollo,9969-9464-1635\nsuccess\n";
	}
	
    // Handle Get request in Q4
	public String q4Get(String tweetid, String seq, String fields) {
		// add to the seq map if not exist
		if (steps.get(tweetid) == null) {
			steps.put(tweetid, 1);
		}
		Integer current = Integer.parseInt(seq);
		// start time of block
		long start = System.currentTimeMillis();
		while (current > steps.get(tweetid)) {
			long end = System.currentTimeMillis();
			if (end - start > 500) { // timeout
				break;
			}
			try {
				// offer time for other Servlet threads to execute
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}		
		
		PreparedStatement stmt = null;
		StringBuilder sb = new StringBuilder();
		
		// generate sql statement
        try {
        	// select the required field of the tweet
    		String sql = "select " + fields + " from twitterq4 where tweetid = ? ";

            if (robin >= conns.length) {
            	robin = 0;
            }
            // access the connections with round robin       
    		stmt = conns[robin].prepareStatement(sql);
    		stmt.setLong(1, Long.parseLong(tweetid));

            robin++;
            
            // execute sql statement
           ResultSet rs = stmt.executeQuery();
           
           sb.append(teamID+","+accountID+"\n");
           if (rs.next()) {
        	   sb.append(rs.getString(1));
               sb.append("\n");
           }         
            
        } catch (Exception e) {
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
		
        // update the seq map
		steps.put(tweetid, current + 1);

		return sb.toString();
	}
	
}

