import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

// A running server-side game instance. Represents threaded game logic calculation for a single player.
// Collects direction inputs as messages from a client instance and uses them to calculate player movement.
// Shuts down when the game state reports a player with 5 points.

public class ServerChild implements Runnable {
    private final Socket client;
    private final int playerID;
    private final GameController gameController;
    private final ClientList clientList;

    public ServerChild(Socket socket, GameController gameController, ClientList clientList) {
        this.client = socket;
        this.clientList = clientList;
        this.playerID = clientList.generatePlayerID();
        this.gameController = gameController;
    }

    @Override
    public void run() {
        try {

            // Set up input and output readers to send and receive messages from a client instance.
            OutputStream os = client.getOutputStream();
            InputStream is = client.getInputStream();
            PrintWriter out = new PrintWriter(os, true);
            BufferedReader in = new BufferedReader(new InputStreamReader(is));

            // Read in messages from a client instance, expecting the '00' initializer char as the first line.
            String initMsg = in.readLine(); // We assume that the first message is "00"
            if (initMsg.equals("00")) {

                // Generate a string encoding the current game state.
                String initResponse = gameController.generateGameStateString(playerID);
                out.println(initResponse);
            }

            while (true) {

                // Read direction inputs from the client.
                String msg = in.readLine();
                System.out.println(msg);

                // Parse the direction from the message.
                Direction proposedDir = parseDirection(msg);

                // Update the client and attempt to update the direction with the suggested direction from the client.
                // If the suggested direction is impossible, movement will continue in the original direction.
                clientList.setClientUpdated(playerID, true);
                gameController.setPlayerNextDirection(playerID, proposedDir);

                // Wait until all clients have resolved their turns.
                while (!clientList.isReadyForNextUpdate()) {} // Block while not ready for next update

                // Retrieve game state from the Controller.
                String gameStateString = gameController.generateGameStateString(playerID);
                System.out.println(gameStateString);
                out.println(gameStateString);

                // End the game if a player has scored WINNING_SCORE points.
                if (gameStateString.equals("V") || gameStateString.equals("L")) {
                    out.close();
                    in.close();
                    client.close();
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Turn raw client message Strings into Directions for use by game logic.
    private Direction parseDirection(String clientMsg) {
        switch (clientMsg) {
            case "N":
                return Direction.North;
            case "S":
                return Direction.South;
            case "E":
                return Direction.East;
            case "W":
                return Direction.West;
            case "Q":
                return Direction.Quit;
            default:
                return Direction.Stop;
        }
    }

}
