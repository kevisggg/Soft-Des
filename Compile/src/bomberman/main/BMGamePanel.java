package bomberman.main;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import bomberman.object.Bomb;
import bomberman.object.BMPowerUp;
import bomberman.entity.BMEnemy;
import bomberman.entity.Explosion;
import bomberman.entity.BMPlayer;
import bomberman.tile.BMTileManager;
import core.AbstractGamePanel;
import core.LeaderboardPlayer;
import core.LeaderboardSorter;
import core.MainWindow;

public class BMGamePanel extends AbstractGamePanel implements Runnable{
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
	public static final String DATA_FILE = "BM_Leaderboard.dat";
	private BMTileManager tileMgr = new BMTileManager();
	private BMScoreHandler scoreH = new BMScoreHandler();
	private BMAssetSetter asset = new BMAssetSetter(this);
	private ArrayList<BMEnemy> enemies = new ArrayList<>();
	private ArrayList<BMPowerUp> obj = new ArrayList<>();
	private ArrayList<Bomb> bombs = new ArrayList<>();
	private ArrayList<Explosion> explosions = new ArrayList<>();
	public BMCollisionChecker colCheck = new BMCollisionChecker(this, asset, tileMgr, explosions);
	private BMPlayer player = new BMPlayer(this, keyH, colCheck);
	private Thread gameThread;
	
	
	public BMGamePanel(MainWindow mainWindow) {
		this.mainWindow = mainWindow;
		this.setPreferredSize(new Dimension(screenWidth, screenHeight));
		this.setDoubleBuffered(true);
		this.addKeyListener(keyH);
		this.addMouseListener(mouseH);
		this.addMouseMotionListener(mouseH);
		this.setFocusable(true);
		ls=loadData();
		setInsState();
	}
	
	//GETTERS
	public BMTileManager getTileManager() {return tileMgr;}
	public BMCollisionChecker getCollisionChecker() {return colCheck;}
	public BMScoreHandler getScoreHandler() {return scoreH;}
	public BMAssetSetter getAssetSetter() {return asset;}
	public ArrayList<BMEnemy> getEnemies() {return enemies;}
	public ArrayList<BMPowerUp> getObjects() {return obj;}
	public ArrayList<Bomb> getBombs() {return bombs;}
	public BMPlayer getPlayer() {return player;}
	public BMScoreHandler getScoreH() {return scoreH;}
	public BMPowerUp getObj(int i) {return obj.get(i);}
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
	public void setInsState() {gamestateH.setState(new BMInsState(ui));}
	public void setOverState() {gamestateH.setState(new BMGameOverState(ui)); stopMusic();}
	public void setWinState() {gamestateH.setState(new BMWinState(this, ui));}
	public void setNameEntered(boolean isEntered) {keyH.setNameEntered(isEntered);}
	public void setCurPlayerName(String name) {currentPlayer.setName(name); mouseH.resetClick();}
	public void setPlayState() {gamestateH.setState(new BMPlayState(this, player, colCheck, bombs, explosions, enemies, ui));}
	public void setPauseState() {gamestateH.setState(new BMPauseState(this, ui)); pauseMusic();}
	public void setObjXY(int x, int y) {
		obj.getLast().setX(x);
		obj.getLast().setY(y);		
	}
	
	
	//INITIAL GAME SETUP
	public void setupGame() {
		asset.setEnemy();
		playMusic(0);
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
		player=new BMPlayer(this, keyH, colCheck);
		stopMusic();
		playMusic(0);
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
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		//RUNTIME
		//long timeStart = 0;
		//timeStart = System.nanoTime();
			//TILE
			tileMgr.draw(g2);
			
			//POWERUPS
			for(BMPowerUp pu: obj) {
				if(pu!= null) {pu.draw(g2, this);}
			}
			
			//BOMBS
			for(Bomb b: bombs) {
				if(b!=null) {b.draw(g2);}
			}
			
			//PLAYER
			player.draw(g2);
			
			//ENEMIES
			for(BMEnemy enemy: enemies) {
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
	public void addExplosions(int x, int y) {explosions.add(new Explosion(x, y));}
	public void removeObj(int i) {obj.remove(i);}
	public void updateLevel() {level = level+1;}
	public void minusObjCnt() {asset.minusCnt();}
	public void addEnemy(BMEnemy enemy) {enemies.add(enemy);}
	public void addObj(BMPowerUp pu) {obj.add(pu);}
	public void removeFirstObj() {obj.remove(0);}

	//LEADERBOARD AND SCORING
	public void updateLeaderboard() {
		currentPlayer = new LeaderboardPlayer(scoreH.getScoreVal());
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
}
