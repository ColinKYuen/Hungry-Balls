import javax.swing.JComponent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;


public class GameController extends JComponent {
    private final int FRAMES_PER_SECOND = 8;
    private final long FRAME_DURATION = 1000 / FRAMES_PER_SECOND;
    private boolean isGameRunning= true;
    private int winningPlayerID = -1;
    private long gameStartTime;
    private final ClientList clientList;
    private List<Player> players = new ArrayList<>();
    private List<GameEntity> foods = new ArrayList<>();
    private Boolean[][] map = new Boolean[Def.MAP_SIZE][Def.MAP_SIZE];
    private ReentrantLock lock = new ReentrantLock();

    private void quit() {
        isGameRunning = false;
    }

    public GameController(ClientList clientList) {
        for (int i = 0; i < Def.MAP_SIZE; i++) {
            for (int j = 0; j < Def.MAP_SIZE; j++) {
                map[i][j] = true;
            }
        }
        map[Def.P1_Y_INITIAL_POS][Def.P1_X_INITIAL_POS] = false;
        map[Def.P2_Y_INITIAL_POS][Def.P2_X_INITIAL_POS] = false;
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
        final int x = player.getXPos();
        final int y = player.getYPos();
        Boolean result = false;
        try {
            if (lock.tryLock(500, TimeUnit.MILLISECONDS)) {
                switch (direction){
                    case North:
                        if(y > 0){
                            result = map[y - 1][x];
                            map[y - 1][x] = false;
                        }
                        break;
                    case South:
                        if(y < Def.MAP_SIZE - 1){
                            result = map[y + 1][x];
                            map[y + 1][x] = false;
                        }
                        break;
                    case East:
                        if(x < Def.MAP_SIZE - 1){
                            result = map[y][x + 1];
                            map[y][x + 1] = false;
                        }
                        break;
                    case West:
                        if(x > 0){
                            result = map[y][x - 1];
                            map[y][x - 1] = false;
                        }
                        break;
                    case Quit:
                        result = true;
                        break;
                    default:
                        result = false;
                }
            }
        } catch (Exception e) {
            // Couldn't obtain lock, ignore
        } finally {
            try {
                lock.unlock();
            } catch (Exception e) {
                // Didn't have lock, ignore
            }
        }
        return result;
    }

    private long getElapsedTime() {
        return System.currentTimeMillis() - gameStartTime;
    }

    private void updateGame() {
        while (!clientList.isAllPlayersUpdated()) {} // Block until server receives update from all players

        // Updating player movement
        for (Player p : players) {
            final int prevX = p.getXPos();
            final int prevY = p.getYPos();t
            try {
                if (lock.tryLock(500, TimeUnit.MILLISECONDS)) { 
                    switch (p.getNextDirection()) {
                        case North:
                            p.setYPos(prevY - 1);
                            break;

                        case South:
                            p.setYPos(prevY + 1);
                            break;

                        case East:
                            p.setXPos(prevX + 1);
                            break;

                        case West:
                            p.setXPos(prevX - 1);
                            break;

                        case Quit:
                            isGameRunning = false;
                            winningPlayerID = p.getPlayerID()==0? 1 : 0;
                    }
                    final int newX = p.getXPos();
                    final int newY = p.getYPos();
                    if (newY != prevY || newX != prevX) {
                        map[prevY][prevX] = true;
                    }
                }
            } catch (Exception e) {
                // Couldn't obtain lock, ignore
            } finally {
                try {
                    lock.unlock();
                } catch (Exception e) {
                    // Didn't have lock, ignore
                }
            }
        }

        //TODO: Update score
        //TODO: Update food location if food is eaten, you can use setRandomFoodPosition()
        //TODO: If score == 5, end game by setting isGameRunning = false and winningPlayerID = *insert playerID*
        for (Player p : players) {
            final int playerX = p.getXPos();
            final int playerY = p.getYPos();
            for (GameEntity f : foods) {
                if (playerX == f.getXPos() && playerY== f.getYPos()) {
                    setRandomFoodPosition(f);
                    p.setScore(p.getScore() + 1);
                    break;
                }
            }
            if (p.getScore() == Def.WINNING_SCORE) {
                isGameRunning = false;
                winningPlayerID = p.getPlayerID();
            }
        }
        
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
