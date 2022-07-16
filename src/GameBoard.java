import javax.swing.JComponent;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class GameBoard extends JComponent {
    private final ReentrantLock[][] map = new ReentrantLock[Def.MAP_SIZE][Def.MAP_SIZE];;
    private final Box[][] board = new Box[Def.MAP_SIZE][Def.MAP_SIZE];
    private List<Entity> players;

    public GameBoard() {
        for (int i = 0; i < Def.MAP_SIZE; i++) {
            for (int j = 0; j < Def.MAP_SIZE; j++) {
                map[i][j] = new ReentrantLock();
                board[i][j] = new Box(j, i);
            }
        }

        players = new ArrayList();
        // For debugging
        for (int i = 0; i < 5; i++) {
            Color col = new Color((float)Math.random(), (float)Math.random(), (float)Math.random());
            int x = (int) (Math.random() * Def.MAP_SIZE);
            int y = (int) (Math.random() * Def.MAP_SIZE);
            players.add(new Entity(x, y, col));
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
                board[i][j].draw(g2);
            }
        }

        // Draw the Player
        for (Entity p : players) {
            p.draw(g2);
        }

        // Draw the Food
    }
}