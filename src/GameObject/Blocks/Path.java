package GameObject.Blocks;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.lang.reflect.Array;
import java.util.Arrays;

public class Path extends Block {
    private double[] arrayX;

    private double fX;
    private double fY;

    private double lX;
    private double lY;

    public double getlX() {
        return lX;
    }

    public double getlY() {
        return lY;
    }

    public Path() {
        super(0, 0);
    }

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


    @Override
    public void Render(GraphicsContext context) {
        context.setFill(Color.YELLOW);
        context.strokePolyline(arrayX,
                arrayY,
                arrayX.length);
    }

    public void setArrayX(double[] arrayX) {
        this.arrayX = new double[arrayX.length];
        System.arraycopy(arrayX, 0, this.arrayX, 0, arrayX.length);
    }

    public void setArrayY(double[] arrayY) {
        this.arrayY = new double[arrayY.length];
        System.arraycopy(arrayY, 0, this.arrayY, 0, arrayY.length);
    }
}
