import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JPanel;
import javax.swing.Timer;

public class GameField extends JPanel implements ActionListener {
    private final Dimension dimension = new Dimension(800, 800);

    private final Font smallFont = new Font("Arial", Font.BOLD, 14);

    private boolean inGame = false;

    private boolean dying = false;

    private final int BLOCK_SIZE = 24;

    private final int N_BLOCKS = 15;

    private final int SCREEN_SIZE = N_BLOCKS * BLOCK_SIZE;

    private final int MAX_GHOSTS = 12;

    private int ghostsCount = 6;
    private int lives, score;

    private final int[] dx;

    private final int[] dy;

    private final int[] ghostX;

    private final int[] ghostY;

    private final int[] ghostDx;

    private final int[] ghostDy;

    private final int[] ghostSpeed;

    private final PacmanImages images;

    private int pacman_x, pacman_y, pacmand_x, pacmand_y;

    private int req_dx, req_dy;

    private final short[] levelData = {
            19, 26, 18, 26, 26, 18, 18, 26, 18, 18, 26, 26, 18, 26, 22,
            21, 0,  21, 0,  0,  17, 20, 0,  17, 20, 0,  0,  21, 0,  21,
            17, 26, 16, 26, 18, 24, 24, 26, 24, 24, 18, 26, 16, 26, 20,
            21, 0,  21, 0,  21, 0,  0,  0,  0,  0,  21, 0,  21, 0,  21,
            25, 26, 20, 0,  25, 26, 22, 0,  19, 26, 28, 0,  17, 26, 28,
            0,  0,  21, 0,  0,  0,  21, 0,  21, 0,  0,  0,  21, 0,  0,
            0,  0,  21, 0,  19, 26, 24, 18, 24, 26, 22, 0,  21, 0,  0,
            27, 26, 20, 0,  21, 0,  0,  0,  0,  0,  21, 0,  17, 26, 30,
            0,  0,  17, 26, 20, 0,  0,  0,  0,  0,  17, 26, 20, 0,  0,
            0,  0,  21, 0,  17, 26, 26, 26, 26, 26, 20, 0,  21, 0,  0,
            19, 26, 20, 0,  21, 0,  0,  0,  0,  0,  21, 0,  17, 26, 22,
            21, 0,  25, 18, 16, 18, 22, 0,  19, 18, 16, 18, 28, 0,  21,
            21, 0,  0,  17, 24, 24, 20, 0,  17, 24, 24, 20, 0,  0,  21,
            17, 18, 18, 20, 0,  0,  17, 18, 20, 0,  0, 17, 18, 18, 20,
            25, 24, 24, 24, 26, 26, 24, 24, 24, 26, 26, 24, 24, 24, 28
    };

    private final int[] validSpeeds = {1, 2, 3, 4, 6, 8};

    private int currentSpeed = 3;

    private final short[] screenData;

    private final Timer timer;

    private int finalScore = 148;

    public GameField() {
        this.images = new PacmanImages();
        this.screenData = new short[N_BLOCKS * N_BLOCKS];
        this.ghostX = new int[MAX_GHOSTS];
        this.ghostDx = new int[MAX_GHOSTS];
        this.ghostY = new int[MAX_GHOSTS];
        this.ghostDy = new int[MAX_GHOSTS];
        this.ghostSpeed = new int[MAX_GHOSTS];
        this.dx = new int[4];
        this.dy = new int[4];
        this.timer = new Timer(40, this);
        this.timer.start();

        addKeyListener(new TAdapter());
        setFocusable(true);
        initGame();
    }

    private void initGame() {
        this.lives = 3;
        this.score = 0;
        this.ghostsCount = 6;
        this.currentSpeed = 3;

        System.arraycopy(levelData, 0, screenData, 0, N_BLOCKS * N_BLOCKS);
        initLevel();
    }

    private void initLevel() {
        int dx = 1;
        int random;

        for (int i = 0; i < ghostsCount; i++) {
            ghostY[i] = 7 * BLOCK_SIZE; //start position
            ghostX[i] = 7 * BLOCK_SIZE;
            ghostDy[i] = 0;
            ghostDx[i] = dx;
            dx = -dx;
            random = (int) (Math.random() * (currentSpeed + 1));

            if (random > currentSpeed) {
                random = currentSpeed;
            }

            ghostSpeed[i] = validSpeeds[random];
        }

        pacman_x = 7 * BLOCK_SIZE;  //start position
        pacman_y = 14 * BLOCK_SIZE;
        pacmand_x = 0;	//reset direction move
        pacmand_y = 0;
        req_dx = 0;		// reset direction controls
        req_dy = 0;
        dying = false;
    }

