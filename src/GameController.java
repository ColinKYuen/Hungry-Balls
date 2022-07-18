import javax.swing.JComponent;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import static java.lang.System.currentTimeMillis;

public class GameController extends JComponent implements KeyListener {
    private static GameBoard gameBoard;
    private final int FRAMES_PER_SECOND = 2;
    private final long FRAME_DURATION = 1000/FRAMES_PER_SECOND;
    private boolean quitNextUpdate = false;

    private void quit() {
        quitNextUpdate = true;
    }

    public GameController() {
        gameBoard = new GameBoard();
        // Set Up
        // Add players into a list and send info to game board

        // Server
        // Client
        // Etc
    }

    public void start(){
        long gameStartTime = System.currentTimeMillis();
        repaint();

        while(!quitNextUpdate)
        {
            long elapsedTime = System.currentTimeMillis() - gameStartTime;
            updateGame();
            repaint();
            elapsedTime += FRAME_DURATION;
            long sleepDuration = elapsedTime - System.currentTimeMillis();
            try
            {
                Thread.sleep(sleepDuration);
            }
            catch(InterruptedException ex)
            {
                Thread.currentThread().interrupt();
            }
        }
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

    public void updateGame()
    {
        // TODO: Update game here
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        Direction direction;
        switch (e.getKeyCode()) {
            default:
                return;

            case KeyEvent.VK_Q:
            case KeyEvent.VK_ESCAPE:
                quit();
                return;

            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                direction = Direction.West;
                break;

            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                direction = Direction.North;
                break;

            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                direction = Direction.East;
                break;

            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                direction = Direction.South;
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}