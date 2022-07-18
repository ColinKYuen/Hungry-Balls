import javax.swing.JComponent;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
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
                Rectangle2D.Double mapGrid = new Rectangle2D.Double(
                    Def.G_GAP + i * Def.G_WIDTH,
                    Def.G_GAP + j * Def.G_WIDTH,
                    Def.G_WIDTH, Def.G_WIDTH);
                board[i][j] = new GameEntity(mapGrid, new Color(0, 0, 0));
            }
        }


        // For debugging
        // Initialize Player
        for (int i = 0; i < 5; i++) {
            Color colour = new Color((float)Math.random(), (float)Math.random(), (float)Math.random());
            int x = (int) (Math.random() * Def.MAP_SIZE);
            int y = (int) (Math.random() * Def.MAP_SIZE);
            Ellipse2D.Double playerCircle = new Ellipse2D.Double(
                    Def.G_GAP + x * Def.G_WIDTH,
                    Def.G_GAP + y * Def.G_WIDTH,
                    Def.G_WIDTH, Def.G_WIDTH);
            players.add(new GameEntity(playerCircle, colour));
        }

        // For debugging
        // Initialize Food
        Color colour = new Color((float)Math.random(), (float)Math.random(), (float)Math.random());
        int x = (int) (Math.random() * Def.MAP_SIZE);
        int y = (int) (Math.random() * Def.MAP_SIZE);
        Rectangle2D.Double playerCircle = new Rectangle2D.Double(
                Def.G_GAP + x * Def.G_WIDTH,
                Def.G_GAP + y * Def.G_WIDTH,
                Def.G_WIDTH, Def.G_WIDTH);
        foods.add(new GameEntity(playerCircle, colour));
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
        for (GameEntity p : players) {
            p.fill(g2);
        }

        // Draw the Food
        for (GameEntity f : foods) {
            f.fill(g2);
        }
    }
}