import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GameServer {
    public GameServer(int port) throws IOException, InterruptedException {
        ServerSocket listener = new ServerSocket(port);
        ClientList clientList = new ClientList();
        GameController gameController = new GameController(clientList);
        ExecutorService executorService = Executors.newFixedThreadPool(Def.NUM_OF_PLAYERS);
        while (clientList.getCurrentCount() != Def.NUM_OF_PLAYERS) {
            System.out.println("Waiting for client to connect...");
            Socket client= listener.accept();
            ServerChild childThread = new ServerChild(client, gameController, clientList);
            executorService.execute(childThread);
        }
        System.out.println("Game Start");
        gameController.start();
        executorService.shutdown();
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    }
}
