package sample.Blocks;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class StarBase extends LiveBlock { //Наша база

    public int radius = 50; //Радиус базы

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
        enemy.life = 0;
        this.life -= enemy.getPower();
    }
}
