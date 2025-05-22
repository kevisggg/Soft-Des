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
import spaceInvaders.*;
import spaceInvaders.leaderboard.SILeaderboardManager;
import spaceInvaders.leaderboard.SIPlayerScore;

public class SpaceInvaders extends AbstractGamePanel implements ActionListener, KeyListener, MouseListener {
    // Window size
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_ESCAPE) returnToMenu();
        // --- Name Entry Handling ---
        if (gameOver && nameEntryActive && !nameEntryComplete) {
            int code = e.getKeyCode();
            if (code == KeyEvent.VK_BACK_SPACE) {
                if (!playerName.isEmpty()) {
                    playerName = playerName.substring(0, playerName.length() - 1);
                    repaint();
                }
                return;
            } else if (code == KeyEvent.VK_ENTER) {
                if (!playerName.trim().isEmpty() && pendingNameEntry && pendingScore != null) {
                    nameEntryComplete = true;
                    nameEntryActive = false;
                    // Update the pending score's name and re-sort leaderboard
                    pendingScore.setName(playerName);
                    leaderboardManager.addPlayer(pendingScore); // This will update the name and re-sort
                    pendingNameEntry = false;
                    pendingScore = null;
                    repaint();
                }
                return;
            }
            // Ignore all other keys here (actual character input handled in keyTyped)
            return;
        }
        // --- GAME OVER MENU SELECTION ---
        if (gameOver && !nameEntryActive && nameEntryComplete) {
            int code = e.getKeyCode();
            if (code == KeyEvent.VK_UP) {
                gameOverMenuIndex = (gameOverMenuIndex - 1 + gameOverMenuOptions.length) % gameOverMenuOptions.length;
                repaint();
                return;
            } else if (code == KeyEvent.VK_DOWN) {
                gameOverMenuIndex = (gameOverMenuIndex + 1) % gameOverMenuOptions.length;
                repaint();
                return;
            } else if (code == KeyEvent.VK_ENTER) {
                switch (gameOverMenuIndex) {
                    case 0: // RESTART
                        resetGame();
                        inGame = false;
                        mainMenu = true;
                        titleScreen = false;
                        gameOver = false;
                        nameEntryActive = false;
                        nameEntryComplete = true;
                        pendingNameEntry = false;
                        pendingScore = null;
                        playerName = "";
                        repaint();
                        break;
                    case 1: // VIEW LEADERBOARD
                        leaderboardScreen = true;
                        gameOver = false;
                        nameEntryActive = false;
                        nameEntryComplete = false;
                        repaint();
                        break;
                    case 2: // RETURN TO MENU
                        resetGame();
                        titleScreen = true;
                        mainMenu = false;
                        gameOver = false;
                        nameEntryActive = false;
                        nameEntryComplete = false;
                        repaint();
                        break;
                }
                return;
            }
            return;
        }
        // --- TITLE SCREEN ---
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
                        repaint();
                        break;
                    //case 2: // Help
                        // TODO: Implement help screen
                       // break;
                    case 2: // Quit
                        //System.exit(0);
                    	//stopMusic();
                        returnToMenu();
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
            // Add scrolling if needed (now supports full leaderboard)
            java.util.List<SIPlayerScore> topPlayers = leaderboardManager.getTopPlayers();
            int totalRows = topPlayers.size();
            int dynamicRows = 20;
            int maxScroll = Math.max(0, totalRows - dynamicRows);
            if (leaderboardScroll > maxScroll) leaderboardScroll = maxScroll;
            if (code == KeyEvent.VK_UP) {
                leaderboardScroll = Math.max(0, leaderboardScroll - 1);
                repaint();
                return;
            } else if (code == KeyEvent.VK_DOWN) {
                leaderboardScroll = Math.min(maxScroll, leaderboardScroll + 1);
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
                        // Show the "Press any key to continue" screen (mainMenu)
                        mainMenu = true;
                        titleScreen = false;
                        gameOver = false;
                        nextRound = false;
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
            //playSound("shoot");
            playSFX(12);
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
        // --- Name Entry Character Input ---
        if (gameOver && nameEntryActive && !nameEntryComplete) {
            char c = e.getKeyChar();
            if ((Character.isLetterOrDigit(c) || c == ' ' || c == '-' || c == '_') && playerName.length() < MAX_NAME_LENGTH) {
                playerName += c;
                repaint();
            }
            // Ignore all other input
            return;
        }
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
    public static final String SOUND_PATH = "/SI/sounds/";

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
    // --- Invincibility state ---
    private boolean playerInvincible = false;
    private long playerInvincibleStart = 0;
    private final long INVINCIBLE_DURATION = 1000; // 1 second

    // --- Mystery Ship Random Appearance ---
    private long mysteryNextAppearTime = 0;
    private boolean mysteryActive = false;
    private boolean mysteryAppearedThisRound = false;
    private Random random = new Random();

    // --- Next Round Timer ---
    private long nextRoundStartTime = 0;

    // --- Title Screen State ---
    private boolean titleScreen = true; // Show title screen first
    private int titleMenuIndex = 0;
    private final String[] titleMenuOptions = {"Start", "Leaderboard", "Quit"};
    private int titleBgOffset = 0;
    private final int titleBgSpeed = 1;
    private BufferedImage titleScreenBgImg = null;
    private BufferedImage titleScreenLogoImg = null;

    // --- Pause Screen State ---
    private boolean paused = false;
    private int pauseMenuIndex = 0;
    private final String[] pauseMenuOptions = {"RESUME", "RESTART", "EXIT"};

    // --- Leaderboard State ---
    private SILeaderboardManager leaderboardManager = new SILeaderboardManager();
    private boolean leaderboardScreen = false;
    private int leaderboardScroll = 0; // For scrolling
    private final int LEADERBOARD_ROWS = 20; // Show up to 20 rows at a time

    // --- Name Entry State ---
    private boolean nameEntryActive = false;
    private boolean nameEntryComplete = true;
    private String playerName = "";
    private final int MAX_NAME_LENGTH = 10;

    // --- Game Over Menu Selection ---
    private int gameOverMenuIndex = 0;
    private final String[] gameOverMenuOptions = {"RESTART", "VIEW LEADERBOARD", "RETURN TO MENU"};

    private Clip themeSongClip = null;

    // --- Pending Name Entry for Game Over ---
    private boolean pendingNameEntry = false;
    private SIPlayerScore pendingScore = null;
	//private MainWindow mainWindow;

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
        // Register mouse wheel listener for leaderboard scrolling
        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (leaderboardScreen) {
                    java.util.List<SIPlayerScore> topPlayers = leaderboardManager.getTopPlayers();
                    int totalRows = topPlayers.size();
                    int tableY = 150;
                    int rowH = 32;
                    int maxTableHeight = HEIGHT - tableY - 40;
                    int visibleRows = Math.min(LEADERBOARD_ROWS, (maxTableHeight - rowH) / rowH);
                    int maxScroll = Math.max(0, totalRows - visibleRows);
                    int notches = e.getWheelRotation();
                    leaderboardScroll += notches;
                    if (leaderboardScroll < 0) leaderboardScroll = 0;
                    if (leaderboardScroll > maxScroll) leaderboardScroll = maxScroll;
                    repaint();
                }
            }
        });
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
        AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File(path));
        Clip clip = AudioSystem.getClip();
        clip.open(audioIn);
        return clip;
    }

    private void resetGame() {
        // Reset all game state variables
        score = 0;
        livesCount = 3;
        enemyPosition = 65;
        player = new Ship(WIDTH / 2 - 25, HEIGHT - 60);
        bullets.clear();
        enemyBullets.clear();
        enemies.clear();
        blockers.clear();
        mysteries.clear(); // Ensure no mystery ships at start
        explosions.clear();
        lives.clear();
        for (int i = 0; i < 3; i++) {
            lives.add(new Life(WIDTH - 85 + i * 27, 10));
        }
        // Create enemies
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 10; col++) {
                int x = 157 + col * 50;
                int y = enemyPosition + row * 45;
                enemies.add(new Enemy(row, col, x, y));
            }
        }
        enemyGroup = new EnemyGroup(enemies);
        // Create blockers
        for (int b = 0; b < 4; b++) {
            for (int row = 0; row < 4; row++) {
                for (int col = 0; col < 9; col++) {
                    int x = 50 + (200 * b) + (col * 10);
                    int y = blockersPosition + (row * 10);
                    blockers.add(new Blocker(x, y, 10, GREEN));
                }
            }
        }
        // Do NOT create a mystery ship here!
        inGame = false;
        gameOver = false;
        // Always start at the title screen
        titleScreen = true;
        mainMenu = false;
        // enemyPosition = 65; // (Moved to top)
        // Don't add mystery here; it will be added randomly in updateGame
        mysteryActive = false;
        mysteryAppearedThisRound = false;
        // Set next random appearance time (1-8s from now, uniform)
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
        // Don't add mystery here; it will be added randomly in updateGame
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
            //playThemeSong();
            //playMusic(11);
            // Animate background
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
            // --- Name Entry Activation ---
            if (!nameEntryActive && !nameEntryComplete) {
                nameEntryActive = true;
                nameEntryComplete = false;
                playerName = "";
            }
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
            //playSound("mysteryentered");
            playSFX(17);
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
                    //playSound("shoot");
                    playSFX(12);
                } else if (maxBullets == 2) {
                    bullets.add(new Bullet(player.x + 8, player.y + 5, -1, 15, "laser", "left"));
                    bullets.add(new Bullet(player.x + 38, player.y + 5, -1, 15, "laser", "right"));
                    //playSound("shoot2");
                    playSFX(13);
                } else if (maxBullets == 3) {
                    bullets.add(new Bullet(player.x + 8, player.y + 5, -1, 15, "laser", "left"));
                    bullets.add(new Bullet(player.x + 23, player.y + 5, -1, 15, "laser", "center"));
                    bullets.add(new Bullet(player.x + 38, player.y + 5, -1, 15, "laser", "right"));
                    //playSound("shoot2");
                    playSFX(13);
                } else if (maxBullets == 4) {
                    bullets.add(new Bullet(player.x + 4, player.y + 5, -1, 15, "laser", "farleft"));
                    bullets.add(new Bullet(player.x + 15, player.y + 5, -1, 15, "laser", "left"));
                    bullets.add(new Bullet(player.x + 31, player.y + 5, -1, 15, "laser", "right"));
                    bullets.add(new Bullet(player.x + 42, player.y + 5, -1, 15, "laser", "farright"));
                    //playSound("shoot2");
                    playSFX(13);
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
        // Music tempo logic based on enemies left
        int count = enemies.size();
        long now = System.currentTimeMillis();
        long musicDelay;
        if (count > 15) {
            musicDelay = 600;
        } else if (count > 5) {
            musicDelay = 300;
        } else {
            musicDelay = 150;
        }
        if (now - lastMusicNote > musicDelay) {
            playMusicNote();
        }
        // Check for round clear
        if (enemies.isEmpty() && explosions.isEmpty() && !nextRound && inGame) {
            nextRound = true;
            inGame = false;
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
                    //playSound("invaderkilled");
                    playSFX(14);
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
                    //playSound("shipexplosion");
                    playSFX(16);
                    livesCount--;
                    if (!lives.isEmpty()) lives.remove(lives.size() - 1);
                    if (livesCount <= 0) {
                        gameOver = true;
                        inGame = false;
                        // Prepare for name entry and leaderboard update
                        if (!pendingNameEntry) {
                            pendingNameEntry = true;
                            nameEntryActive = true;
                            nameEntryComplete = false;
                            playerName = "";
                            // Do NOT add score to leaderboard here; wait until name entry is complete
                            pendingScore = new SIPlayerScore("", score);
                            // Score will be added to leaderboard after name entry
                        }
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
                    // Award 0, 150, or 300 points with equal probability
                    int[] possible = {0, 150, 300};
                    int idx = random.nextInt(possible.length); // 0, 1, or 2
                    int value = possible[idx];
                    score += value;
                    bit.remove();
                    mit.remove(); // Remove the mystery ship icon immediately (only once)
                    mysteryActive = false;
                    mysteryAppearedThisRound = true;
                    // Show the awarded points as a popup at the ship's location
                    mysteryPopups.add(new MysteryScorePopup(m.x + 37, m.y + 20, value));
                    //playSound("mysterykilled");
                    playSFX(15);
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

    /*private void playSound(String name) {
        Clip clip = sounds.get(name);
        if (clip != null) {
            if (clip.isRunning()) clip.stop();
            clip.setFramePosition(0);
            clip.start();
        }
    }*/

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
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // --- TITLE SCREEN ---
        if (titleScreen) {
            // Draw animated background if available
            if (titleScreenBgImg != null) {
                // Tile the background to cover the whole screen, even if the image is smaller
                int imgW = titleScreenBgImg.getWidth();
                int imgH = titleScreenBgImg.getHeight();
                for (int x = -titleBgOffset; x < WIDTH; x += imgW) {
                    for (int y = 0; y < HEIGHT; y += imgH) {
                        g.drawImage(titleScreenBgImg, x, y, null);
                    }
                }
            } else {
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, WIDTH, HEIGHT);
            }
            // Draw logo if available, scale to fit nicely
            int logoY = 60;
            int logoH = 0;
            if (titleScreenLogoImg != null) {
                int logoW = (int)(WIDTH * 0.56); // Slightly wider (was 0.48)
                logoH = (int)(logoW * ((double)titleScreenLogoImg.getHeight() / titleScreenLogoImg.getWidth()));
                int logoX = (WIDTH - logoW) / 2;
                g.drawImage(titleScreenLogoImg, logoX, logoY, logoW, logoH, null);
            } else {
                g.setFont(gameFont.deriveFont(Font.BOLD, 60f));
                g.setColor(WHITE);
                String title = "SPACE INVADERS";
                FontMetrics fm = g.getFontMetrics();
                int tx = (WIDTH - fm.stringWidth(title)) / 2;
                logoH = 90;
                g.drawString(title, tx, logoY + logoH / 2);
            }
            // Draw menu options below the logo, with more vertical space
            // Restore larger font and spacing for menu
            g.setFont(gameFont.deriveFont(Font.BOLD, 32f)); // Restore to 32f
            int menuStartY = logoY + (titleScreenLogoImg != null ? logoH : 90) + 60; // Restore to 60px below logo
            int menuSpacing = 50; // Restore spacing between options
            int totalMenuHeight = (titleMenuOptions.length - 1) * menuSpacing;
            // Center the menu block vertically in the remaining space below the logo, but keep a safe margin from the bottom
            int maxMenuY = HEIGHT - 60; // 60px margin from bottom
            if (menuStartY + totalMenuHeight > maxMenuY) {
                menuStartY = maxMenuY - totalMenuHeight;
            }
            for (int i = 0; i < titleMenuOptions.length; i++) {
                String opt = titleMenuOptions[i];
                int x = WIDTH / 2;
                int y = menuStartY + i * menuSpacing;
                if (i == titleMenuIndex) {
                    g.setColor(Color.YELLOW);
                    drawCenteredString((Graphics2D) g, opt, x, y);
                } else {
                    g.setColor(Color.WHITE);
                    drawCenteredString((Graphics2D) g, opt, x, y);
                }
            }
            return;
        }
        // --- GAME OVER SCREEN ---
        if (gameOver) {
            // --- Bomberman-style GAME OVER screen ---
            // Moving/tiled background (like title screen)
            if (titleScreenBgImg != null) {
                int imgW = titleScreenBgImg.getWidth();
                int imgH = titleScreenBgImg.getHeight();
                for (int x = -titleBgOffset; x < WIDTH; x += imgW) {
                    for (int y = 0; y < HEIGHT; y += imgH) {
                        g.drawImage(titleScreenBgImg, x, y, null);
                    }
                }
                // Animate background offset
                titleBgOffset = (titleBgOffset + titleBgSpeed) % imgW;
            } else {
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, WIDTH, HEIGHT);
            }
            // GAME OVER title with shadow
            g.setFont(gameFont.deriveFont(Font.BOLD, 72f));
            String gameOverText = "GAME OVER";
            int goX = (WIDTH - g.getFontMetrics().stringWidth(gameOverText)) / 2;
            int goY = HEIGHT / 3;
            g.setColor(new Color(0,0,0,180));
            g.drawString(gameOverText, goX+4, goY+4);
            g.setColor(RED);
            g.drawString(gameOverText, goX, goY);
            // Score
            g.setFont(gameFont.deriveFont(Font.BOLD, 36f));
            String scoreText = "SCORE: " + score;
            int scoreX = (WIDTH - g.getFontMetrics().stringWidth(scoreText)) / 2;
            int scoreY = goY + 60;
            g.setColor(Color.WHITE);
            g.drawString(scoreText, scoreX, scoreY);
            // Rank (if available)
            int rank = 0;
            java.util.List<SIPlayerScore> topPlayers = leaderboardManager.getTopPlayers();
            for (SIPlayerScore p : topPlayers) {
                if (p.getName().equals(playerName) && p.getScore() == score) {
                    rank = p.getRank();
                    break;
                }
            }
            if (rank > 0) {
                String rankText = "RANK: " + rank;
                int rankX = (WIDTH - g.getFontMetrics().stringWidth(rankText)) / 2;
                int rankY = scoreY + 40;
                g.drawString(rankText, rankX, rankY);
            }
            // Options (plain text, no boxes, highlight selected)
            g.setFont(gameFont.deriveFont(Font.BOLD, 32f));
            int btnYStart = HEIGHT/2 + 60;
            int btnSpacing = 50;
            for (int i = 0; i < gameOverMenuOptions.length; i++) {
                String btn = gameOverMenuOptions[i];
                int btnW = g.getFontMetrics().stringWidth(btn);
                int btnX = (WIDTH - btnW) / 2;
                int btnY = btnYStart + i * btnSpacing;
                if (!nameEntryActive && nameEntryComplete && i == gameOverMenuIndex) {
                    g.setColor(Color.WHITE);
                    g.drawString(btn, btnX, btnY);
                    g.setColor(Color.YELLOW);
                    g.drawLine(btnX, btnY + 5, btnX + btnW, btnY + 5); // underline
                } else {
                    g.setColor(Color.YELLOW);
                    g.drawString(btn, btnX, btnY);
                }
            }
            // Name entry box (if needed)
            if (nameEntryActive && !nameEntryComplete) {
                int boxX = (WIDTH - 400) / 2;
                int boxY = btnYStart + gameOverMenuOptions.length * btnSpacing + 20;
                int boxW = 400;
                int boxH = 50;
                g.setColor(new Color(0, 0, 0, 200));
                g.fillRect(boxX, boxY, boxW, boxH);
                g.setColor(Color.WHITE);
                g.drawRect(boxX, boxY, boxW, boxH);
                g.setFont(gameFont.deriveFont(Font.PLAIN, 24f));
                String instructions = "Enter your name: (max 10 chars)";
                int instrW = g.getFontMetrics().stringWidth(instructions);
                g.drawString(instructions, (WIDTH - instrW) / 2, boxY - 10);
                g.setFont(gameFont.deriveFont(Font.BOLD, 28f));
                int nameX = boxX + 10;
                int nameY = boxY + 35;
                g.drawString(playerName, nameX, nameY);
            }
            return;
        }
        // Draw background
        if (images.get("background") != null) {
            g.drawImage(images.get("background"), 0, 0, WIDTH, HEIGHT, null);
        }
        g.setFont(gameFont);
        g.setColor(WHITE);
        if (mainMenu) {
            g.setFont(gameFont.deriveFont(50f));
            g.drawString("Space Invaders", 164, 155);
            g.setFont(gameFont.deriveFont(25f));
            g.drawString("Press any key to continue", 201, 225);
            g.drawImage(images.get("enemy3_1"), 318, 270, 40, 40, null);
            g.drawString("=   10 pts", 368, 290);
            g.drawImage(images.get("enemy2_2"), 318, 320, 40, 40, null);
            g.drawString("=  20 pts", 368, 340);
            g.drawImage(images.get("enemy1_2"), 318, 370, 40, 40, null);
            g.drawString("=  30 pts", 368, 390);
            g.drawImage(images.get("mystery"), 299, 420, 80, 40, null);
            g.drawString("=  ?????", 368, 440);
            return;
        }
        if (gameOver) {
            g.setFont(gameFont.deriveFont(50f));
            g.drawString("Game Over", 250, 270);
            return;
        }
        if (nextRound) {
            // Draw score and lives at the top
            g.setFont(gameFont.deriveFont(20f));
            g.setColor(WHITE);
            g.drawString("SCORE", 5, 25);
            g.setColor(GREEN);
            g.drawString(String.valueOf(score), 85, 25);
            // Draw lives at the top right, spaced from the right edge
            g.setColor(WHITE);
            String livesLabel = "LIVES";
            FontMetrics fm = g.getFontMetrics();
            int labelWidth = fm.stringWidth(livesLabel);
            int iconWidth = 23;
            int iconSpacing = 27;
            int rightMargin = 10; // pixels from right edge
            int totalIconsWidth = livesCount * iconWidth + (livesCount - 1) * (iconSpacing - iconWidth);
            int lifeX = WIDTH - rightMargin - totalIconsWidth;
            int labelX = lifeX - 8 - labelWidth; // 8px gap between label and first icon
            g.drawString(livesLabel, labelX, 25);
            for (int i = 0; i < livesCount; i++) {
                g.drawImage(images.get("ship"), (lifeX + i * iconSpacing), 7, iconWidth, iconWidth, null);
            }
            // Draw centered 'NEXT ROUND' text
            g.setFont(gameFont.deriveFont(Font.BOLD, 56f));
            g.setColor(WHITE);
            String msg = "NEXT ROUND";
            fm = g.getFontMetrics();
            int msgWidth = fm.stringWidth(msg);
            int msgX = (WIDTH - msgWidth) / 2;
            int msgY = HEIGHT / 2 + fm.getAscent() / 2;
            g.drawString(msg, msgX, msgY);
            // --- Fix: force the timer to keep running so next round resumes ---
            if (!inGame) {
                SwingUtilities.invokeLater(this::updateGame);
            }
            return;
        }
        // Draw score and lives
        g.setFont(gameFont.deriveFont(20f));
        g.setColor(WHITE);
        g.drawString("Score", 5, 25);
        g.setColor(GREEN);
        g.drawString(String.valueOf(score), 85, 25);
        g.setColor(WHITE);
        g.drawString("Lives", 640, 25);
        for (Life life : lives) {
            life.draw((Graphics2D) g);
        }
        // Draw blockers
        for (Blocker b : blockers) b.draw((Graphics2D) g);
        // Draw player (with blinking if invincible)
        boolean drawPlayer = true;
        if (playerInvincible) {
            // Blink: only draw on even 100ms intervals
            long t = (System.currentTimeMillis() - playerInvincibleStart) / 100;
            drawPlayer = (t % 2 == 0);
        }
        if (drawPlayer) {
            player.draw((Graphics2D) g);
        }
        // Draw enemies
        for (Enemy e : enemies) e.draw((Graphics2D) g);
        // Draw bullets
        for (Bullet b : bullets) b.draw((Graphics2D) g);
        for (Bullet b : enemyBullets) b.draw((Graphics2D) g);
        // Draw mystery
        for (Mystery m : mysteries) m.draw((Graphics2D) g);
        // Draw mystery score popups
        for (Iterator<MysteryScorePopup> it = mysteryPopups.iterator(); it.hasNext(); ) {
            MysteryScorePopup popup = it.next();
            popup.draw((Graphics2D) g);
            if (popup.isExpired()) it.remove();
        }
        // Draw explosions
        for (Explosion ex : explosions) ex.draw((Graphics2D) g);
        // --- PAUSE SCREEN ---
        if (paused) {
            // Dim the game screen
            g.setColor(new Color(0, 0, 0, 180));
            g.fillRect(0, 0, WIDTH, HEIGHT);
            // Draw "PAUSED" in big red font
            g.setFont(gameFont.deriveFont(Font.BOLD, 80f));
            g.setColor(RED);
            String pausedText = "PAUSED";
            FontMetrics fm = g.getFontMetrics();
            int px = (WIDTH - fm.stringWidth(pausedText)) / 2;
            int py = 200;
            g.drawString(pausedText, px, py);
            // Draw pause menu options
            g.setFont(gameFont.deriveFont(Font.BOLD, 40f));
            int menuStartY = py + 60;
            int menuSpacing = 55;
            for (int i = 0; i < pauseMenuOptions.length; i++) {
                String opt = pauseMenuOptions[i];
                int x = WIDTH / 2;
                int y = menuStartY + i * menuSpacing;
                if (i == pauseMenuIndex) {
                    g.setColor(Color.WHITE);
                    drawCenteredString((Graphics2D) g, opt, x, y);
                } else {
                    g.setColor(RED);
                    drawCenteredString((Graphics2D) g, opt, x, y);
                }
            }
            return;
        }
        // --- LEADERBOARD SCREEN ---
        if (leaderboardScreen) {
            // Draw moving/tiled background (like title screen)
            if (titleScreenBgImg != null) {
                int imgW = titleScreenBgImg.getWidth();
                int imgH = titleScreenBgImg.getHeight();
                for (int x = -titleBgOffset; x < WIDTH; x += imgW) {
                    for (int y = 0; y < HEIGHT; y += imgH) {
                        g.drawImage(titleScreenBgImg, x, y, null);
                    }
                }
                // Animate background offset
                titleBgOffset = (titleBgOffset + titleBgSpeed) % imgW;
            } else {
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, WIDTH, HEIGHT);
            }
            // Draw leaderboard title
            g.setFont(gameFont.deriveFont(48f));
            g.setColor(YELLOW);
            g.drawString("LEADERBOARD", WIDTH / 2 - 200, 100);
            // Draw < BACK
            g.setFont(gameFont.deriveFont(28f));
            g.setColor(BLUE);
            g.drawString("< BACK", 40, 80);
            // Draw leaderboard table
            int tableX = 80;
            int tableY = 150;
            int tableW = 640;
            int rowH = 32; // Fixed row height for clarity
            // Set column widths for clean separation and centered NAME
            int colRankW = 110; // RANK column width
            int colNameW = 300; // NAME column width (centered)
            int colScoreW = tableW - colRankW - colNameW; // SCORE column width
            // Header
            g.setColor(new Color(180, 120, 200));
            g.fillRect(tableX, tableY, tableW, rowH);
            g.setColor(Color.BLACK);
            g.setFont(gameFont.deriveFont(28f));
            // Calculate x positions for each column
            int rankColX = tableX + 12; // 12px left padding
            int nameColX = tableX + colRankW + 12; // 12px left padding after RANK
            int scoreColX = tableX + colRankW + colNameW + 12; // 12px left padding after NAME
            g.drawString("RANK", rankColX, tableY + 24);
            g.drawString("NAME", nameColX, tableY + 24);
            g.drawString("SCORE", scoreColX, tableY + 24);
            // Draw vertical lines to separate columns
            g.setColor(new Color(200, 200, 200));
            int lineY1 = tableY;
            int lineY2 = tableY + rowH * (Math.min(LEADERBOARD_ROWS, (HEIGHT - tableY - 40 - rowH) / rowH) + 1);
            g.drawLine(tableX + colRankW, lineY1, tableX + colRankW, lineY2);
            g.drawLine(tableX + colRankW + colNameW, lineY1, tableX + colRankW + colNameW, lineY2);
            // Entries
            java.util.List<SIPlayerScore> topPlayers = leaderboardManager.getTopPlayers();
            int totalRows = topPlayers.size();
            int maxTableHeight = HEIGHT - tableY - 40;
            int visibleRows = Math.min(LEADERBOARD_ROWS, (maxTableHeight - rowH) / rowH);
            int maxScroll = Math.max(0, totalRows - visibleRows);
            if (leaderboardScroll > maxScroll) leaderboardScroll = maxScroll;
            if (leaderboardScroll < 0) leaderboardScroll = 0;
            int startIdx = leaderboardScroll;
            for (int i = 0; i < visibleRows; i++) {
                int entryIdx = startIdx + i;
                int y = tableY + rowH * (i + 1);
                if (i % 2 == 0) g.setColor(new Color(40, 40, 40));
                else g.setColor(new Color(60, 60, 60));
                g.fillRect(tableX, y, tableW, rowH);
                g.setFont(gameFont.deriveFont(26f));
                g.setColor(Color.WHITE);
                if (entryIdx < totalRows) {
                    SIPlayerScore score = topPlayers.get(entryIdx);
                    // Left-align RANK in its column
                    g.drawString(String.valueOf(entryIdx + 1), rankColX, y + 24);
                    // Left-align NAME in its column (centered in table)
                    g.drawString(score.getName(), nameColX, y + 24);
                    // Left-align SCORE in its column
                    g.drawString(String.valueOf(score.getScore()), scoreColX, y + 24);
                } else {
                    g.drawString("-", rankColX, y + 24);
                }
            }
            // Draw scroll bar if needed
            if (maxScroll > 0) {
                int barX = tableX + tableW + 8;
                int barY = tableY + rowH;
                int barH = rowH * visibleRows;
                int barW = 12;
                g.setColor(new Color(180, 180, 180));
                g.fillRect(barX, barY, barW, barH);
                int thumbH = Math.max(24, barH * visibleRows / (totalRows + 1));
                int thumbY = barY + (barH - thumbH) * leaderboardScroll / maxScroll;
                g.setColor(new Color(120, 120, 120));
                g.fillRect(barX, thumbY, barW, thumbH);
                g.setColor(Color.WHITE);
                int arrowX = barX + barW / 2;
                int[] upX = {arrowX - 6, arrowX, arrowX + 6};
                int[] upY = {barY - 18, barY - 6, barY - 18};
                g.fillPolygon(upX, upY, 3);
                int[] downX = {arrowX - 6, arrowX, arrowX + 6};
                int[] downY = {barY + barH + 18, barY + barH + 6, barY + barH + 18};
                g.fillPolygon(downX, downY, 3);
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
                // Find leftmost and rightmost enemy (include width for rightmost)
                int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
                for (Enemy e : enemies) {
                    if (e.x < minX) minX = e.x;
                    if (e.x + 40 > maxX) maxX = e.x + 40; // 40 = enemy width
                }
                boolean hitEdge = false;
                if (direction == 1 && maxX + step > rightBound) hitEdge = true;
                if (direction == -1 && minX - step < leftBound) hitEdge = true;
                if (hitEdge) {
                    // Only move down and reverse if NOT already at the edge
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
                int totalRows = leaderboardManager.getTotalScores();
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

    private void handleLeaderboardKey(int keyCode) {
        java.util.List<SIPlayerScore> topPlayers = leaderboardManager.getTopPlayers();
        int totalRows = topPlayers.size();
        int dynamicRows = LEADERBOARD_ROWS;
        int maxScroll = Math.max(0, totalRows - dynamicRows);
        if (keyCode == KeyEvent.VK_UP) {
            leaderboardScroll = Math.max(0, leaderboardScroll - 1);
            repaint();
        } else if (keyCode == KeyEvent.VK_DOWN) {
            leaderboardScroll = Math.min(maxScroll, leaderboardScroll + 1);
            repaint();
        } else if (keyCode == KeyEvent.VK_ESCAPE || keyCode == KeyEvent.VK_LEFT) {
            leaderboardScreen = false;
            repaint();
        }
    }
}
