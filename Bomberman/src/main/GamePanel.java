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
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import object.Bomb;
import object.PowerUp;
import entity.Enemy;
import entity.Explosion;
import entity.Player;
import tile.TileManager;

public class GamePanel extends JPanel implements Runnable{
	
	private final int ogTileSize = 16;
	private final int scale = 3;
	
	public final int tileSize = ogTileSize * scale;
	public final int maxScreenCol = 16;
	public final int maxScreenRow = 12;
	public final int screenWidth = tileSize * maxScreenCol; //768
	public final int screenHeight = tileSize * maxScreenRow; //576
	
	public final int FPS = 60;
	
	private static final String DATA_FILE = "BM_Leaderboard.dat";
	private TileManager tileMgr = new TileManager(this);
	private KeyHandler keyH = new KeyHandler(this);
	private Sound sound = new Sound();
	private MouseHandler mouseH = new MouseHandler(this);
	private ScoreHandler scoreH = new ScoreHandler();
	private AssetSetter asset = new AssetSetter(this);
	
	private ArrayList<Enemy> enemies = new ArrayList<>();
	private ArrayList<PowerUp> obj = new ArrayList<>();
	private ArrayList<Bomb> bombs = new ArrayList<>();
	private ArrayList<Explosion> explosions = new ArrayList<>();
	BMPlayerLeaderboard currentPlayer;
	public static BMLeaderboardSorter bml = new BMLeaderboardSorter();
	private GameStateHandler gamestateH = new GameStateHandler();
	private CollisionChecker colCheck = new CollisionChecker(this, asset, tileMgr, explosions);
	
	private Player player = new Player(this, keyH, colCheck);
    public boolean usernameRequested = false;
	
	public UI ui = new UI(this, mouseH, scoreH);
	
	// GAME STATE
	private int level=1;
	
	Thread gameThread;
	
	public GamePanel() {
		
		this.setPreferredSize(new Dimension(screenWidth, screenHeight));
		//this.setBackground(Color.blue);
		this.setDoubleBuffered(true);
		this.addKeyListener(keyH);
		this.addMouseListener(mouseH);
		this.setFocusable(true);
		usernameRequested = false;
		loadData();
		setInsState();
	}
	
	public TileManager getTileManager() {
		return tileMgr;
	}
	
	public CollisionChecker getCollisionChecker() {
		return colCheck;
	}
	
	public ScoreHandler getScoreHandler() {
		return scoreH;
	}
	
	public AssetSetter getAssetSetter() {
		return asset;
	}
	
	public ArrayList<Enemy> getEnemies() {
		return enemies;
	}
	
	public ArrayList<PowerUp> getObjects() {
		return obj;
	}
	
	public ArrayList<Bomb> getBombs() {
		return bombs;
	}
	
	public Player getPlayer() {
	    return player;
	}
	
	public void setupGame() {
		//asset.setObject();
		asset.setEnemy();
		//playMusic(0);
		//add state initialization
		setInsState();
	}
	
	public void newLevel() {//new level
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
	
	public void restartGame() {//restart from level 1
		player=new Player(this, keyH, colCheck);
		scoreH.resetScore();
	    level = 1;
		newLevel();
	}
	
	public void updateLevel() {
		level = level+1;
	}
	
	public GameState getGameState() {
		return gamestateH.getState();
	}
	
	public void setInsState() {
		gamestateH.setState(new InsState(ui));
		
	}
	
	public void setPlayState() {
		gamestateH.setState(new PlayState(this, player, colCheck, bombs, explosions, enemies, ui));
	}
	
	public void setPauseState() {
		gamestateH.setState(new PauseState(this, ui));
	}
	
	public void setOverState() {
		gamestateH.setState(new GameOverState(ui));
	}
	
	public void setWinState() {
		gamestateH.setState(new WinState(this, ui));
	}
	
	public void startGameThread() {
		gameThread = new Thread(this);
		gameThread.start();
	}
	
	public void run() {
		/*
		 * MAKE FPS CLASS
		 */
		double drawInterval = 1000000000/FPS;
		double delta = 0;
		long lastTime = System.nanoTime();
		long currentTime;
		
		while(gameThread != null) {
		
			//System.out.println("running");
			
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
	
	public void update() {
		gamestateH.request();
	}
	
	public void addExplosions(int x, int y) {
		explosions.add(new Explosion(x, y, this));
	}
	
	public PowerUp getObj(int i) {
		return obj.get(i);
	}
	
	public void removeObj(int i) {
		obj.remove(i);
	}
	
	public String getLvl() {
		String lvl;
		lvl = String.valueOf(level);
		return lvl;
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		//RUNTIME
		long timeStart = 0;
		timeStart = System.nanoTime();
		
		//TILE
		tileMgr.draw(g2);
		
		//OBJECT
		for(PowerUp pu: obj) {
			if(pu!= null) {
				pu.draw(g2, this);
			}
		}
		
		for(Bomb b: bombs) {
			if(b!=null) {
				b.draw(g2);
			}
		}
		
		
		//player
		player.draw(g2);
		
		//enemy
		for(Enemy enemy: enemies) {
			if(!enemies.isEmpty()) {
				//g2.setColor(Color.white);
				//g2.fillRect(enemy.worldX + enemy.collisionBox.x, enemy.worldY + enemy.collisionBox.y, enemy.collisionBox.width, enemy.collisionBox.height);
				enemy.draw(g2);
			}
		}
		
		for(Explosion e: explosions) {
			if(e!=null) {
				if(e.getExpStart()) {
					e.draw(g2, this);
				}
			}
		}
		//g2.setColor(Color.white);
		//g2.fillRect(player.worldX + player.collisionBox.x, player.worldY + player.collisionBox.y, player.collisionBox.width, player.collisionBox.height);
		//UI
		ui.draw(g2);
		long timeEnd = System.nanoTime();
		long passed = timeEnd - timeStart;
		System.out.println(passed);
		g2.dispose();
	}

	public void playMusic(int i) {
		sound.setFile(i);
		sound.play();
		sound.loop();
	}
	
	public void stopMusic() {
		sound.stop();
	}
	
	public void playSFX(int i) {
		sound.setFile(i);
		sound.play();
	}
	
	public void updateLeaderboard() {
		currentPlayer = new BMPlayerLeaderboard(scoreH.getScoreVal());
		bml.addPlayer(currentPlayer);
		bml.sort();
	}
	
	public String getCurPlayerRank() {
		String rank = String.valueOf(currentPlayer.getRank());
		System.out.println("rANK" + rank);
		if(rank.equals("0")) {
			rank = "-";
		}
		return rank;
	}
	
	public void setCurPlayerName(String name) {
		currentPlayer.setName(name);
	}
	
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
		bml.out();
		 try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
	            oos.writeObject(bml);
	            oos.flush();
				oos.close();
	        } catch (IOException e) {
	            System.out.println("Error saving data.");
	            e.printStackTrace();
	        }
	}

	public void minusObjCnt() {
		asset.minusCnt();
	}
	
	public void addEnemy(Enemy enemy) {
		enemies.add(enemy);
	}
	
	public void addObj(PowerUp pu) {
		obj.add(pu);
	}

	public void setObjXY(int x, int y) {
		obj.getLast().setX(x);
		obj.getLast().setY(y);		
	}
	
	public void removeFirstObj() {
		obj.remove(0);
	}
}

/*class AppendableObjectOutputStream extends ObjectOutputStream {
    public AppendableObjectOutputStream(OutputStream out) throws IOException {
        super(out);
    }
    @Override
    protected void writeStreamHeader() throws IOException {
        // do not write a header when appending
        reset();
    }
}*/
