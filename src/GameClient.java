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
The 'player' client of the app.

Decodes game state strings from the Server to update the local rendering of the board,
Collects key presses, then parses and sends them as Directions to the server,
and gracefully closes the game if a player disconnects or wins.

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
        // TODO: Make a loop of rendering the gameBoard and sending the direction
        // TODO: Make sure to return true if won and false if lost. We'll know if it won or lost if the string is "W" or "L"
        while (true) {
            // To do in this loop:
            // 1. Send message of player input to the server
            sendMessage();
            // 2. Wait for a response from the server
            String initResponse = in.readLine();
            System.out.println(initResponse);
            // 3. Parse the response
            // Check if the response is win or lose, if it is, return true or false
            String[] gameStateStrings = initResponse.split(",");
            if (gameStateStrings[0].equals("V")) {
                //when a player win a game
                out.close();
                in.close();
                socket.close();
                return true;
            } else if (gameStateStrings[0].equals("L")) {
                //when a player lose a game
                out.close();
                in.close();
                socket.close();
                return false;
            } else {
                //the game is still on
                // 4. Update the game board according to the response from Server
                // Implement and call the updateEntities() function below
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

    public void sendMessage() {
        //direction depends on the key pressed[keypressed are in game controller part]
        //direction will come from keylistener or keybinding
        //then direction get convert into a message to be sent to the server side
        Direction dir = inputDirection;
        String message = createMsg(dir);
        out.println(message);
    }

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
