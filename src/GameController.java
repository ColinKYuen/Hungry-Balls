import javax.swing.JComponent;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


public class GameController extends JComponent implements KeyListener {
    private static GameBoard gameBoard;
    private final int FRAMES_PER_SECOND = 2;
    private final long FRAME_DURATION = 1000 / FRAMES_PER_SECOND;
    private boolean isGameRunning= true;
    private long gameStartTime;
    private Player controlledPlayer;
    private Player enemyPlayer;

    private void quit() {
        isGameRunning = false;
    }

    public GameController() {
        gameBoard = new GameBoard();
        controlledPlayer = gameBoard.getControllablePlayer();
        enemyPlayer = gameBoard.getEnemyPlayer();


        // Set Up
        // Add players into a list and send info to game board


        // Server
        // Client
        // Etc
    }

    private long getElaspedTime()
    {
        return System.currentTimeMillis() - gameStartTime;
    }

    public void start(){
        gameStartTime = System.currentTimeMillis();
        repaint();

        while(!quitNextUpdate) {
            long elapsedTime = getElaspedTime();
            updateGame();
            repaint();
            elapsedTime += FRAME_DURATION;
            long sleepDuration = elapsedTime - getElaspedTime();
            if (sleepDuration>=0)
            {
                try
                {
                    Thread.sleep(sleepDuration);
                }
                catch(InterruptedException ex)
                {
                    Thread.currentThread().interrupt();
                }
            }
            else
            {
                System.out.println("Update and repaint took too long");
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

    public void updateGame() {
        // TODO: Update game here
        // Later, all update game does is send and receive answer from server, and update game state according to server's answer

        debugMovement();
    }

    // To debug movement, delete this once we have server implementation
    public void debugMovement() {
        switch (controlledPlayer.getNextDirection()) {
            default:
            case Stop:
                return;

            case North:
                controlledPlayer.setYPos(controlledPlayer.getYPos()-1);
                return;

            case South:
                controlledPlayer.setYPos(controlledPlayer.getYPos()+1);
                return;

            case East:
                controlledPlayer.setXPos(controlledPlayer.getXPos()+1);
                return;

            case West:
                controlledPlayer.setXPos(controlledPlayer.getXPos()-1);
                return;
        }
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
                controlledPlayer.setNextDirection(Direction.West);
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
        controlledPlayer.setNextDirection(direction);
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}