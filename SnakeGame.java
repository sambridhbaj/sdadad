import javax.swing.*;
import java.awt.*; 
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;

public class SnakeGame extends JPanel implements ActionListener, KeyListener {

    
    private static final int WIDTH = 400; 
    private static final int HEIGHT = 400;
    private static final int GRID_SIZE = 20;
    private static final int GAME_SPEED = 100; 
    
    private ArrayList<Point> snake;
    private Point food;
    private String direction; 
    private boolean gameRunning;
    private Timer timer;
    private int score;
    private Random random;
    private boolean soundEnabled = true; 

        private void playBiteSound() {
        if (soundEnabled) {
            Toolkit.getDefaultToolkit().beep(); 
        }
    }

    private void playGameOverSound() {
        if (soundEnabled) {
            Toolkit.getDefaultToolkit().beep();
        }
    }


    
    public SnakeGame() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(new Color(0x0f172a)); 
        setFocusable(true);
        addKeyListener(this);
        initGame();
        timer = new Timer(GAME_SPEED, this);
    }

    
    private void initGame() {
        snake = new ArrayList<>();
        snake.add(new Point(10, 10)); 
        food = new Point(15, 15);    
        direction = "right";        
        gameRunning = false;
        score = 0;
        random = new Random();
        if (timer != null) { 
            timer.stop();
        }
    }

    
    public void startGame() {
        if (!gameRunning) {
            gameRunning = true;
            timer.start();
            requestFocusInWindow(); 
        }
    }

    
    public void pauseGame() {
        if (gameRunning) {
            gameRunning = false;
            timer.stop();
        }
    }

    
    private void generateFood() {
        int maxX = WIDTH / GRID_SIZE;
        int maxY = HEIGHT / GRID_SIZE;
        food = new Point(random.nextInt(maxX), random.nextInt(maxY));
        
        while (isFoodInsideSnake()) {
            food = new Point(random.nextInt(maxX), random.nextInt(maxY));
        }
    }

    private boolean isFoodInsideSnake() {
        for (Point segment : snake) {
            if (segment.equals(food)) {
                return true;
            }
        }
        return false;
    }

    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawGameBoard(g);
        drawSnake(g);
        drawFood(g);
        drawScore(g);
    }

    private void drawGameBoard(Graphics g) {
        g.setColor(new Color(0x1d4ed8)); 
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.setColor(new Color(0x60a5fa)); 
        g.drawRect(0, 0, WIDTH - 1, HEIGHT - 1); 
    }

    private void drawSnake(Graphics g) {
        for (int i = 0; i < snake.size(); i++) {
            Point segment = snake.get(i);
            
            Color startColor = new Color(
                    (int) (180 + i * 30) % 360, 100, 65); 
            Color endColor = new Color(
                    (int) (200 + i * 30) % 360, 100, 85);

            GradientPaint gradient = new GradientPaint(
                    segment.x * GRID_SIZE, segment.y * GRID_SIZE, startColor,
                    (segment.x + 1) * GRID_SIZE, (segment.y + 1) * GRID_SIZE, endColor);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setPaint(gradient);

            g.setColor(new Color(0x6ee7b7)); 
            g.fillRect(segment.x * GRID_SIZE, segment.y * GRID_SIZE, GRID_SIZE, GRID_SIZE);
            g.setColor(new Color(0x6ee7b7));
            g.drawRect(segment.x * GRID_SIZE, segment.y * GRID_SIZE, GRID_SIZE, GRID_SIZE);
        }
    }

    private void drawFood(Graphics g) {
        int foodSize = (int) (GRID_SIZE * (1 + 0.15 * Math.sin(System.currentTimeMillis() / 150)));
        g.setColor(new Color(0xf87171)); 
        int x = food.x * GRID_SIZE - (foodSize - GRID_SIZE) / 2;
        int y = food.y * GRID_SIZE - (foodSize - GRID_SIZE) / 2;
        g.fillRect(x, y, foodSize, foodSize);
    }

    private void drawScore(Graphics g) {
        g.setColor(new Color(0xf5f5f5)); 
        g.setFont(new Font("Press Start 2P", Font.PLAIN, 16));
        g.drawString("Score: " + score, 10, 20);
    }

    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameRunning) {
            moveSnake();
            checkCollision();
            checkFood();
            repaint(); 
        }
    }

    private void moveSnake() {
        Point head = new Point(snake.get(0)); 

        
        switch (direction) {
            case "up":
                head.y--;
                break;
            case "down":
                head.y++;
                break;
            case "left":
                head.x--;
                break;
            case "right":
                head.x++;
                break;
        }
        snake.add(0, head); 
        if (head.x == food.x && head.y == food.y) {
            playBiteSound();
            score += 10;
            generateFood(); 
        } else {
            snake.remove(snake.size() - 1); 
        }
    }

    private void checkCollision() {
        Point head = snake.get(0);
       
        if (head.x < 0 || head.x >= WIDTH / GRID_SIZE || head.y < 0 || head.y >= HEIGHT / GRID_SIZE) {
            gameOver();
            return;
        }
        
        for (int i = 1; i < snake.size(); i++) {
            if (head.equals(snake.get(i))) {
                gameOver();
                return;
            }
        }
    }

    private void checkFood() {
        Point head = snake.get(0);
        if (head.x == food.x && head.y == food.y) {
            playBiteSound();
            score += 10;
            generateFood();
        }
    }

    private void gameOver() {
        playGameOverSound();
        gameRunning = false;
        timer.stop();
        JOptionPane.showMessageDialog(this, "Game Over! Score: " + score, "Game Over", JOptionPane.INFORMATION_MESSAGE);
        initGame(); 
        startGame();
    }

    
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (gameRunning) {
            switch (key) {
                case KeyEvent.VK_W:
                case KeyEvent.VK_UP:
                    if (!direction.equals("down")) {
                        direction = "up";
                    }
                    break;
                case KeyEvent.VK_S:
                case KeyEvent.VK_DOWN:
                    if (!direction.equals("up")) {
                        direction = "down";
                    }
                    break;
                case KeyEvent.VK_A:
                case KeyEvent.VK_LEFT:
                    if (!direction.equals("right")) {
                        direction = "left";
                    }
                    break;
                case KeyEvent.VK_D:
                case KeyEvent.VK_RIGHT:
                    if (!direction.equals("left")) {
                        direction = "right";
                    }
                    break;
            }
        }

        if (key == KeyEvent.VK_SPACE) { 
            if (!gameRunning){
                startGame();
            } else {
                pauseGame();
            }
        }
        if (key == KeyEvent.VK_M){
            soundEnabled = !soundEnabled;
        }
    }

    
    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    
    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false); 
        SnakeGame game = new SnakeGame();
        frame.add(game);
        frame.pack();
        frame.setLocationRelativeTo(null); 
        frame.setVisible(true);

        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(createButton("Start", game::startGame));
        buttonPanel.add(createButton("Pause", game::pauseGame));
        buttonPanel.add(createButton("Sound", () -> {
            game.soundEnabled = !game.soundEnabled;
        }));
        frame.add(buttonPanel, BorderLayout.SOUTH); 

        game.startGame(); 
    }

    private static JButton createButton(String text, Runnable action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Press Start 2P", Font.PLAIN, 12));
        button.setBackground(new Color(0x4338ca)); 
        button.setForeground(new Color(0xf5f5f5)); 
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.addActionListener(e -> action.run());
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0x6d28d9)); 
                button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0x4338ca)); 
                button.setCursor(Cursor.getDefaultCursor());
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0x4c1d95));
            }
             public void mouseReleased(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0x6d28d9));
            }
        });
        return button;
    }
}

