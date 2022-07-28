import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GameServer{
    private ServerSocket listener;
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;

    private GameController gameController;
    private ExecutorService executorService;
    private ClientList clientList;

    public GameServer() throws IOException, InterruptedException {
        listener = new ServerSocket(3000);
        clientList = new ClientList();
        gameController = new GameController(clientList);
        executorService = Executors.newFixedThreadPool(Def.NUM_OF_PLAYERS);
        while (clientList.getCurrentCount() != Def.NUM_OF_PLAYERS) {
            System.out.println("Waiting for client to connect...");
            Socket client= listener.accept();
            ServerChild childThread = new ServerChild(client,gameController,clientList);
            executorService.execute(childThread);
        }
        System.out.println("Game Start");
        gameController.start();
        executorService.shutdown();
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    }

    public static void main(String[] args) throws Exception {
        GameServer server = new GameServer();
    }
}
