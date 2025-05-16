package main;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import javax.swing.JPanel;
import object.Bomb;
import object.PowerUp;
import entity.Enemy;
import entity.Explosion;
import entity.Player;
import tile.TileManager;

public class GamePanel extends JPanel implements Runnable{
	//SCREEN SETTINGS
	private static final int ogTileSize = 16;
	private static final int scale = 3;
	public static final int tileSize = ogTileSize * scale;
	public static final int maxScreenCol = 16;
	public static final int maxScreenRow = 12;
	public static final int screenWidth = tileSize * maxScreenCol; //768
	public static final int screenHeight = tileSize * maxScreenRow; //576
	public final int FPS = 60;
	//SET LEVEL
	private int level=1;
	private static final String DATA_FILE = "BM_Leaderboard.dat";
	private TileManager tileMgr = new TileManager(this);
	private KeyHandler keyH = new KeyHandler(this);
	private Sound sfx = new Sound();
	private Sound music = new Sound();
	private MouseHandler mouseH = new MouseHandler(this);
	private ScoreHandler scoreH = new ScoreHandler();
	private AssetSetter asset = new AssetSetter(this);
	private ArrayList<Enemy> enemies = new ArrayList<>();
	private ArrayList<PowerUp> obj = new ArrayList<>();
	private ArrayList<Bomb> bombs = new ArrayList<>();
	private ArrayList<Explosion> explosions = new ArrayList<>();
	private BMPlayerLeaderboard currentPlayer;
	public static BMLeaderboardSorter bml = new BMLeaderboardSorter();
	private GameStateHandler gamestateH = new GameStateHandler();
	private CollisionChecker colCheck = new CollisionChecker(this, asset, tileMgr, explosions);
	private Player player = new Player(this, keyH, colCheck);
	public UI ui = new UI(this, mouseH, scoreH);
	
	private Thread gameThread;
	
	public GamePanel() {
		this.setPreferredSize(new Dimension(screenWidth, screenHeight));
		this.setDoubleBuffered(true);
		this.addKeyListener(keyH);
		this.addMouseListener(mouseH);
		this.addMouseMotionListener(mouseH);
		this.setFocusable(true);
		loadData();
		//setInsState();
		setInsState();
	}
	
	
	//GETTERS
	public TileManager getTileManager() {return tileMgr;}
	public CollisionChecker getCollisionChecker() {return colCheck;}
	public ScoreHandler getScoreHandler() {return scoreH;}
	public AssetSetter getAssetSetter() {return asset;}
	public ArrayList<Enemy> getEnemies() {return enemies;}
	public ArrayList<PowerUp> getObjects() {return obj;}
	public ArrayList<Bomb> getBombs() {return bombs;}
	public Player getPlayer() {return player;}
	public GameState getGameState() {return gamestateH.getState();}
	public PowerUp getObj(int i) {return obj.get(i);}
	public String getNameInput() {return keyH.getName();}
	public boolean getNameEntered() {return keyH.getNameEntered();}
	public String getLvl() {
		String lvl;
		lvl = String.valueOf(level);
		return lvl;
	}
	public String getCurPlayerRank() {
		String rank = String.valueOf(currentPlayer.getRank());
		if(rank.equals("0")) {
			rank = "-";
		}
		return rank;
	}
	
	
	//SETTERS
	public void setInsState() {gamestateH.setState(new InsState(ui));}
	public void setMainLeaderState() {gamestateH.setState(new MainLeaderboardState(ui));System.out.println("MAINLEADERBOAED");}
	public void setPlayState() {gamestateH.setState(new PlayState(this, player, colCheck, bombs, explosions, enemies, ui));}
	public void setPauseState() {gamestateH.setState(new PauseState(this, ui)); pauseMusic();}
	public void setOverState() {gamestateH.setState(new GameOverState(ui)); stopMusic();}
	public void setWinState() {gamestateH.setState(new WinState(this, ui));}
	public void setNameEntered(boolean isEntered) {keyH.setNameEntered(isEntered);}
	public void setCurPlayerName(String name) {currentPlayer.setName(name); mouseH.resetClick();}
	public void setObjXY(int x, int y) {
		obj.getLast().setX(x);
		obj.getLast().setY(y);		
	}
	
	
	//INITIAL GAME SETUP
	public void setupGame() {
		asset.setEnemy();
		playMusic();
		//setInsState();
		setInsState();
		keyH.setNameEntered(false);
	}
	
	
	//NEW LEVEL
	public void newLevel() {
		player.setDefaultVal();
		bombs.clear();
		explosions.clear();
		enemies.clear();
	    obj.clear();
		tileMgr.loadMap("/maps/mapblank.txt"); 
		asset.setEnemy();
		asset.resetObjCnt();
		keyH.resetKeys();
		setPlayState();
	}
	
	
	//RESTART FROM LEVEL 1
	public void restartGame() {
		player=new Player(this, keyH, colCheck);
		stopMusic();
		playMusic();
		scoreH.resetScore();
		ui.getUIUtil().resetToggleLeaderboard();
	    level = 1;
	    setNameEntered(false);
	    keyH.setName("");
		newLevel();
	}
	

