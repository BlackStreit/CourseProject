package sample.Blocks;

import javafx.scene.canvas.GraphicsContext;

public class Block { //Супер-класс для любых объектов
    public double x;
    public double y;

    public Block(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void Render(GraphicsContext context){ }
    public void UpdateState(double delta){ }
}
