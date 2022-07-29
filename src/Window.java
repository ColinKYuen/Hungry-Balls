import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.io.IOException;

// Window will start the game on the client
public class Window {
    private static String host;
    private static int port;
    private static GameServer server;
    private static GameClient client;
    private static JFrame frame;

    public static void main(String[] args) throws Exception {
        parseArgs(args);
        setUpConnection();
        setUpFrame();
        startGame();
    }

    private static void setUpConnection() throws Exception {
        if (host == null) {
            // Create Server
            new Thread(() -> {
                try {
                    server = new GameServer(port);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("Window - Error with starting Server\nExiting.");
                    System.exit(1);
                }
            }).start();
            client = new GameClient("127.0.0.1", port);
        } else {
            client = new GameClient(host, port);
            System.out.println("Client");
        }
    }

    private static void setUpFrame() {
        frame = new JFrame();
        frame.setSize(Def.WIDTH, Def.HEIGHT);
        frame.setTitle("CMPT371 - Group 10");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        GameBoard component = client.getGameBoard();
        frame.add(component);
        frame.addKeyListener(client);

        frame.setVisible(true);
    }

    private static void startGame() throws IOException {
        Boolean isWon = client.start();
        triggerGameEnd(isWon);
        frame.setVisible(false);
        frame.dispose();
    }

    private static void parseArgs(String[] args) throws Exception {
        if (args.length == 1) {
            // Only one argument, start Server
            // Check if it's a number, ie a port
            port = validateInput(args[0]);
        } else if (args.length == 2) {
            // Two arguments, connect to specified server
            host = args[0];
            port = validateInput(args[1]);
        } else {
            System.out.println("Start a Server: java Window <port>\nConnect to a Server: java Window <host> <port>");
            System.exit(0);
        }
    }

    private static int validateInput(String portArg) throws Exception {
        int port = Integer.parseInt(portArg);
        if (port < 0 || port > 65535) {
            throw new Exception("Incorrect Argument, invalid port, exiting.");
        }
        return port;
    }

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
}
