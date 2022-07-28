//import javax.swing.JComponent;
//import java.awt.Graphics2D;
//import java.awt.Graphics;

import javax.swing.JFrame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

//commented out some import that are not used right now but may need it later

public class GameClient extends JFrame implements KeyListener {
    private static int PORT = 3000; //hard code the port number
    private boolean isGameRunning;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private int playerID;

    private GameBoard gameBoard;
    private Player controllablePlayer;
    private Direction inputDirection;


    public GameClient(String serverAddress) throws Exception { //serverAddress = IP(hard code too)
        socket = new Socket(serverAddress,PORT);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(),true);
        //init function;
//            while (true){
//                setPosition(socket);
//                sendMessage();
//            }
        out.println("00"); // First, send initializing message
        String initResponse = in.readLine();
        String[] gameStateStrings = initResponse.split(",");
        List<Player> players = new ArrayList<>();
        players.add(new Player(Integer.getInteger(gameStateStrings[0]),Integer.getInteger(gameStateStrings[1]),Def.P1_COLOR,0));
        players.add(new Player(Integer.getInteger(gameStateStrings[3]),Integer.getInteger(gameStateStrings[4]),Def.P1_COLOR,0));
        List<GameEntity> foods = new ArrayList<>();
        foods.add(new GameEntity(Integer.getInteger(gameStateStrings[6]),Integer.getInteger(gameStateStrings[7]),Def.F_COLOR));
        playerID = Integer.getInteger(gameStateStrings[8]);
        gameBoard = new GameBoard(players,foods,playerID);
//        } finally {
//            System.out.println("closing socket");
//            socket.close();
//        }

    }

    // Returns the win/lose state
    public Boolean start() throws IOException {
        // TODO: Make a loop of rendering the gameBoard and sending the direction
        // TODO: Make sure to return true if won and false if lost. We'll know if it won or lost if the string is "W" or "L"
        while (isGameRunning) {
            // To do in this loop:
            // 1. Send message of player input to the server
                // If inputDirection is Quit, immediately return false
            sendMessage();
            // 2. Wait for a response from the server
            String initResponse = in.readLine();
            // 3. Parse the response
                // Check if the response is win or lose, if it is, return true or false
            String[] gameStateStrings = initResponse.split(",");
            if(gameStateStrings.equals("W")){
                //when a player win a game
                return TRUE;
            } else if (gameStateStrings.equals("L")) {
                //when a player lose a game
                return FALSE;
            } else {
                //the game is still on
                // 4. Update the game board according to the response from Server
                // Implement and call the updateGameBoard() function below
                players.add(new Player(Integer.getInteger(gameStateStrings[0]),Integer.getInteger(gameStateStrings[1]),Def.P1_COLOR,0));
                players.add(new Player(Integer.getInteger(gameStateStrings[3]),Integer.getInteger(gameStateStrings[4]),Def.P1_COLOR,0));
                foods.add(new GameEntity(Integer.getInteger(gameStateStrings[6]),Integer.getInteger(gameStateStrings[7]),Def.F_COLOR));
                playerID = Integer.getInteger(gameStateStrings[8]);
                gameBoard = new GameBoard(players,foods,playerID);
            }


        }
    }

    private void setPosition(Socket socket) { //set position function for the player
        this.socket = socket;
        try {
            while (true) {
                String GameState = in.readLine();
                //more below to get the move and move the player
                //parse GameState after recevied the GameState
                //GameState come in the form of x1,y1,s1,x2,y2,s2 
                //position of player 1 with coordinate x1,y1 and then position of player 2 with coordinate x2,y2
                //s1 is score of player 1, s2 is score of player 2 which the server will keep track
                String Location1 = GameState.substring(0, 3);     //x1,y1 [ 0 to 2]
                String score1 = Character.toString(GameState.charAt(4));        //s1 [4]
                String Location2 = GameState.substring(6, 9);    //x2,y2 [6 to 8]
                String score2 = Character.toString(GameState.charAt(10));      //s2 [10]
                // Update Game Board using gameBoard.updateEntities
                // listen to the move and then send to the server side
                sendMessage();

            }
        } catch (IOException e) {
            //TODO: handle exception
            System.out.println("Game end: " + e);
        }


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
        Direction dir=inputDirection;
        String message = createMsg(dir);
        out.println(message);
    }

    private void updateGameBoard(String[] gameStateString){
        // TODO: Update the game board here
    }

    private void quit() {
        isGameRunning = false;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_Q:
            case KeyEvent.VK_ESCAPE:
                quit();
                break;

            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                inputDirection=Direction.East;
                break;

            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                inputDirection= Direction.North;
                break;

            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                inputDirection= Direction.West;
                break;

            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                inputDirection= Direction.South;
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }


    //init function can be here

//    public static void main(String[] args) throws Exception{
//        GameClient client = new GameClient("127.0.0.1"); // local host IP add for now
//
//    }



}