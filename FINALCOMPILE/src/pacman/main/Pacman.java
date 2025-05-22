package pacman.main;

import javax.swing.*;

import core.AbstractGamePanel;
import core.LeaderboardPlayer;
import core.LeaderboardSorter;
import core.MainWindow;
import core.Sound;
//import pacman.leaderboard.PlayerScore;
//import pacman.leaderboard.LeaderboardManager;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.List;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;

import pacman.main.Game;
import pacman.main.state.PMLeaderboardObserver;

public class Pacman extends AbstractGamePanel implements ActionListener, KeyListener, MouseWheelListener {
    private Game game;
    private PMLeaderboardObserver observer;
    
    
    class Block {
        int x, y, width, height;
        Image image;
        int startX, startY;
        char direction = 'U'; // U D L R
        int velocityX = 0;
        int velocityY = 0;
        int collisionPadding = 4; // Padding on all sides
        GhostState state = GhostState.NORMAL;
        long stateTimer = 0;
        int personality;

        Block(Image image, int x, int y, int height, int width) {
            this.x = x;
            this.y = y;
            this.height = height;
            this.width = width;
            this.image = image;
            this.startX = x;
            this.startY = y;
        }

        void updateDirection(char direction) {
            char prevDirection = this.direction;
            this.direction = direction;
            updateVelocity();
            this.x += this.velocityX;
            this.y += this.velocityY;

            // Collision check with walls after move
            for (Block wall : walls) {
                if (collision(this, wall)) {
                    this.x -= this.velocityX;
                    this.y -= this.velocityY;
                    this.direction = prevDirection;
                    updateVelocity();
                }
            }
        }

        private void updateVelocity() {
            int speed;

            if (this == pacman) {
                speed = PACMAN_SPEED;
            } else {
                // Ghost speeds
                if (this.state == GhostState.FRIGHTENED) {
                    speed = FRIGHTENED_GHOST_SPEED;
                } else {
                    speed = BASE_GHOST_SPEED;
                }
            }


            // Set velocity based on direction
            if (this.direction == 'U') {
                this.velocityX = 0;
                this.velocityY = -speed;
            } else if (this.direction == 'D') {
                this.velocityX = 0;
                this.velocityY = speed;
            } else if (this.direction == 'L') {
                this.velocityY = 0;
                this.velocityX = -speed;
            } else if (this.direction == 'R') {
                this.velocityY = 0;
                this.velocityX = speed;
            }
        }


        void reset() {
            this.x = this.startX;
            this.y = this.startY;
            this.velocityX = 0;
            this.velocityY = 0;
            this.direction = 'R'; // Reset to default right-facing direction
        }
    }
    
	public static final String DATA_FILE = "leaderboard.dat";
	public LeaderboardPlayer currentPlayer;
	//public Sound soundSystem;
    // Game board dimensions
    private int columnCount = 19;
    private int rowCount = 21;
    private static int tileSize = 32;
    private int boardWidth = columnCount * tileSize;
    private int boardHeight = rowCount * tileSize;

    // Ghost personality identifiers
    private static final int BLINKY = 0;
    private static final int PINKY = 1;
    private static final int INKY = 2;
    private static final int CLYDE = 3;
    private static final int BASE_GHOST_SPEED = tileSize / 5; // Slower than original tileSize/4
    private static final int FRIGHTENED_GHOST_SPEED = tileSize / 10; // Even slower when frightened
    private static final int PACMAN_SPEED = tileSize / 4; // Faster than ghosts
    private enum GhostMode { SCATTER, CHASE }
    private GhostMode currentMode = GhostMode.SCATTER;
    private long modeTimer = 0;
    private long frightenedTimer = 0;
    private int level = 1;

    // Cherry
    private Image cherryImage;
    private Image cherry2Image;
    private Block cherry;
    private boolean cherryActive = false;
    private long cherryTimer = 0;
    private final int CHERRY_POINTS = 100;
    private final long CHERRY_DURATION = 10000; // 10 seconds cherry stays visible
    private final long CHERRY_INTERVAL = 30000; // Cherries appear every 30 seconds

    // Images
    private Image wallImage;
    private Image blueGhostImage;
    private Image orangeGhostImage;
    private Image pinkGhostImage;
    private Image redGhostImage;
    private Image pacmanUpImage;
    private Image pacmanDownImage;
    private Image pacmanRightImage;
    private Image pacmanLeftImage;
    private Image scaredGhostImage;
    private Image eyesImage;
    private Image pacmanWholeImage;

    // Pacman animation
    private boolean pacmanMouthOpen = true;
    private long pacmanAnimationTimer = 0;
    private final long PACMAN_ANIMATION_INTERVAL = 200; // 200ms between mouth open/close       

    //Entity or Environment List
    HashSet<Block> walls;
    HashSet<Block> foods;
    HashSet<Block> ghosts;
    HashSet<Block> powerPellets;

    //Pacman
    Block pacman;
    Timer gameLoop;
    char[] directions = {'U', 'D', 'R', 'L'}; // up down right left
    Random random = new Random();
    int score = 0;
    int lives = 3;
    boolean gameOver = false;
    char bufferedDirection = 'R'; // Store the last direction key pressed

    // Scared ghost mechanic
    private boolean ghostsScared = false;
    private long scaredEndTime = 0;
    private final int SCARED_DURATION = 6000; // milliseconds

    // Game Start Delays
    private boolean gameStarted = false;
    private long gameStartTimer = 0;
    private static final long INITIAL_GAME_START_DELAY = 5000; // 5 seconds for first start
    private static final long RESET_GAME_START_DELAY = 2000;  // 2 seconds for resets
    private boolean isRespawning = false;
    private long respawnTimer = 0;
    private static final long RESPAWN_DELAY = 2000;
    private boolean isFirstStart = true;

    // Game state
    public int gameState;
    public final int playState = 1;
    public final int pauseState = 2;
    public final int instructionState = 4;  // New state for instructions
    private Rectangle okButtonBounds;
    private boolean instructionsShown = false;

    // Custom Font
    private Font customFont;
    private final int SMALL_FONT_SIZE = 18;
    private final int LARGE_FONT_SIZE = 48;

    // RES PATHS
    private static final String RES_PATH = "res/";
    private static final String SOUNDS_PATH = RES_PATH + "sounds/";
    private static final String IMAGES_PATH = RES_PATH + "images/";
    private static final String FONTS_PATH = RES_PATH + "fonts/";

    // Pause Screen
    private Rectangle resumeButtonBounds;
    private Rectangle restartButtonBounds;
    private Rectangle menuButtonBounds;
    private Rectangle hoveredButton = null;

    // Leaderboard Screen
    //private LeaderboardManager leaderboardManager = new LeaderboardManager();
    //private LeaderboardSorter
    public final int leaderboardState = 3;
    private String enteredName = "";
    private boolean nameEntered = false;
    private Rectangle leaderboardButtonBounds;
    private Rectangle returnButtonBounds;
    private Rectangle backButtonBounds;

    // Leaderboard Scroll
    private int scrollOffset = 0;
    private int entryHeight = 30; // height per entry
    private int visibleEntries = 10;
    private int maxScrollOffset = 0;
    private boolean draggingScrollbar = false;
    private int dragStartY = 0;
    private int initialScrollOffset = 0;

    // Scrollbar dimensions
    private static final int SCROLLBAR_WIDTH = 12;
    private static final int SCROLLBAR_MARGIN = 8;
    private static final int MIN_THUMB_HEIGHT = 30;

    private Rectangle volumeUpBounds;
    private Rectangle volumeDownBounds;

    //private SoundManager soundManager = new SoundManager();

    // X = wall, O = skip, P = pac man, ' ' = food
    // Ghosts: b = blue, o = orange, p = pink, r = red
    private String[] tileMap = {
            "XXXXXXXXXXXXXXXXXXX",
            "X        X        X",
            "X XX XXX X XXX XX X",
            "X                 X",
            "X XX X XXXXX X XX X",
            "X    X       X    X",
            "XXXX XXXX XXXX XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXrXX X XXXX",
            "O      ObpoO      O",
            "XXXX X XXOXX X XXXX",
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

    public Pacman(Game game, PMLeaderboardObserver observer, Sound soundSystem, MainWindow mainWindow) {
        this.game = game;
        this.observer = observer;
        this.mainWindow = mainWindow;
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);
        ls=loadData();
        gameState = instructionState; // Start with instructions
        instructionsShown = false;
        
        // Load images 
        try {
                wallImage = ImageIO.read(getClass().getResource("/PM/images/wall.png"));
                blueGhostImage = ImageIO.read(getClass().getResource("/PM/images/blueGhost.png"));
                orangeGhostImage = ImageIO.read(getClass().getResource("/PM/images/orangeGhost.png"));
                redGhostImage = ImageIO.read(getClass().getResource("/PM/images/redGhost.png"));
                pinkGhostImage = ImageIO.read(getClass().getResource("/PM/images/pinkGhost.png"));
                pacmanUpImage = ImageIO.read(getClass().getResource("/PM/images/pacmanUp.png"));
                pacmanRightImage = ImageIO.read(getClass().getResource("/PM/images/pacmanRight.png"));
                pacmanDownImage = ImageIO.read(getClass().getResource("/PM/images/pacmanDown.png"));
                pacmanLeftImage = ImageIO.read(getClass().getResource("/PM/images/pacmanLeft.png"));
                pacmanWholeImage = ImageIO.read(getClass().getResource("/PM/images/pacmanWhole.png"));
                eyesImage = ImageIO.read(getClass().getResource("/PM/images/ghostEye.png"));
                scaredGhostImage = ImageIO.read(getClass().getResource("/PM/images/scaredGhost.png"));
                cherryImage = ImageIO.read(getClass().getResource("/PM/images/cherry.png"));
                cherry2Image = ImageIO.read(getClass().getResource("/PM/images/cherry2.png"));
            } catch (IOException e) {
                System.err.println("Error loading images: " + e.getMessage());
            }

        loadCustomFont();
        // Start cherry timer
        cherryTimer = System.currentTimeMillis() + CHERRY_INTERVAL;
        pacman = new Block(pacmanWholeImage, 0, 0, tileSize, tileSize);
        loadMap();
        for (Block ghost : ghosts) {
        for (char dir : directions) {
            ghost.updateDirection(dir);
            ghost.updateVelocity();
            ghost.x += ghost.velocityX;
            ghost.y += ghost.velocityY;

            boolean valid = true;
            for (Block wall : walls) {
                if (collision(ghost, wall)) {
                    valid = false;
                    break;
                }
            }

            // Reset position
            ghost.x -= ghost.velocityX;
            ghost.y -= ghost.velocityY;

            if (valid) {
                ghost.updateDirection(dir);
                break;
            }
        }
    }
        addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            Point click = e.getPoint();

            if (gameState == instructionState) {
                if (okButtonBounds != null && okButtonBounds.contains(click)) {
                    gameState = playState;
                    instructionsShown = true;
                    gameStartTimer = System.currentTimeMillis() + INITIAL_GAME_START_DELAY;
                    //soundManager.playBeginning();
                    // Don't play beginning sound until player clicks OK
                    //if (soundManager != null) {
                        /*soundManager.stopAllSounds();
                        soundManager.playBeginning();*/
                    	playSFX(22);
                    //}
                    repaint();
                    return;
                }
            }

            if (restartButtonBounds != null && restartButtonBounds.contains(click)) {
                //soundManager.stopAllSounds();
                //soundManager.playBeginning(); // Play beginning sound on restart
                playSFX(22);
            	resetGame();
                gameState = playState;
                repaint();
            }

            if (gameState == pauseState) {
                if (resumeButtonBounds != null && resumeButtonBounds.contains(click)) {
                    gameState = playState;
                    //soundManager.resumeAllSounds(); // Resume all sounds from where they were paused
                    repaint();
                } else if (restartButtonBounds != null && restartButtonBounds.contains(click)) {
                    //soundManager.stopAllSounds();
                    //soundManager.playBeginning(); // Play beginning sound on restart
                	playSFX(22);
                    resetGame();
                    gameState = playState;
                } else if (menuButtonBounds != null && menuButtonBounds.contains(click)) {
                    //soundManager.stopAllSounds();
                	//stopMusic();
                    returnToMenu();
                    // Add code to return to main menu here
                }

                // Volume control clicks
                if (volumeDownBounds != null && volumeDownBounds.contains(click)) {
                    //soundManager.adjustVolume(-1);
                    repaint();
                } else if (volumeUpBounds != null && volumeUpBounds.contains(click)) {
                    //soundManager.adjustVolume(1);
                    repaint();
                }
            }
            else if (gameOver && gameState != leaderboardState) {
                if (!nameEntered && enteredName.length() > 0) {
                    //ls.addPlayer(new PlayerScore(enteredName, score));
                    nameEntered = true;
                } else if (!nameEntered) {
                    // Save as GUEST if no name was entered
                    //leaderboardManager.addPlayer(new PlayerScore("GUEST", score));
                	//updateLeaderboard();
                    nameEntered = true;
                }

                if (leaderboardButtonBounds != null && leaderboardButtonBounds.contains(click)) {
                    gameState = leaderboardState;
                    repaint();
                } else if (returnButtonBounds != null && returnButtonBounds.contains(click)) {
                    //soundManager.stopAllSounds();
                    returnToMenu();
                    // Add code to return to main menu here
                } else if (restartButtonBounds != null && restartButtonBounds.contains(click)) {
                    //soundManager.stopAllSounds();
                    //soundManager.playBeginning();
                	playSFX(22);
                	resetGame();
                    gameState = playState;
                    repaint();
                }
            }
            else if (gameState == leaderboardState) {
                int x = e.getX();
                int y = e.getY();

                // Scrollbar drag
                int scrollbarX = boardWidth - 16;
                int scrollbarY = tileSize * 5;
                int scrollbarHeight = visibleEntries * entryHeight;

                List<LeaderboardPlayer> topPlayers = ls.getTopPlayers();
                int totalHeight = topPlayers.size() * entryHeight;
                int thumbHeight = Math.max((int)((float)scrollbarHeight / totalHeight * scrollbarHeight), 30);
                int maxThumbY = scrollbarHeight - thumbHeight;

                int thumbY = scrollbarY + (int)(((float)scrollOffset / maxScrollOffset) * maxThumbY);

                Rectangle thumbBounds = new Rectangle(scrollbarX, thumbY, 12, thumbHeight);
                if (thumbBounds.contains(x, y)) {
                    draggingScrollbar = true;
                    dragStartY = y;
                    initialScrollOffset = scrollOffset;
                }

                // Back button
                if (backButtonBounds != null && backButtonBounds.contains(click)) {
                    gameOver = true;
                    gameState = playState;
                    repaint();
                }
            }
        }
        
