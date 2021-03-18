package sample.Blocks;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import sample.Outher.BustClass;
import sample.Outher.TimeClass;

import java.time.Instant;

public class StarBase extends LiveBlock { //Наша база

    private boolean isFirstArrack = true;
    public int radius = 50; //Радиус базы
    private int damage = 0;

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
        TimeClass.lastAttackTime = Instant.now();
        enemy.life = 0;
        this.life -= enemy.getPower();
        damage += enemy.getPower();
        if(isFirstArrack) {
            TimeClass.firstAttackTime = Instant.now();
            isFirstArrack = false;
        }
        if(TimeClass.firstAttackTime!=null){
            double delta = java.time.Duration.between(TimeClass.firstAttackTime, Instant.now()).toSeconds();
            if(delta>=5){
                TimeClass.firstAttackTime = null;
                isFirstArrack = true;
                if(damage >= 70) {
                    BustClass.subBust();
                    TimeClass.lastDamageTime = Instant.now();
                }
                damage = 0;
            }
        }
    }
}
