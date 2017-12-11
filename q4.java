
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class q4
 */
public class q4 extends HttpServlet {
	private static final long serialVersionUID = 1L;
	// object to connect and query mysql database
	private static Database db = new Database();
	// id of the current data center (0-4)
	private static final int id = 0;
	// DNS of all data centers
	private static String[] dataCenters;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public q4() {
        super();
        dataCenters = new String[5];
        // DNS for forwarding
        dataCenters[0] = "http://ec2-54-175-155-183.compute-1.amazonaws.com";
        dataCenters[1] = "http://ec2-54-89-49-133.compute-1.amazonaws.com";
        dataCenters[2] = "http://ec2-54-88-70-110.compute-1.amazonaws.com";
        dataCenters[3] = "http://ec2-52-87-171-221.compute-1.amazonaws.com";
        dataCenters[4] = "http://ec2-54-174-229-19.compute-1.amazonaws.com";
    }

    // hash tweetid to a data center based on the last digit
    private int hashTweet(String tweetid) {
    	Integer digit = Integer.parseInt(tweetid.substring(tweetid.length() - 1));
    	return digit / 2;
    }
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// get all parameters
		String tweetid = request.getParameter("tweetid");
		String op = request.getParameter("op");
		String seq = request.getParameter("seq");
		String fields = request.getParameter("fields");
		String payload = request.getParameter("payload");

		String result;
		
		int hash = hashTweet(tweetid);
		
		if (hash == id) {
			// this tweetid belongs to the current data center
			PrintWriter out = response.getWriter();
			if (op.equals("set")) {
				// response before actually writing to database
				// to avoid blocking
				out.print("Apollo,9969-9464-1635\nsuccess\n");
				result = db.q4Set(tweetid, seq, fields, payload);
			} else {
				// get result from database
				result = db.q4Get(tweetid, seq, fields);
				out.print(result);
			}
			
		} else {
			// this tweetid does not belong to the current data center
			// need to redirect
			StringBuilder url = new StringBuilder();
			url.append("/q4?tweetid=");
			url.append(tweetid);
			url.append("&op=");
			url.append(op);
			url.append("&seq=");
			url.append(seq);
			url.append("&fields=");
			url.append(fields);
			url.append("&payload=");
			url.append(payload);
			// generate redirect link
			String link = dataCenters[hash] + url.toString();
			// servlet may parse "+" as space
			link = link.replaceAll(" ", "+");
			// redirect to the right data center
			response.sendRedirect(link);
		}
	}
    
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
