//import javax.swing.JComponent;
//import java.awt.Graphics2D;
//import java.awt.Graphics;
import javax.swing.JFrame;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
//import java.net.ServerSocket;
import java.net.Socket;
//import javax.swing.Timer;

//commented out some import that are not used right now but may need it later


public class GameClient extends JFrame {
    private static int PORT = 3000; //hard code the port number
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private GameBoard gameBoard;
    private Player controllablePlayer;

    // TODO: Make this multithreaded (runnable?)
    public GameClient(String serverAddress) throws Exception{ //serverAddress = IP(hard code too)

        try {
            socket = new Socket(serverAddress,PORT);
            //init function;
            while(true){
                setPosition(socket);
                sendMessage();
            }
        } finally {
            System.out.println("closing socket");
            socket.close();
        }

    }

    private void setPosition(Socket socket){ //set position function for the player
        this.socket = socket;
        try{
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(),true);
            while(true){
                String commandMove = in.readLine();
                //more below to get the move and move the player

            }
        } catch (IOException e) {
            //TODO: handle exception
            System.out.println("Game end: " + e);
        }


    }


    public String createMsg(Direction direction) {
        switch (direction){
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

    public void sendMessage(){
        //TODO: Finish this
    }

    public void parseGameState() {
        // TODO: Parse game state
    }

    //init function can be here

//    public static void main(String[] args) throws Exception{
//        GameClient client = new GameClient("127.0.0.1"); // local host IP add for now
//
//    }



}