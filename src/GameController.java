import javax.swing.JComponent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;


public class GameController extends JComponent {
    private final int FRAMES_PER_SECOND = 2;
    private final long FRAME_DURATION = 1000 / FRAMES_PER_SECOND;
    private boolean isGameRunning= true;
    private int winningPlayerID = -1;
    private long gameStartTime;
    private final ClientList clientList;
    private List<Player> players = new ArrayList<>();
    private List<GameEntity> foods = new ArrayList<>();
    private ReentrantLock[][] cells = new ReentrantLock[Def.MAP_SIZE][Def.MAP_SIZE];

    private void quit() {
        isGameRunning = false;
    }

    public GameController(ClientList clientList) {
        for(ReentrantLock[] col : cells){
            for(ReentrantLock cell : col){
                cell = new ReentrantLock();
            }
        }
        this.clientList = clientList;

        players.add(new Player(Def.P1_X_INITIAL_POS, Def.P1_Y_INITIAL_POS, Def.P1_COLOR, 0));
        players.add(new Player(Def.P2_X_INITIAL_POS, Def.P2_Y_INITIAL_POS, Def.P2_COLOR, 1));

        int foodYPos;
        int foodXPos;
        do {
            foodXPos = (int) (Math.random() * Def.MAP_SIZE);
        } while (foodXPos == players.get(0).getXPos() || foodXPos == players.get(1).getXPos());

        do {
            foodYPos = (int) (Math.random() * Def.MAP_SIZE);
        } while (foodYPos == players.get(0).getYPos() || foodYPos == players.get(1).getYPos());

        foods.add(new GameEntity(foodXPos,foodYPos,Def.F_COLOR));



        // Set Up
        // Add players into a list and send info to game board

        // Server
        // Client
        // Etc
    }

    public void start() {
        gameStartTime = System.currentTimeMillis();
        repaint();

        while (isGameRunning) {
            long elapsedTime = getElapsedTime();
            updateGame();
            elapsedTime += FRAME_DURATION;
            long sleepDuration = elapsedTime - getElapsedTime();
            if (sleepDuration >= 0) {
                try {
                    Thread.sleep(sleepDuration);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            } else {
                System.out.println("Update and repaint took too long");
            }
        }
    }

    public void setPlayerNextDirection(int playerID, Direction direction) throws InterruptedException {
        Player player = players.get(playerID);
        if (isMovementValid(player,direction)) {
            player.setNextDirection(direction);
        }
        else {
            player.setNextDirection(Direction.Stop);
        }
    }

    private boolean isMovementValid (Player player, Direction direction) throws InterruptedException {
        int x = player.getXPos();
        int y = player.getYPos();
        ReentrantLock cell;
        switch (direction){
//            case North:
//                return cells[x][y - 1].tryLock(0, TimeUnit.SECONDS);
//            case South:
//                return cells[x][y + 1].tryLock(0, TimeUnit.SECONDS);
//            case East:
//                return cells[x + 1][y].tryLock(0, TimeUnit.SECONDS);
//            case West:
//                return cells[x - 1][y].tryLock(0, TimeUnit.SECONDS);
            case North:
                cell = cells[x][y - 1];
                return cell.isHeldByCurrentThread() || cell.getHoldCount() < 1;
            case South:
                cell = cells[x][y + 1];
                return cell.isHeldByCurrentThread() || cell.getHoldCount() < 1;
            case East:
                cell = cells[x + 1][y];
                return cell.isHeldByCurrentThread() || cell.getHoldCount() < 1;
            case West:
                cell = cells[x - 1][y];
                return cell.isHeldByCurrentThread() || cell.getHoldCount() < 1;
            case Quit:
                return true;
        }
        return false;
    }

    private long getElapsedTime() {
        return System.currentTimeMillis() - gameStartTime;
    }

    private void updateGame() {
        while (!clientList.isAllPlayersUpdated()) {} // Block until server receives update from all players

        // Updating player movement
        for (Player p : players){

            cells[p.getXPos()][p.getYPos()].unlock();
            switch (p.getNextDirection()) {
                case North:
                    p.setYPos(p.getYPos() - 1);
                    break;

                case South:
                    p.setYPos(p.getYPos() + 1);
                    break;

                case East:
                    p.setXPos(p.getXPos() + 1);
                    break;

                case West:
                    p.setXPos(p.getXPos() - 1);
                    break;

                case Quit:
                    isGameRunning = false;
                    winningPlayerID = p.getPlayerID()==0? 1 : 0;
                    return;
            }
            cells[p.getXPos()][p.getYPos()].lock();
        }

        //TODO: Update score
        //TODO: If score == 5, end game by setting isGameRunning = false and winningPlayerID = *insert playerID*
        //TODO: Update food location if food is eaten, you can use setRandomFoodPosition()

        // Setting isClientUpdated back to false to prepare for next update session
        for (int i = 0; i < Def.NUM_OF_PLAYERS; i++) {
            clientList.setClientUpdated(i,false);
        }
    }

    public String generateGameStateString(int playerID) {
        if (!isGameRunning) {
            if (playerID == winningPlayerID) {
                return "V";
            }
            else {
                return "L";
            }
        }
        else {
            String result = "";
            for (Player p : players) {
                result = result + Integer.toString(p.getXPos()) + "," + Integer.toString(p.getYPos()) + "," + Integer.toString(p.getScore()) + ",";
            }
            for (GameEntity f : foods) {
                result = result + Integer.toString(f.getXPos()) + "," + Integer.toString(f.getYPos()) + ",";
            }
            result = result + Integer.toString(playerID);
            return result;
        }
    }

    private void setRandomFoodPosition (GameEntity food) {
        int foodYPos;
        int foodXPos;
        do {
            foodXPos = (int) (Math.random() * Def.MAP_SIZE);
        } while (foodXPos == players.get(0).getXPos() || foodXPos == players.get(1).getXPos());

        do {
            foodYPos = (int) (Math.random() * Def.MAP_SIZE);
        } while (foodYPos == players.get(0).getYPos() || foodYPos == players.get(1).getYPos());

        food.setXPos(foodXPos);
        food.setYPos(foodYPos);
    }
}
