package spaceInvaders.main;

import javax.swing.*;

import core.AbstractGamePanel;
import core.MainWindow;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
//import java.awt.geom.AffineTransform;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;
import javax.sound.sampled.*;

public class SpaceInvaders extends AbstractGamePanel implements ActionListener, KeyListener, MouseListener {
    // Window size
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) System.exit(0);
        if (titleScreen) {
            int code = e.getKeyCode();
            if (code == KeyEvent.VK_UP) {
                titleMenuIndex = (titleMenuIndex - 1 + titleMenuOptions.length) % titleMenuOptions.length;
                repaint();
                return;
            } else if (code == KeyEvent.VK_DOWN) {
                titleMenuIndex = (titleMenuIndex + 1) % titleMenuOptions.length;
                repaint();
                return;
            } else if (code == KeyEvent.VK_ENTER) {
                switch (titleMenuIndex) {
                    case 0: // Start
                        titleScreen = false;
                        mainMenu = true;
                        repaint();
                        break;
                    case 1: // Leaderboard
                        // Show leaderboard screen
                        titleScreen = false;
                        leaderboardScreen = true;
                        leaderboardMenuIndex = 0;
                        repaint();
                        break;
                    case 2: // Help
                        // TODO: Implement help screen
                        break;
                    case 3: // Quit
                    	//stopThemeSong();
                    	stopMusic();
                        mainWindow.returnToMenu();
                        break;
                }
                return;
            }
            return;
        }
        // --- LEADERBOARD SCREEN ---
        if (leaderboardScreen) {
            int code = e.getKeyCode();
            // Only one option: < BACK
            if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_ENTER) {
                leaderboardScreen = false;
                titleScreen = true;
                repaint();
                return;
            }
            return;
        }
        // --- PAUSE TOGGLE ---
        if (!paused && inGame && !mainMenu && !nextRound && !gameOver) {
            if (e.getKeyCode() == KeyEvent.VK_P) { // Use 'P' for pause
                paused = true;
                pauseMenuIndex = 0;
                repaint();
                return;
            }
        } else if (paused) {
            if (e.getKeyCode() == KeyEvent.VK_P) {
                paused = false;
                repaint();
                return;
            }
        }
        if (paused) {
            int code = e.getKeyCode();
            if (code == KeyEvent.VK_UP) {
                pauseMenuIndex = (pauseMenuIndex - 1 + pauseMenuOptions.length) % pauseMenuOptions.length;
                repaint();
                return;
            } else if (code == KeyEvent.VK_DOWN) {
                pauseMenuIndex = (pauseMenuIndex + 1) % pauseMenuOptions.length;
                repaint();
                return;
            } else if (code == KeyEvent.VK_ENTER) {
                switch (pauseMenuIndex) {
                    case 0: // RESUME
                        paused = false;
                        repaint();
                        break;
                    case 1: // RESTART
                        paused = false;
                        resetGame();
                        inGame = true;
                        repaint();
                        break;
                    case 2: // EXIT
                        paused = false;
                        // Go to title screen instead of exiting
                        titleScreen = true;
                        mainMenu = false;
                        inGame = false;
                        gameOver = false;
                        nextRound = false;
                        repaint();
                        break;
                }
                return;
            }
            return;
        }
        if (mainMenu) {
            // Play sound effect on any key press
            playSound("shoot");
            mainMenu = false;
            inGame = true;
            return;
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT) leftPressed = true;
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) rightPressed = true;
        if (e.getKeyCode() == KeyEvent.VK_SPACE) spacePressed = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) leftPressed = false;
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) rightPressed = false;
        if (e.getKeyCode() == KeyEvent.VK_SPACE) spacePressed = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // REMOVE name input handling
    }

    // Colors
    public static final Color WHITE = new Color(255, 255, 255);
    public static final Color GREEN = new Color(78, 255, 87);
    public static final Color YELLOW = new Color(241, 255, 0);
    public static final Color BLUE = new Color(80, 255, 239);
    public static final Color PURPLE = new Color(203, 0, 255);
    public static final Color RED = new Color(237, 28, 36);
    public static final Color ORANGE = new Color(255, 165, 0);

    // Resource paths
    public static final String FONT_PATH = "/SI/fonts/space_invaders.ttf";
    public static final String IMAGE_PATH = "/SI/images/";
    public static final String SOUND_PATH = "/SI/sound/";

    // Game objects
    private Ship player;
    private java.util.List<Bullet> bullets = new ArrayList<>();
    private java.util.List<Bullet> enemyBullets = new ArrayList<>();
    private java.util.List<Enemy> enemies = new ArrayList<>();
    private java.util.List<Blocker> blockers = new ArrayList<>();
    private java.util.List<Mystery> mysteries = new ArrayList<>();
    private java.util.List<Explosion> explosions = new ArrayList<>();
    private java.util.List<Life> lives = new ArrayList<>();

    // Enemy group
    private EnemyGroup enemyGroup;

    // Images
    private Map<String, BufferedImage> images = new HashMap<>();

    // Sounds
    private Map<String, Clip> sounds = new HashMap<>();
    private Clip[] musicNotes = new Clip[4];
    private int noteIndex = 0;

    // Fonts
    private Font gameFont;

    // Game state
    private int score = 0;
    private int livesCount = 3;
    private boolean leftPressed = false, rightPressed = false, spacePressed = false;
    private boolean inGame = false, gameOver = false, mainMenu = true, nextRound = false;
    private int enemyPosition = 65;
    private int enemyMoveDown = 35;
    private int blockersPosition = 450;
    private javax.swing.Timer timer;
    private long lastEnemyShot = 0;
    private long lastPlayerShot = 0;
    private long lastMusicNote = 0;
    private int round = 1;
    private long nextRoundStartTime = 0;
    // --- Invincibility state ---
    private boolean playerInvincible = false;
    private long playerInvincibleStart = 0;
    private final long INVINCIBLE_DURATION = 1000; // 1 second

    // --- Mystery Ship Random Appearance ---
    private long mysteryNextAppearTime = 0;
    private boolean mysteryActive = false;
    private boolean mysteryAppearedThisRound = false;
    private Random random = new Random();

    // --- Title Screen State ---
    private boolean titleScreen = true; // Show title screen first
    private int titleMenuIndex = 0;
    private final String[] titleMenuOptions = {"Start", "Leaderboard", "Help", "Quit"};
    private int titleBgOffset = 0;
    private final int titleBgSpeed = 1;
    private BufferedImage titleScreenBgImg = null;
    private BufferedImage titleScreenLogoImg = null;

    // --- Pause Screen State ---
    private boolean paused = false;
    private int pauseMenuIndex = 0;
    private final String[] pauseMenuOptions = {"RESUME", "RESTART", "EXIT"};

    // --- Leaderboard State ---
    private boolean leaderboardScreen = false;
    private int leaderboardMenuIndex = 0; // Only one option: < BACK
    private int leaderboardScroll = 0; // For scrolling
    private final int LEADERBOARD_ROWS = 10;
    private java.util.List<String> leaderboardNames = new ArrayList<>();
    private java.util.List<Integer> leaderboardScores = new ArrayList<>();

    // --- Game Over Menu State ---
    private boolean gameOverMenu = false;
    private int gameOverMenuIndex = 0;
    private final String[] gameOverMenuOptions = {"RESTART", "LEADERBOARD", "RETURN TO MENU"};
    private boolean enteringName = false;
    private String playerNameInput = "";
    private int playerRank = -1;
    private boolean scoreSaved = false;

    private Clip themeSongClip = null;

    public SpaceInvaders(MainWindow mainWindow) {
    	this.mainWindow = mainWindow;
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        addKeyListener(this);
        loadResources();
        playMusic(11);
        // Load title screen images
        try {
            titleScreenBgImg = ImageIO.read(getClass().getResourceAsStream(IMAGE_PATH + "titlescreenbackground.png"));
            titleScreenLogoImg = ImageIO.read(getClass().getResourceAsStream(IMAGE_PATH + "titlescreen.png"));
        } catch (IOException e) {
            titleScreenBgImg = null;
            titleScreenLogoImg = null;
        }
        // Load theme song
        try {
            themeSongClip = AudioSystem.getClip();
            AudioInputStream ais = AudioSystem.getAudioInputStream(getClass().getResourceAsStream(SOUND_PATH + "themesong.wav"));
            themeSongClip.open(ais);
        } catch (Exception e) {
            themeSongClip = null;
        }
        timer = new javax.swing.Timer(16, this);
        timer.start();
        resetGame();
        // Start in title screen, not mainMenu
        titleScreen = true;
        mainMenu = false;
        // Register mouse listener
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { SpaceInvaders.this.mousePressed(e); }
        });
        System.out.println("spaceinvaders setup done");
    }

    private void loadResources() {
        // Load images
        String[] imgNames = {"ship", "mystery", "enemy1_1", "enemy1_2", "enemy2_1", "enemy2_2", "enemy3_1", "enemy3_2", "explosionblue", "explosiongreen", "explosionpurple", "laser", "enemylaser"};
        for (String name : imgNames) {
        	System.out.println("loading: " + name);
            try {
                images.put(name, ImageIO.read(getClass().getResourceAsStream(IMAGE_PATH + name + ".png")));
            } catch (IOException e) {
                if (!name.equals("background")) System.err.println("Image not found: " + name);
            }
        }
        try {
            images.put("background", ImageIO.read(getClass().getResourceAsStream(IMAGE_PATH + "background.jpg")));
            System.out.println("put bg");
        } catch (IOException e) {
            System.err.println("Background image not found");
        }
        // Load sounds
        String[] soundNames = {"shoot", "shoot2", "invaderkilled", "mysterykilled", "shipexplosion", "mysteryentered"};
        for (String name : soundNames) {
        	System.out.println("sound loading: " + name);
            try {
                sounds.put(name, loadClip(SOUND_PATH + name + ".wav"));
            } catch (Exception e) {
                System.err.println("Sound not found: " + name);
            }
        }
        for (int i = 0; i < 4; i++) {
            try {
                musicNotes[i] = loadClip(SOUND_PATH + i + ".wav");
            } catch (Exception e) {
                System.err.println("Music note not found: " + i);
            }
        }
        // Load font
        try {
            gameFont = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream(FONT_PATH)).deriveFont(24f);
        } catch (Exception e) {
            gameFont = new Font("Arial", Font.BOLD, 24);
        }
        try {
            images.put("gameoverbg", ImageIO.read(getClass().getResourceAsStream(IMAGE_PATH + "titlescreenbackground.png")));
        } catch (IOException e) {
            // fallback handled in paintComponent
        }
    }

    private Clip loadClip(String path) throws Exception {
        AudioInputStream audioIn = AudioSystem.getAudioInputStream(getClass().getResourceAsStream(path));
        Clip clip = AudioSystem.getClip();
        clip.open(audioIn);
        return clip;
    }

    private void resetGame() {
        enemyPosition = 65; // Reset enemy position to original value at the very start
        player = new Ship(WIDTH / 2 - 25, HEIGHT - 60);
        bullets.clear();
        enemyBullets.clear();
        enemies.clear();
        blockers.clear();
        mysteries.clear(); //no mystery ships at start
        explosions.clear();
        lives.clear();
        for (int i = 0; i < 3; i++) {
            lives.add(new Life(WIDTH - 85 + i * 27, 10));
        }
        //Create enemies
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 10; col++) {
                int x = 157 + col * 50;
                int y = enemyPosition + row * 45;
                enemies.add(new Enemy(row, col, x, y));
            }
        }
        enemyGroup = new EnemyGroup(enemies);
        //Create blockers
        for (int b = 0; b < 4; b++) {
            for (int row = 0; row < 4; row++) {
                for (int col = 0; col < 9; col++) {
                    int x = 50 + (200 * b) + (col * 10);
                    int y = blockersPosition + (row * 10);
                    blockers.add(new Blocker(x, y, 10, GREEN));
                }
            }
        }
        score = 0;
        livesCount = 3;
        inGame = false;
        gameOver = false;
        titleScreen = true;
        mainMenu = false;
        round = 1;
        // enemyPosition = 65; // (Moved to top)
        mysteryActive = false;
        mysteryAppearedThisRound = false;
        mysteryNextAppearTime = System.currentTimeMillis() + 1000 + random.nextInt(7001);
    }

    private void resetEnemiesAndMysteryForNextRound() {
        enemies.clear();
        // Move enemies closer to bottom for next round
        enemyPosition += enemyMoveDown;
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 10; col++) {
                int x = 157 + col * 50;
                int y = enemyPosition + row * 45;
                enemies.add(new Enemy(row, col, x, y));
            }
        }
        enemyGroup = new EnemyGroup(enemies);
        mysteries.clear();
        mysteryActive = false;
        mysteryAppearedThisRound = false;
        // Set next random appearance time (1-8s from now, uniform)
        mysteryNextAppearTime = System.currentTimeMillis() + 1000 + random.nextInt(7001);
    }

    /*private void playThemeSong() {
        if (themeSongClip != null) {
            if (!themeSongClip.isRunning()) {
                themeSongClip.setFramePosition(0);
                themeSongClip.loop(Clip.LOOP_CONTINUOUSLY);
            }
        }
    }
    private void stopThemeSong() {
        if (themeSongClip != null && themeSongClip.isRunning()) {
            themeSongClip.stop();
        }
    }*/

    @Override
    public void actionPerformed(ActionEvent e) {
        if (titleScreen) {
        	//playMusic(11);
            //playThemeSong();
            //Animate background
            titleBgOffset = (titleBgOffset + titleBgSpeed) % WIDTH;
            repaint();
            return;
        } else {
            //stopThemeSong();
            stopMusic();
        }
        if (mainMenu) {
            repaint();
            return;
        }
        if (gameOver) {
            repaint();
            return;
        }
        if (paused) {
            repaint();
            return;
        }
        if (inGame) {
            updateGame();
        }
        repaint();
    }

    private void updateGame() {
        if (nextRound) {
            // Wait for 2 seconds before starting next round
            if (System.currentTimeMillis() - nextRoundStartTime > 2000) {
                nextRound = false;
                inGame = true;
                // Reset player position only
                player.x = WIDTH / 2 - 25;
                player.y = HEIGHT - 60;
                resetEnemiesAndMysteryForNextRound();
                // Clear bullets and explosions for new round
                bullets.clear();
                enemyBullets.clear();
                explosions.clear();
                // --- Reset invincibility on new round ---
                playerInvincible = false;
            }
            repaint();
            return;
        }
        // --- Handle invincibility timer ---
        if (playerInvincible && System.currentTimeMillis() - playerInvincibleStart > INVINCIBLE_DURATION) {
            playerInvincible = false;
        }
        // --- Handle random mystery ship appearance (only once per round) ---
        if (!mysteryActive && !mysteryAppearedThisRound && System.currentTimeMillis() >= mysteryNextAppearTime && inGame) {
            mysteries.add(new Mystery(-80, 45));
            mysteryActive = true;
            mysteryAppearedThisRound = true;
            playSound("mysteryentered");
        }
        // Remove mysteryActive flag if no mystery is present
        if (mysteryActive && mysteries.isEmpty()) {
            mysteryActive = false;
        }
        // Player movement
        if (leftPressed) player.move(-1);
        if (rightPressed) player.move(1);
        // Player shooting
        int maxBullets = getPlayerMaxBullets();
        if (spacePressed && System.currentTimeMillis() - lastPlayerShot > 400) {
            if (bullets.size() < maxBullets) {
                if (maxBullets == 1) {
                    bullets.add(new Bullet(player.x + 23, player.y + 5, -1, 15, "laser", "center"));
                    playSound("shoot");
                } else if (maxBullets == 2) {
                    bullets.add(new Bullet(player.x + 8, player.y + 5, -1, 15, "laser", "left"));
                    bullets.add(new Bullet(player.x + 38, player.y + 5, -1, 15, "laser", "right"));
                    playSound("shoot2");
                } else if (maxBullets == 3) {
                    bullets.add(new Bullet(player.x + 8, player.y + 5, -1, 15, "laser", "left"));
                    bullets.add(new Bullet(player.x + 23, player.y + 5, -1, 15, "laser", "center"));
                    bullets.add(new Bullet(player.x + 38, player.y + 5, -1, 15, "laser", "right"));
                    playSound("shoot2");
                } else if (maxBullets == 4) {
                    bullets.add(new Bullet(player.x + 4, player.y + 5, -1, 15, "laser", "farleft"));
                    bullets.add(new Bullet(player.x + 15, player.y + 5, -1, 15, "laser", "left"));
                    bullets.add(new Bullet(player.x + 31, player.y + 5, -1, 15, "laser", "right"));
                    bullets.add(new Bullet(player.x + 42, player.y + 5, -1, 15, "laser", "farright"));
                    playSound("shoot2");
                }
                lastPlayerShot = System.currentTimeMillis();
            }
        }
        // Enemies movement and shooting
        enemyGroup.update();
        if (System.currentTimeMillis() - lastEnemyShot > 700 && !enemies.isEmpty()) {
            Enemy shooter = randomBottomEnemy();
            if (shooter != null) {
                enemyBullets.add(new Bullet(shooter.x + 14, shooter.y + 20, 1, 5, "enemylaser", "center"));
                lastEnemyShot = System.currentTimeMillis();
            }
        }
        // Bullets update
        for (Iterator<Bullet> it = bullets.iterator(); it.hasNext(); ) {
            Bullet b = it.next();
            b.update();
            if (b.y < 0) it.remove();
        }
        for (Iterator<Bullet> it = enemyBullets.iterator(); it.hasNext(); ) {
            Bullet b = it.next();
            b.update();
            if (b.y > HEIGHT) it.remove();
        }
        // Mystery update
        for (Mystery m : mysteries) {
            m.update();
        }
        // Collisions
        checkCollisions();
        // Explosions
        for (Iterator<Explosion> it = explosions.iterator(); it.hasNext(); ) {
            Explosion ex = it.next();
            ex.update();
            if (ex.done) it.remove();
        }
        // Music
        int count = enemies.size();
        int musicDelay = 600;
        if (count > 15) musicDelay = 600;
        else if (count > 5) musicDelay = 300;
        else musicDelay = 150;
        if (System.currentTimeMillis() - lastMusicNote > musicDelay) {
            playMusicNote();
        }
        // Check for round clear
        if (enemies.isEmpty() && explosions.isEmpty() && !nextRound && inGame) {
            nextRound = true;
            inGame = false;
            round++;
            nextRoundStartTime = System.currentTimeMillis();
        }
    }

    private void checkCollisions() {
        // Player bullet vs enemy
        for (Iterator<Bullet> it = bullets.iterator(); it.hasNext(); ) {
            Bullet b = it.next();
            for (Iterator<Enemy> eit = enemies.iterator(); eit.hasNext(); ) {
                Enemy enemy = eit.next();
                if (b.getRect().intersects(enemy.getRect())) {
                    explosions.add(new Explosion(enemy.x, enemy.y, enemy.row));
                    playSound("invaderkilled");
                    score += enemy.getScore();
                    eit.remove();
                    it.remove();
                    break;
                }
            }
        }
        // Enemy bullet vs player
        if (!playerInvincible) {
            for (Iterator<Bullet> it = enemyBullets.iterator(); it.hasNext(); ) {
                Bullet b = it.next();
                if (b.getRect().intersects(player.getRect())) {
                    playSound("shipexplosion");
                    livesCount--;
                    if (!lives.isEmpty()) lives.remove(lives.size() - 1);
                    if (livesCount <= 0) {
                        gameOver = true;
                        inGame = false;
                    }
                    it.remove();
                    // --- Start invincibility ---
                    playerInvincible = true;
                    playerInvincibleStart = System.currentTimeMillis();
                    break;
                }
            }
        }
        // Bullets vs blockers
        for (Iterator<Bullet> it = bullets.iterator(); it.hasNext(); ) {
            Bullet b = it.next();
            for (Iterator<Blocker> bit = blockers.iterator(); bit.hasNext(); ) {
                Blocker bl = bit.next();
                if (b.getRect().intersects(bl.getRect())) {
                    bit.remove();
                    it.remove();
                    break;
                }
            }
        }
        for (Iterator<Bullet> it = enemyBullets.iterator(); it.hasNext(); ) {
            Bullet b = it.next();
            for (Iterator<Blocker> bit = blockers.iterator(); bit.hasNext(); ) {
                Blocker bl = bit.next();
                if (b.getRect().intersects(bl.getRect())) {
                    bit.remove();
                    it.remove();
                    break;
                }
            }
        }
        // Enemy vs blockers
        for (Iterator<Enemy> eit = enemies.iterator(); eit.hasNext(); ) {
            Enemy enemy = eit.next();
            for (Iterator<Blocker> bit = blockers.iterator(); bit.hasNext(); ) {
                Blocker bl = bit.next();
                if (enemy.getRect().intersects(bl.getRect())) {
                    bit.remove();
                }
            }
        }
        // Mystery ship
        for (Iterator<Mystery> mit = mysteries.iterator(); mit.hasNext(); ) {
            Mystery m = mit.next();
            boolean hit = false;
            for (Iterator<Bullet> bit = bullets.iterator(); bit.hasNext(); ) {
                Bullet b = bit.next();
                if (b.getRect().intersects(m.getRect())) {
                    int[] possible = {0, 150, 300};
                    int idx = random.nextInt(3);
                    int value = possible[idx];
                    score += value;
                    bit.remove();
                    mit.remove(); // Remove the mystery ship icon immediately (only once)
                    mysteryActive = false;
                    mysteryAppearedThisRound = true;
                    mysteryPopups.add(new MysteryScorePopup(m.x + 37, m.y + 20, value));
                    playSound("mysterykilled");
                    hit = true;
                    break;
                }
            }
            if (hit) break; // Only allow one hit per stray
        }
    }

    private Enemy randomBottomEnemy() {
        Map<Integer, Enemy> bottom = new HashMap<>();
        for (Enemy e : enemies) {
            if (!bottom.containsKey(e.col) || e.y > bottom.get(e.col).y) {
                bottom.put(e.col, e);
            }
        }
        if (bottom.isEmpty()) return null;
        java.util.List<Enemy> list = new java.util.ArrayList<>(bottom.values());
        return list.get(new Random().nextInt(list.size()));
    }

    private void playSound(String name) {
        /*Clip clip = sounds.get(name);
        if (clip != null) {
            if (clip.isRunning()) clip.stop();
            clip.setFramePosition(0);
            clip.start();
        }*/
    	switch (name) {
        case "shoot": playSFX(12); break;
        case "shoot2": playSFX(13); break;
        case "invaderkilled": playSFX(14); break;
        case "mysterykilled": playSFX(15); break;
        case "shipexplosion": playSFX(16); break;
        case "mysteryentered": playSFX(17); break;
    	}
    }

    private void playMusicNote() {
        int count = enemies.size();
        int musicDelay = 600;
        if (count > 15) musicDelay = 600; // normal
        else if (count > 5) musicDelay = 300; // 2x faster
        else musicDelay = 150; // 3x faster
        /*if (musicNotes[noteIndex] != null) {
            if (musicNotes[noteIndex].isRunning()) musicNotes[noteIndex].stop();
            musicNotes[noteIndex].setFramePosition(0);
            musicNotes[noteIndex].start();
        }*/
        playSFX(18+noteIndex);
        noteIndex = (noteIndex + 1) % 4;
        lastMusicNote = System.currentTimeMillis();
        // Adjust next note timing in updateGame
    }

    private int getPlayerMaxBullets() {
        if (score >= 6000) return 4;
        if (score >= 4000) return 3;
        if (score >= 2000) return 2;
        return 1;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        int drawW = WIDTH;
        int drawH = HEIGHT;
        // --- TITLE SCREEN ---
        if (titleScreen) {
            // Draw animated background if available
            if (titleScreenBgImg != null) {
                // Tile the background to cover the whole screen, even if the image is smaller
                int imgW = titleScreenBgImg.getWidth();
                int imgH = titleScreenBgImg.getHeight();
                for (int x = -titleBgOffset; x < drawW; x += imgW) {
                    for (int y = 0; y < drawH; y += imgH) {
                        g2.drawImage(titleScreenBgImg, x, y, null);
                    }
                }
            } else {
                g2.setColor(Color.BLACK);
                g2.fillRect(0, 0, drawW, drawH);
            }
            // Draw logo if available, scale to fit nicely
            int logoY = 60;
            int logoH = 0;
            if (titleScreenLogoImg != null) {
                int logoW = (int)(drawW * 0.56); // Slightly wider (was 0.48)
                logoH = (int)(logoW * ((double)titleScreenLogoImg.getHeight() / titleScreenLogoImg.getWidth()));
                int logoX = (drawW - logoW) / 2;
                g2.drawImage(titleScreenLogoImg, logoX, logoY, logoW, logoH, null);
            } else {
                g2.setFont(gameFont.deriveFont(Font.BOLD, 60f));
                g2.setColor(WHITE);
                String title = "SPACE INVADERS";
                FontMetrics fm = g2.getFontMetrics();
                int tx = (drawW - fm.stringWidth(title)) / 2;
                logoH = 90;
                g2.drawString(title, tx, logoY + logoH / 2);
            }
            // Draw menu options below the logo, with more vertical space
            g2.setFont(gameFont.deriveFont(Font.BOLD, 32f)); // Smaller font for options
            int menuStartY = logoY + (titleScreenLogoImg != null ? logoH : 90) + 60; // 60px below logo
            int menuSpacing = 50; // Slightly less spacing
            int totalMenuHeight = (titleMenuOptions.length - 1) * menuSpacing;
            // Center the menu block vertically in the remaining space below the logo, but keep a safe margin from the bottom
            int maxMenuY = drawH - 60; // 60px margin from bottom
            if (menuStartY + totalMenuHeight > maxMenuY) {
                menuStartY = maxMenuY - totalMenuHeight;
            }
            for (int i = 0; i < titleMenuOptions.length; i++) {
                String opt = titleMenuOptions[i];
                int x = drawW / 2;
                int y = menuStartY + i * menuSpacing;
                if (i == titleMenuIndex) {
                    g2.setColor(Color.YELLOW);
                    drawCenteredString(g2, opt, x, y);
                } else {
                    g2.setColor(Color.WHITE);
                    drawCenteredString(g2, opt, x, y);
                }
            }
            return;
        }
        // --- GAME OVER SCREEN ---
        if (gameOver) {
            // Draw moving/tiled background using titlescreenbackground.png
            if (titleScreenBgImg != null) {
                int imgW = titleScreenBgImg.getWidth();
                int imgH = titleScreenBgImg.getHeight();
                for (int x = -titleBgOffset; x < drawW; x += imgW) {
                    for (int y = 0; y < drawH; y += imgH) {
                        g2.drawImage(titleScreenBgImg, x, y, null);
                    }
                }
            } else {
                g2.setColor(Color.DARK_GRAY);
                g2.fillRect(0, 0, drawW, drawH);
            }
            // Animate background offset (same as title screen)
            titleBgOffset = (titleBgOffset + titleBgSpeed) % (titleScreenBgImg != null ? titleScreenBgImg.getWidth() : WIDTH);
            // Dim overlay
            g2.setColor(new Color(0, 0, 0, 180));
            g2.fillRect(0, 0, drawW, drawH);
            // Draw GAME OVER text
            g2.setFont(gameFont.deriveFont(Font.BOLD, 72f));
            g2.setColor(RED);
            String gameOverText = "GAME OVER";
            int goW = g2.getFontMetrics().stringWidth(gameOverText);
            g2.drawString(gameOverText, (drawW - goW) / 2, 140);
            // Draw score and rank
            g2.setFont(gameFont.deriveFont(Font.BOLD, 36f));
            g2.setColor(YELLOW);
            String scoreText = "SCORE: " + score;
            int scoreW = g2.getFontMetrics().stringWidth(scoreText);
            g2.drawString(scoreText, (drawW - scoreW) / 2, 220);
            String rankText = "RANK: " + (playerRank > 0 ? "#" + playerRank : "N/A");
            int rankW = g2.getFontMetrics().stringWidth(rankText);
            g2.drawString(rankText, (drawW - rankW) / 2, 270);
            // Name entry
            if (enteringName) {
                g2.setFont(gameFont.deriveFont(Font.BOLD, 28f));
                g2.setColor(Color.WHITE);
                String prompt = "ENTER YOUR NAME (8 CHAR MAX):";
                int promptW = g2.getFontMetrics().stringWidth(prompt);
                g2.drawString(prompt, (drawW - promptW) / 2, 340);
                String nameBox = playerNameInput + (playerNameInput.length() < 8 ? "_" : "");
                int nameW = g2.getFontMetrics().stringWidth(nameBox);
                g2.setColor(YELLOW);
                g2.drawString(nameBox, (drawW - nameW) / 2, 390);
                g2.setFont(gameFont.deriveFont(Font.PLAIN, 20f));
                g2.setColor(Color.LIGHT_GRAY);
                g2.drawString("Press ENTER to confirm", (drawW - 220) / 2, 420);
                return;
            }
            // Game Over Menu
            if (gameOverMenu) {
                g2.setFont(gameFont.deriveFont(Font.BOLD, 32f));
                int menuStartY = 340;
                int menuSpacing = 50;
                for (int i = 0; i < gameOverMenuOptions.length; i++) {
                    String opt = gameOverMenuOptions[i];
                    int x = drawW / 2;
                    int y = menuStartY + i * menuSpacing;
                    if (i == gameOverMenuIndex) {
                        g2.setColor(Color.WHITE);
                        drawCenteredString(g2, opt, x, y);
                    } else {
                        g2.setColor(Color.LIGHT_GRAY);
                        drawCenteredString(g2, opt, x, y);
                    }
                }
                return;
            }
            // If not entering name or in menu, prompt to enter name
            if (!scoreSaved) {
                g2.setFont(gameFont.deriveFont(Font.BOLD, 28f));
                g2.setColor(Color.WHITE);
                String prompt = "Press any key to enter your name";
                int promptW = g2.getFontMetrics().stringWidth(prompt);
                g2.drawString(prompt, (drawW - promptW) / 2, 340);
                return;
            }
            return;
        }
        // Draw background
        if (images.get("background") != null) {
            g2.drawImage(images.get("background"), 0, 0, drawW, drawH, null);
        }
        g2.setFont(gameFont);
        g2.setColor(WHITE);
        if (mainMenu) {
            g2.setFont(gameFont.deriveFont(50f));
            g2.drawString("Space Invaders", 164, 155);
            g2.setFont(gameFont.deriveFont(25f));
            g2.drawString("Press any key to continue", 201, 225);
            g2.drawImage(images.get("enemy3_1"), 318, 270, 40, 40, null);
            g2.drawString("=   10 pts", 368, 290);
            g2.drawImage(images.get("enemy2_2"), 318, 320, 40, 40, null);
            g2.drawString("=  20 pts", 368, 340);
            g2.drawImage(images.get("enemy1_2"), 318, 370, 40, 40, null);
            g2.drawString("=  30 pts", 368, 390);
            g2.drawImage(images.get("mystery"), 299, 420, 80, 40, null);
            g2.drawString("=  ?????", 368, 440);
            return;
        }
        if (gameOver) {
            g2.setFont(gameFont.deriveFont(50f));
            g2.drawString("Game Over", 250, 270);
            return;
        }
        if (nextRound) {
            // Draw score and lives at the top
            g2.setFont(gameFont.deriveFont(20f));
            g2.setColor(WHITE);
            g2.drawString("SCORE", 5, 25);
            g2.setColor(GREEN);
            g2.drawString(String.valueOf(score), 85, 25);
            // Draw lives at the top right, spaced from the right edge
            g2.setColor(WHITE);
            String livesLabel = "LIVES";
            FontMetrics fm = g2.getFontMetrics();
            int labelWidth = fm.stringWidth(livesLabel);
            int iconWidth = 23;
            int iconSpacing = 27;
            int rightMargin = 10; // pixels from right edge
            int totalIconsWidth = livesCount * iconWidth + (livesCount - 1) * (iconSpacing - iconWidth);
            int lifeX = WIDTH - rightMargin - totalIconsWidth;
            int labelX = lifeX - 8 - labelWidth; // 8px gap between label and first icon
            g2.drawString(livesLabel, labelX, 25);
            for (int i = 0; i < livesCount; i++) {
                g2.drawImage(images.get("ship"), (lifeX + i * iconSpacing), 7, iconWidth, iconWidth, null);
            }
            // Draw centered 'NEXT ROUND' text
            g2.setFont(gameFont.deriveFont(Font.BOLD, 56f));
            g2.setColor(WHITE);
            String msg = "NEXT ROUND";
            fm = g2.getFontMetrics();
            int msgWidth = fm.stringWidth(msg);
            int msgX = (WIDTH - msgWidth) / 2;
            int msgY = HEIGHT / 2 + fm.getAscent() / 2;
            g2.drawString(msg, msgX, msgY);
            // --- Fix: force the timer to keep running so next round resumes ---
            if (!inGame) {
                SwingUtilities.invokeLater(this::updateGame);
            }
            return;
        }
        // Draw score and lives
        g2.setFont(gameFont.deriveFont(20f));
        g2.setColor(WHITE);
        g2.drawString("Score", 5, 25);
        g2.setColor(GREEN);
        g2.drawString(String.valueOf(score), 85, 25);
        g2.setColor(WHITE);
        g2.drawString("Lives", 640, 25);
        for (Life life : lives) {
            life.draw(g2);
        }
        // Draw blockers
        for (Blocker b : blockers) b.draw(g2);
        // Draw player (with blinking if invincible)
        boolean drawPlayer = true;
        if (playerInvincible) {
            // Blink: only draw on even 100ms intervals
            long t = (System.currentTimeMillis() - playerInvincibleStart) / 100;
            drawPlayer = (t % 2 == 0);
        }
        if (drawPlayer) {
            player.draw(g2);
        }
        // Draw enemies
        for (Enemy e : enemies) e.draw(g2);
        // Draw bullets
        for (Bullet b : bullets) b.draw(g2);
        for (Bullet b : enemyBullets) b.draw(g2);
        // Draw mystery
        for (Mystery m : mysteries) m.draw(g2);
        // Draw mystery score popups
        for (Iterator<MysteryScorePopup> it = mysteryPopups.iterator(); it.hasNext(); ) {
            MysteryScorePopup popup = it.next();
            popup.draw(g2);
            if (popup.isExpired()) it.remove();
        }
        // Draw explosions
        for (Explosion ex : explosions) ex.draw(g2);
        // --- PAUSE SCREEN ---
        if (paused) {
            // Dim the game screen
            g2.setColor(new Color(0, 0, 0, 180));
            g2.fillRect(0, 0, WIDTH, HEIGHT);
            // Draw "PAUSED" in big red font
            g2.setFont(gameFont.deriveFont(Font.BOLD, 80f));
            g2.setColor(RED);
            String pausedText = "PAUSED";
            FontMetrics fm = g2.getFontMetrics();
            int px = (WIDTH - fm.stringWidth(pausedText)) / 2;
            int py = 200;
            g2.drawString(pausedText, px, py);
            // Draw pause menu options
            g2.setFont(gameFont.deriveFont(Font.BOLD, 40f));
            int menuStartY = py + 60;
            int menuSpacing = 55;
            for (int i = 0; i < pauseMenuOptions.length; i++) {
                String opt = pauseMenuOptions[i];
                int x = WIDTH / 2;
                int y = menuStartY + i * menuSpacing;
                if (i == pauseMenuIndex) {
                    g2.setColor(Color.WHITE);
                    drawCenteredString(g2, opt, x, y);
                } else {
                    g2.setColor(RED);
                    drawCenteredString(g2, opt, x, y);
                }
            }
            return;
        }
        // --- LEADERBOARD SCREEN ---
        if (leaderboardScreen) {
            // Draw moving/tiled background using titlescreenbackground.png
            if (titleScreenBgImg != null) {
                int imgW = titleScreenBgImg.getWidth();
                int imgH = titleScreenBgImg.getHeight();
                for (int x = -titleBgOffset; x < drawW; x += imgW) {
                    for (int y = 0; y < drawH; y += imgH) {
                        g2.drawImage(titleScreenBgImg, x, y, null);
                    }
                }
            } else {
                g2.setColor(Color.BLACK);
                g2.fillRect(0, 0, drawW, drawH);
            }
            // Animate background offset
            titleBgOffset = (titleBgOffset + titleBgSpeed) % (titleScreenBgImg != null ? titleScreenBgImg.getWidth() : WIDTH);
            // Dim overlay
            g2.setColor(new Color(0, 0, 0, 180));
            g2.fillRect(0, 0, drawW, drawH);
            // Draw LEADERBOARD title
            g2.setFont(gameFont.deriveFont(Font.BOLD, 48f));
            g2.setColor(new Color(255, 221, 51)); // Yellow
            String lbTitle = "LEADERBOARD";
            int lbTitleW = g2.getFontMetrics().stringWidth(lbTitle);
            g2.drawString(lbTitle, (drawW - lbTitleW) / 2, 100);
            // Draw < BACK
            g2.setFont(gameFont.deriveFont(Font.BOLD, 24f));
            g2.setColor(new Color(80, 255, 239)); // Cyan
            g2.drawString("< BACK", 40, 80);
            // Draw header row
            int tableY = 150;
            int rowH = 48;
            int colRankX = 80, colNameX = 220, colScoreX = 600;
            int tableW = drawW - 2 * colRankX;
            g2.setColor(new Color(180, 140, 200)); // Purple header
            g2.fillRect(colRankX, tableY, tableW, rowH);
            g2.setFont(gameFont.deriveFont(Font.BOLD, 24f));
            g2.setColor(Color.BLACK);
            g2.drawString("RANK", colRankX + 20, tableY + 32);
            g2.drawString("NAME", colNameX, tableY + 32);
            g2.drawString("SCORE", colScoreX, tableY + 32);
            // Draw leaderboard rows (scrollable)
            int totalRows = leaderboardNames.size();
            int maxScroll = Math.max(0, totalRows - LEADERBOARD_ROWS);
            leaderboardScroll = Math.max(0, Math.min(leaderboardScroll, maxScroll));
            for (int i = 0; i < LEADERBOARD_ROWS; i++) {
                int y = tableY + rowH * (i + 1);
                int idx = i + leaderboardScroll;
                g2.setColor(i % 2 == 0 ? new Color(60, 60, 60) : new Color(40, 40, 40));
                g2.fillRect(colRankX, y, tableW, rowH);
                g2.setFont(gameFont.deriveFont(Font.BOLD, 22f));
                g2.setColor(Color.WHITE);
                if (idx < totalRows) {
                    g2.drawString(String.valueOf(idx + 1), colRankX + 32, y + 30);
                    g2.drawString(leaderboardNames.get(idx), colNameX, y + 30);
                    g2.drawString(String.valueOf(leaderboardScores.get(idx)), colScoreX, y + 30);
                }
            }
            // Draw scroll bar (static, for style)
            int barX = colRankX + tableW - 16;
            int barY = tableY + rowH;
            int barH = rowH * LEADERBOARD_ROWS;
            g2.setColor(new Color(80, 80, 80));
            g2.fillRect(barX, barY, 16, barH);
            // Draw thumb proportional to scroll
            if (totalRows > LEADERBOARD_ROWS) {
                int thumbH = Math.max(32, barH * LEADERBOARD_ROWS / totalRows);
                int thumbY = barY + (barH - thumbH) * leaderboardScroll / maxScroll;
                g2.setColor(new Color(180, 180, 180));
                g2.fillRect(barX, thumbY, 16, thumbH);
            } else {
                g2.setColor(new Color(180, 180, 180));
                g2.fillRect(barX, barY, 16, barH);
            }
            // If empty, show message
            if (totalRows == 0) {
                g2.setFont(gameFont.deriveFont(Font.BOLD, 28f));
                g2.setColor(Color.LIGHT_GRAY);
                String msg = "No scores yet!";
                int msgW = g2.getFontMetrics().stringWidth(msg);
                g2.drawString(msg, (drawW - msgW) / 2, tableY + rowH * 6);
            }
            return;
        }
    }

    // Helper to draw centered text (unscaled)
    private void drawCenteredString(Graphics2D g2, String text, int x, int y) {
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent();
        g2.drawString(text, x - textWidth / 2, y + textHeight / 2);
    }

    // --- Classes for game objects ---
    class Ship {
        int x, y, speed = 5;
        public Ship(int x, int y) { this.x = x; this.y = y; }
        public void move(int dir) {
            x += dir * speed;
            if (x < 10) x = 10;
            if (x > WIDTH - 60) x = WIDTH - 60;
        }
        public void draw(Graphics2D g) {
            g.drawImage(images.get("ship"), x, y, 50, 40, null);
        }
        public Rectangle getRect() { return new Rectangle(x, y, 50, 40); }
    }
    class Bullet {
        int x, y, dir, speed;
        String type, side;
        public Bullet(int x, int y, int dir, int speed, String type, String side) {
            this.x = x; this.y = y; this.dir = dir; this.speed = speed; this.type = type; this.side = side;
        }
        public void update() { y += speed * dir; }
        public void draw(Graphics2D g) {
            g.drawImage(images.get(type), x, y, 10, 20, null);
        }
        public Rectangle getRect() { return new Rectangle(x, y, 10, 20); }
    }
    class Enemy {
        int row, col, x, y, frame = 0;
        BufferedImage[] frames;
        public Enemy(int row, int col, int x, int y) {
            this.row = row; this.col = col; this.x = x; this.y = y;
            String[] names = getFrameNames(row);
            frames = new BufferedImage[2];
            frames[0] = images.get(names[0]);
            frames[1] = images.get(names[1]);
        }
        public void draw(Graphics2D g) {
            g.drawImage(frames[frame], x, y, 40, 35, null);
        }
        public Rectangle getRect() { return new Rectangle(x, y, 40, 35); }
        public int getScore() {
            if (row == 0) return 30;
            if (row == 1 || row == 2) return 20;
            return 10;
        }
        private String[] getFrameNames(int row) {
            if (row == 0) return new String[]{"enemy1_1", "enemy1_2"};
            if (row == 1 || row == 2) return new String[]{"enemy2_1", "enemy2_2"};
            return new String[]{"enemy3_1", "enemy3_2"};
        }
    }
    class Blocker {
        int x, y, size;
        Color color;
        public Blocker(int x, int y, int size, Color color) {
            this.x = x; this.y = y; this.size = size; this.color = color;
        }
        public void draw(Graphics2D g) {
            g.setColor(color);
            g.fillRect(x, y, size, size);
        }
        public Rectangle getRect() { return new Rectangle(x, y, size, size); }
    }
    class Mystery {
        int x, y, dir = 1;
        int timer = 0;
        public Mystery(int x, int y) { this.x = x; this.y = y; }
        public void update() {
            x += dir * 2;
            if (x > WIDTH) { x = -80; dir = 1; }
            if (x < -80) { x = WIDTH; dir = -1; }
        }
        public void draw(Graphics2D g) {
            g.drawImage(images.get("mystery"), x, y, 75, 35, null);
        }
        public Rectangle getRect() { return new Rectangle(x, y, 75, 35); }
    }
    class Explosion {
        int x, y, row, timer = 0;
        boolean done = false;
        public Explosion(int x, int y, int row) { this.x = x; this.y = y; this.row = row; }
        public void update() { timer += 16; if (timer > 400) done = true; }
        public void draw(Graphics2D g) {
            String[] colors = {"explosionpurple", "explosionblue", "explosionblue", "explosiongreen", "explosiongreen", "explosionpurple"};
            String img = colors[Math.min(row, colors.length - 1)];
            if (images.get(img) != null) {
                g.drawImage(images.get(img), x, y, 50, 45, null);
            }
        }
    }
    class Life {
        int x, y;
        public Life(int x, int y) { this.x = x; this.y = y; }
        public void draw(Graphics2D g) {
            g.drawImage(images.get("ship"), x, y, 23, 23, null);
        }
        public Rectangle getRect() { return new Rectangle(x, y, 23, 23); }
    }

    // --- Enemy Group Logic ---
    class EnemyGroup {
        java.util.List<Enemy> enemies;
        int direction = 1; // 1 = right, -1 = left
        int moveTick = 0;
        int moveSpeed = 18; // base speed (lower = faster)
        int leftBound = 0; // true screen edge
        int rightBound = WIDTH; // true screen edge
        int step = 10;

        public EnemyGroup(java.util.List<Enemy> enemies) {
            this.enemies = enemies;
        }

        public void update() {
            if (enemies.isEmpty()) return;
            moveTick++;
            int speed = getCurrentSpeed();
            if (moveTick >= speed) {
                // Find leftmost and rightmost enemy
                int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
                for (Enemy e : enemies) {
                    if (e.x < minX) minX = e.x;
                    if (e.x > maxX) maxX = e.x + 40;
                }
                boolean hitEdge = false;
                if (direction == 1 && maxX + step >= rightBound) hitEdge = true;
                if (direction == -1 && minX - step <= leftBound) hitEdge = true;
                if (hitEdge) {
                    for (Enemy e : enemies) {
                        e.y += enemyMoveDown;
                    }
                    direction *= -1;
                } else {
                    for (Enemy e : enemies) {
                        e.x += step * direction;
                        e.frame = (e.frame + 1) % 2;
                    }
                }
                moveTick = 0;
            }
        }
        private int getCurrentSpeed() {
            int count = enemies.size();
            if (count > 15) return moveSpeed; // normal
            if (count > 5) return Math.max(4, moveSpeed / 2); // 2x faster
            return Math.max(2, moveSpeed / 3); // 3x faster
        }
    }

    // --- Mystery Score Popup ---
    class MysteryScorePopup {
        int x, y, value;
        long startTime;
        public MysteryScorePopup(int x, int y, int value) {
            this.x = x; this.y = y; this.value = value;
            this.startTime = System.currentTimeMillis();
        }
        public boolean isExpired() {
            return System.currentTimeMillis() - startTime > 900; // show for 0.9s
        }
        public void draw(Graphics2D g) {
            // Blinking effect: only draw on even 100ms intervals
            long t = (System.currentTimeMillis() - startTime) / 100;
            if (t % 2 != 0) return;
            g.setFont(gameFont.deriveFont(Font.BOLD, 18f)); // Smaller font
            if (value == 0) g.setColor(WHITE);
            else if (value == 150) g.setColor(BLUE);
            else g.setColor(RED);
            String txt = String.valueOf(value);
            FontMetrics fm = g.getFontMetrics();
            int w = fm.stringWidth(txt);
            g.drawString(txt, x - w/2, y);
        }
    }
    private java.util.List<MysteryScorePopup> mysteryPopups = new ArrayList<>();

    // --- Mouse Handling ---
    @Override
    public void mousePressed(MouseEvent e) {
        if (leaderboardScreen) {
            int mx = e.getX();
            int my = e.getY();
            // < BACK clickable area (left top)
            if (mx >= 40 && mx <= 140 && my >= 60 && my <= 90) {
                leaderboardScreen = false;
                titleScreen = true;
                repaint();
                return;
            }
            // Scrollbar area
            int drawW = WIDTH;
            int tableY = 150;
            int rowH = 48;
            int colRankX = 80;
            int tableW = drawW - 2 * colRankX;
            int barX = colRankX + tableW - 16;
            int barY = tableY + rowH;
            int barH = rowH * LEADERBOARD_ROWS;
            if (mx >= barX && mx <= barX + 16 && my >= barY && my <= barY + barH) {
                // Scroll up or down depending on click position
                int relY = my - barY;
                int totalRows = leaderboardNames.size();
                int maxScroll = Math.max(0, totalRows - LEADERBOARD_ROWS);
                if (relY < barH / 2) {
                    leaderboardScroll = Math.max(0, leaderboardScroll - 1);
                } else {
                    leaderboardScroll = Math.min(maxScroll, leaderboardScroll + 1);
                }
                repaint();
                return;
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // Not used
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // Not used
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // Not used
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // Not used
    }

    /*public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Space Invaders");
            SpaceInvaders game = new SpaceInvaders();
            // game.frameRef = frame; // Removed: frameRef does not exist
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.add(game);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }*/
}