    private void playGame(Graphics2D g2d) {
        if (dying) {
            death();
        } else {
            movePacman();
            drawPacman(g2d);
            moveGhosts(g2d);
            checkMaze();
        }
    }

    private void showIntroScreen(Graphics2D g2d) {

        String start = "Press SPACE to start";
        g2d.setColor(Color.yellow);
        g2d.drawString(start, (SCREEN_SIZE)/4, 150);
    }

    private void drawScore(Graphics2D g) {
        g.setFont(smallFont);
        g.setColor(new Color(5, 181, 79));
        String s = "Score: " + score;
        g.drawString(s, SCREEN_SIZE / 2 + 96, SCREEN_SIZE + 16);

        for (int i = 0; i < lives; i++) {
            g.drawImage(images.getHeart(), i * 28 + 8, SCREEN_SIZE + 1, this);
        }
    }

    private void checkMaze() {
        int i = 0;
        boolean finished = true;
        while (i < N_BLOCKS * N_BLOCKS && finished) {
            if ((screenData[i]) != 0) {
                finished = false;
            }
            i++;
        }

        if (score == finalScore) {
            score += 50;
            finalScore +=198;
            if (ghostsCount < MAX_GHOSTS) {
                ghostsCount++;
            }

            int maxSpeed = 6;
            if (currentSpeed < maxSpeed) {
                currentSpeed++;
            }

            initLevel();
        }
    }

    private void death() {
        lives--;
        if (lives == 0) {
            inGame = false;
        }
        initLevel();
    }

    private void moveGhosts(Graphics2D g2d) {
        int pos;
        int count;

        for (int i = 0; i < ghostsCount; i++) {
            if (ghostX[i] % BLOCK_SIZE == 0 && ghostY[i] % BLOCK_SIZE == 0) {
                pos = ghostX[i] / BLOCK_SIZE + N_BLOCKS * (int) (ghostY[i] / BLOCK_SIZE);

                count = 0;

                if ((screenData[pos] & 1) == 0 && ghostDx[i] != 1) {
                    dx[count] = -1;
                    dy[count] = 0;
                    count++;
                }

                if ((screenData[pos] & 2) == 0 && ghostDy[i] != 1) {
                    dx[count] = 0;
                    dy[count] = -1;
                    count++;
                }

                if ((screenData[pos] & 4) == 0 && ghostDx[i] != -1) {
                    dx[count] = 1;
                    dy[count] = 0;
                    count++;
                }

                if ((screenData[pos] & 8) == 0 && ghostDy[i] != -1) {
                    dx[count] = 0;
                    dy[count] = 1;
                    count++;
                }

                if (count == 0) {

                    if ((screenData[pos] & 15) == 15) {
                        ghostDx[i] = 0;
                        ghostDy[i] = 0;
                    } else {
                        ghostDx[i] = -ghostDx[i];
                        ghostDy[i] = -ghostDy[i];
                    }

                } else {

                    count = (int) (Math.random() * count);

                    if (count > 3) {
                        count = 3;
                    }

                    ghostDx[i] = dx[count];
                    ghostDy[i] = dy[count];
                }

            }

            ghostX[i] = ghostX[i] + (ghostDx[i] * ghostSpeed[i]);
            ghostY[i] = ghostY[i] + (ghostDy[i] * ghostSpeed[i]);
            drawGhost(g2d, ghostX[i] + 1, ghostY[i] + 1);

            if (pacman_x > (ghostX[i] - 12) && pacman_x < (ghostX[i] + 12)
                    && pacman_y > (ghostY[i] - 12) && pacman_y < (ghostY[i] + 12)
                    && inGame) {

                dying = true;
            }
        }
    }

    private void drawGhost(Graphics2D g2d, int x, int y) {
        g2d.drawImage(images.getGhost(), x, y, this);
    }

