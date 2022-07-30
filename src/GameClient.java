import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class GameClient implements KeyListener {
    private static int PORT = 3000; //hard code the port number
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private int playerID;

    private GameBoard gameBoard;
    private Direction inputDirection = Direction.Stop;


    public GameClient(String serverAddress) throws Exception { //serverAddress = IP(hard code too)
        socket = new Socket(serverAddress,PORT);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(),true);
        out.println("00"); // First, send initializing message
        String initResponse = in.readLine();
        String[] gameStateStrings = initResponse.split(",");
        List<Player> players = new ArrayList<>();
        players.add(new Player(Integer.parseInt(gameStateStrings[0]),
                Integer.parseInt(gameStateStrings[1]),
                Def.P1_COLOR,0));
        players.add(new Player(Integer.parseInt(gameStateStrings[3]),
                Integer.parseInt(gameStateStrings[4]),
                Def.P2_COLOR,1));
        List<GameEntity> foods = new ArrayList<>();
        foods.add(new GameEntity(Integer.parseInt(gameStateStrings[6]),
                Integer.parseInt(gameStateStrings[7]),
                Def.F_COLOR));
        playerID = Integer.parseInt(gameStateStrings[8]);
        gameBoard = new GameBoard(players,foods,playerID);

    }

    // Returns the win/lose state
    public Boolean start() throws IOException {
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
                Player player1 = new Player(Integer.parseInt(gameStateStrings[0]),Integer.parseInt(gameStateStrings[1]),Def.P1_COLOR,0);
                player1.setScore(Integer.parseInt(gameStateStrings[2]));
                Player player2 = new Player(Integer.parseInt(gameStateStrings[3]),Integer.parseInt(gameStateStrings[4]),Def.P2_COLOR,1);
                player2.setScore(Integer.parseInt(gameStateStrings[5]));
                players.add(player1);
                players.add(player2);
                List<GameEntity> foods = new ArrayList<>();
                foods.add(new GameEntity(Integer.parseInt(gameStateStrings[6]),Integer.parseInt(gameStateStrings[7]),Def.F_COLOR));
                playerID = Integer.parseInt(gameStateStrings[8]);
                gameBoard.updateEntities(players,foods);
            }
        }
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
