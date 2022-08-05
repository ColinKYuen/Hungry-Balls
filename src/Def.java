import java.awt.Color;

public class Def {
    // Window Size
    final static int WINDOW_SIZE = 600;
    final static int WIDTH = WINDOW_SIZE + 16;
    final static int HEIGHT = WINDOW_SIZE + 39;

    // Game Info
    final static int MAP_SIZE = 8;
    final static int FRAMES_PER_SECOND = 8;

    // For Drawing
    final static double G_WIDTH = Math.ceil((double) WINDOW_SIZE / (MAP_SIZE + 1));
    final static double G_GAP = G_WIDTH / 2;

    // Number of players
    final static int NUM_OF_PLAYERS = 2;

    // Player 1 Initial Position and Color (Blue)
    final static int P1_X_INITIAL_POS = 0;
    final static int P1_Y_INITIAL_POS = 0;
    final static Color P1_COLOR = new Color(0, 0, 255);

    // Player 2 Initial Position and Color (Red)
    final static int P2_X_INITIAL_POS = 7;
    final static int P2_Y_INITIAL_POS = 7;
    final static Color P2_COLOR = new Color(255, 0, 0);

    // Food Color (Green)
    final static Color F_COLOR = new Color(0, 255, 0);

    //Winning score
    final static int WINNING_SCORE = 5;
}
