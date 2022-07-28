import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerChild implements Runnable {
    private final Socket client;
    private final int playerID;
    private final GameController gameController;
    private final ClientList clientList;



    public ServerChild(Socket socket, GameController gameController, ClientList clientList) {
        this.client= socket;
        this.clientList = clientList;
        this.playerID = clientList.generatePlayerID();
        this.gameController = gameController;
    }

    @Override
    public void run() {
        try{
            OutputStream os = client.getOutputStream();
            InputStream is = client.getInputStream();
            PrintWriter out = new PrintWriter(os,true);
            BufferedReader in = new BufferedReader(new InputStreamReader(is));

            String initMsg = in.readLine(); // We assume that the first message is "00"
            if (initMsg.equals("00")){
                String initResponse = gameController.generateGameStateString(playerID);
                out.println(initResponse);
            }

            while (true) {
                String msg = in.readLine();
                System.out.println(msg);
                Direction proposedDir = parseDirection(msg);
                clientList.setClientUpdated(playerID,true);
                gameController.setPlayerNextDirection(playerID,proposedDir);

                while (!clientList.isReadyForNextUpdate()){} // Block while not ready for next update
                String gameStateString = gameController.generateGameStateString(playerID);
                System.out.println(gameStateString);
                out.println(gameStateString);
                if (gameStateString.equals("V") || gameStateString.equals("L")) {
                    out.close();
                    in.close();
                    client.close();
                    return;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
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

}
