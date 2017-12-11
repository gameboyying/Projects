
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Parse {
    
    // Stop words that should NOT be counted as an effective word
    private static Set<String> common;
    
    // Banned word list
    private static Set<String> banned;
    
    /**
     * Constructor
     */
    public Parse() {
        String filename = "common-english-word.txt";
        // read common word list
        if (common == null) {
            common = new HashSet<>();
            try {
                // as utf8
                BufferedReader buff =
                new BufferedReader(new InputStreamReader(
                                                         new FileInputStream(filename), StandardCharsets.UTF_8));
                while (true) {
                    String line = buff.readLine();
                    if (line == null) {
                        break;
                    }
                    String[] splits = line.split(",");
                    for (int i = 0; i < splits.length; i++) {
                        common.add(splits[i]);
                        // add to stop words
                    }
                }
                buff.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        // read banned word list
        if (banned == null) {
            banned = new HashSet<>();
            filename = "ban.txt";
            try {
                // as utf8
                BufferedReader buff =
                new BufferedReader(new InputStreamReader(
                                                         new FileInputStream(filename), StandardCharsets.UTF_8));
                while (true) {
                    String line = buff.readLine();
                    if (line == null) {
                        break;
                    }
                    banned.add(line);
                    // add to banned words
                }
                buff.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private String wordCount(String text) {
        StringBuilder result = new StringBuilder(); // text after censoring
        StringBuilder word = new StringBuilder(); // current word
        Map<String, Integer> count = new TreeMap<>();
        char[] chars = text.toCharArray();
        int index = 0;
        while (index < chars.length) {
            char c = chars[index];
            if (isAlphanumeric(c)) { // add to the word
                word.append(c);
            } else {
                if (word.length() > 0) { // check whether it is banned
                    String current = word.toString().toLowerCase();
                    if (!banned.contains(current) && !common.contains(current)) {
                        if (count.get(current) == null) {
                            count.put(current, 1);
                        } else {
                            count.put(current, count.get(current) + 1);
                        }
                    }
                    word = new StringBuilder();
                }
                if (c == '\\') {
                    if (index < chars.length - 1) {
                        char next = chars[index + 1];
                        if (next == 'n' || next == 'r' || next == 't') {
                            // \n, \r or \t
                            index++;
                        }
                    }
                }
            }
            index++; // go through the characters one by one
        }
        if (word.length() > 0) { // last word
            String current = word.toString().toLowerCase();
            if (!banned.contains(current) && !common.contains(current)) {
                if (count.get(current) == null) {
                    count.put(current, 1);
                } else {
                    count.put(current, count.get(current) + 1);
                }
            }
        }
        for(Map.Entry<String,Integer> entry : count.entrySet()) {
            result.append("\t");
            result.append(entry.getKey());
            result.append(":");
            result.append(entry.getValue());
        }
        return result.toString();
    }
    
    // whether a character is an alphanumeric character ([a-zA-Z0-9]+)
    private boolean isAlphanumeric(char c) {
        if (c < '0' || c > 'z') {
            return false;
        }
        if (c <= '9' || c >= 'a') {
            return true;
        }
        if (c >= 'A' && c <= 'Z') {
            return true;
        }
        return false;
    }
    
    // call this method in Mapper to parse the JSON file
    public void mapper(String line, PrintWriter stdout) {
        JsonParser jsonParser = new JsonParser();
        try{
            JsonObject tweetObj = jsonParser.parse(line).getAsJsonObject();
            
            // language
            String lang = tweetObj.get("lang").toString();
            lang = lang.substring(1, lang.length() - 1);
            if (!lang.equals("en")) {
                return; // not English
            }
            
            // date
            String created = tweetObj.get("created_at").toString(); // column 3
            if (created.length() < 10) {
                return; //malformed
            }
            
            // tweet id
            JsonElement idElement = tweetObj.get("id"); // column 1
            String id = null;
            if (idElement == null) {
                id = tweetObj.get("id_str").toString();
                id = id.substring(1, id.length() - 1);
                if (id.length() < 1) {
                    return; // malformed
                }
            } else {
                id = idElement.toString();
            }
            
            // text
            String text = tweetObj.get("text").toString(); // column 5
            text = text.substring(1, text.length() - 1);
            if (text.length() < 1) {
                return; // malformed
            }
            
            // remove small urls
            text = text.replaceAll("\\\\n","\n");
            text = text.replaceAll("\\\\t","\t");
            text = text.replaceAll("\\\\r","\r");
            text = text.replaceAll("(https?|ftp):\\/\\/[^\\s/$.?#][^\\s]*", "");
            text = text.replaceAll("\n","\\\\n");
            text = text.replaceAll("\t","\\\\t");
            text = text.replaceAll("\r","\\\\r");
            // user id
            JsonObject user = tweetObj.getAsJsonObject("user");
            JsonElement user_idElement = user.get("id"); // column 2
            String user_id = null;
            if (user_idElement == null) {
                user_id = user.get("id_str").toString();
                user_id = user_id.substring(1, user_id.length() - 1);
                if (user_id.length() < 1) {
                    return; // malformed
                }
            } else {
                user_id = user_idElement.toString();
            }
            
            // word count
            String words = wordCount(text);
            if (words.length() < 1) {
                return; // no valid words
            }
            
            // store data in a customized data structure
            Twitter enery = new Twitter(id, user_id, created, words);
            stdout.println(enery.toString());
            
        } catch (Exception e) {
            // a malformed tweet occurs
            // skip it and keep going
        }
        
    }
}
