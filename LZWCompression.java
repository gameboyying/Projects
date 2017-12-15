import java.io.*;

public class LZWCompression {

     /*
        My program works well in both ASCII and binary files. It means, my program can compress and decompress file
        successfully. Also, I control bits(move bits) to implement this program such as using operator(&) 0xf,0xff, 0xfff
        Structure: Compressor.java -> I used map to save String, Integer. When overflowing, I clean up the table and start a new process
                   DeCompressor.java -> I used array to save String. When overflowing, I clean up the table and start a new process
        words.html -> After compressing, file size is 1.1mb. Degree of compressing is 1.1/2.5 = 44%
        CrimeLatLonXY1990.csv -> After compressing, file size is 136kb. Degree of compressing is 136/276 = 49.28%
        Overview.mp4 -> After compressing, file size is 33.8mb. Degree of compressing is 33.8/25 = 135.2%
     */

     /*
     First of all, I just use map in compressor.jave to control (String, Integer). Decompressor.java used arrays to save
     corresponding Strings.
     TreeMap Testing
     words.html           : 4246ms - compress only 5117ms - compress and decompress
     CrimeLatLonXY1990.csv: 918ms - compress only 1217ms - compress and decompress
     Overview.mp4         : 39188ms - compress only 62892ms - compress and decompress
     HashMap Testing
     words.html           : 3621ms - compress only 4533ms - compress and decompress
     CrimeLatLonXY1990.csv: 671ms - compress only  1008ms - compress and decompress
     Overview.mp4         : 30780ms - compress only 55086ms - compress and decompress
     Summary:
     All in all, HashMap performance is better than the TreeMap. It make sense, because treeMap is implemented by RedblackTree
     And HashMap implemented by Arrays with HashCode. Normally, HashMap can read within O(1).
     */

    public static void main(String[] args) throws IOException {
            //precondition check
            if(args==null || args.length<3 ||(!args[0].equals("c") && !args[0].equals("d"))){
                System.out.println("Your arguments are wrong! Please re-run the program");
                return;
            }
            //compress file
            if(args[0].equals("c")){
                String source = args[1];
                String destination = args[2];
                Compressor compressor = new Compressor();
                compressor.compress(source,destination);
                return;
            }
            //decompress file
            if(args[0].equals("d")){
                String source = args[1];
                String destination = args[2];
                DeCompressor deCompressor = new DeCompressor();
                deCompressor.decompress(source,destination);
                return;
            }

    }
}