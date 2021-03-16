package sample.Blocks;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Path extends Block {
    private double[] arrayX;

    public double[] getArrayX() {
        return arrayX;
    }

    public double[] getArrayY() {
        return arrayY;
    }

    private double[] arrayY;
    public Path() {
        super(0, 0);
        arrayX = new double[]{0, 650, 650, 220, 220, 400, 400};
        arrayY = new double[]{120, 120, 620, 620, 240, 240, 350};
    }

    @Override
    public void Render(GraphicsContext context) {
        context.setFill(Color.DARKRED);
        context.strokePolyline(arrayX,
                arrayY,
                arrayX.length);
    }
}
