import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ClientList {
    // Shared map to signify that client has sent the player input to the server
    // Key: The player ID
    // Value: True if the client has sent the player input to the server, false otherwise
    private ConcurrentHashMap<Integer,Boolean> clientUpdatedMap;
    private int playerIDCounter;

    public ClientList (){
        clientUpdatedMap = new ConcurrentHashMap<>();
        for (int i=0; i<Def.NUM_OF_PLAYERS;i++) {
            clientUpdatedMap.put(i,false);
        }
        playerIDCounter = -1;
    }

    public synchronized int generatePlayerID() {
        playerIDCounter++;
        return playerIDCounter;
    }

    public int getCurrentCount() {
        return playerIDCounter;
    }

    public void setClientUpdated(int playerID, boolean value){
        clientUpdatedMap.replace(playerID,value);
    }

    public boolean isAllPlayersUpdated () {
        // Return true if all elements are true
        for (int i=0;i<Def.NUM_OF_PLAYERS;i++){
            if (!clientUpdatedMap.get(i)){
                return false;
            }
        }
        return true;
    }

    public boolean isReadyForNextUpdate () {
        // Return true if all elements are false
        for (int i=0;i<Def.NUM_OF_PLAYERS;i++){
            if (clientUpdatedMap.get(i)){
                return false;
            }
        }
        return true;
    }
}
