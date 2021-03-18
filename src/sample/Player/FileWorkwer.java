package sample.Player;

import java.io.*;
import java.util.HashMap;

public class FileWorkwer {
    public static HashMap<String, String> readSQLData(){
        HashMap<String, String> map = new HashMap<>();
        try(BufferedReader br = new BufferedReader(new FileReader("sqlDate.txt"))){
            String line;
            while ((line=br.readLine())!=null){
                var value = line.split("\\|");
                map.put(value[0], value[1]);
            }
        } catch (IOException ignored){}
        return map;
    }
}
