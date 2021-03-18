package sample.Outher;

public class BustClass {
    private static int lifeBust = 1;
    public static void addBust(){
        lifeBust++;
    }
    public static void subBust(){
        if(lifeBust>1) {
            lifeBust--;
        }
    }
    public static int getBust(){
        return lifeBust;
    }

    public static void setLifeBust(int lifeBust) {
        BustClass.lifeBust = lifeBust;
    }

}
