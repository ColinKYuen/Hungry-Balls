import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

public class GameEntity {
    private int xPos;
    private int yPos;
    private Color colour;

    // Constructor
    public GameEntity(int xPos, int yPos, Color colour) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.colour = colour;
    }

    // Setters
    public void setXPos(int xPos) {
        // Bound checking, don't unwrap.
        if (xPos < Def.MAP_SIZE && xPos >= 0) {
            this.xPos = xPos;
        }
    }

    public void setYPos(int yPos) {
        // Bound checking, don't unwrap.
        if (yPos < Def.MAP_SIZE && yPos >= 0) {
            this.yPos = yPos;
        }
    }

    public void setColour(Color colour) {
        this.colour = colour;
    }

    // Getters
    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }

    public Color getColour() {
        return colour;
    }

    // Drawing functions
    public void drawRect(Graphics2D g2){
        draw(g2, getRect());
    }

    public void fillRect(Graphics2D g2){
        fill(g2, getRect());
    }

    public void drawCircle(Graphics2D g2){
        draw(g2, getCircle());
    }

    public void fillCircle(Graphics2D g2){
        fill(g2, getCircle());
    }

    // Private functions
    private Shape getRect() {
        return new Rectangle2D.Double(
            Def.G_GAP + xPos * Def.G_WIDTH,
            Def.G_GAP + yPos * Def.G_WIDTH,
            Def.G_WIDTH, Def.G_WIDTH);
    }

    private Shape getCircle() {
        return new Ellipse2D.Double(
            Def.G_GAP + xPos * Def.G_WIDTH,
            Def.G_GAP + yPos * Def.G_WIDTH,
            Def.G_WIDTH, Def.G_WIDTH);
    }

    private void draw(Graphics2D g2, Shape shape) {
        g2.setColor(colour);
        g2.draw(shape);
    }

    private void fill(Graphics2D g2, Shape shape) {
        g2.setColor(colour);
        g2.fill(shape);
    }
}
