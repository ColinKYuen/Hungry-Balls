import javax.swing.JFrame;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class GameServer extends JFrame {

    ServerSocket listener;
    Socket socket;
    BufferedReader input;
    PrintWriter output;

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

    public GameServer() throws IOException{
        ServerSocket listener = new ServerSocket(3000);
        System.out.println("Game is running");
        //init function
        try{
            while(true){
                setPositions(listener.accept());
                System.out.println("inside");
            }
        } finally {
            System.out.println("closing");
            listener.close();
        }

    }
    public static void main(String[] args) throws Exception{
        GameServer server = new GameServer();
    }


}
