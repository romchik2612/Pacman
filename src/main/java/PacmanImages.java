import javax.swing.*;
import java.awt.*;

public class PacmanImages {
    private final Image heart;

    private final Image ghost;

    private final Image up;

    private final Image down;

    private final Image left;

    private final Image right;

    public PacmanImages() {
        down = new ImageIcon("src/main/resources/images/down.gif").getImage();
        up = new ImageIcon("src/main/resources/images/up.gif").getImage();
        left = new ImageIcon("src/main/resources/images/left.gif").getImage();
        right = new ImageIcon("src/main/resources/images/right.gif").getImage();
        ghost = new ImageIcon("src/main/resources/images/ghost.gif").getImage();
        heart = new ImageIcon("src/main/resources/images/heart.png").getImage();
    }

    public Image getHeart() {
        return heart;
    }

    public Image getGhost() {
        return ghost;
    }

    public Image getUp() {
        return up;
    }

    public Image getDown() {
        return down;
    }

    public Image getLeft() {
        return left;
    }

    public Image getRight() {
        return right;
    }
}
