import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

//merge userinfo and links file . make my own hbase schema
public class Merger {
	
	Map<String,String> hashName = new HashMap<String,String>();
	Map<String,String> hashURL = new HashMap<String,String>();
	
	
	public Merger(){
		try{
		
		String input="";
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("userinfo.csv"), "UTF-8"));
		while((input=br.readLine())!=null){
			String[] s = input.split(",");
			hashName.put(s[0],s[1]);
			hashURL.put(s[0],s[2]);	
			}
		br.close();
		}
		
		catch(Exception ex){
			
		}
		finally{
		
		}
	}
	
	public void merger2(){
		try{
			FileWriter fw = new FileWriter("output");
			
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("links.csv"), "UTF-8"));
			//Scanner scan = new Scanner(new InputStreamReader(new FileInputStream(fileName), "UTF-8"));
			System.out.println("Start!");
			String input="";
			while((input=br.readLine())!=null){
				String[] s = input.split(",");
				fw.write(s[0]+","+s[1]+"\t"+hashName.get(s[1])+"\t"+hashURL.get(s[1])+"\n");
			}
			fw.close();
			br.close();
			System.out.println("End!");	
    	 } catch (Exception e) {
             e.printStackTrace();
         } 
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Merger k = new Merger ();
		k.merger2();
	}
}
