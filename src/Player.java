import java.awt.*;

public class Player extends GameEntity{
    private int score=0;
    private Direction nextDirection = Direction.Stop;

    public Player(int xPos, int yPos, Color colour) {
        super(xPos, yPos, colour);
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Direction getNextDirection() {
        return nextDirection;
    }

    public void setNextDirection(Direction direction)
    {
        nextDirection = direction;
    }
}
