import javax.swing.JComponent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/*
The main logic and communication hub of the app.

Handles initialization, validation, and updating of game state and rules.

 */
public class GameController extends JComponent {
    private final long FRAME_DURATION = 1000 / Def.FRAMES_PER_SECOND;
    private boolean isGameRunning = true;
    private int winningPlayerID = -1;
    private long gameStartTime;
    private final ClientList clientList;
    private final List<Player> players = new ArrayList<>();
    private final List<GameEntity> foods = new ArrayList<>();
    private final boolean[][] map = new boolean[Def.MAP_SIZE][Def.MAP_SIZE];
    private final ReentrantLock lock = new ReentrantLock();

    // Initialize the game state.
    public GameController(ClientList clientList) {
        for (int i = 0; i < Def.MAP_SIZE; i++) {
            for (int j = 0; j < Def.MAP_SIZE; j++) {
                map[i][j] = true;
            }
        }

        // Player locations are false tiles or illegal moves.
        // Set up the initial player locations as false tiles.
        map[Def.P1_Y_INITIAL_POS][Def.P1_X_INITIAL_POS] = false;
        map[Def.P2_Y_INITIAL_POS][Def.P2_X_INITIAL_POS] = false;
        this.clientList = clientList;

        players.add(new Player(Def.P1_X_INITIAL_POS, Def.P1_Y_INITIAL_POS, Def.P1_COLOR, 0));
        players.add(new Player(Def.P2_X_INITIAL_POS, Def.P2_Y_INITIAL_POS, Def.P2_COLOR, 1));

        // Place food randomly, and make sure it isn't under a player to begin with.
        int foodYPos;
        int foodXPos;
        do {
            foodXPos = (int) (Math.random() * Def.MAP_SIZE);
        } while (foodXPos == players.get(0).getXPos() || foodXPos == players.get(1).getXPos());

        do {
            foodYPos = (int) (Math.random() * Def.MAP_SIZE);
        } while (foodYPos == players.get(0).getYPos() || foodYPos == players.get(1).getYPos());

        foods.add(new GameEntity(foodXPos, foodYPos, Def.F_COLOR));
    }

    // Run the app.
    // Threads sleep for the 'FRAME_DURATION' between each game tick, in effect defining a 'tick rate'
    // or 'frames per second' for the game.
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

    // Change the direction of a player's movement based on a key input, as long as that movement is 'valid'.
    public void setPlayerNextDirection(int playerID, Direction direction) {
        Player player = players.get(playerID);
        if (isMovementValid(player, direction)) {
            player.setNextDirection(direction);
        } else {
            player.setNextDirection(Direction.Stop);
        }
    }

    // See if a desired movement is 'valid'.
    // A valid movement:
    // 1) Does not escape the board
    // 2) Does not collide with another player
    private boolean isMovementValid(Player player, Direction direction) {
        final int x = player.getXPos();
        final int y = player.getYPos();
        boolean result = false;
        try {
            // The game board is a thread-locked object which only one player thread can modify at a time.
            // Upon attempting to check if movement is valid, attempt to acquire a lock on the board.
            // If it succeeds, i.e. the board is not currently held by another server-side player thread:
            // 1) check for out-of-range coordinates to make sure a move doesn't escape the board
            // 2) check if a tile is 'FALSE' in the occupancy map; if so, it is occupied by another player.
            // If the move is within bounds and unoccupied, occupy the cell from the move and return TRUE.
            if (lock.tryLock(500, TimeUnit.MILLISECONDS)) {
                switch (direction) {
                    case North:
                        if (y > 0 && map[y - 1][x]) {
                            map[y - 1][x] = false;
                            result = true;
                        }
                        break;
                    case South:
                        if (y < Def.MAP_SIZE - 1 && map[y + 1][x]) {
                            map[y + 1][x] = false;
                            result = true;
                        }
                        break;
                    case East:
                        if (x < Def.MAP_SIZE - 1 && map[y][x + 1]) {
                            map[y][x + 1] = false;
                            result = true;
                        }
                        break;
                    case West:
                        if (x > 0 && map[y][x - 1]) {
                            map[y][x - 1] = false;
                            result = true;
                        }
                        break;
                    case Quit:
                        result = true;
                        break;
                }
            }
        } catch (Exception e) {
            // Couldn't obtain lock, ignore
        } finally {
            try {
                // After finding a legal move, unlock the board for the other threads to use.
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

        // Updating player movement.
        // setYPos and setXPos perform implicit bounds checking, so it is not needed here.
        for (Player p : players) {
            final int prevX = p.getXPos();
            final int prevY = p.getYPos();
            try {
                // The game board is a thread-locked object which only one player thread can modify at a time.
                // Upon attempting to make a move, attempt to acquire a lock on the board.
                // If it succeeds, i.e. the board is not currently held by another server-side player thread,
                // Move the player in the requested direction.
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
                            winningPlayerID = p.getPlayerID() == 0 ? 1 : 0;
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
                    // After moving, unlock the board object for other players.
                    lock.unlock();
                } catch (Exception e) {
                    // Didn't have lock, ignore
                }
            }
        }

        // Check to see if players have eaten a food (if food and player positions intersect on the board).
        // If so, increment the intersecting player's score and create a new food.
        for (Player p : players) {
            final int playerX = p.getXPos();
            final int playerY = p.getYPos();
            for (GameEntity f : foods) {
                if (playerX == f.getXPos() && playerY == f.getYPos()) {
                    setRandomFoodPosition(f);
                    p.setScore(p.getScore() + 1);
                    break;
                }
            }

            // End the game when a player reaches WINNING_SCORE.
            if (p.getScore() == Def.WINNING_SCORE) {
                isGameRunning = false;
                winningPlayerID = p.getPlayerID();
            }
        }

        // Setting isClientUpdated back to 'false' to prepare for next update session
        for (int i = 0; i < Def.NUM_OF_PLAYERS; i++) {
            clientList.setClientUpdated(i, false);
        }
    }

    // Translate the game state into a string of chars that can easily be sent from socket to socket and parsed by the client.
    public String generateGameStateString(int playerID) {

        // Return simple Victory or Loss (for the host) chars if the game has ended.
        if (!isGameRunning) {
            if (playerID == winningPlayerID) {
                return "V";
            } else {
                return "L";
            }
        } else {

            // Otherwise, encode the entire game state into a string.
            StringBuilder result = new StringBuilder();
            for (Player p : players) {
                // Append each player's coordinates and score.
                result.append(p.getXPos()).append(",").append(p.getYPos()).append(",").append(p.getScore()).append(",");
            }
            for (GameEntity f : foods) {
                // Append the coordinates of the food.
                result.append(f.getXPos()).append(",").append(f.getYPos()).append(",");
            }
            // Append the current playerID.
            result.append(playerID);
            return result.toString();
        }
    }

    // Put a piece of Food on a random tile on the board that doesn't overlap with a player.
    private void setRandomFoodPosition(GameEntity food) {
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
