import javax.swing.JFrame;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class GameServer extends JFrame {

    private ServerSocket listener;
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;

    private GameController gameController;
    private GameBoard gameBoard;

    // TODO: Make this into multithread (runnable?)
    public GameServer() throws IOException{
        ServerSocket listener = new ServerSocket(3000);
        System.out.println("Game is running");
        //init function
        try{
            while(true){
                setPositions(listener.accept());

            }
        } finally {
            System.out.println("closing");
            listener.close();
        }

    }

    private void setPositions(Socket socket) {
        this.socket = socket;
        try {
                input = new BufferedReader( new InputStreamReader(socket.getInputStream()));
                output = new PrintWriter(socket.getOutputStream(), true);
                while(true) {
                String command = input.readLine();        
                }
        } catch (IOException e) {
            System.out.println("Game end: " + e);
        }
    }

    public void updateGameState() {

    }

    private Direction parseDirection (String clientMsg) {
        switch (clientMsg) {
            case "N":
                return Direction.North;
            case "S":
                return Direction.South;
            case "E":
                return Direction.East;
            case "W":
                return Direction.West;
            case "T":
                return Direction.Stop;
            case "Q":
                return Direction.Quit;
            default:
                return null;
        }
    }

    public String createMsg()
    {
        //TODO: Finish this
        return "";
    }

    public void sendGameState () {
        // TODO: Prepare string to send
        // TODO: Make sure both clients have connected before sending game state
    }

//    public static void main(String[] args) throws Exception{
//        GameServer server = new GameServer();
//    }


}
