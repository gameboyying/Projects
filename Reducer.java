import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Reducer {
	BufferedReader br;
	
	Reducer(){
		br = new BufferedReader(new InputStreamReader(System.in));
	}
	
	void analyze() {
		try{
			String input=null;
			String word=null;
			String currWord=null;
			int days=0;
			long total=0;
			String yearsAndMonth=null;
			Map<String,Long> daysOfViews = new HashMap<String,Long>();
			while((input=br.readLine())!=null){
				String[] arrays = input.split("\t");
				if(days==0){
					//obtained the days of the month
					days= getNumberOfDays(arrays[1]);
					yearsAndMonth=arrays[1].substring(0,6);
					if(days==0) break;
				}
				// get article name
				word = arrays[0];
				// get article views
				Long count = Long.parseLong(arrays[2]);
				// compare whether the currword is equal to the last word. If yes, just add count into total.
				if(currWord!=null && currWord.equals(word)==true){
					total += count;
					if(daysOfViews.containsKey(arrays[1])){
						daysOfViews.put(arrays[1], daysOfViews.get(arrays[1])+count);
					}
					else{
						daysOfViews.put(arrays[1], count);
					}
				}
				
				// find different word, printout
				else{
					if(currWord!=null && total> 100000){
						StringBuilder out = new StringBuilder();
						out.append(total);
						out.append("	");
						out.append(currWord);
						out.append("	");
						
						for(int i=1;i<=9;i++){
							String s1 = yearsAndMonth+"0"+ String.valueOf(i);
							out.append(s1+":");
							if(daysOfViews.containsKey(s1)){
								out.append(daysOfViews.get(s1).toString());
							}
							else{
								out.append("0");
							}
							out.append("	");
						}
						
						for(int i=10;i<=days;i++){
							String s1= yearsAndMonth+String.valueOf(i);
							out.append(s1+":");
							if(daysOfViews.containsKey(s1)){
								out.append(daysOfViews.get(s1).toString());
							}
							else{
								out.append("0");
							}
							if(i!=days){
								out.append("	");		
							}
						}
						
						System.out.println(out.toString());
					}				
					//initial again, reset word and total
					daysOfViews.clear();
					total=0;
					currWord=word;
					total=count;
					daysOfViews.put(arrays[1], count);
				}
			}
			// check the last word
			if(currWord!=null && total> 100000){
				StringBuilder out = new StringBuilder();
				out.append(total);
				out.append("	");
				out.append(currWord);
				out.append("	");
				// read days from 1 to 9
				for(int i=1;i<=9;i++){
					String s1 = yearsAndMonth+"0"+ String.valueOf(i);
					out.append(s1+":");
					if(daysOfViews.containsKey(s1)){
						out.append(daysOfViews.get(s1).toString());
					}
					else{
						out.append("0");
					}
					out.append("	");
				}
				// read days from 10 to the end
				for(int i=10;i<=days;i++){
					String s1= yearsAndMonth+String.valueOf(i);
					out.append(s1+":");
					if(daysOfViews.containsKey(s1)){
						out.append(daysOfViews.get(s1).toString());
					}
					else{
						out.append("0");
					}
					if(i!=days){
						out.append("	");		
					}
				
				}
				System.out.println(out.toString());
				//initial again
				daysOfViews.clear();
				total=0;
			}				
			
			
		}
		catch(IOException io){		
		}
	}
	
	// get the day of the month
	int getNumberOfDays(String s){
		s = s.trim();
		s = s.substring(4, 6);
		int v = Integer.parseInt(s);
		switch(v){
		case 1: 
		case 3:
		case 5:
		case 7:
		case 8:
		case 10:
		case 12:return 31;
		case 2: return 28;
		case 4:
		case 6:
		case 9:
		case 11:	return 30;
		}
			
		
		return 0;
	}

	public static void main(String[] args) {
		
		Reducer wcr = new Reducer();
		wcr.analyze();
		
	}

}
