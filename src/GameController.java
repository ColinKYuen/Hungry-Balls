import javax.swing.JComponent;
import java.awt.Graphics2D;
import java.awt.Graphics;

public class GameController extends JComponent {
    private static GameBoard gameBoard;

    public GameController() {
        gameBoard = new GameBoard();
        // Set Up
        // Add players into a list and send info to game board

        // Server
        // Client
        // Etc
    }

    public void start(){
        repaint();
    }

    /**
     * Render game board for client
     * @param g
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        gameBoard.draw(g2);
    }
}