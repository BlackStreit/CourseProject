package sample.Blocks;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.time.Instant;

public class StarBase extends LiveBlock { //Наша база

    private boolean isFirstArrack = true;
    public int radius = 50; //Радиус базы
    private int damage = 0;
    private Instant lastDamageTime = null;
    private Instant firstAttackTime;

    public StarBase(double x, double y) {
        super(x, y);
        setMaxLife(1000);
    }

    @Override
    public void Render(GraphicsContext context) {
        context.setFill(Color.NAVY); //Залить Нови
        context.fillOval(
                x - radius,
                y - radius,
                radius * 2,
                radius * 2
        ); //Нарисовать элипс
        //Нарисовать жизни
        context.setFill(Color.GREEN);
        context.setStroke(Color.DARKGREEN);
        DrawLife(x-6, y+6, context);
    }
    //регестрируем попадаение
    public void hit(Enemy enemy){
        lastDamageTime = Instant.now();
        enemy.life = 0;
        this.life -= enemy.getPower();
        if(isFirstArrack) {
            firstAttackTime = Instant.now();
            isFirstArrack = false;
        }
        if(firstAttackTime!=null){
            double delta = java.time.Duration.between(firstAttackTime, Instant.now()).toSeconds();
            if(delta>=5){
                firstAttackTime = null;
                isFirstArrack = true;
                if(damage>=70 && enemy.getLifeBust()>0){
                    enemy.setLifeBust(enemy.getLifeBust()-1);
                }
                damage = 0;
                return;
            }
            damage+=enemy.getPower();
        }
    }
}
