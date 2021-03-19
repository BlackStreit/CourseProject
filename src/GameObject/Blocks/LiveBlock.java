package GameObject.Blocks;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class LiveBlock extends Block { //Живой блок
    public int life;
    public int maxLife;

    public LiveBlock(double x, double y) {
        super(x, y);
    }

    public int getLife() {
        return life;
    }

    public void setLife(int life) {
        this.life = life;
    }

    public int getMaxLife() {
        return maxLife;
    }

    public void setMaxLife(int maxLife) {
        this.maxLife = maxLife;
        this.life = maxLife;
    }

    public void DrawLife(double x, double y, GraphicsContext graphicsContextk){ //Полоска жизней
        graphicsContextk.setFill(Color.WHITE);
        graphicsContextk.setFont(Font.font("Verdana", 14));
        graphicsContextk.fillText(String.format("%s", life *100 / maxLife),
                x, y);
    }
}
