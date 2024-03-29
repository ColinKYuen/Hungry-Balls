import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

// Starts the game, either as host or client depending on command line arguments.
// See the readme for full details!

public class Launcher {
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

    // Sets up the game client. If a host address was passed to the command line when the application was run,
    // connect a new game client to the running host specified by the host address. Otherwise, instantiate
    // a new host. The GameClient handles the network Sockets for the application.
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
            client = new GameClient(port);
        } else {
            client = new GameClient(host, port);
        }
    }

    // Prepare the window for rendering the game.
    private static void setUpFrame() {
        frame = new JFrame();
        frame.setSize(Def.WIDTH, Def.HEIGHT);
        frame.setTitle("CMPT371 - Group 10");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setResizable(false);
        frame.addWindowListener(
                new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        client.triggerQuit();
                        try {
                            // We sleep for one frame before exiting to let the game update for the other client before closing
                            Thread.sleep(1000 / Def.FRAMES_PER_SECOND);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                        System.exit(1);
                    }
                }
        );
        GameBoard component = client.getGameBoard();
        frame.add(component);
        frame.addKeyListener(client);

        frame.setVisible(true);
    }

    private static void startGame() throws IOException {
        boolean isWon = client.start();
        triggerGameEnd(isWon);
        frame.setVisible(false);
        frame.dispose();
    }

    // Parse the collected command line arguments.
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
            // Explain proper input if malformed input is provided.
            System.out.println("Start a Server: java Launcher <port>\nConnect to a Server: java Launcher <host> <port>");
            System.exit(0);
        }
    }

    // Expect a valid port number from the port argument.
    private static int validateInput(String portArg) throws Exception {
        int port = Integer.parseInt(portArg);
        if (port < 0 || port > 65535) {
            throw new Exception("Incorrect Argument, invalid port, exiting.");
        }
        return port;
    }

    // Display a 'game over' screen based on whether your won or lost!
    private static void triggerGameEnd(boolean isWinner) {
        final JFrame frame = new JFrame("Results");
        final String result;
        if (isWinner) {
            result = "Congrats, you won!";
        } else {
            result = "You lost. Better luck next time.";
        }
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JOptionPane.showMessageDialog(frame, result, "Game Results", JOptionPane.INFORMATION_MESSAGE);
        frame.dispose();
    }
}
