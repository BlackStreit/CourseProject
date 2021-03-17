package sample.Blocks;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Enemy extends LiveBlock {
    private StarBase starBase;
    private int speed = 60;


    private int power = 30;

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

    @Override
    public void UpdateState(double delta) {
        if(starBase.life <= 0){ //Если база мертва
            return;
        }
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
        double gX = starBase.x - x;
        double gY = starBase.y - y;
        double length = Math.sqrt(gX * gX + gY * gY); //Длина
        //Нормализованый вектор
        /*
        gX /=length;
        gY /=length;
        x += gX * speed * delta;
        y += gY * speed * delta;
         */
        //Если враг коснулся базы
        if(starBase.radius + getRadius() / 2 > length){
            starBase.hit(this);
        }
    }

    public Enemy(double x, double y, StarBase starBase) {
        super(x, y);
        this.starBase = starBase;
        setMaxLife(30);
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }
}
