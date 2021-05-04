package GameObject.Blocks;

import javafx.scene.canvas.GraphicsContext;

public class Block { //Супер-класс для любых объектов
    protected double x;

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    protected double y;

    public Block(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void Render(GraphicsContext context){ }
    public void UpdateState(double delta){ }
}
