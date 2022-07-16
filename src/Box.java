import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

public class Box {
    private final Rectangle2D.Double box;
    private Color col;

    public Box(double x, double y) {
        box = new Rectangle2D.Double(
                Def.G_GAP + x * Def.G_WIDTH,
                Def.G_GAP + y * Def.G_WIDTH,
                Def.G_WIDTH, Def.G_WIDTH);
        col = new Color(0, 0, 0);
    }

    public void draw(Graphics2D g2){
        g2.setColor(col);
        g2.draw(box);
    }
}