import javax.swing.JComponent;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class GameBoard extends JComponent {
    private final ReentrantLock[][] map = new ReentrantLock[Def.MAP_SIZE][Def.MAP_SIZE];
    private final GameEntity[][] board = new GameEntity[Def.MAP_SIZE][Def.MAP_SIZE];
    private final List<GameEntity> foods = new ArrayList();
    private final Player controllablePlayer;
    private final Player enemyPlayer;

    public GameBoard() {
        // Initialize Game Board
        for (int i = 0; i < Def.MAP_SIZE; i++) {
            for (int j = 0; j < Def.MAP_SIZE; j++) {
                map[i][j] = new ReentrantLock();
                board[i][j] = new GameEntity(i, j, new Color(0, 0, 0));
            }
        }

        // Assumes that your controllable player is blue and the default location is 0,0
        // TODO: Let Server determine starting position for both players, so each client can set their controllable player to the correct spot
        Color controllablePlayerColor = new Color(0, 0, 255);
        controllablePlayer = new Player(0,0,controllablePlayerColor);

        // Assumes that your controllable player is blue and the default location is 7,7
        // TODO: Let Server determine starting position for both players, so each client can set their controllable player to the correct spot
        Color enemyPlayerColor = new Color(255, 0, 0);
        enemyPlayer = new Player(7,7,enemyPlayerColor);

        // For debugging
        // Initialize Food
        Color colour = new Color((float)Math.random(), (float)Math.random(), (float)Math.random());
        int x = (int) (Math.random() * Def.MAP_SIZE);
        int y = (int) (Math.random() * Def.MAP_SIZE);
        foods.add(new GameEntity(x, y, colour));
    }

    public Player getControllablePlayer() {
        return controllablePlayer;
    }

    public Player getEnemyPlayer() {
        return enemyPlayer;
    }

    /**
     * Render game board for client
     */
    public void draw(Graphics2D g2) {
        super.paintComponent(g2);

        // Draw empty boxes, the game box
        for (int i = 0; i < Def.MAP_SIZE; i++) {
            for (int j = 0; j < Def.MAP_SIZE; j++) {
                board[i][j].drawRect(g2);
            }
        }

        // Draw the Player & Score
        controllablePlayer.fillCircle(g2);
        g2.drawString("Your Score: " + controllablePlayer.getScore(),0, (int) (1 * Def.G_GAP  / 2));
        enemyPlayer.fillCircle(g2);
        g2.drawString("Opponent Score: " + enemyPlayer.getScore(),0, (int) (3 * Def.G_GAP / 4));

        // Draw the Food
        for (GameEntity f : foods) {
            f.fillRect(g2);
        }
    }
}
