import javax.swing.JComponent;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

public class GameBoard extends JComponent {
    private final GameEntity[][] board = new GameEntity[Def.MAP_SIZE][Def.MAP_SIZE];
    private final List<GameEntity> foods = new ArrayList<>();
    private final List<Player> players = new ArrayList<>();
    private final int playerID;

    public GameBoard(List<Player> players, List<GameEntity> foods, int playerID) {
        // Initialize Game Board
        for (int i = 0; i < Def.MAP_SIZE; i++) {
            for (int j = 0; j < Def.MAP_SIZE; j++) {
                board[i][j] = new GameEntity(i, j, new Color(0, 0, 0));
            }
        }

        this.players.addAll(players);
        this.foods.addAll(foods);
        this.playerID = playerID;
    }

    public void updateEntities(List<Player> newPlayers, List<GameEntity> newFoods) {
        this.players.clear();
        this.players.addAll(newPlayers);
        this.foods.clear();
        this.foods.addAll(newFoods);
        repaint();
    }

    /**
     * Render game board for client
     *
     * @param g
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        draw(g2);
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
        Player controllablePlayer = players.get(playerID);
        controllablePlayer.fillCircle(g2);
        g2.drawString("Your Score: " + controllablePlayer.getScore(), 0, (int) (1 * Def.G_GAP / 2));
        int enemyPlayerID = playerID == 0 ? 1 : 0; // Assumes only 2 players
        Player enemyPlayer = players.get(enemyPlayerID);
        enemyPlayer.fillCircle(g2);
        g2.drawString("Opponent Score: " + enemyPlayer.getScore(), 0, (int) (3 * Def.G_GAP / 4));

        // Draw the Food
        for (GameEntity f : foods) {
            f.fillRect(g2);
        }
    }
}
