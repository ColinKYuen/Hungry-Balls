import javax.swing.*;
import java.awt.Graphics;
import java.awt.Graphics2D;
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

    public void start() {
        gameStartTime = System.currentTimeMillis();
        repaint();

        while (isGameRunning) {
            long elapsedTime = getElapsedTime();
            updateGame();
            repaint();
            elapsedTime += FRAME_DURATION;
            long sleepDuration = elapsedTime - getElapsedTime();
            if (sleepDuration >= 0) {
                try {
                    Thread.sleep(sleepDuration);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            } else {
                System.out.println("Update and repaint took too long");
            }
        }

        // Boolean value to indicate win / lose
        triggerGameEnd(true);
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

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_Q, KeyEvent.VK_ESCAPE -> quit();
            case KeyEvent.VK_LEFT, KeyEvent.VK_A -> controlledPlayer.setNextDirection(Direction.West);
            case KeyEvent.VK_UP, KeyEvent.VK_W -> controlledPlayer.setNextDirection(Direction.North);
            case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> controlledPlayer.setNextDirection(Direction.East);
            case KeyEvent.VK_DOWN, KeyEvent.VK_S -> controlledPlayer.setNextDirection(Direction.South);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    private long getElapsedTime() {
        return System.currentTimeMillis() - gameStartTime;
    }

    private void updateGame() {
        // TODO: Update game here
        // Later, all update game does is send and receive answer from server, and update game state according to server's answer

        debugMovement();
    }

    // To debug movement, delete this once we have server implementation
    private void debugMovement() {
        switch (controlledPlayer.getNextDirection()) {
            case North -> controlledPlayer.setYPos(controlledPlayer.getYPos() - 1);
            case South -> controlledPlayer.setYPos(controlledPlayer.getYPos() + 1);
            case East -> controlledPlayer.setXPos(controlledPlayer.getXPos() + 1);
            case West -> controlledPlayer.setXPos(controlledPlayer.getXPos() - 1);
        }
    }

    private void triggerGameEnd(boolean isWinner) {
        final JFrame frame = new JFrame("Results");
        final String result;
        if (isWinner) {
            result = "Congrats, you won!";
        } else {
            result = "You lost. Better luck next time.";
        }
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JOptionPane.showMessageDialog(frame, result, "Game Results", JOptionPane.INFORMATION_MESSAGE);
    }
}
