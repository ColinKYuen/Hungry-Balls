import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/*
The 'player' client of the app. Handles board population and input collection.

Decodes game state strings from the Server to update the local rendering of the board;
Collects key presses, then parses and sends them as Directions to the server;
Gracefully closes the game if a player disconnects or wins.

 */

public class GameClient implements KeyListener {
    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;
    private int playerID;

    private final GameBoard gameBoard;
    private Direction inputDirection = Direction.Stop;

    public GameClient(int serverPort) throws Exception {
        this("localhost", serverPort);
    }

    // Initialize a new game client, collecting initial game state information from the connection to the server.
    public GameClient(String serverAddress, int serverPort) throws Exception {

        // Create a new socket from the arguments passed by the client player.
        socket = new Socket(serverAddress, serverPort);

        // Set up in and out readers to communicate with the server via the socket.
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        // First, send initializing message.
        // Let the server know that the client has successfully connected and is ready to receive game state information.
        out.println("00");

        // Collect the initial response from the server and use it to initialize the game.
        String initResponse = in.readLine();

        // The game state is provided by the server as a comma separated string of arguments.
        // Split the string to parse the arguments individually.
        String[] gameStateStrings = initResponse.split(",");

        List<Player> players = new ArrayList<>();

        // The first two characters represent the X, Y of player 1, the host.
        players.add(new Player(Integer.parseInt(gameStateStrings[0]),
                Integer.parseInt(gameStateStrings[1]),
                Def.P1_COLOR, 0));

        // The third and fourth character represent the X, Y of player 2, the client.
        players.add(new Player(Integer.parseInt(gameStateStrings[3]),
                Integer.parseInt(gameStateStrings[4]),
                Def.P2_COLOR, 1));

        List<GameEntity> foods = new ArrayList<>();

        // The sixth and seventh character represent the location of the food on the board.
        foods.add(new GameEntity(Integer.parseInt(gameStateStrings[6]),
                Integer.parseInt(gameStateStrings[7]),
                Def.F_COLOR));

        // Finally, the eighth character represents the player ID.
        playerID = Integer.parseInt(gameStateStrings[8]);

        // Set up the game board based on what we know about the state from the server.
        gameBoard = new GameBoard(players, foods, playerID);
    }

    // Returns the win/lose state
    public boolean start() throws IOException {
        while (true) {
            // Send currently chosen direction to the server.
            sendMessage();
            // Wait for the server to respond with a game state update.
            String initResponse = in.readLine();
            System.out.println(initResponse);
            // Parse the game state update.
            // Check if the response is V (victory) or L (loss). Return true or false, respectively, if so.
            String[] gameStateStrings = initResponse.split(",");
            if (gameStateStrings[0].equals("V")) {
                // Player 1 (the host) wins!
                out.close();
                in.close();
                socket.close();
                return true;
            } else if (gameStateStrings[0].equals("L")) {
                // Player 1 (the host) loses.
                out.close();
                in.close();
                socket.close();
                return false;
            } else {
                // Otherwise, parse the gameStateString to obtain player and food coordinates and update the board with them.
                List<Player> players = new ArrayList<>();
                Player player1 = new Player(Integer.parseInt(gameStateStrings[0]), Integer.parseInt(gameStateStrings[1]), Def.P1_COLOR, 0);
                player1.setScore(Integer.parseInt(gameStateStrings[2]));
                Player player2 = new Player(Integer.parseInt(gameStateStrings[3]), Integer.parseInt(gameStateStrings[4]), Def.P2_COLOR, 1);
                player2.setScore(Integer.parseInt(gameStateStrings[5]));
                players.add(player1);
                players.add(player2);
                List<GameEntity> foods = new ArrayList<>();
                foods.add(new GameEntity(Integer.parseInt(gameStateStrings[6]), Integer.parseInt(gameStateStrings[7]), Def.F_COLOR));
                playerID = Integer.parseInt(gameStateStrings[8]);
                gameBoard.updateEntities(players, foods);
            }
        }
    }

    public void triggerQuit() {
        inputDirection = Direction.Quit;
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }

    // Parse the Directions obtained from key presses into raw chars to send in messages to the server
    public String createMsg(Direction direction) {
        switch (direction) {
            case North:
                return "N";
            case South:
                return "S";
            case East:
                return "E";
            case West:
                return "W";
            case Stop:
                return "T";
            case Quit:
                return "Q";
            default:
                return "";
        }
    }

    // Send the currently chosen direction to the server.
    // The currently chosen direction is obtained via keypress, below.
    public void sendMessage() {
        Direction dir = inputDirection;
        String message = createMsg(dir);
        out.println(message);
    }

    // Collect key presses for direction input. Accepts arrow keys or WASD.
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_Q:
            case KeyEvent.VK_ESCAPE:
                inputDirection = Direction.Quit;
                break;

            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                inputDirection = Direction.West;
                break;

            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                inputDirection = Direction.North;
                break;

            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                inputDirection = Direction.East;
                break;

            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                inputDirection = Direction.South;
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }
}
