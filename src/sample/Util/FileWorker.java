package sample.Util;

import GameObject.Blocks.TowerPosition;

import java.io.*;
import java.util.HashMap;
import GameObject.Blocks.*;

public class FileWorker {
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

    public static Path[] readPath(String path){
        Path[] paths = null;
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            int count = Integer.parseInt(br.readLine());
            paths = new Path[count / 2];
            int rows = Integer.parseInt(br.readLine());
            double[] arr = new double[rows];
            for (int r = 0; r < count; r++) {
                for (int i = 0; i < rows; i++){
                    arr[i] = Double.parseDouble(br.readLine());
                }
                if(r%2==0){
                    paths[r/2] = new Path();
                    paths[r/2].setArrayX(arr);
                }
                else{
                    paths[r/2].setArrayY(arr);
                }
                arr = new double[rows];
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return paths;
    }

    public static TowerPosition[] readTP(String path){
        TowerPosition[] arr = null;
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            int count = Integer.parseInt(br.readLine());
            arr = new TowerPosition[count];
            for (int i = 0; i < count; i++) {
                String line = br.readLine();
                var values = line.split("\\|");
                double x = Double.parseDouble(values[0]);
                double y = Double.parseDouble(values[1]);
                int num = Integer.parseInt(values[2]);
                arr[i] = new TowerPosition(x, y, num);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return arr;
    }
}