        @Override
        public void mousePressed(MouseEvent e) {
            if (gameState == leaderboardState) {
                Point click = e.getPoint();
                
                // Calculate scrollbar bounds
                int scrollbarX = boardWidth - SCROLLBAR_WIDTH - SCROLLBAR_MARGIN;
                int scrollbarTrackY = tileSize * 5 + entryHeight;
                int scrollbarTrackHeight = visibleEntries * entryHeight;
                
                // Calculate thumb position and size
                List<LeaderboardPlayer> topPlayers = ls.getTopPlayers();
                int totalHeight = topPlayers.size() * entryHeight;
                int thumbHeight = calculateThumbHeight(totalHeight, scrollbarTrackHeight);
                int thumbY = calculateThumbY(scrollOffset, totalHeight, scrollbarTrackHeight, thumbHeight, scrollbarTrackY);
                
                // Check if click is on thumb
                Rectangle thumbBounds = new Rectangle(scrollbarX, thumbY, SCROLLBAR_WIDTH, thumbHeight);
                if (thumbBounds.contains(click)) {
                    draggingScrollbar = true;
                    dragStartY = e.getY();
                    initialScrollOffset = scrollOffset;
                }
            }
        }
        
        @Override
        public void mouseReleased(MouseEvent e) {
            draggingScrollbar = false;
        }
    });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Point mouse = e.getPoint();
                Rectangle previous = hoveredButton;
                hoveredButton = null;

                if (gameState == instructionState) {
                    if (okButtonBounds != null && okButtonBounds.contains(mouse)) {
                        hoveredButton = okButtonBounds;
                    }
                }

                if (gameState == pauseState) {
                    if (resumeButtonBounds != null && resumeButtonBounds.contains(mouse)) {
                        hoveredButton = resumeButtonBounds;
                    } else if (restartButtonBounds != null && restartButtonBounds.contains(mouse)) {
                        hoveredButton = restartButtonBounds;
                    } else if (menuButtonBounds != null && menuButtonBounds.contains(mouse)) {
                        hoveredButton = menuButtonBounds;
                    }

                    // Volume control hover
                    if (volumeDownBounds != null && volumeDownBounds.contains(mouse)) {
                        hoveredButton = volumeDownBounds;
                    } else if (volumeUpBounds != null && volumeUpBounds.contains(mouse)) {
                        hoveredButton = volumeUpBounds;
                    }
                } else if (gameOver && gameState != leaderboardState) {
                    if (leaderboardButtonBounds != null && leaderboardButtonBounds.contains(mouse)) {
                        hoveredButton = leaderboardButtonBounds;
                    } else if (returnButtonBounds != null && returnButtonBounds.contains(mouse)) {
                        hoveredButton = returnButtonBounds;
                    } else if (restartButtonBounds != null && restartButtonBounds.contains(mouse)) {
                        hoveredButton = restartButtonBounds;
                    }

                } else if (gameState == leaderboardState) {
                    if (backButtonBounds != null && backButtonBounds.contains(mouse)) {
                        hoveredButton = backButtonBounds;
                    }
                } else if (gameOver && gameState != leaderboardState) {
                    if (restartButtonBounds != null && restartButtonBounds.contains(mouse)) {
                        hoveredButton = restartButtonBounds;
                    } else if (leaderboardButtonBounds != null && leaderboardButtonBounds.contains(mouse)) {
                        hoveredButton = leaderboardButtonBounds;
                    } else if (returnButtonBounds != null && returnButtonBounds.contains(mouse)) {
                        hoveredButton = returnButtonBounds;
                    }
                }

                // Always show hand if hovering any button
                if (hoveredButton != null) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                } else {
                    setCursor(Cursor.getDefaultCursor());
                }

                if (hoveredButton != previous) {
                    repaint();
                }
            }
            
            @Override
            public void mouseDragged(MouseEvent e) {
                if (draggingScrollbar && gameState == leaderboardState) {
                    List<LeaderboardPlayer> topPlayers = ls.getTopPlayers();
                    int totalHeight = topPlayers.size() * entryHeight;
                    int scrollbarTrackHeight = visibleEntries * entryHeight;
                    int thumbHeight = calculateThumbHeight(totalHeight, scrollbarTrackHeight);
                    
                    // Calculate available track space for thumb movement
                    int availableTrackSpace = scrollbarTrackHeight - thumbHeight;
                    
                    // Calculate new scroll position based on drag distance
                    int dragDistance = e.getY() - dragStartY;
                    float scrollRatio = (float)dragDistance / availableTrackSpace;
                    scrollOffset = initialScrollOffset + (int)(scrollRatio * maxScrollOffset);
                    
                    // Clamp to valid range
                    scrollOffset = Math.max(0, Math.min(scrollOffset, maxScrollOffset));
                    repaint();
                }
            }
        });
        
        //soundManager.playBeginning(); // Add this line
        gameStartTimer = System.currentTimeMillis() + INITIAL_GAME_START_DELAY;
        gameLoop = new Timer(40, this); // 25fps (1000/40)
        gameLoop.start();
        //gameState = playState;
        addMouseWheelListener(this);
    }
    
    public void loadMap() {
        walls = new HashSet<>();
        foods = new HashSet<>();
        ghosts = new HashSet<>();
        powerPellets = new HashSet<>();

        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < columnCount; c++) {
                String row = tileMap[r];
                char tileMapChar = row.charAt(c);

                int x = c * tileSize + (tileSize - tileSize) / 2; // stays the same
                int y = r * tileSize + (tileSize - tileSize) / 2; // stays the same

                switch (tileMapChar) {
                    case 'X': // WALLS
                        Block wall = new Block(wallImage, x, y, tileSize, tileSize);
                        walls.add(wall);
                        break;
                        case 'b': // BLUE GHOST
                        Block blueGhost = new Block(blueGhostImage, x, y, tileSize, tileSize);
                        blueGhost.personality = INKY;
                        blueGhost.state = GhostState.HOUSE;
                        blueGhost.stateTimer = System.currentTimeMillis() + 2000;
                        ghosts.add(blueGhost);
                        break;
                    case 'r': // RED GHOST
                        Block redGhost = new Block(redGhostImage, x, y, tileSize, tileSize);
                        redGhost.personality = BLINKY;
                        redGhost.state = GhostState.HOUSE;
                        redGhost.stateTimer = System.currentTimeMillis() + 1000;
                        ghosts.add(redGhost);
                        break;
                    case 'p': // PINK GHOST
                        Block pinkGhost = new Block(pinkGhostImage, x, y, tileSize, tileSize);
                        pinkGhost.personality = PINKY;
                        pinkGhost.state = GhostState.HOUSE;
                        pinkGhost.stateTimer = System.currentTimeMillis() + 1500;
                        ghosts.add(pinkGhost);
                        break;
                    case 'o': // ORANGE GHOST
                        Block orangeGhost = new Block(orangeGhostImage, x, y, tileSize, tileSize);
                        orangeGhost.personality = CLYDE;
                        orangeGhost.state = GhostState.HOUSE;
                        orangeGhost.stateTimer = System.currentTimeMillis() + 2500;
                        ghosts.add(orangeGhost);
                        break;
                    case 'P': // PACMAN
                            pacman.x = x;
                            pacman.y = y;
                            pacman.startX = x;  // Explicitly set startX
                            pacman.startY = y;  // Explicitly set startY
                            //pacman.direction = 'R'; // Set default direction
                            break;
                    case ' ': // FOOD
                        Block food = new Block(null, x + 14, y + 14, 4, 4);
                        foods.add(food);
                        break;
                    case 'O': // INSIDE WALLS (SKIP)
                        break;
                }
            }
        }

        // After loading all ghosts:
        for (Block ghost : ghosts) {
            // Ensure proper personality assignment
            if (ghost.personality < BLINKY || ghost.personality > CLYDE) {
                ghost.personality = BLINKY;
            }
            
            // Properly position ghosts within their grid cells
                ghost.x = (ghost.x / tileSize) * tileSize; // Align to grid
                ghost.y = (ghost.y / tileSize) * tileSize;

            // Set appropriate starting directions based on personality
            switch (ghost.personality) {
                case BLINKY: ghost.direction = 'L'; break;
                case PINKY: ghost.direction = 'U'; break;
                case INKY: ghost.direction = 'R'; break;
                case CLYDE: ghost.direction = 'D'; break;
            }
    
            // Initialize velocities
            ghost.updateVelocity();
        }   

        // Add power pellets in the 4 corners
        int pelletSize = 12;
        int offset = (tileSize - pelletSize) / 2;
        int[][] pelletPositions = {
                {1, 1},
                {1, columnCount - 2},
                {rowCount - 2, 1},
                {rowCount - 2, columnCount - 2}
        };
        for (int[] pos : pelletPositions) {
            int y = pos[0] * tileSize + offset;
            int x = pos[1] * tileSize + offset;
            powerPellets.add(new Block(null, x, y, pelletSize, pelletSize));
        }
    }
    private enum GhostState {
        NORMAL, FRIGHTENED, EYES, HOUSE
    }

    private Image getGhostImage(Block ghost) {
        if (ghost == null) return blueGhostImage;
        if (ghost.state == GhostState.FRIGHTENED) return scaredGhostImage;
        if (ghost.state == GhostState.EYES) return eyesImage; 
        
        switch (ghost.personality) {
            case BLINKY: return redGhostImage;
            case PINKY: return pinkGhostImage;
            case INKY: return blueGhostImage;
            case CLYDE: return orangeGhostImage;
            default: return blueGhostImage;
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g); // Always draw game background

        if (gameState == instructionState) {
            drawInstructionsScreen(g);
            return;
        }

        if (gameState == leaderboardState) {
            drawLeaderboardScreen(g); // Only draw leaderboard
            return; // Don't draw any other overlays
        }

        if (gameState == pauseState) {
            drawPauseScreen(g);
            //soundManager.stopAllSounds();
        } else if (gameOver) {
            drawGameOverScreen(g);
        }
    }

    private void animatePacman() {
        long currentTime = System.currentTimeMillis();
        
        if (currentTime >= pacmanAnimationTimer) {
            // Check if Pac-Man is actually moving
            if (Math.abs(pacman.velocityX) > 0 || Math.abs(pacman.velocityY) > 0) {
                pacmanMouthOpen = !pacmanMouthOpen;
                
                if (pacmanMouthOpen) {
                    // Use directional images when mouth is open
                    switch (pacman.direction) {
                        case 'U': pacman.image = pacmanUpImage; break;
                        case 'D': pacman.image = pacmanDownImage; break;
                        case 'L': pacman.image = pacmanLeftImage; break;
                        case 'R': pacman.image = pacmanRightImage; break;
                    }
                } else {
                    // Use whole image when mouth is closed
                    pacman.image = pacmanWholeImage;
                }
                
                // Reset animation timer
                pacmanAnimationTimer = currentTime + PACMAN_ANIMATION_INTERVAL;
            } else {
                // When not moving, use whole Pac-Man image
                pacman.image = pacmanWholeImage;
            }
        }
    }

    public void draw(Graphics g) {
        super.paintComponent(g);
        
        // Draw all game elements immediately
        for (Block wall : walls) {
            g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);
        }
        
        // Draw pellets and food (always visible)
        g.setColor(Color.YELLOW);
        for (Block food : foods) {
            g.fillRect(food.x, food.y, food.width, food.height);
        }
        
        g.setColor(Color.WHITE);
        for (Block pellet : powerPellets) {
            g.fillOval(pellet.x, pellet.y, pellet.width, pellet.height);
        }

        // Draw cherry if active
        if (cherryActive && cherry != null) {
            g.drawImage(cherry.image, cherry.x, cherry.y, cherry.width, cherry.height, null);
        }
        
        // Draw ghosts (always visible)
        for (Block ghost : ghosts) {
            g.drawImage(getGhostImage(ghost), ghost.x, ghost.y, ghost.width, ghost.height, null);
        }
        
        // Draw Pacman (always visible)
        g.drawImage(pacman.image, pacman.x, pacman.y, pacman.width, pacman.height, null);
        
        // Draw score and lives
        g.setFont(customFont.deriveFont(Font.PLAIN, SMALL_FONT_SIZE));
        g.setColor(Color.WHITE);
        g.drawString("Lives: " + lives + "  Score: " + score, tileSize / 2, tileSize / 2);
        
        // Draw game over/pause screens if needed
        if (gameState == pauseState) {
            drawPauseScreen(g);
        } else if (gameOver) {
            drawGameOverScreen(g);
        }
    }

    private void updateCherry() {
        long currentTime = System.currentTimeMillis();
        
        // If cherry is active but time expired, remove it
        if (cherryActive && currentTime > cherryTimer) {
            cherryActive = false;
            cherry = null;
            // Set next cherry appearance
            cherryTimer = currentTime + CHERRY_INTERVAL;
        }
        
        // If cherry not active and it's time to spawn one
        else if (!cherryActive && currentTime > cherryTimer) {
            spawnCherry();
            // Set despawn timer
            cherryTimer = currentTime + CHERRY_DURATION;
        }
    }

    private void spawnCherry() {
        List<Point> validPositions = new ArrayList<>();
        
        for (int r = 1; r < rowCount - 1; r++) {
            for (int c = 1; c < columnCount - 1; c++) {
                int x = c * tileSize;
                int y = r * tileSize;
                
                // Check if position is valid
                boolean valid = true;
                
                // Skip 'O' positions in tilemap
                if (r < tileMap.length && c < tileMap[r].length() && tileMap[r].charAt(c) == 'O') {
                    valid = false;
                    continue;
                }
                
                // Create a temporary block to check collisions
                Block temp = new Block(null, x, y, tileSize, tileSize);
                
                // Check walls
                for (Block wall : walls) {
                    if (collision(temp, wall)) {
                        valid = false;
                        break;
                    }
                }
                
                // Skip if in ghost house area
                if (x > boardWidth/2 - tileSize*2 && x < boardWidth/2 + tileSize*2 &&
                    y > tileSize*7 && y < tileSize*11) {
                    valid = false;
                }
                
                if (valid) {
                    validPositions.add(new Point(x, y));
                }
            }
        }
        
        // If we found valid positions, pick one randomly
        if (!validPositions.isEmpty()) {
            Point pos = validPositions.get(random.nextInt(validPositions.size()));
            cherry = new Block(cherryImage, pos.x, pos.y, tileSize, tileSize);
            cherryActive = true;
        }
    }

    private void checkCherryCollision() {
        if (cherryActive && cherry != null && collision(pacman, cherry)) {
            score += CHERRY_POINTS;
            cherryActive = false;
            cherry = null;
            cherryTimer = System.currentTimeMillis() + CHERRY_INTERVAL;
            //soundManager.playEatFruit(); // Add this line
            playSFX(25);
        }
    }

    private void animateCherry() {
        if (cherryActive && cherry != null) {
            // Switch between cherry images every 500ms for animation
            if ((System.currentTimeMillis() / 500) % 2 == 0) {
                cherry.image = cherryImage;
            } else {
                cherry.image = cherry2Image;
            }
        }
    }

    // Ghost behavior timing patterns (in milliseconds)
    private final int[][] MODE_SEQUENCE = {
        {7000, 20000},  // Level 1: 7s scatter, 20s chase
        {7000, 20000},  // Level 2
        {5000, 20000},  // Level 3
        {5000, 20000}   // Level 4+ (repeats last pattern)
    };

    // Ghost target positions for scatter mode (corners)
    private final Point[] SCATTER_TARGETS = {
        new Point(0, 0),                    // Red ghost - top left
        new Point(boardWidth, 0),           // Pink ghost - top right
        new Point(0, boardHeight),          // Blue ghost - bottom left
        new Point(boardWidth, boardHeight)  // Orange ghost - bottom right
    };

    private void handleGhostStates() {
        long currentTime = System.currentTimeMillis();
        boolean anyGhostStillFrightened = false;
        
        for (Block ghost : ghosts) {
            switch (ghost.state) {
                case FRIGHTENED:
                    if (currentTime > ghost.stateTimer) {
                        ghost.state = GhostState.NORMAL;
                        ghost.image = getGhostImage(ghost);
                    } else {
                        anyGhostStillFrightened = true;
                    }
                    break;
                    
                case EYES:
                    if (currentTime > ghost.stateTimer) {
                        // Directly teleport to house when timer expires
                        Point houseTarget = getGhostHouseTarget();
                        ghost.state = GhostState.HOUSE;
                        ghost.stateTimer = currentTime + 1000; // 1 second in house
                        ghost.image = getGhostImage(ghost);
                        ghost.x = houseTarget.x;
                        ghost.y = houseTarget.y;
                    }
                    break;
                    
                case HOUSE:
                    if (currentTime > ghost.stateTimer) {
                        ghost.state = GhostState.NORMAL;
                        ghost.image = getGhostImage(ghost);
                    }
                    break;

                case NORMAL:
                    // No specific action needed
                    break;
            }
        }
        
        // Update global frightened state and sound
        if (ghostsScared) {
            if (!anyGhostStillFrightened) {
                ghostsScared = false;
                //soundManager.stopGhostScared();
            }
        }
    }
        
    private void setGhostsScared() {
        ghostsScared = true;
        scaredEndTime = System.currentTimeMillis() + SCARED_DURATION;
        frightenedTimer = scaredEndTime;
        //soundManager.playGhostScared(); // Play scared sound
        previousMode = currentMode;
        remainingModeTime = Math.max(0, modeTimer - System.currentTimeMillis());

        //soundManager.stopGhostScared(); // Stop scared sound
        //soundManager.playGhostScared(); // Play scared sound
        playSFX(26);
        for (Block ghost : ghosts) {
            if (ghost.state != GhostState.EYES) {
                ghost.state = GhostState.FRIGHTENED;
                ghost.stateTimer = scaredEndTime;
                ghost.image = scaredGhostImage;
                ghost.updateVelocity();
                
                char oppositeDir = getOppositeDirection(ghost.direction);
                
                // Save original position to test move
                int originalX = ghost.x;
                int originalY = ghost.y;
                
                // Test the opposite direction
                boolean validReversal = true;
                
                // Simulate movement in opposite direction
                int testX = originalX;
                int testY = originalY;
                switch (oppositeDir) {
                    case 'L': testX -= tileSize / 4; break;
                    case 'R': testX += tileSize / 4; break;
                    case 'U': testY -= tileSize / 4; break;
                    case 'D': testY += tileSize / 4; break;
                }
                
                // Check if new position would collide with wall
                ghost.x = testX;
                ghost.y = testY;
                for (Block wall : walls) {
                    if (collision(ghost, wall)) {
                        validReversal = false;
                        break;
                    }
                }
                
                // Reset position
                ghost.x = originalX;
                ghost.y = originalY;
                
                // Apply direction change if valid, otherwise find another valid direction
                if (validReversal) {
                    ghost.updateDirection(oppositeDir);
                } else {
                    // Find a valid direction (existing logic remains the same)
                    List<Character> validDirs = new ArrayList<>();
                    for (char dir : directions) {
                        testX = originalX;
                        testY = originalY;
                        switch (dir) {
                            case 'L': testX -= tileSize / 4; break;
                            case 'R': testX += tileSize / 4; break;
                            case 'U': testY -= tileSize / 4; break;
                            case 'D': testY += tileSize / 4; break;
                        }
                        
                        ghost.x = testX;
                        ghost.y = testY;
                        boolean valid = true;
                        for (Block wall : walls) {
                            if (collision(ghost, wall)) {
                                valid = false;
                                break;
                            }
                        }
                        ghost.x = originalX;
                        ghost.y = originalY;
                        
                        if (valid) validDirs.add(dir);
                    }
                    
                    if (!validDirs.isEmpty()) {
                        ghost.updateDirection(validDirs.get(random.nextInt(validDirs.size())));
                    }
                    // If no valid direction, ghost will keep current direction
                }
            }
        }
    }

    private void unsetGhostsScared() {
        ghostsScared = false;
        //soundManager.stopGhostScared();  // Ensure sound stops immediately
        
        for (Block ghost : ghosts) {
            if (ghost.state == GhostState.FRIGHTENED) {
                ghost.state = GhostState.NORMAL;
                ghost.image = getGhostImage(ghost);
            }
        }
    }

    private GhostMode previousMode = GhostMode.SCATTER;
    private long remainingModeTime = 0;

    private void updateGhostModes() {
        long currentTime = System.currentTimeMillis();
        
        if (ghostsScared) {
            if (currentTime > frightenedTimer) {
                unsetGhostsScared();
                // Restore previous mode with remaining time
                currentMode = previousMode;
                modeTimer = currentTime + remainingModeTime;
            }
            return;
        }

        if (currentTime > modeTimer) {
            // Switch between scatter and chase modes
            currentMode = (currentMode == GhostMode.SCATTER) ? GhostMode.CHASE : GhostMode.SCATTER;
            modeTimer = currentTime + getCurrentModeDuration();
        }
    }

    private int getCurrentModeDuration() {
        int levelIndex = Math.min(level - 1, MODE_SEQUENCE.length - 1);
        return MODE_SEQUENCE[levelIndex][currentMode == GhostMode.SCATTER ? 0 : 1];
    }

    private Point getGhostHouseTarget() {
        return new Point((columnCount / 2) * tileSize, 9 * tileSize); // row 9 = center line of house
    }

    private void moveGhosts() {
        long currentTime = System.currentTimeMillis();
        
        for (Block ghost : ghosts) {
            // Skip movement for EYES state (they remain stationary)
            if (ghost.state == GhostState.EYES) {
                continue;
            }
            
            // Skip if ghost is in house and timer not expired
            if ((ghost.state == GhostState.HOUSE) && currentTime < ghost.stateTimer) {
                continue;
            }

            // Handle state transitions
            if (ghost.state == GhostState.HOUSE && currentTime >= ghost.stateTimer) {
                // After house timer expires, revert to normal state
                ghost.state = GhostState.NORMAL;
                ghost.image = getGhostImage(ghost); // Update image
                
                // Help ghost leave the house by setting an initial direction
                ghost.updateDirection('U');
                
                // Make sure they have a valid position outside the house walls
                Point houseExit = new Point(boardWidth/2, tileSize * 8);
                ghost.x = houseExit.x;
                ghost.y = houseExit.y;
                ghost.updateVelocity(); // Force velocity refresh for new state+direction 
            }

            // Determine target based on state
            Point target;
            if (ghostsScared && ghost.state == GhostState.FRIGHTENED) {
                // Use improved random movement for frightened ghosts
                moveGhostRandomly(ghost);
                continue;
            }
            else {
                target = calculateGhostTarget(ghost);
            }

            // Move ghost toward target
            moveGhostToTarget(ghost, target);
            
            // Apply tunnel warping at the end
            handleTunnelWarping(ghost);
        }
    }
    
       private boolean isInTunnel(Block entity) {
        // Check if entity is in the tunnel row (y coordinate)
        boolean inTunnelRow = entity.y >= (tileSize * 9) - 4 && entity.y <= (tileSize * 9) + 4;
        
        // Only consider it a tunnel if also near the edges of the screen
        return inTunnelRow && (entity.x < tileSize * 2 || entity.x > boardWidth - (tileSize * 2));
    }

    private void handleTunnelWarping(Block entity) {
        // Apply warping for all entities consistently
        if (isInTunnel(entity)) {
            if (entity.x < -tileSize) {
                // Warp from left to right
                entity.x = boardWidth;
            } else if (entity.x > boardWidth) {
                // Warp from right to left
                entity.x = -tileSize;
            }
        }
    }

    private void moveGhostToTarget(Block ghost, Point target) {
        int originalX = ghost.x;
        int originalY = ghost.y;
        
        // Remove special handling for EYES state

        // Try to align ghost to grid before changing direction
        boolean isAtIntersection = false;
        
        // Only align at intersections or when changing direction
        if (countValidDirections(ghost) > 2) {
            alignGhostToGrid(ghost);
            isAtIntersection = true;
        }
        
        // Check if ghost is in tunnel
        boolean inTunnel = isInTunnel(ghost);
        
        List<Character> possibleDirections = new ArrayList<>();
        char oppositeDir = getOppositeDirection(ghost.direction);
        
        for (char dir : directions) {
            // Prevent vertical movement in the tunnel areas only
            if (inTunnel && (dir == 'U' || dir == 'D')) {
                continue;
            }
            
            // Don't allow reversal direction unless it's the only option
            if (dir == oppositeDir) continue;
            
            // Test movement
            ghost.x = originalX;
            ghost.y = originalY;
            
            // Move in smaller increments for better collision detection
            int steps = 4; // Break movement into 4 steps
            int dx = 0, dy = 0;
            
            switch (dir) {
                case 'L': dx = -tileSize / 4; break;
                case 'R': dx = tileSize / 4; break;
                case 'U': dy = -tileSize / 4; break;
                case 'D': dy = tileSize / 4; break;
            }
            
            // Check movement validity
            boolean validMove = true;
            for (int i = 1; i <= steps; i++) {
                ghost.x = originalX + (dx * i / steps);
                ghost.y = originalY + (dy * i / steps);
                
                for (Block wall : walls) {
                    if (collision(ghost, wall)) {
                        validMove = false;
                        break;
                    }
                }
                if (!validMove) break;
            }
            
            // Reset position
            ghost.x = originalX;
            ghost.y = originalY;
            
            if (validMove) {
                possibleDirections.add(dir);
            }
        }
        
        // Choose direction (if available)
        if (!possibleDirections.isEmpty()) {
            // Stick to previous direction unless forced to change
            boolean needsNewDirection = isAtIntersection || !possibleDirections.contains(ghost.direction);

            if (needsNewDirection) {
                if (ghost.state == GhostState.FRIGHTENED) {
                    ghost.updateDirection(possibleDirections.get(random.nextInt(possibleDirections.size())));
                } else {
                    char newDir = getBestDirection(ghost, possibleDirections, target);
                    ghost.updateDirection(newDir);
                }
            }
        }
        
        // Always enforce velocity to match state, even if direction hasn't changed
        ghost.updateVelocity();

        // Move ghost in current direction
        ghost.x += ghost.velocityX;
        ghost.y += ghost.velocityY;
        
        // Handle tunnel warping for ghosts
        handleTunnelWarping(ghost);
    }
    
    private char getBestDirection(Block ghost, List<Character> possibleDirections, Point target) {
        char currentDir = ghost.direction;
        char oppositeDir = getOppositeDirection(currentDir);
        char bestDir = currentDir;
        double minDistance = Double.MAX_VALUE;

        for (char dir : possibleDirections) {
            // Avoid reversing unless it's the *only* valid move
            if (dir == oppositeDir && possibleDirections.size() > 1) continue;

            int newX = ghost.x;
            int newY = ghost.y;

            switch (dir) {
                case 'L': newX -= tileSize; break;
                case 'R': newX += tileSize; break;
                case 'U': newY -= tileSize; break;
                case 'D': newY += tileSize; break;
            }

            double distance = Math.hypot(newX - target.x, newY - target.y);
            if (distance < minDistance) {
                minDistance = distance;
                bestDir = dir;
            }
        }

        return bestDir;
    }
    
    private void alignGhostToGrid(Block ghost) {
        // Align to nearest grid position
        int gridX = Math.round((float)ghost.x / tileSize) * tileSize;
        int gridY = Math.round((float)ghost.y / tileSize) * tileSize;
        
        // Only align if we're close enough to avoid teleporting
        if (Math.abs(ghost.x - gridX) < tileSize/4) {
            ghost.x = gridX;
        }
        if (Math.abs(ghost.y - gridY) < tileSize/4) {
            ghost.y = gridY;
        }
    }
    
    private int countValidDirections(Block ghost) {
        int count = 0;
        int originalX = ghost.x;
        int originalY = ghost.y;
        
        for (char dir : directions) {
            // Test movement
            int newX = originalX;
            int newY = originalY;
            
            switch (dir) {
                case 'L': newX -= tileSize / 4; break;
                case 'R': newX += tileSize / 4; break;
                case 'U': newY -= tileSize / 4; break;
                case 'D': newY += tileSize / 4; break;
            }
            
            ghost.x = newX;
            ghost.y = newY;
            
            boolean validMove = true;
            for (Block wall : walls) {
                if (collision(ghost, wall)) {
                    validMove = false;
                    break;
                }
            }
            
            if (validMove) count++;
        }
        
        // Reset position
        ghost.x = originalX;
        ghost.y = originalY;
        
        return count;
    }
    
    private char getOppositeDirection(char direction) {
        switch (direction) {
            case 'L': return 'R';
            case 'R': return 'L';
            case 'U': return 'D';
            case 'D': return 'U';
            default: return 'U'; // Default
        }
    }
    
    private void moveGhostRandomly(Block ghost) {
        // Get current and opposite directions
        char currentDir = ghost.direction;
        char oppositeDir = getOppositeDirection(currentDir);
        
        // Check if ghost is in tunnel
        boolean inTunnel = isInTunnel(ghost);
        
        // Get valid directions (excluding opposite)
        List<Character> validDirs = new ArrayList<>();
        int originalX = ghost.x;
        int originalY = ghost.y;
        
        // The key issue - Check if we can continue in current direction
        boolean canContinue = false;
        
        for (char dir : directions) {
            if (dir == oppositeDir) continue; // Don't allow 180-degree turns
            
            // Prevent vertical movement only in tunnel areas, not the entire row
            if (inTunnel && (dir == 'U' || dir == 'D')) {
                continue;
            }
            
            // Test movement
            int newX = originalX;
            int newY = originalY;
            
            switch (dir) {
                case 'L': newX -= tileSize / 4; break;
                case 'R': newX += tileSize / 4; break;
                case 'U': newY -= tileSize / 4; break;
                case 'D': newY += tileSize / 4; break;
            }
            
            ghost.x = newX;
            ghost.y = newY;
            
            boolean validMove = true;
            for (Block wall : walls) {
                if (collision(ghost, wall)) {
                    validMove = false;
                    break;
                }
            }
            
            // Reset position
            ghost.x = originalX;
            ghost.y = originalY;
            
            if (validMove) {
                validDirs.add(dir);
                if (dir == currentDir) {
                    canContinue = true;
                }
            }
        }
        
        // Choose direction:
        if (!validDirs.isEmpty()) {
            // If we can't continue in current direction, we must change
            if (!canContinue) {
                int randomIndex = random.nextInt(validDirs.size());
                ghost.updateDirection(validDirs.get(randomIndex));
            } 
            // If at intersection (multiple options), randomly decide whether to turn
            else if (validDirs.size() > 1 && random.nextInt(3) == 0) {
                // Remove current direction to force a turn at intersection
                List<Character> turnOptions = new ArrayList<>(validDirs);
                turnOptions.remove(Character.valueOf(currentDir));
                
                if (!turnOptions.isEmpty()) {
                    int randomIndex = random.nextInt(turnOptions.size());
                    ghost.updateDirection(turnOptions.get(randomIndex));
                } else {
                    // Keep going same direction if somehow we have no other options
                    ghost.updateDirection(currentDir);
                }
            } else {
                // Continue in same direction
                ghost.updateDirection(currentDir);
            }
        }
        
        // Ensure the ghost moves by applying velocity
        ghost.x += ghost.velocityX;
        ghost.y += ghost.velocityY;
        
        // Make sure to apply tunnel warping for frightened ghosts too
        handleTunnelWarping(ghost);
    }
    private Point calculateGhostTarget(Block ghost) {
        Point target = new Point();
        
        if (ghost.state == GhostState.NORMAL) {
            if (System.currentTimeMillis() < gameStartTimer + 10000) { // First 10 seconds
                // Move randomly instead of chasing
                target.x = ghost.x + random.nextInt(3 * tileSize) - tileSize;
                target.y = ghost.y + random.nextInt(3 * tileSize) - tileSize;
                return target;
            }
            if (currentMode == GhostMode.SCATTER) {
                target.x = SCATTER_TARGETS[ghost.personality].x;
                target.y = SCATTER_TARGETS[ghost.personality].y;
            } else {
                // Chase mode logic (unchanged)
                switch (ghost.personality) {
                    case BLINKY: 
                        target.x = pacman.x;
                        target.y = pacman.y;
                        break;
                        
                    case PINKY:
                        target.x = pacman.x;
                        target.y = pacman.y;
                        
                        switch (pacman.direction) {
                            case 'U': target.y -= 4 * tileSize; break;
                            case 'D': target.y += 4 * tileSize; break;
                            case 'L': target.x -= 4 * tileSize; break;
                            case 'R': target.x += 4 * tileSize; break;
                        }
                        break;
                        
                    case INKY:
                        Block blinky = null;
                        for (Block g : ghosts) {
                            if (g.personality == BLINKY) {
                                blinky = g;
                                break;
                            }
                        }
                        
                        target.x = pacman.x;
                        target.y = pacman.y;
                        
                        switch (pacman.direction) {
                            case 'U': target.y -= 2 * tileSize; break;
                            case 'D': target.y += 2 * tileSize; break;
                            case 'L': target.x -= 2 * tileSize; break;
                            case 'R': target.x += 2 * tileSize; break;
                        }
                        
                        if (blinky != null) {
                            int vectorX = target.x - blinky.x;
                            int vectorY = target.y - blinky.y;
                            
                            target.x = blinky.x + (vectorX * 2);
                            target.y = blinky.y + (vectorY * 2);
                        }
                        break;
                        
                    case CLYDE:
                        double distance = Math.sqrt(Math.pow(ghost.x - pacman.x, 2) + Math.pow(ghost.y - pacman.y, 2));
                        
                        if (distance > 8 * tileSize) {
                            target.x = pacman.x;
                            target.y = pacman.y;
                        } else {
                            target.x = SCATTER_TARGETS[CLYDE].x;
                            target.y = SCATTER_TARGETS[CLYDE].y;
                        }
                        break;
                }
            }
        } else if (ghost.state == GhostState.FRIGHTENED) {
            // Random movement handled separately
            target.x = ghost.x;
            target.y = ghost.y;
        }
        
        return target;
    }

    public void actionPerformed(ActionEvent e) {
        long currentTime = System.currentTimeMillis();
        
        if (gameState == instructionState) {
            repaint();
            return;
        }

        if (gameState == playState) {
            // Handle respawn delay if active
            if (isRespawning && currentTime >= respawnTimer) {
                resetPositions();
                isRespawning = false;
            }
            
            // Check if game start delay is active
            if (!gameStarted) {
                if (currentTime >= gameStartTimer) {
                    gameStarted = true;
                }
                repaint();
                return; // Skip game logic until start delay is over
            }
            
            if (!gameOver && !isRespawning) {
                // Existing game logic
                handleBufferedDirection();
                updateCherry();
                animateCherry();
                checkCherryCollision();
                updatePacmanDirection();
                updateGhostModes();
                handleGhostStates();
                moveGhosts();
                checkFoodCollisions();
                checkPowerPelletCollisions();
                checkGhostCollisions();
                checkGameConditions();
                animatePacman();
            }
        }
        repaint();
    }
    
    private void handleBufferedDirection() {
        if (bufferedDirection != pacman.direction) {
            // Save original position and direction
            int originalX = pacman.x;
            int originalY = pacman.y;
            char originalDirection = pacman.direction;
            
            // Try to move in buffered direction
            pacman.direction = bufferedDirection;
            pacman.updateVelocity();
            pacman.x += pacman.velocityX;
            pacman.y += pacman.velocityY;
            
            // Check if new position collides with wall
            boolean collision = false;
            for (Block wall : walls) {
                if (collision(pacman, wall)) {
                    collision = true;
                    break;
                }
            }
            
            // If collision occurred, revert to original position and direction
            if (collision) {
                pacman.x = originalX;
                pacman.y = originalY;
                pacman.direction = originalDirection;
                pacman.updateVelocity();
                // Don't reset the bufferedDirection - keep it for later when turn is possible
            }
            // If no collision, the direction change was successful, so clear buffer
            else {
                // Direction change was successful, no need to keep buffer
                // The upcoming updatePacmanDirection will move Pacman again, 
                // so we need to revert the position but keep the new direction
                pacman.x = originalX;
                pacman.y = originalY;
            }
        }
    }
    
    private void updatePacmanDirection() {
        // Determine which directional image to use based on current movement
        switch (pacman.direction) {
            case 'U': pacman.image = pacmanUpImage; break;
            case 'D': pacman.image = pacmanDownImage; break;
            case 'L': pacman.image = pacmanLeftImage; break;
            case 'R': pacman.image = pacmanRightImage; break;
        }
        
        // Update pacman position
        pacman.x += pacman.velocityX;
        pacman.y += pacman.velocityY;
        
        // Check for tunnel warping
        handleTunnelWarping(pacman);
        
        // Check for wall collisions
        boolean collision = false;
        for (Block wall : walls) {
            if (collision(pacman, wall)) {
                // Undo movement
                pacman.x -= pacman.velocityX;
                pacman.y -= pacman.velocityY;
                collision = true;
                break;
            }
        }
        
        // If we didn't have a collision and successfully moved, 
        // and our buffered direction matches our current direction,
        // we can clear the buffer
        if (!collision && pacman.direction == bufferedDirection) {
            // We've successfully changed to the buffered direction, so clear it
            bufferedDirection = pacman.direction;
        }
    }
    
    private void checkGameConditions() {
        // Check if all food is eaten
        if (foods.isEmpty() && powerPellets.isEmpty()) {
            //soundManager.stopAllSounds();
            level++;
            resetForNextLevel(); // Reset for the next level without resetting score and lives

            // Increase difficulty with level
            if (level > 4) {
                // Decrease ghost scatter time after level 4
                MODE_SEQUENCE[3][0] = Math.max(3000, MODE_SEQUENCE[3][0] - 1000);
            }
        }
    }
    private long lastChompStartTime = 0;  // Add this as a class field
    private static final long CHOMP_SOUND_DURATION = 150; // Reduce duration to 150ms

    private void checkFoodCollisions() {
        Block foodToRemove = null;
        long currentTime = System.currentTimeMillis();
        
        for (Block food : foods) {
            if (collision(pacman, food)) {
                foodToRemove = food;
                score += 10;
                // Only play sound if enough time has passed since last chomp
                if (currentTime - lastChompStartTime >= CHOMP_SOUND_DURATION) {
                    playSFX(23);
                    lastChompStartTime = currentTime;
                }
                break;
            }
        }
        if (foodToRemove != null) {
            foods.remove(foodToRemove);
        }
    }
    
    private void checkPowerPelletCollisions() {
        Block pelletToRemove = null;
        for (Block pellet : powerPellets) {
            if (collision(pacman, pellet)) {
                pelletToRemove = pellet;
                score += 50;
                
                if (ghostsScared) {
                    // Reset the frightened timer completely
                    scaredEndTime = System.currentTimeMillis() + SCARED_DURATION;
                    frightenedTimer = scaredEndTime;
                    
                    // Update all ghosts' state timers
                    for (Block ghost : ghosts) {
                        if (ghost.state == GhostState.FRIGHTENED) {
                            ghost.stateTimer = scaredEndTime;
                        }
                    }
                    
                    // Restart the ghost blue sound
                    //soundManager.stopGhostScared();
                    //soundManager.playGhostScared();
                    playSFX(26);
                } else {
                    // Start new frightened state
                    setGhostsScared();
                }
                break;
            }
        }
        if (pelletToRemove != null) {
            powerPellets.remove(pelletToRemove);
        }
    }
    
    private void checkGhostCollisions() {
        for (Block ghost : ghosts) {
            if (collision(pacman, ghost)) {
                if (ghost.state == GhostState.FRIGHTENED) {
                    // Play the eat fruit sound when eating a frightened ghost
                    //soundManager.playEatFruit();
                	playSFX(25);
                    ghost.state = GhostState.EYES;
                    ghost.stateTimer = System.currentTimeMillis() + 2000;
                    ghost.image = eyesImage;
                    ghost.velocityX = 0;
                    ghost.velocityY = 0;
                    score += 200;
                } else if (ghost.state == GhostState.NORMAL && !isRespawning) {
                    lives--;
                    //soundManager.playDeath();
                    playSFX(24);
                    if (lives <= 0) {
                    	updateLeaderboard();
                        gameOver = true;
                        
                    } else {
                        // Start respawn delay instead of immediately resetting
                        isRespawning = true;
                        respawnTimer = System.currentTimeMillis() + RESPAWN_DELAY;
                    }
                    break;
                }
            }
        }
    }
    
    private void resetPositions() {
        //soundManager.stopAllSounds();
        
        // Reset positions but keep everything visible
        pacman.x = pacman.startX;
        pacman.y = pacman.startY;
        bufferedDirection = 'R';
        pacman.direction = 'R';
        pacman.image = pacmanRightImage;
        pacman.updateVelocity();

        for (Block ghost : ghosts) {
            ghost.reset();
            ghost.state = GhostState.HOUSE;
            ghost.stateTimer = System.currentTimeMillis() + 2000 + (1000 * ghost.personality);
        }

        // Set shorter delay for reset
        isFirstStart = false;
        gameStarted = false;
        gameStartTimer = System.currentTimeMillis() + RESET_GAME_START_DELAY;
    }
    
    private void resetGame() {
        //soundManager.stopAllSounds();  // This will stop all sounds
        loadMap();
        score = 0;
        lives = 3;
        gameOver = false;
        isRespawning = false;
        ghostsScared = false;  // Ensure this is reset
        
        // Reset game start conditions
        isFirstStart = true;
        gameStarted = false;
        gameStartTimer = System.currentTimeMillis() + INITIAL_GAME_START_DELAY;
        //soundManager.playBeginning();
        playSFX(22);
        modeTimer = System.currentTimeMillis() + MODE_SEQUENCE[0][0];
        currentMode = GhostMode.SCATTER;
        cherryActive = false;
        cherry = null;
        cherryTimer = System.currentTimeMillis() + CHERRY_INTERVAL;
        enteredName = "";
        nameEntered = false;
    }
    
    private void resetForNextLevel() {
        loadMap();
        ghostsScared = false;
        gameStarted = false;
        gameStartTimer = System.currentTimeMillis() + INITIAL_GAME_START_DELAY;
        modeTimer = System.currentTimeMillis() + MODE_SEQUENCE[Math.min(level - 1, MODE_SEQUENCE.length - 1)][0];
        currentMode = GhostMode.SCATTER;
        pacman.reset();
        cherryActive = false;
        cherry = null;
        cherryTimer = System.currentTimeMillis() + CHERRY_INTERVAL;
        for (Block ghost : ghosts) {
            ghost.reset();
            ghost.state = GhostState.HOUSE;
            ghost.stateTimer = System.currentTimeMillis() + 2000 + (1000 * ghost.personality);
        }
    }
    public boolean collision(Block a, Block b) {
        int aLeft = a.x + a.collisionPadding;
        int aRight = a.x + a.width - a.collisionPadding;
        int aTop = a.y + a.collisionPadding;
        int aBottom = a.y + a.height - a.collisionPadding;
        
        int bLeft = b.x;
        int bRight = b.x + b.width;
        int bTop = b.y;
        int bBottom = b.y + b.height;
        
        // Check for overlap
        return !(aLeft > bRight || aRight < bLeft || aTop > bBottom || aBottom < bTop);
    }

    private void drawPauseScreen(Graphics g) {
        // Background overlay
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, boardWidth, boardHeight);

        // Font sizes
        final int TITLE_FONT_SIZE = 90;
        final int BUTTON_FONT_SIZE = 40;

        // Calculate positions
        int titleY = boardHeight / 4;  // Move title higher up
        int buttonStartY = titleY + 120;  // Start buttons below title
        int buttonSpacing = 70;  // Reduce space between buttons

        // Title
        g.setFont(customFont.deriveFont(Font.BOLD, TITLE_FONT_SIZE));
        g.setColor(Color.YELLOW);
        String title = "PAUSED";
        FontMetrics fmTitle = g.getFontMetrics();
        g.drawString(title, (boardWidth - fmTitle.stringWidth(title)) / 2, titleY);

        // Buttons
        g.setFont(customFont.deriveFont(Font.PLAIN, BUTTON_FONT_SIZE));
        FontMetrics fmBtn = g.getFontMetrics();

        int buttonWidth = 300;
        int buttonHeight = 70;
        int buttonX = (boardWidth - buttonWidth) / 2;

        // RESUME
        String resumeText = "RESUME";
        resumeButtonBounds = new Rectangle(buttonX, buttonStartY - 50, buttonWidth, buttonHeight);
        g.setColor(hoveredButton == resumeButtonBounds ? Color.WHITE : Color.YELLOW);
        g.drawString(resumeText, buttonX + (buttonWidth - fmBtn.stringWidth(resumeText)) / 2, buttonStartY);

        // RESTART
        String restartText = "RESTART";
        int restartY = buttonStartY + buttonSpacing;
        restartButtonBounds = new Rectangle(buttonX, restartY - 50, buttonWidth, buttonHeight);
        g.setColor(hoveredButton == restartButtonBounds ? Color.WHITE : Color.YELLOW);
        g.drawString(restartText, buttonX + (buttonWidth - fmBtn.stringWidth(restartText)) / 2, restartY);

        // AUDIO controls
        String audioText = "AUDIO: ";
        //String volumeLevel = "< " + soundManager.getVolumeScale() + " >";
        int audioY = restartY + buttonSpacing;
        
        g.setFont(customFont.deriveFont(Font.PLAIN, BUTTON_FONT_SIZE));
        FontMetrics fmAudio = g.getFontMetrics();
        
        // Center the audio controls
        int audioTextWidth = fmAudio.stringWidth(audioText);
        //int volumeLevelWidth = fmAudio.stringWidth(volumeLevel);
        //int totalWidth = audioTextWidth + volumeLevelWidth;
        //int startX = (boardWidth - totalWidth) / 2;
        
        // Draw AUDIO text
        //g.setColor(Color.YELLOW);
        //g.drawString(audioText, startX, audioY);
        
        // Draw volume controls with arrows
        //g.setColor(Color.WHITE);
        //g.drawString(volumeLevel, startX + audioTextWidth, audioY);

        // Define clickable areas for volume controls
        //int arrowWidth = 30;
        //volumeDownBounds = new Rectangle(startX + audioTextWidth, audioY - 30, arrowWidth, 40);
        //volumeUpBounds = new Rectangle(startX + audioTextWidth + volumeLevelWidth - arrowWidth, audioY - 30, arrowWidth, 40);

        // MENU (now last)
        String menuText = "MAIN MENU";
        int menuY = audioY + buttonSpacing;
        menuButtonBounds = new Rectangle(buttonX, menuY - 50, buttonWidth, buttonHeight);
        g.setColor(hoveredButton == menuButtonBounds ? Color.WHITE : Color.YELLOW);
        g.drawString(menuText, buttonX + (buttonWidth - fmBtn.stringWidth(menuText)) / 2, menuY);
    }

    private void loadCustomFont() {
        try {
            // Load the font file from resources using getResourceAsStream
            InputStream is = getClass().getResourceAsStream("/PM/fonts/Pixeboy-z8XGD.ttf");
            if (is == null) {
                throw new IOException("Font file not found in resources");
            }
            
            // Create font from the input stream
            customFont = Font.createFont(Font.TRUETYPE_FONT, is);
            
            // Register the font with the graphics environment
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFont);
            
            System.out.println("Custom font loaded successfully: Pixeboy");
        } catch (IOException | FontFormatException e) {
            System.out.println("Error loading custom font: " + e.getMessage());
            // Fallback to default font if custom font fails to load
            customFont = new Font("Arial", Font.PLAIN, SMALL_FONT_SIZE);
        }
    }

    private void drawGameOverScreen(Graphics g) {
        // Background overlay
        g.setColor(new Color(0, 0, 0, 220));
        g.fillRect(0, 0, boardWidth, boardHeight);

        // Font sizes (using your requested sizes)
        final int TITLE_FONT_SIZE = 90;     // "GAME OVER"
        final int INFO_FONT_SIZE = 35;      // Score and rank
        final int NAME_FONT_SIZE = 45;      // Player name
        final int BUTTON_FONT_SIZE = 25;    // Button text

        // Calculate total content height
        int contentHeight = TITLE_FONT_SIZE + INFO_FONT_SIZE*2 + NAME_FONT_SIZE + BUTTON_FONT_SIZE*3 + 200;
        int startY = (boardHeight - contentHeight) / 2;

        // GAME OVER title
        g.setFont(customFont.deriveFont(Font.BOLD, TITLE_FONT_SIZE));
        g.setColor(Color.RED);
        String title = "GAME OVER";
        FontMetrics fmTitle = g.getFontMetrics();
        int y = startY;
        g.drawString(title, (boardWidth - fmTitle.stringWidth(title)) / 2, y + fmTitle.getAscent());

        // SCORE and RANK
        int potentialRank = getPlayerRank(score);
        y += TITLE_FONT_SIZE + 30;
        
        g.setFont(customFont.deriveFont(Font.PLAIN, INFO_FONT_SIZE));
        g.setColor(Color.YELLOW);
        
        String scoreStr = "SCORE: " + score;
        FontMetrics fmInfo = g.getFontMetrics();
        g.drawString(scoreStr, (boardWidth - fmInfo.stringWidth(scoreStr)) / 2, y + fmInfo.getAscent());

        y += INFO_FONT_SIZE + 20;
        String rankStr = "RANK: " + (potentialRank == -1 ? "N/A" : potentialRank);
        g.drawString(rankStr, (boardWidth - fmInfo.stringWidth(rankStr)) / 2, y + fmInfo.getAscent());

        // PLAYER name
        if (potentialRank != -1) {
            y += INFO_FONT_SIZE + 30;
            g.setFont(customFont.deriveFont(Font.PLAIN, NAME_FONT_SIZE));
            g.setColor(Color.CYAN);
            
            String displayText;
            if (!nameEntered) {
                // Show input field with cursor
                displayText = "Enter Name: " + enteredName + "_";
            } else {
                // Show saved name (use "GUEST" if empty)
                String playerName = enteredName.trim().isEmpty() ? "GUEST" : enteredName;
                displayText = "Player: " + playerName;
            }
            
            FontMetrics fmName = g.getFontMetrics();
            g.drawString(displayText, (boardWidth - fmName.stringWidth(displayText)) / 2, y + fmName.getAscent());
        }
        

        // Buttons
        y += NAME_FONT_SIZE + 50;
        g.setFont(customFont.deriveFont(Font.PLAIN, BUTTON_FONT_SIZE));
        FontMetrics fmBtn = g.getFontMetrics();

        // RESTART Button
        String restartText = "RESTART";
        int restartX = (boardWidth - fmBtn.stringWidth(restartText)) / 2;
        restartButtonBounds = new Rectangle(restartX - 15, y - 25, fmBtn.stringWidth(restartText) + 30, 50);
        g.setColor(hoveredButton == restartButtonBounds ? Color.WHITE : Color.LIGHT_GRAY);
        g.drawString(restartText, restartX, y + fmBtn.getAscent());

        // LEADERBOARD Button
        y += BUTTON_FONT_SIZE + 30;
        String leaderboardText = "LEADERBOARD";
        int lbX = (boardWidth - fmBtn.stringWidth(leaderboardText)) / 2;
        leaderboardButtonBounds = new Rectangle(lbX - 15, y - 25, fmBtn.stringWidth(leaderboardText) + 30, 50);
        g.setColor(hoveredButton == leaderboardButtonBounds ? Color.WHITE : Color.LIGHT_GRAY);
        g.drawString(leaderboardText, lbX, y + fmBtn.getAscent());

        // RETURN TO MENU Button
        y += BUTTON_FONT_SIZE + 30;
        String returnText = "RETURN TO MENU";
        int retX = (boardWidth - fmBtn.stringWidth(returnText)) / 2;
        returnButtonBounds = new Rectangle(retX - 15, y - 25, fmBtn.stringWidth(returnText) + 30, 50);
        g.setColor(hoveredButton == returnButtonBounds ? Color.WHITE : Color.LIGHT_GRAY);
        g.drawString(returnText, retX, y + fmBtn.getAscent());
    }

    private void drawLeaderboardScreen(Graphics g) {
        // Background overlay
        g.setColor(new Color(0, 0, 0, 220));
        g.fillRect(0, 0, boardWidth, boardHeight);

        // Title: "LEADERBOARD"
        g.setFont(customFont.deriveFont(Font.BOLD, LARGE_FONT_SIZE));
        g.setColor(Color.ORANGE);
        String title = "LEADERBOARD";
        FontMetrics fmTitle = g.getFontMetrics();
        int titleX = (boardWidth - fmTitle.stringWidth(title)) / 2;
        g.drawString(title, titleX, tileSize * 2);

        // Subheading: Game Over & Score
        g.setFont(customFont.deriveFont(Font.PLAIN, SMALL_FONT_SIZE));
        g.setColor(Color.YELLOW);
        g.drawString("GAME OVER - FINAL SCORE: " + score, tileSize * 2, tileSize * 3 + 10);

        g.setColor(Color.WHITE);

        // Set up table dimensions
        int col1X = tileSize;
        int col2X = tileSize * 5;
        int col3X = boardWidth - tileSize * 5;
        int tableStartY = tileSize * 5;
        int rowHeight = 28;

        // Back Button
        String backText = "< BACK";
        g.setFont(customFont.deriveFont(Font.PLAIN, SMALL_FONT_SIZE + 2));
        FontMetrics fm = g.getFontMetrics();
        int backX = tileSize;
        int backY = tileSize * 2;
        backButtonBounds = new Rectangle(backX - 5, backY - 25, fm.stringWidth(backText) + 10, 30);

        g.setColor(hoveredButton == backButtonBounds ? Color.WHITE : Color.CYAN);
        g.drawString(backText, backX, backY);

        // Create a clipping region for the scrollable content
        Shape oldClip = g.getClip();
        g.setClip(0, tableStartY + rowHeight, boardWidth, visibleEntries * entryHeight);

        // Draw table content
        List<LeaderboardPlayer> topPlayers = ls.getTopPlayers();
        g.setFont(customFont.deriveFont(Font.PLAIN, SMALL_FONT_SIZE));
        int totalEntries = topPlayers.size();
        int tableY = tableStartY + rowHeight;

        // Determine visible window
        int startIndex = scrollOffset / entryHeight;
        int yOffset = -(scrollOffset % entryHeight);

        // Draw table rows
        for (int i = startIndex; i < Math.min(startIndex + visibleEntries + 1, topPlayers.size()); i++) {
            LeaderboardPlayer p = topPlayers.get(i);
            int y = tableY + (i - startIndex) * entryHeight + yOffset;

            // Alternate row colors
            if (i % 2 == 0) g.setColor(new Color(30, 30, 30));
            else g.setColor(new Color(50, 50, 50));
            g.fillRect(0, y, boardWidth, entryHeight);

            // Text
            g.setColor(Color.LIGHT_GRAY);
            g.drawString(String.valueOf(p.getRank()), col1X, y + 20);
            g.drawString(p.getName(), col2X, y + 20);
            g.drawString(String.valueOf(p.getScore()), col3X, y + 20);
        }

        // Restore original clipping region
        g.setClip(oldClip);

        // Draw header row (after restoring clip to keep it always visible)
        g.setColor(new Color(180, 140, 200)); // Light purple
        g.fillRect(0, tableStartY, boardWidth, rowHeight);
 
        // Draw header text
        g.setFont(customFont.deriveFont(Font.BOLD, SMALL_FONT_SIZE));
        g.setColor(Color.BLACK);
        g.drawString("RANK", col1X, tableStartY + 20);
        g.drawString("NAME", col2X, tableStartY + 20);
        g.drawString("SCORE", col3X, tableStartY + 20);

        // Scrollbar rendering
        int totalHeight = topPlayers.size() * entryHeight;
        int scrollbarTrackHeight = visibleEntries * entryHeight;
        
        if (totalHeight > scrollbarTrackHeight) {
            maxScrollOffset = totalHeight - scrollbarTrackHeight;
            
            // Calculate thumb dimensions
            int thumbHeight = calculateThumbHeight(totalHeight, scrollbarTrackHeight);
            int scrollbarX = boardWidth - SCROLLBAR_WIDTH - SCROLLBAR_MARGIN;
            int scrollbarTrackY = tileSize * 5 + entryHeight; // Below header
            int thumbY = calculateThumbY(scrollOffset, totalHeight, scrollbarTrackHeight, thumbHeight, scrollbarTrackY);
            
            // Draw track
            g.setColor(new Color(80, 80, 80));
            g.fillRect(scrollbarX, scrollbarTrackY, SCROLLBAR_WIDTH, scrollbarTrackHeight);
            
            // Draw thumb
            g.setColor(draggingScrollbar ? Color.WHITE : Color.LIGHT_GRAY);
            g.fillRect(scrollbarX, thumbY, SCROLLBAR_WIDTH, thumbHeight);
        }
    }

    private void drawInstructionsScreen(Graphics g) {
        // Dark overlay background
        g.setColor(new Color(0, 0, 0, 220));
        g.fillRect(0, 0, boardWidth, boardHeight);

        // Calculate content dimensions for proper centering
        int totalContentHeight = 600; // Approximate total height needed
        int startY = (boardHeight - totalContentHeight) / 2;

        // Title
        g.setFont(customFont.deriveFont(Font.BOLD, 48));
        g.setColor(Color.YELLOW);
        String title = "HOW TO PLAY";
        FontMetrics fmTitle = g.getFontMetrics();
        g.drawString(title, (boardWidth - fmTitle.stringWidth(title)) / 2, startY);

        // Instructions section
        g.setFont(customFont.deriveFont(Font.PLAIN, 20));
        int iconSize = 24;
        int lineHeight = 42; // Slightly increased for better spacing
        int instructionWidth = 330; // Reduced width to prevent overflow
        
        // Calculate positions for instructions
        int contentStartX = (boardWidth - instructionWidth) / 2;
        int iconX = contentStartX;
        int textX = contentStartX + iconSize + 15;
        int y = startY + 80; // Increased space after title

        // Game mechanics instructions
        String[] instructions = {
            "USE ARROW KEYS OR WASD TO MOVE PACMAN",
            "EAT ALL DOTS TO COMPLETE THE LEVEL",
            "POWER PELLETS MAKE GHOSTS VULNERABLE",
            "EAT FRIGHTENED GHOSTS FOR EXTRA POINTS",
            "AVOID GHOSTS IN THEIR NORMAL STATE",
            "COLLECT FRUITS FOR BONUS POINTS"
        };

        // Draw instructions with their icons
        for (int i = 0; i < instructions.length; i++) {
            // Draw appropriate icon
            switch (i) {
                case 0: // Pacman
                    g.drawImage(pacmanRightImage, iconX, y - iconSize + 8, iconSize, iconSize, null);
                    break;
                case 1: // Dot
                    g.setColor(Color.YELLOW);
                    g.fillRect(iconX + 10, y - 8, 4, 4);
                    g.setColor(Color.WHITE);
                    break;
                case 2: // Power Pellet
                    g.setColor(Color.WHITE);
                    g.fillOval(iconX + 6, y - 13, 12, 12);
                    break;
                case 3: // Frightened Ghost
                    g.drawImage(scaredGhostImage, iconX, y - iconSize + 8, iconSize, iconSize, null);
                    break;
                case 4: // Normal Ghost
                    g.drawImage(redGhostImage, iconX, y - iconSize + 8, iconSize, iconSize, null);
                    break;
                case 5: // Cherry
                    g.drawImage(cherryImage, iconX, y - iconSize + 8, iconSize, iconSize, null);
                    break;
            }
            g.setColor(Color.WHITE);
            g.drawString(instructions[i], textX, y);
            y += lineHeight;
        }

        // Add extra spacing between instructions and controls
        y += lineHeight * 1; // Increased from just lineHeight

        // Controls section header
        g.setFont(customFont.deriveFont(Font.PLAIN, 20));
        g.setColor(Color.WHITE);
        String controlsHeader = "CONTROLS:";
        FontMetrics fmControls = g.getFontMetrics();
        int controlsX = (boardWidth - fmControls.stringWidth(controlsHeader)) / 2;
        g.drawString(controlsHeader, controlsX, y);
        y += lineHeight;

        // Controls content
        FontMetrics fm = g.getFontMetrics();
        int controlWidth = 280; // Width for each control column
        int spacing = 20; // Space between columns
        int leftX = (boardWidth - (controlWidth * 2 + spacing)) / 2;
        int rightX = leftX + controlWidth + spacing;

        // Movement controls
        g.setColor(Color.WHITE);
        g.drawString("W OR UP ARROW", leftX, y);
        g.setColor(Color.YELLOW);
        g.drawString("- MOVE UP", leftX + 150, y);

        g.setColor(Color.WHITE);
        g.drawString("S OR DOWN ARROW", rightX, y);
        g.setColor(Color.YELLOW);
        g.drawString("- MOVE DOWN", rightX + 150, y);
        y += lineHeight;

        g.setColor(Color.WHITE);
        g.drawString("A OR LEFT ARROW", leftX, y);
        g.setColor(Color.YELLOW);
        g.drawString("- MOVE LEFT", leftX + 150, y);

        g.setColor(Color.WHITE);
        g.drawString("D OR RIGHT ARROW", rightX, y);
        g.setColor(Color.YELLOW);
        g.drawString("- MOVE RIGHT", rightX + 150, y);
        y += lineHeight;

        // Pause control (centered)
        y += lineHeight/2;
        g.setColor(Color.WHITE);
        String pauseControl = "P OR ESC";
        g.drawString(pauseControl, (boardWidth - fm.stringWidth(pauseControl + " - PAUSE GAME")) / 2, y);
        g.setColor(Color.YELLOW);
        g.drawString("    - PAUSE GAME", 
            (boardWidth - fm.stringWidth(pauseControl + " - PAUSE GAME")) / 2 + 
            fm.stringWidth(pauseControl + " "), y);

        // OK Button
        String okText = "OK";
        g.setFont(customFont.deriveFont(Font.BOLD, 36));
        FontMetrics fmBtn = g.getFontMetrics();
        int btnWidth = 100;
        int btnHeight = 50;
        int btnX = (boardWidth - btnWidth) / 2;
        int btnY = boardHeight - btnHeight - 40;

        okButtonBounds = new Rectangle(btnX, btnY, btnWidth, btnHeight);
        g.setColor(hoveredButton == okButtonBounds ? Color.WHITE : Color.YELLOW);
        g.drawString(okText, 
            btnX + (btnWidth - fmBtn.stringWidth(okText)) / 2, 
            btnY + btnHeight - (btnHeight - fmBtn.getAscent()) / 2);
    }
    

    private int calculateThumbHeight(int totalHeight, int visibleHeight) {
        if (totalHeight <= visibleHeight) return 0; // No scrollbar needed
        
        float visibleRatio = (float)visibleHeight / totalHeight;
        int thumbHeight = (int)(visibleRatio * visibleHeight);
        return Math.max(thumbHeight, MIN_THUMB_HEIGHT);
    }

    private int calculateThumbY(int currentScroll, int totalHeight, int trackHeight, int thumbHeight, int trackY) {
        if (totalHeight <= trackHeight) return trackY; // No scrolling needed
        
        float scrollRatio = (float)currentScroll / (totalHeight - trackHeight);
        int availableSpace = trackHeight - thumbHeight;
        return trackY + (int)(scrollRatio * availableSpace);
    }

    /*class SoundManager {
        private Clip beginningSound;
        private Clip chompSound;
        private Clip deathSound;
        private Clip eatFruitSound;
        private Clip ghostScaredSound;
        private boolean soundsLoaded = false;
        private long lastChompEndTime = 0;
        private final long CHOMP_SOUND_DURATION = 650; // milliseconds between chomps
        private long[] pausePositions = new long[5]; // To store pause positions
        private boolean[] wasPlaying = new boolean[5];
        private boolean wasBeginningSoundPlaying = false;
        private int volumeScale = 3; // Default volume level (0-5)
        private float volume;
        private FloatControl[] volumeControls;

        public SoundManager() {
            try {
                // Load all sound files with buffering for better performance
                beginningSound = loadBufferedSound("/PM/sounds/pacman_beginning.wav");
                chompSound = loadBufferedSound("/PM/sounds/pacman_chomp.wav");
                deathSound = loadBufferedSound("/PM/sounds/pacman_death.wav");
                eatFruitSound = loadBufferedSound("/PM/sounds/pacman_eatfruit.wav");
                ghostScaredSound = loadBufferedSound("/PM/sounds/pacman_ghostBlue.wav");

                // Configure sound properties
                beginningSound.setFramePosition(0);
                chompSound.setFramePosition(0);
                deathSound.setFramePosition(0);
                eatFruitSound.setFramePosition(0);
                ghostScaredSound.setFramePosition(0);

                soundsLoaded = true;

                // Initialize volume controls array
                volumeControls = new FloatControl[5]; // One for each sound clip
                
                // Get volume controls for each clip
                volumeControls[0] = (FloatControl) beginningSound.getControl(FloatControl.Type.MASTER_GAIN);
                volumeControls[1] = (FloatControl) chompSound.getControl(FloatControl.Type.MASTER_GAIN);
                volumeControls[2] = (FloatControl) deathSound.getControl(FloatControl.Type.MASTER_GAIN);
                volumeControls[3] = (FloatControl) eatFruitSound.getControl(FloatControl.Type.MASTER_GAIN);
                volumeControls[4] = (FloatControl) ghostScaredSound.getControl(FloatControl.Type.MASTER_GAIN);
                
                updateVolume();
            } catch (Exception e) {
                System.err.println("Error loading sounds: " + e.getMessage());
                soundsLoaded = false;
            }
        }

        public void playBeginning() {
            if (soundsLoaded && !beginningSound.isActive()) {
                beginningSound.setFramePosition(0);
                beginningSound.start();
            }
        }

        public void playChomp() {
            long currentTime = System.currentTimeMillis();
            if (soundsLoaded && currentTime > lastChompEndTime) {
                // Stop previous chomp if still playing
                if (chompSound.isActive()) {
                    chompSound.stop();
                }
                chompSound.setFramePosition(0);
                chompSound.start();
                lastChompEndTime = currentTime + CHOMP_SOUND_DURATION;
            }
        }

        public void playDeath() {
            if (soundsLoaded) {
                // Stop all other sounds
                stopAllSounds();
                deathSound.setFramePosition(0);
                deathSound.start();
            }
        }

        public void playEatFruit() {
            if (soundsLoaded) {
                // Stop previous fruit sound if still playing
                if (eatFruitSound.isActive()) {
                    eatFruitSound.stop();
                }
                eatFruitSound.setFramePosition(0);
                eatFruitSound.start();
            }
        }
        public void playGhostScared() {
            if (soundsLoaded && !ghostScaredSound.isActive()) {
                ghostScaredSound.setFramePosition(0);
                ghostScaredSound.start();
            }
        }

        public void stopGhostScared() {
            if (soundsLoaded && ghostScaredSound.isActive()) {
                ghostScaredSound.stop();
            }
        }

        public void stopAllSounds() {
        	//stopSFX();
            if (beginningSound.isActive()) beginningSound.stop();
            if (chompSound.isActive()) chompSound.stop();
            if (deathSound.isActive()) deathSound.stop();
            if (eatFruitSound.isActive()) eatFruitSound.stop();
            if (ghostScaredSound.isActive()) ghostScaredSound.stop();
        }

        public void pauseAllSounds() {
            if (soundsLoaded) {
                // Store both position and playing state for each sound
                wasPlaying[0] = beginningSound.isActive();
                wasPlaying[1] = chompSound.isActive();
                wasPlaying[2] = deathSound.isActive();
                wasPlaying[3] = eatFruitSound.isActive();
                wasPlaying[4] = ghostScaredSound.isActive();

                // Store positions for all sounds
                if (wasPlaying[0]) pausePositions[0] = beginningSound.getMicrosecondPosition();
                if (wasPlaying[1]) pausePositions[1] = chompSound.getMicrosecondPosition();
                if (wasPlaying[2]) pausePositions[2] = deathSound.getMicrosecondPosition();
                if (wasPlaying[3]) pausePositions[3] = eatFruitSound.getMicrosecondPosition();
                if (wasPlaying[4]) pausePositions[4] = ghostScaredSound.getMicrosecondPosition();

                // Stop all sounds
                stopAllSounds();
            }
        }

        public void resumeAllSounds() {
            if (soundsLoaded) {
                // Resume each sound if it was playing
                if (wasPlaying[0]) {
                    beginningSound.setMicrosecondPosition(pausePositions[0]);
                    beginningSound.start();
                }
                if (wasPlaying[1]) {
                    chompSound.setMicrosecondPosition(pausePositions[1]);
                    chompSound.start();
                }
                if (wasPlaying[2]) {
                    deathSound.setMicrosecondPosition(pausePositions[2]);
                    deathSound.start();
                }
                if (wasPlaying[3]) {
                    eatFruitSound.setMicrosecondPosition(pausePositions[3]);
                    eatFruitSound.start();
                }
                if (wasPlaying[4]) {
                    ghostScaredSound.setMicrosecondPosition(pausePositions[4]);
                    ghostScaredSound.start();
                }

                // Reset arrays
                pausePositions = new long[5];
                wasPlaying = new boolean[5];
            }
        }

        private Clip loadBufferedSound(String path) throws Exception {
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(
                getClass().getResource("/sounds" + path)
            );
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            return clip;
        }
        public void adjustVolume(int adjustment) {
            int newScale = volumeScale + adjustment;
            if (newScale >= 0 && newScale <= 5) {
                volumeScale = newScale;
                updateVolume();
            }
        }

        public int getVolumeScale() {
            return volumeScale;
        }

        private void updateVolume() {
            // Convert scale to actual volume values
            switch(volumeScale) {
                case 0: volume = -80f; break; // Mute
                case 1: volume = -20f; break;
                case 2: volume = -12f; break;
                case 3: volume = -5f; break;  // Default
                case 4: volume = 1f; break;
                case 5: volume = 6f; break;
            }
            
            // Apply volume to all sound clips
            if (soundsLoaded) {
                for (FloatControl control : volumeControls) {
                    if (control != null) {
                        control.setValue(volume);
                    }
                }
            }
        }
    }*/

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        long currentTime = System.currentTimeMillis();

        // Only handle pause functionality if game is not over
        if ((key == KeyEvent.VK_P || key == KeyEvent.VK_ESCAPE) && !gameOver) {
            if (gameState == playState) {
                // Pausing the game
                gameState = pauseState;
                //soundManager.pauseAllSounds();
                
                // Adjust remaining start delay time if in start delay
                if (!gameStarted) {
                    long elapsed = currentTime - (gameStartTimer - INITIAL_GAME_START_DELAY);
                    gameStartTimer = currentTime + (INITIAL_GAME_START_DELAY - elapsed);
                }
            } else if (gameState == pauseState) {
                // Resuming the game
                gameState = playState;
                //soundManager.resumeAllSounds();
                
                // Reset start delay timer if we were in start delay
                if (!gameStarted) {
                    gameStartTimer = currentTime + (gameStartTimer - currentTime);
                }
            }
            repaint();
            return;
        }

        // Game controls (only when playing and not game over)
        if (!gameOver && gameState == playState) {
            switch (key) {
                case KeyEvent.VK_UP:
                case KeyEvent.VK_W:
                    bufferedDirection = 'U';
                    break;
                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_S:
                    bufferedDirection = 'D';
                    break;
                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_A:
                    bufferedDirection = 'L';
                    break;
                case KeyEvent.VK_RIGHT:
                case KeyEvent.VK_D:
                    bufferedDirection = 'R';
                    break;
            }
            return;
        }

        // Name entry handling during Game Over
        if (gameOver && !nameEntered) {
            if (key == KeyEvent.VK_BACK_SPACE && enteredName.length() > 0) {
                // Handle backspace
                enteredName = enteredName.substring(0, enteredName.length() - 1);
                repaint();
            } else if (key == KeyEvent.VK_ENTER) {
                // Handle Enter key - save as GUEST if empty, otherwise save entered name
                String nameToSave = enteredName.trim().isEmpty() ? "GUEST" : enteredName.trim();
                //leaderboardManager.addPlayer(new PlayerScore(nameToSave, score));
                currentPlayer.setName(nameToSave);
                nameEntered = true;
                saveData();
                repaint();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Handle letter/number input
        if (gameOver && !nameEntered) {
            char c = e.getKeyChar();
            
            // Only allow A-Z, 0-9 and space, limit to 12 characters
            if ((Character.isLetterOrDigit(c) || c == ' ') && enteredName.length() < 12) {
                enteredName += Character.toUpperCase(c);
                repaint();
            }
        }
    }

    @Override 
    public void keyReleased(KeyEvent e) {
        // Not used but required by interface
    }
    public void updateLeaderboard() {
		currentPlayer = new LeaderboardPlayer(score);
		ls.addPlayer(currentPlayer);
		ls.sort();
	}

	
	//FILE HANDLING
	public LeaderboardSorter loadData() {
		LeaderboardSorter ls = new LeaderboardSorter();
		File file = new File(DATA_FILE);
		if(file.exists()) {
			try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
				ls = (LeaderboardSorter) ois.readObject();
	            ois.close();
	        } catch (IOException | ClassNotFoundException e) {
	            System.out.println("No previous data found. Starting fresh.");
	            e.printStackTrace();
	        }
		}
		return ls;
    }
	public void saveData() {
		//ls.out();
		 try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
	            oos.writeObject(ls);
	            oos.flush();
				oos.close();
	        } catch (IOException e) {
	            System.out.println("Error saving data.");
	            e.printStackTrace();
	        }
		 System.out.println("DATA SAVED!");
	}
    private int getPlayerRank(int score) {
        List<LeaderboardPlayer> top = ls.getTopPlayers();
        int rank = 1;
        for (LeaderboardPlayer p : top) {
            if (score < p.getScore()) {
                rank++;
            }
        }
        return (rank > 20) ? -1 : rank;
    }
    
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (gameState == leaderboardState) {
            int wheelRotation = e.getWheelRotation();
            scrollOffset += wheelRotation * entryHeight * 3; // Faster scrolling
            scrollOffset = Math.max(0, Math.min(scrollOffset, maxScrollOffset));
            repaint();
        }
    }

    public void updateGame() {
        repaint(); // Or call your main game update logic
    }

    public void renderGame(Graphics2D g) {
        paintComponent(g);
    }
}

