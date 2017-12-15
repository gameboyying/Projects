import java.io.*;
import java.util.Map;
import java.util.TreeMap;
import java.util.HashMap;

public class Compressor {

    // define map save String and Value
    private Map<String, Integer> map;
    // define map size()
    private int dictSize;
    // whether it is left chars
    private boolean isLeft = true;
    // used to save bytes. and used for writing into file
    // i need to use 3 bytes array to control 12 bits.
    private byte[] bytesSaveToFile  = new byte[3];

    //initialized map, dictSize and first 255 chars
    private void buildSymbols(){
        map = new HashMap<String, Integer>();
        dictSize = 256;
        for (int i = 0; i < 256; i++) {
            map.put(Character.toString((char) i), i);
        }
    }

    public void compress(String inputFile, String outputFile) throws java.io.IOException {

        // read file
        DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(inputFile)));

        // write file
        DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(outputFile)));

        //initialized and enter all symbols in the table; 
        buildSymbols();

        // read first byte
        byte inputByte = in.readByte();
        // clean up high digits with 1s
        int intValue = inputByte & 0xff;

        // change integer value to char
        char c = (char) intValue;

        // define string
        String str = "" + c;

        //if file still have unread bytes, continue to read
        while (in.available() > 0) {
            //if table is overflow, start a new table and start a new process
            if(dictSize>=4096) {
                buildSymbols();
            }

            inputByte = in.readByte();
            intValue = inputByte & 0xff;
            c = (char) intValue;

            // if map contains str+c, just set str = str + c
            if (map.containsKey(str + c)) {
                str = str + c;
            } else {
                // get value if string is in the table
                int value = map.get(str);
                // if it is left
                if (isLeft == true) {
                    // get first 8 bits
                    bytesSaveToFile[0] =  (byte) ((value & 0xfff)>>4);
                    // get next 4 bits to save in second bytes
                    bytesSaveToFile[1] = (byte) (((value & 0xf))<<4);
                } else {
                    // get first 4 bits to  attach behind previous bytes
                    bytesSaveToFile[1] +=(byte)((value & 0xfff)>>8);
                    // get 8 bits
                    bytesSaveToFile[2] = (byte) ((value & 0xff));
                    // write into the file
                    out.write(bytesSaveToFile);
                    // clean up bytes array
                    bytesSaveToFile[0] = bytesSaveToFile[1] = bytesSaveToFile[2] = 0;
                }
                // next read shall be left or right
                isLeft = !isLeft;
                // map put str+c
                map.put(str + c, dictSize++);
                // set s = c
                str = "" + c;
            }
        }

        // write last str
        int value = map.get(str);
        if (isLeft == true) {
            //get first 8 bits
            //bytesSaveToFile[0] = (byte) Integer.parseInt(newStrFrom8To12.substring(0, 8), 2);
            bytesSaveToFile[0] =  (byte) ((value & 0xfff)>>4);
            // get next 4 bits to save in second bytes
            bytesSaveToFile[1] = (byte) (((value & 0xf))<<4);

            out.write(bytesSaveToFile[0]);
            out.write(bytesSaveToFile[1]);

        } else {
            // get first 4 bits to  attach behind previous bytes
            bytesSaveToFile[1] +=(byte)((value & 0xfff)>>8);
            // get 8 bits
            bytesSaveToFile[2] = (byte) ((value & 0xff));
            // write into the file
            out.write(bytesSaveToFile);
            // clean up bytes array
            //bytesSaveToFile[0] = bytesSaveToFile[1] = bytesSaveToFile[2] = 0;
        }

        // close reading operation
       in.close();
        // close writing operation
        out.close();
    }
}
