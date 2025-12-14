import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Random;
import javax.swing.*;

public class PackMan extends JPanel implements ActionListener, KeyListener {

    class Block {
        int x, y, width, height;
        Image image;

        int startX, startY;
        char direction = 'U';
        int velocityX = 0;
        int velocityY = 0;

        Block(Image image, int x, int y, int height, int width) {
            this.image = image;
            this.x = x;
            this.y = y;
            this.height = height;
            this.width = width;
            this.startX = x;
            this.startY = y;
        }

        void updateDirection(char newDirection) {
            char prev = direction;
            direction = newDirection;
            updateVelocity();

            x += velocityX;
            y += velocityY;

            for (Block wall : walls) {
                if (collision(this, wall)) {
                    x -= velocityX;
                    y -= velocityY;
                    direction = prev;
                    updateVelocity();
                    break;
                }
            }
        }

        void updateVelocity() {
            velocityX = velocityY = 0;
            if (direction == 'U') velocityY = -tileSize / 4;
            if (direction == 'D') velocityY = tileSize / 4;
            if (direction == 'L') velocityX = -tileSize / 4;
            if (direction == 'R') velocityX = tileSize / 4;
        }

        void reset() {
            x = startX;
            y = startY;
            velocityX = velocityY = 0;
        }
    }

    private final int rowCount = 21;
    private final int columnCount = 19;
    private final int tileSize = 32;
    private final int boardWidth = columnCount * tileSize;
    private final int boardHeight = rowCount * tileSize;

    private Image wallImage, blueGhostImage, orangeGhostImage, pinkGhostImage, redGhostImage;
    private Image pacmanUpImage, pacmanDownImage, pacmanLeftImage, pacmanRightImage;

    private final String[] tileMap = {
            "XXXXXXXXXXXXXXXXXXX",
            "X        X        X",
            "X XX XXX X XXX XX X",
            "X                 X",
            "X XX X XXXXX X XX X",
            "X    X       X    X",
            "XXXX XXXX XXXX XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXrXX X XXXX",
            "X       bpo       X",
            "XXXX X XXXXX X XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXXXX X XXXX",
            "X        X        X",
            "X XX XXX X XXX XX X",
            "X  X     P     X  X",
            "XX X X XXXXX X X XX",
            "X    X   X   X    X",
            "X XXXXXX X XXXXXX X",
            "X                 X",
            "XXXXXXXXXXXXXXXXXXX"
    };

    HashSet<Block> walls, foods, ghosts;
    Block pacman;

    Timer gameLoop;
    char[] directions = {'U', 'D', 'L', 'R'};
    Random random = new Random();

    int score = 0;
    int lives = 3;
    boolean gameOver = false;

    PackMan() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        wallImage = new ImageIcon(getClass().getResource("./wall.png")).getImage();
        blueGhostImage = new ImageIcon(getClass().getResource("./blueGhost.png")).getImage();
        orangeGhostImage = new ImageIcon(getClass().getResource("./orangeGhost.png")).getImage();
        pinkGhostImage = new ImageIcon(getClass().getResource("./pinkGhost.png")).getImage();
        redGhostImage = new ImageIcon(getClass().getResource("./redGhost.png")).getImage();

        pacmanUpImage = new ImageIcon(getClass().getResource("./pacmanUp.png")).getImage();
        pacmanDownImage = new ImageIcon(getClass().getResource("./pacmanDown.png")).getImage();
        pacmanLeftImage = new ImageIcon(getClass().getResource("./pacmanLeft.png")).getImage();
        pacmanRightImage = new ImageIcon(getClass().getResource("./pacmanRight.png")).getImage();

        loadMap();

        for (Block ghost : ghosts) {
            ghost.updateDirection(directions[random.nextInt(4)]);
        }

        gameLoop = new Timer(50, this);
        gameLoop.start();
    }

    public void loadMap() {
        walls = new HashSet<>();
        foods = new HashSet<>();
        ghosts = new HashSet<>();

        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < columnCount; c++) {
                char ch = tileMap[r].charAt(c);
                int x = c * tileSize;
                int y = r * tileSize;

                if (ch == 'X') walls.add(new Block(wallImage, x, y, tileSize, tileSize));
                else if (ch == 'b') ghosts.add(new Block(blueGhostImage, x, y, tileSize, tileSize));
                else if (ch == 'o') ghosts.add(new Block(orangeGhostImage, x, y, tileSize, tileSize));
                else if (ch == 'p') ghosts.add(new Block(pinkGhostImage, x, y, tileSize, tileSize));
                else if (ch == 'r') ghosts.add(new Block(redGhostImage, x, y, tileSize, tileSize));
                else if (ch == 'P') pacman = new Block(pacmanRightImage, x, y, tileSize, tileSize);
                else if (ch == ' ') foods.add(new Block(null, x + 14, y + 14, 4, 4));
            }
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.drawImage(pacman.image, pacman.x, pacman.y, pacman.width, pacman.height, null);

        for (Block ghost : ghosts)
            g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);

        for (Block wall : walls)
            g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);

        g.setColor(Color.WHITE);
        for (Block food : foods)
            g.fillRect(food.x, food.y, food.width, food.height);

        g.setFont(new Font("Arial", Font.PLAIN, 18));
        if (gameOver)
            g.drawString("Game Over  Score: " + score, tileSize / 2, tileSize / 2);
        else
            g.drawString("x" + lives + "  Score: " + score, tileSize / 2, tileSize / 2);
    }

    public void move() {
        pacman.x += pacman.velocityX;
        pacman.y += pacman.velocityY;

        for (Block wall : walls) {
            if (collision(pacman, wall)) {
                pacman.x -= pacman.velocityX;
                pacman.y -= pacman.velocityY;
                pacman.velocityX = pacman.velocityY = 0;
                break;
            }
        }

        for (Block ghost : ghosts) {
            ghost.x += ghost.velocityX;
            ghost.y += ghost.velocityY;

            for (Block wall : walls) {
                if (collision(ghost, wall)) {
                    ghost.x -= ghost.velocityX;
                    ghost.y -= ghost.velocityY;
                    ghost.updateDirection(directions[random.nextInt(4)]);
                    break;
                }
            }

            if (collision(ghost, pacman)) {
                lives--;
                if (lives == 0) {
                    gameOver = true;
                    return;
                }
                resetPositions();
            }
        }

        foods.removeIf(food -> {
            if (collision(pacman, food)) {
                score += 10;
                return true;
            }
            return false;
        });

        if (foods.isEmpty()) {
            loadMap();
            resetPositions();
        }
    }

    public boolean collision(Block a, Block b) {
        return a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    public void resetPositions() {
        pacman.reset();
        for (Block ghost : ghosts) {
            ghost.reset();
            ghost.updateDirection(directions[random.nextInt(4)]);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            move();
            repaint();
        }
    }

    @Override public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP) pacman.updateDirection('U');
        if (e.getKeyCode() == KeyEvent.VK_DOWN) pacman.updateDirection('D');
        if (e.getKeyCode() == KeyEvent.VK_LEFT) pacman.updateDirection('L');
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) pacman.updateDirection('R');

        if (pacman.direction == 'U') pacman.image = pacmanUpImage;
        if (pacman.direction == 'D') pacman.image = pacmanDownImage;
        if (pacman.direction == 'L') pacman.image = pacmanLeftImage;
        if (pacman.direction == 'R') pacman.image = pacmanRightImage;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (gameOver && e.getKeyCode() == KeyEvent.VK_ENTER) {
            lives = 3;
            score = 0;
            gameOver = false;
            loadMap();
            resetPositions();
        }
    }
}
