import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

public class Entity {
    double xPos, yPos;
    Color col;

    public Entity(double x, double y, Color col) {
        this.xPos = x;
        this.yPos = y;
        this.col = col;
    }

    public void draw(Graphics2D g2){
        Ellipse2D entity = new Ellipse2D.Double(
                Def.G_GAP + this.xPos * Def.G_WIDTH,
                Def.G_GAP + this.yPos * Def.G_WIDTH,
                Def.G_WIDTH, Def.G_WIDTH);

        g2.setColor(col);
        g2.fill(entity);
    }
}