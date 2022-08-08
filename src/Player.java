import java.awt.Color;

// A specific implementation of GameEntity for Players. Has an ID, a score, and a direction, on top of other
// GameEntity fields.

public class Player extends GameEntity {
    private final int playerID;
    private int score = 0;
    private Direction nextDirection = Direction.Stop;

    public Player(int xPos, int yPos, Color colour, int playerID) {
        super(xPos, yPos, colour);
        this.playerID = playerID;
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

    public void setNextDirection(Direction direction) {
        nextDirection = direction;
    }

    public int getPlayerID() {
        return playerID;
    }
}
