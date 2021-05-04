package GameObject.Blocks;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.Consumer;

public class Tower extends LiveBlock {
    private ArrayList<Block> blocks;
    private int radius = 100; //Радиус выстрела
    private double fireRate = 0.1; //Частоты выстрела
    private double timeForLastShot = 0;
    private Enemy targetEnemy;
    private int power = 25;

    public ArrayList<Block> getBlocks() {
        return blocks;
    }

    public void setBlocks(ArrayList<Block> blocks) {
        this.blocks = blocks;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public double getFireRate() {
        return fireRate;
    }

    public void setFireRate(double fireRate) {
        this.fireRate = fireRate;
    }

    public double getTimeForLastShot() {
        return timeForLastShot;
    }

    public void setTimeForLastShot(double timeForLastShot) {
        this.timeForLastShot = timeForLastShot;
    }

    public Enemy getTargetEnemy() {
        return targetEnemy;
    }

    public void setTargetEnemy(Enemy targetEnemy) {
        this.targetEnemy = targetEnemy;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Consumer<Enemy> getOnEnemyDestroy() {
        return onEnemyDestroy;
    }

    public void setOnEnemyDestroy(Consumer<Enemy> onEnemyDestroy) {
        this.onEnemyDestroy = onEnemyDestroy;
    }

    private Color color = Color.WHITE;


    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int number;

    Consumer<Enemy> onEnemyDestroy = null;
    public Tower(double x, double y, ArrayList<Block> blocks, Consumer<Enemy> onEnemyDestroy, int number) {
        super(x, y);
        this.blocks = blocks;
        this.number = number;
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
