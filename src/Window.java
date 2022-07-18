import javax.swing.JFrame;

public class Window {
    public static void main(String[] args) {
        JFrame frame = new JFrame();

        frame.setSize(Def.WIDTH, Def.HEIGHT);
        frame.setTitle("CMPT371 - Group 10");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        GameController component = new GameController();
        frame.add(component);
        frame.addKeyListener(component);

        frame.setVisible(true);

        component.start();
        frame.setVisible(false);
        frame.dispose();
    }
}