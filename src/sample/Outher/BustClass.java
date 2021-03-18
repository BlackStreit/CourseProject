package sample.Outher;

public class BustClass {
    private static int lifeBust = 1;
    public static void addBust(){
        lifeBust++;
    }
    public static void subBust(){
        lifeBust--;
    }
    public static int getBust(){
        return lifeBust;
    }
}
