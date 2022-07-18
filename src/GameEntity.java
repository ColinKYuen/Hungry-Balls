import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;

public class GameEntity {
    private final Shape shape;
    private final Color colour;

    public GameEntity(Shape shape, Color colour) {
        this.shape = shape;
        this.colour = colour;
    }

    public void draw(Graphics2D g2){
        g2.setColor(colour);
        g2.draw(shape);
    }

    public void fill(Graphics2D g2){
        g2.setColor(colour);
        g2.fill(shape);
    }
}