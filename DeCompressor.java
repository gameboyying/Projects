import java.io.*;

public class DeCompressor {

    // define table
    private String[] deMap;
    // define current word
    private int currentWord;
    // define previous word
    private int previousWord;
    private boolean isLeft = true;
    // define table's size
    private int dictSize;
    private byte[] bytesSaveToFile = new byte[3];
    private DataInputStream in;
    private DataOutputStream out;

    public void decompress(String inputFile, String outputFile) throws java.io.IOException {
        // read file
        in = new DataInputStream(new BufferedInputStream(new FileInputStream(inputFile)));
        // write file
        out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(outputFile)));
        // decompress file
        process();
        // close reading operation
        in.close();
        // close writing operation
        out.close();
    }

    private void process() throws java.io.IOException{
        // initalized table and parameters
        buildSymbols();
        // read first byte
        bytesSaveToFile[0] = in.readByte();
        // read second byte
        bytesSaveToFile[1] = in.readByte();
        // get previous word
        previousWord = getValue(bytesSaveToFile[0], bytesSaveToFile[1], isLeft);
        isLeft = !isLeft;
        // write into the file
        out.writeBytes(deMap[previousWord]);
        // if file is not empty, continue to read
        while (in.available() > 0) {
            // if table is overflow, create a new table and start new process
            if (dictSize >= 4096) {
                buildSymbols();
            }

            if (isLeft) {
                // read first byte
                bytesSaveToFile[0] = in.readByte();
                // read second byte
                bytesSaveToFile[1] = in.readByte();
                // get current word
                currentWord = getValue(bytesSaveToFile[0], bytesSaveToFile[1], isLeft);
            } else {
                // read third byte
                bytesSaveToFile[2] = in.readByte();
                // get current word
                currentWord = getValue(bytesSaveToFile[1], bytesSaveToFile[2], isLeft);
            }
            isLeft = !isLeft;

            // if current word is not in talbe
            if (deMap[currentWord] == null) {
                // get previous string
                String prev = deMap[previousWord];
                // put preString + first char of previou String into table
                deMap[dictSize++] = prev + prev.charAt(0);
                // write into file
                out.writeBytes(prev + prev.charAt(0));

            } else {
                // get previous string
                String prev = deMap[previousWord];
                // get current string
                String curr = deMap[currentWord];
                // put previous String + first char of current String into table
                deMap[dictSize++] = prev + curr.charAt(0);
                // write into file
                out.writeBytes(curr);
            }
            // re-set up previous word
            previousWord = currentWord;
        }
    }

    // initialized table and parameters
    private void buildSymbols(){
        deMap = new String[4096];
        dictSize = 256;
        for (int i = 0; i < 256; i++) {
            deMap[i] = Character.toString((char) i);
        }
    }

    private int getValue(byte a, byte b, boolean isLeft){
        // clean up high level 1s digits
        int first = a & 0xff;
        // clean up high level 1s digits
        int second = b & 0xff;
        if(isLeft==true){
            // get first byte + second byte with 4 bits
            return (first<<4) + (second>>4);
        }
        else{
            // get first byte with last 4 bits + second byte
            return ((first & 0xf) << 8) + second;
        }
    }
}
