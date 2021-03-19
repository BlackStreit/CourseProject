package GameObject.Blocks;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Path extends Block {
    private double[] arrayX;

    private double fX;
    private double fY;

    public double getfX() {
        return fX;
    }

    public double getfY() {
        return fY;
    }

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
        fX = arrayX[0];
        fY = arrayY[0];
    }

    @Override
    public void Render(GraphicsContext context) {
        context.setFill(Color.DARKRED);
        context.strokePolyline(arrayX,
                arrayY,
                arrayX.length);
    }
}
