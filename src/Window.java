import javax.swing.*;

// Window will start the game on the client
public class Window {
    private static void triggerGameEnd(boolean isWinner) {
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

    public static void main(String[] args) throws Exception {
        JFrame frame = new JFrame();

        frame.setSize(Def.WIDTH, Def.HEIGHT);
        frame.setTitle("CMPT371 - Group 10");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        GameClient client = new GameClient(args[0]);
        GameBoard component = client.getGameBoard();
        frame.add(component);
        frame.addKeyListener(client);

        frame.setVisible(true);

        Boolean isWon = client.start();

        frame.setVisible(false);
        frame.dispose();
        triggerGameEnd(isWon);
    }

}
