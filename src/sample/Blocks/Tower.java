package sample.Blocks;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.Consumer;

public class Tower extends LiveBlock {
    private ArrayList<Block> blocks;
    public int radius = 100; //Радиус выстрела
    public double fireRate = 0.1; //Частоты выстрела
    public double timeForLastShot = 0;
    private Enemy targetEnemy;
    public int power = 25;
    public Color color = Color.WHITE;

    Consumer<Enemy> onEnemyDestroy = null;
    public Tower(double x, double y, ArrayList<Block> blocks, Consumer<Enemy> onEnemyDestroy) {
        super(x, y);
        this.blocks = blocks;
        setMaxLife(100);
        this.onEnemyDestroy = onEnemyDestroy;
    }

    @Override
    public void Render(GraphicsContext context) {
        context.setFill(color);
        context.fillOval(
                x - 10,
                y - 10,
                10 * 2,
                10 * 2
        ); //Нарисовать элипс

        context.setStroke(Color.YELLOW); //Залить Нови
        context.strokeOval(
                x - radius,
                y - radius,
                radius * 2,
                radius * 2
        ); //Нарисовать элипс
        //Нарисовать направление атаки
        if(targetEnemy != null){
            context.setStroke(Color.LIGHTGREEN);
            context.setLineWidth(4);
            context.strokeLine(x, y,
                    targetEnemy.x, targetEnemy.y);
            targetEnemy = null;
        }
    }

    @Override
    public void UpdateState(double delta) {
        timeForLastShot += delta;
        if(timeForLastShot >= fireRate){ //Когда пройдет достаточно времени от выстрела
            blocks.stream().
                    filter(block -> block instanceof Enemy)
                    .map(block -> (Enemy)block)
                    .min(Comparator.comparing(enemy ->
                    {
                        double gX = enemy.x - x;
                        double gY = enemy.y - y;
                        double length = Math.sqrt(gX * gX + gY * gY);
                        return length;
                    })).ifPresent( //Если нашел
                            enemy -> {
                                double gX = enemy.x - x;
                                double gY = enemy.y - y;
                                double length = Math.sqrt(gX * gX + gY * gY);
                                if(length < radius){
                                    targetEnemy = enemy;
                                }
                            }
            );//найти врагов
            if(targetEnemy != null && targetEnemy.life > 0){  //Если враг жив, то ударить
                targetEnemy.life -= power;
                if(onEnemyDestroy != null && targetEnemy.life <= 0){
                    onEnemyDestroy.accept(targetEnemy);
                }
            }
            timeForLastShot = 0;
        }
    }
}
