package GameObject.Blocks;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Enemy extends LiveBlock {
    private StarBase starBase;
    private Path path;
    private int speed = 60;
    private boolean[] pounts;
    private int power = 30;

    private double[] pX;
    private double[] pY;

    public int getRadius(){
        return (int)Math.sqrt(maxLife);
    }
    @Override
    public void Render(GraphicsContext context) {
        context.setFill(Color.DARKRED); //Залить Нови
        context.fillOval(
                x - getRadius() / 2,
                y - getRadius() / 2,
                getRadius() ,
                getRadius()
        ); //Нарисовать элипс

        context.setFill(Color.ORANGE);
        context.setStroke(Color.RED);
        DrawLife(x-10, y+2 , context);
    }

    boolean isFirst = true;
    boolean isSecond = false;
    boolean isTherd = false;
    boolean isFifth = false;
    boolean isFiveth = false;
    boolean isSixth = false;

    double totalX = 0;
    double totalY = 0;

    @Override
    public void UpdateState(double delta) {
        if(starBase.life <= 0){ //Если база мертва
            return;
        }
        for(int i = 0; i < pounts.length; i++){
            boolean selectX = false;
            boolean selectY = false;
            if(pounts[i]){
                if(i != pounts.length - 1){
                    if(pX[i]!=pX[i+1]){
                        selectX = true;
                        totalX = pX[i + 1];
                    }
                    if(pY[i]!=pY[i+1]){
                        selectY = true;
                        totalY = pY[i + 1];
                    }
                    if(selectX){
                        if(pX[i]>pX[i+1]){
                            x -= (speed * delta);
                            if(x <= totalX){
                                pounts[i] = false;
                                pounts[i+1] = true;
                            }
                        }
                        else{
                            x += (speed * delta);
                            if(x >= totalX){
                                pounts[i] = false;
                                pounts[i+1] = true;
                            }
                        }
                    }
                    if(selectY){
                        if(pY[i]>pY[i+1]){
                            y -= (speed * delta);
                            if(y <= totalY){
                                pounts[i] = false;
                                pounts[i+1] = true;
                            }
                        }
                        else{
                            y += (speed * delta);
                            if(y >= totalY){
                                pounts[i] = false;
                                pounts[i+1] = true;
                            }
                        }
                    }
                }
            }
        }
        /*
        if(x >= 650 && isFirst){
            isFirst = false;
            isSecond = true;
        }
        else if(isFirst) {
            x += speed * delta;
        }
        if(y>=620 && isSecond){
            isSecond = false;
            isTherd = true;
        }
        else if(isSecond){
            y+=speed*delta;
        }
        if(x<=220 && isTherd){
            isTherd = false;
            isFifth = true;
        }
        else if(isTherd) {
            x -= speed * delta;
        }
        if(y<=240 & isFifth){
            isFifth = false;
            isFiveth = true;
        }
        else if(isFifth){
            y-=speed*delta;
        }
        if(x>=400 && isFiveth){
            isFiveth = false;
            isSixth = true;
        }
        else if(isFiveth){
            x+=speed * delta;
        }
        if(isSixth){
            y+=speed * delta;
        }
         */
        double gX = starBase.x - x;
        double gY = starBase.y - y;
        double length = Math.sqrt(gX * gX + gY * gY); //Длина
        //Если враг коснулся базы
        if(starBase.radius + getRadius() / 2 > length){
            starBase.hit(this);
        }
    }

    public Enemy(double x, double y, StarBase starBase, Path path) {
        super(x, y);
        this.starBase = starBase;
        this.path = path;
        setMaxLife(30);
        pounts = new boolean[path.getArrayX().length];
        pounts[0] = true;
        pX = path.getArrayX();
        pY = path.getArrayY();
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }
}
