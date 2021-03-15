package sample.Blocks;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class TowerPosition extends Block {


    private boolean freedoom;
    private int number;

    public boolean isFreedoom() {
        return freedoom;
    }

    public int getNumber() {
        return number;
    }

    public TowerPosition(double x, double y, int number) {
        super(x, y);
        this.number = number;
        freedoom = true;
    }

    @Override
    public void Render(GraphicsContext context) {
        context.setFill(Color.YELLOW);
        context.fillRect(x, y, 20, 20);
    }

    public void setFreedoom(boolean freedoom) {
        this.freedoom = freedoom;
    }
}
