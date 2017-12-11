import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;


public class Mapper {
	BufferedReader br;
	
	Mapper(){
		br=
			new BufferedReader(new InputStreamReader(System.in));
	}
	
	void analyze() {
		try{
			String input;
			while((input=br.readLine())!=null){
				formular(input);
			}
			
		}
		catch(IOException io){		
		}
	}
	
	void formular(String str) throws IOException{
		//initial setting and check
		if(str==null||str=="") return;
		str= str.trim();
		String[] arrays= str.split(" ");
		
		// check rule0
	
		if(arrays.length<4){
			return;
		}
		
		//check rule0
		if(arrays[1].equals("")){		
			return;
		}
				
		// check rule1
		if(!arrays[0].equals("en")){
			return;
		}
		

		
		// check rule2
		String[] rule2= {"Media:","Special:","Talk:","User:","User_talk:","Project:","Project_talk:","File:","File_talk:","MediaWiki:","MediaWiki_talk:","Template:","Template_talk:","Help:","Help_talk:","Category:","Category_talk:","Portal:","Wikipedia:","Wikipedia_talk:"};
		for(String match:rule2){
			if(arrays[1].indexOf(match)==0){	
				return;
			}
		}
		// check rule3
		if(arrays[1].charAt(0)>='a' && arrays[1].charAt(0)<='z'){
			return;
		}
		
		// check rule4
		String[] rule4={".jpg", ".gif", ".png",".JPG",".GIF",".PNG",".txt",".ico"};
		for(String match:rule4){
			if(arrays[1].indexOf(match)!=-1&& arrays[1].lastIndexOf(match)+match.length()==arrays[1].length()){
				return;
			}
		}
			
		// check rule5
		String[] rule5 ={"404_error/","Main_Page","Hypertext_Transfer_Protocol","Search"};
		for(String match:rule5){
			if(arrays[1].equals(match)){
				return;
			}
		}
	
		//System.getenv("mapreduce_map_input_file") to get date
		System.out.println(arrays[1]+"\t"+System.getenv("mapreduce_map_input_file").split("-")[2]+"\t"+arrays[2]);
		return;
	}
	
	
	
	
	public static void main(String[] args) {
		Mapper wcm = new Mapper();
		wcm.analyze();

	}

}