    private void movePacman() {
        int pos;
        short ch;

        if (pacman_x % BLOCK_SIZE == 0 && pacman_y % BLOCK_SIZE == 0) {
            pos = pacman_x / BLOCK_SIZE + N_BLOCKS * (int) (pacman_y / BLOCK_SIZE);
            ch = screenData[pos];

            if ((ch & 16) != 0) {
                screenData[pos] = (short) (ch & 15);
                score++;
            }

            if (req_dx != 0 || req_dy != 0) {
                if (!((req_dx == -1 && req_dy == 0 && (ch & 1) != 0)
                        || (req_dx == 1 && req_dy == 0 && (ch & 4) != 0)
                        || (req_dx == 0 && req_dy == -1 && (ch & 2) != 0)
                        || (req_dx == 0 && req_dy == 1 && (ch & 8) != 0))) {
                    pacmand_x = req_dx;
                    pacmand_y = req_dy;
                }
            }

            if ((pacmand_x == -1 && pacmand_y == 0 && (ch & 1) != 0)
                    || (pacmand_x == 1 && pacmand_y == 0 && (ch & 4) != 0)
                    || (pacmand_x == 0 && pacmand_y == -1 && (ch & 2) != 0)
                    || (pacmand_x == 0 && pacmand_y == 1 && (ch & 8) != 0)) {
                pacmand_x = 0;
                pacmand_y = 0;
            }
        }
        int PACMAN_SPEED = 6;
        pacman_x = pacman_x + PACMAN_SPEED * pacmand_x;
        pacman_y = pacman_y + PACMAN_SPEED * pacmand_y;
    }

    private void drawPacman(Graphics2D g2d) {
        if (req_dx == -1) {
            g2d.drawImage(images.getLeft(), pacman_x + 1, pacman_y + 1, this);
        } else if (req_dx == 1) {
            g2d.drawImage(images.getRight(), pacman_x + 1, pacman_y + 1, this);
        } else if (req_dy == -1) {
            g2d.drawImage(images.getUp(), pacman_x + 1, pacman_y + 1, this);
        } else {
            g2d.drawImage(images.getDown(), pacman_x + 1, pacman_y + 1, this);
        }
    }

    private void drawMaze(Graphics2D g2d) {
        short i = 0;
        int x, y;
        for (y = 0; y < SCREEN_SIZE; y += BLOCK_SIZE) {
            for (x = 0; x < SCREEN_SIZE; x += BLOCK_SIZE) {
                g2d.setColor(new Color(20,72,169));
                g2d.setStroke(new BasicStroke(3));

                if ((screenData[i] & 1) != 0) {
                    g2d.drawLine(x, y, x, y + BLOCK_SIZE - 1);
                }

                if ((screenData[i] & 2) != 0) {
                    g2d.drawLine(x, y, x + BLOCK_SIZE - 1, y);
                }

                if ((screenData[i] & 4) != 0) {
                    g2d.drawLine(x + BLOCK_SIZE - 1, y, x + BLOCK_SIZE - 1,
                            y + BLOCK_SIZE - 1);
                }

                if ((screenData[i] & 8) != 0) {
                    g2d.drawLine(x, y + BLOCK_SIZE - 1, x + BLOCK_SIZE - 1,
                            y + BLOCK_SIZE - 1);
                }

                if ((screenData[i] & 16) != 0) {
                    g2d.setColor(new Color(255,255,255));
                    g2d.fillOval(x + 10, y + 10, 6, 6);
                }

                i++;
            }
        }
    }


    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(Color.black);
        g2d.fillRect(0, 0, dimension.width, dimension.height);

        drawMaze(g2d);
        drawScore(g2d);

        if (inGame) {
            playGame(g2d);
        } else {
            showIntroScreen(g2d);
        }

        Toolkit.getDefaultToolkit().sync();
        g2d.dispose();
    }


    //controls
    class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            int key = e.getKeyCode();

            if (inGame) {
                if (key == KeyEvent.VK_LEFT) {
                    req_dx = -1;
                    req_dy = 0;
                } else if (key == KeyEvent.VK_RIGHT) {
                    req_dx = 1;
                    req_dy = 0;
                } else if (key == KeyEvent.VK_UP) {
                    req_dx = 0;
                    req_dy = -1;
                } else if (key == KeyEvent.VK_DOWN) {
                    req_dx = 0;
                    req_dy = 1;
                } else if (key == KeyEvent.VK_ESCAPE && timer.isRunning()) {
                    inGame = false;
                }
            } else {
                if (key == KeyEvent.VK_SPACE) {
                    inGame = true;
                    initGame();
                }
            }
        }
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }

}