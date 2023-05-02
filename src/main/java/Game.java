import maze.MazeGenerator;

import javax.swing.JFrame;
import java.awt.*;

public class Game extends JFrame {
    public Game() throws HeadlessException {
        add(new GameField());
    }

    public static void main(String[] args) {
        Game pac = new Game();
        pac.setVisible(true);
        pac.setTitle("Pacman");
        pac.setSize(380, 420);
        pac.setDefaultCloseOperation(EXIT_ON_CLOSE);
        pac.setLocationRelativeTo(null);

    }
}