	//RUNNING GAME
	public void startGameThread() {
		gameThread = new Thread(this);
		gameThread.start();
	}
	
	public void run() {
		double drawInterval = 1000000000/FPS;
		double delta = 0;
		long lastTime = System.nanoTime();
		long currentTime;
		while(gameThread != null) {
			currentTime = System.nanoTime();
			delta += (currentTime - lastTime) / drawInterval;
			lastTime = currentTime;
			if (delta>=1) {
				update();
				repaint();
				delta--;
			}
		}
	}
	
	public void update() {gamestateH.request();}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		//RUNTIME
		//long timeStart = 0;
		//timeStart = System.nanoTime();
		
		//TILE
		tileMgr.draw(g2);
		
		//POWERUPS
		for(PowerUp pu: obj) {
			if(pu!= null) {pu.draw(g2, this);}
		}
		
		//BOMBS
		for(Bomb b: bombs) {
			if(b!=null) {b.draw(g2);}
		}
		
		//PLAYER
		player.draw(g2);
		
		//ENEMIES
		for(Enemy enemy: enemies) {
			if(!enemies.isEmpty()) {enemy.draw(g2);}
		}
		
		//EXPLOSIONS
		for(Explosion e: explosions) {
			if(e!=null) {
				if(e.getExpStart()) {e.draw(g2, this);}
			}
		}
		
		//UI
		ui.draw(g2);
		//RUNTIME
		//long timeEnd = System.nanoTime();
		//long passed = timeEnd - timeStart;
		//System.out.println(passed);
		g2.dispose();
	}
	
	
	//GAME COMMANDS
	public void addExplosions(int x, int y) {explosions.add(new Explosion(x, y, this));}
	public void removeObj(int i) {obj.remove(i);}
	public void updateLevel() {level = level+1;}
	public void minusObjCnt() {asset.minusCnt();}
	public void addEnemy(Enemy enemy) {enemies.add(enemy);}
	public void addObj(PowerUp pu) {obj.add(pu);}
	public void removeFirstObj() {obj.remove(0);}
	
	
	//SOUNDS
	public void pauseMusic() {music.pause();}
	public void stopMusic() {music.stop();}
	public void playMusic() {
		music.setFile(0);
		music.play();
		music.loop();
	}
	
	public void playSFX(int i) {
		sfx.setFile(i);
		sfx.play();
	}
	
	
	//LEADERBOARD AND SCORING
	public void updateLeaderboard() {
		currentPlayer = new BMPlayerLeaderboard(scoreH.getScoreVal());
		bml.addPlayer(currentPlayer);
		bml.sort();
	}

	
	//FILE HANDLING
	public static void loadData() {
		File file = new File(DATA_FILE);
		if(file.exists()) {
			try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
				bml = (BMLeaderboardSorter) ois.readObject();
	            ois.close();
	        } catch (IOException | ClassNotFoundException e) {
	            System.out.println("No previous data found. Starting fresh.");
	            e.printStackTrace();
	        }
		}
    }
	
	public void saveData() {
		//bml.out();
		 try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
	            oos.writeObject(bml);
	            oos.flush();
				oos.close();
	        } catch (IOException e) {
	            System.out.println("Error saving data.");
	            e.printStackTrace();
	        }
		 System.out.println("DATA SAVED!");
	}
}
