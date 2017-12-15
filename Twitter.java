
/**
 * Data Structure to store information of one tweet
 *
 */
public class Twitter {

	private String tweet_id;
	private String user_id;
	private String date;
	private String words;
	
	public Twitter(String id, String user_id, String created, String words) {
		this.tweet_id = id;
		this.user_id = user_id;
		this.date = formatDate(created);
		this.words = words;
	}

	// date should be in the following format:yyyy-MM-dd HH-mm-ss
	// (UTC time, 24-hour clock) e.g. 2014-05-15 09-02-20
	private String formatDate(String created) {
		StringBuilder result = new StringBuilder();
		String[] splits = created.substring(1, created.length() - 1).split(" ");
		result.append(splits[5]); // year
		result.append("-");
		String month = null;
		switch (splits[1]) {
		case "Jan":
			month = "01";
			break;
		case "Feb":
			month = "02";
			break;
		case "Mar":
			month = "03";
			break;
		case "Apr":
			month = "04";
			break;
		case "May":
			month = "05";
			break;
		case "Jun":
			month = "06";
			break;
		case "Jul":
			month = "07";
			break;
		case "Aug":
			month = "08";
			break;
		case "Sep":
			month = "09";
			break;
		case "Oct":
			month = "10";
			break;
		case "Nov":
			month = "11";
			break;
		case "Dec":
			month = "12";
			break;
		}
		result.append(month); // month
		result.append("-");
		result.append(splits[2]); // day
		return result.toString();
	}
	
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append(tweet_id);
		result.append("\t");
		result.append(user_id);
		result.append("\t");
		result.append(date);
		result.append(words); // words already contain tabs
		return result.toString();
	}
}
