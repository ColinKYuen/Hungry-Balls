import java.util.concurrent.ConcurrentHashMap;

public class ClientList {
    // Shared map to signify that client has sent the player input to the server
    // Key: The player ID
    // Value: True if the client has sent the player input to the server, false otherwise
    private ConcurrentHashMap<Integer,Boolean> clientUpdatedMap;
    private int clientCounter;

    public ClientList (){
        clientUpdatedMap = new ConcurrentHashMap<>();
    }

    public synchronized int generatePlayerID() {
        int playerID=clientCounter;
        clientCounter++;
        clientUpdatedMap.put(playerID, false);
        return playerID;
    }

    public int getCurrentCount() {
        return clientCounter;
    }

    public void setClientUpdated(int playerID, boolean value){
        clientUpdatedMap.replace(playerID,value);
    }

    public boolean isAllPlayersUpdated () {
        // Return true if all elements are true
        for (int i = 0; i < Def.NUM_OF_PLAYERS; i++) {
            if (!clientUpdatedMap.get(i)) {
                return false;
            }
        }
        return true;
    }

    public boolean isReadyForNextUpdate () {
        // Return true if all elements are false
        for (int i = 0; i < Def.NUM_OF_PLAYERS; i++) {
            if (clientUpdatedMap.get(i)){
                return false;
            }
        }
        return true;
    }

    public ConcurrentHashMap<Integer, Boolean> getClientUpdatedMap() {
        return clientUpdatedMap;
    }
}
