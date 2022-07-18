import javax.swing.JComponent;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class GameBoard extends JComponent {
    private final ReentrantLock[][] map = new ReentrantLock[Def.MAP_SIZE][Def.MAP_SIZE];
    private final GameEntity[][] board = new GameEntity[Def.MAP_SIZE][Def.MAP_SIZE];
    private final List<GameEntity> players = new ArrayList();
    private final List<GameEntity> foods = new ArrayList();

    public GameBoard() {
        // Initialize Game Board
        for (int i = 0; i < Def.MAP_SIZE; i++) {
            for (int j = 0; j < Def.MAP_SIZE; j++) {
                map[i][j] = new ReentrantLock();
                board[i][j] = new GameEntity(i, j, new Color(0, 0, 0));
            }
        }

        // For debugging
        // Initialize Player
        for (int i = 0; i < 5; i++) {
            Color colour = new Color((float)Math.random(), (float)Math.random(), (float)Math.random());
            int x = (int) (Math.random() * Def.MAP_SIZE);
            int y = (int) (Math.random() * Def.MAP_SIZE);
            players.add(new GameEntity(x, y, colour));
        }

        // For debugging
        // Initialize Food
        Color colour = new Color((float)Math.random(), (float)Math.random(), (float)Math.random());
        int x = (int) (Math.random() * Def.MAP_SIZE);
        int y = (int) (Math.random() * Def.MAP_SIZE);
        foods.add(new GameEntity(x, y, colour));
    }

    public void testDebug() {
        for (GameEntity p : players) {
            p.setXPos((p.getXPos() + 1) % Def.MAP_SIZE);
            p.setYPos((p.getYPos() + 1) % Def.MAP_SIZE);
        }
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

        // Draw the Player
        for (GameEntity p : players) {
            p.fillCircle(g2);
        }

        // Draw the Food
        for (GameEntity f : foods) {
            f.fillRect(g2);
        }
    }
}
