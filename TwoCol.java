
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public class TwoCol {
	// transfer data to a two-column schema before storing into database
	public static void main(String[] args) throws IOException {
		String filename = args[0];
		// input buffer
		BufferedReader br = 
                new BufferedReader(new InputStreamReader(
                		new FileInputStream(filename), StandardCharsets.UTF_8));
		// output writer
		PrintWriter stdout = new PrintWriter(
			    new OutputStreamWriter(new FileOutputStream("two"), StandardCharsets.UTF_8),
			    true);
		//  col1                             col2
		// userid       date1:word1,count1;word2,count2 date2:word3,count3
		String input;
		String prevId = null;
		String prevDate = null;
		StringBuilder line = new StringBuilder();

		while ((input=br.readLine())!=null) { // read line by line
			String[] splits = input.split("\t");
			String user_id = splits[0];
			String date = splits[1];
			String word = splits[2];
			String count = splits[3];
			
			if (!user_id.equals(prevId)) { // another user occurs
				if (prevId != null) {
					// output prev to the new file
					stdout.println(line.toString());
				}
				line = new StringBuilder();
				line.append(user_id);
				line.append("\t");
				line.append(date);
				line.append(":");
				line.append(word);
				line.append(",");
				line.append(count);
				prevId = user_id;
				prevDate = date;
			} else { // add to the current user record
				if (!date.equals(prevDate)) {
					prevDate = date;
					line.append(" ");
					line.append(date);
					line.append(":");
					line.append(word);
					line.append(",");
					line.append(count);
				} else {
					line.append(";");
					line.append(word);
					line.append(",");
					line.append(count);
				}
			}
		}
		// handle the last user
		stdout.println(line.toString());
		br.close();
		stdout.close();
	}

}
