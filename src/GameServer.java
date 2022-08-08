import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


// Initialize a host server to run the game and accept client connections.

public class GameServer {
    public GameServer(int port) throws IOException, InterruptedException {

        // Create a new ServerSocket from the port passed into the arguments.
        ServerSocket listener = new ServerSocket(port);
        ClientList clientList = new ClientList();
        GameController gameController = new GameController(clientList);

        // Prepare threads for the application. Each client runs on its own Thread.
        // Def.NUM_OF_PLAYERS is a hardcoded value for how many players the game is designed to handle;
        // For the scope of this project, NUM_OF_PLAYERS == 2.
        ExecutorService executorService = Executors.newFixedThreadPool(Def.NUM_OF_PLAYERS);

        // Wait until two players (the host and any other client) have connected.
        while (clientList.getCurrentCount() != Def.NUM_OF_PLAYERS) {
            System.out.println("Waiting for client to connect...");

            // Accept incoming connections from clients using the port of the host.
            // Kick off server-side threads for a connecting client to handle game logic for that client.
            Socket client = listener.accept();
            ServerChild childThread = new ServerChild(client, gameController, clientList);
            executorService.execute(childThread);
        }
        System.out.println("Game Start");
        gameController.start();
        executorService.shutdown();
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    }
}
