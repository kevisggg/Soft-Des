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
	private CollisionChecker colCheck = new CollisionChecker(this);
	Thread gameThread;
	public Player player = new Player(this, keyH, colCheck);
	//public Enemy enemy[] = new Enemy[5];
	private ArrayList<Enemy> enemies = new ArrayList<>();
	//public SuperObject obj[] = new SuperObject[5];
	private ArrayList<PowerUp> obj = new ArrayList<>();
	public ArrayList<Bomb> bombs = new ArrayList<>();
	public ArrayList<Explosion> explosions = new ArrayList<>();
	BMPlayerLeaderboard currentPlayer;
	public static BMLeaderboardSorter bml = new BMLeaderboardSorter();
	
	//public final int usernameState = 5; // New game state
    //private JTextField usernameField; // Text field for input
    public boolean usernameRequested = false;
	
	public UI ui = new UI(this, mouseH, scoreH);
	
	// GAME STATE
	private int gameState;
	public final int instructionState = 0;
	public final int playState = 1;
	public final int pauseState = 2;
	public final int gameoverState = 3;
	public final int winState = 4;
	int cnt=0, level=1;
	
	
	public GamePanel() {
		
		this.setPreferredSize(new Dimension(screenWidth, screenHeight));
		//this.setBackground(Color.blue);
		this.setDoubleBuffered(true);
		this.addKeyListener(keyH);
		this.addMouseListener(mouseH);
		this.setFocusable(true);
		usernameRequested = false;
		loadData();
	}
	
	/*public void saveUsername(String username) {
	    try (FileWriter writer = new FileWriter("scores.txt", true)) {
	        writer.write(username + "\n");
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}*/
	
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
	
	public void setupGame() {
		//asset.setObject();
		asset.setEnemy();
		//playMusic(0);
		gameState = instructionState;
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
		// TODO Auto-generated method stub
		player=new Player(this, keyH, colCheck);
		scoreH.resetScore();
	    level = 1;
		newLevel();
	}
	
	public int getGameState() {
		return gameState;
	}
	
	public void setPlayState() {
		gameState = playState;
	}
	
	public void setPauseState() {
		gameState = pauseState;
	}
	
	public void setOverState() {
		gameState = gameoverState;
	}
	
	public void setWinState() {
		gameState = winState;
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
	
	public void playState() {
		player.update();
		//CREATE EXPLOSION HIT METHOD
		if(!player.getInvincible()) {
			/*for(Explosion e: explosions) {
				//if(e.getExpStatus() && !e.getHasHit()) {
					
				//}
				//System.out.println(player.getX()/tileSize + "/" + e.getX()/tileSize + "        " + player.getY()/tileSize + "/" + e.getY()/tileSize);
				if(player.getX()/tileSize == e.getX()/tileSize && player.getY()/tileSize == e.getY()/tileSize ) {
					System.out.println("SETTING PLAYER HIT ======");
					player.setHit(true);
					//e.setHit(true);
					player.setInvincible(true);
					break;
				}
			}*/
			if(colCheck.checkEntityExp(player.getX(), player.getY(), player.getWidth(), player.getHeight())) {
				player.setHit(true);
		       	player.setInvincible(true);
			}
		}
		
		//System.out.println(bombs.size());/////////////////////
		for(int i = bombs.size() - 1; i >= 0; i--){
			Bomb b = bombs.get(i);
			if(b!=null) {
				b.update();
				if(!b.getBombStatus()) {
					explode(b);
					bombs.remove(i);
					player.removeBombsPlaced();
				}
			}
			//System.out.println("[" + (b.getX() + player.collisionBox.x)/tileSize + "] [" + (b.getY()+player.collisionBox.y+10)/tileSize + "]");
		}
		for (int i = explosions.size() - 1; i >= 0; i--) {
            Explosion e = explosions.get(i);
            e.update(); // Add an update method to Explosion for timing
            if (!e.getExpStatus()) { // Add an expiration check in Explosion
                explosions.remove(i);
            }
        }
		for(int i = 0; i<enemies.size(); i++) {
			//System.out.println("CHEKCING ENEMIES");
			enemies.get(i).update();
			for(Explosion e: explosions) {
				//if(e.getExpStatus() && !e.getHasHit()) {
				
				if(colCheck.checkEntityExp(enemies.get(i).getX(), enemies.get(i).getY(), enemies.get(i).getWidth(), enemies.get(i).getHeight())) {
					enemies.remove(i);
					playSFX(10);
					scoreH.addScoreElim();
					break;
				}
				//}
				//System.out.println(player.getX()/tileSize + "/" + e.getX()/tileSize + "        " + player.getY()/tileSize + "/" + e.getY()/tileSize);
				/*if(enemies.get(i).getX()/tileSize == e.getX()/tileSize && enemies.get(i).getY()/tileSize == e.getY()/tileSize ) {
					System.out.println("SETTING ENEMY HIT ======");
					enemies.remove(i);
					//addScore()
					//player.addScore(i);
					scoreH.addScoreElim();
					//e.setHit(true);
					break;
				}*/
			}
		}
		if(enemies.size() == 0) {
			setWinState();
			playSFX(9);
		}
	}
	
	public void winState() {
		//TIMER
		cnt++;
		if(cnt>=100) {
			cnt=0;
			level=level+1;
			scoreH.addScoreLvl();
			newLevel();
		}
	}
	
	public void update() {
		if(gameState == instructionState) {
			
		}
		else if(gameState == playState) {
			playState();
		}
		
		else if(gameState == pauseState) {
			
		}
		
		else if(gameState == gameoverState) {
			
		}
		else if(gameState == winState) {
			winState();
		}
	}
	
	public void addExplosions(int x, int y) {
		explosions.add(new Explosion(x, y, this));
	}
	
	private void explode(Bomb b) {
		int eX = b.getX()/tileSize;
		int eY = b.getY()/tileSize;
		//System.out.println("x: " + eX + " y: " + eY);
		boolean upEmpty = colCheck.checkTileExp(eX, eY-1, true);
		boolean downEmpty = colCheck.checkTileExp(eX, eY+1, true);
		boolean leftEmpty = colCheck.checkTileExp(eX-1, eY, true);
		boolean rightEmpty = colCheck.checkTileExp(eX+1, eY, true);
		addExplosions(eX*tileSize, eY*tileSize);
		for(int i = 0; i <= player.getBombRadius(); i++) {
			//System.out.println("ADDING EXPLOSIONS");
			if(eY-i>0 && upEmpty) {
				//System.out.println("UP EMPTY");
				
				upEmpty = colCheck.checkTileExp(eX, eY-i, upEmpty);
				if(upEmpty) {
					addExplosions(eX*tileSize, (eY-i)*tileSize);
				}
			}
			if(eY+i>0 && downEmpty) {
				//System.out.println("DOWN EMPTY");
				downEmpty = colCheck.checkTileExp(eX, eY+i, downEmpty);
				if(downEmpty) {
					addExplosions(eX*tileSize, (eY+i)*tileSize);
				}
			}
			if(eX-i>0 && leftEmpty) {
				//System.out.println("LEFT EMPTY");
				//addExplosions((eX-i)*tileSize, eY*tileSize);
				leftEmpty = colCheck.checkTileExp(eX-i, eY, leftEmpty);
				if(leftEmpty) {
					addExplosions((eX-i)*tileSize, eY*tileSize);
				}
			}
			if(eX+i>0 && rightEmpty) {
				//System.out.println("RIGHT EMPTY");
				//addExplosions((eX+i)*tileSize, eY*tileSize);
				rightEmpty = colCheck.checkTileExp(eX+i, eY, rightEmpty);
				if(rightEmpty) {
					addExplosions((eX+i)*tileSize, eY*tileSize);
				}
			}
		}
		//System.out.println("EXPLOSIONS: " + explosions.size());
		for(Explosion e: explosions) {
			//System.out.println("TRUE SET");
			e.setExpStatus(true);
		}
		playSFX(2);
	}
	
	/*public boolean checkTileExp(int x, int y, boolean empty) {
		int tile = tileMgr.mapTileNum[x][y];
		if(tileMgr.tile[tile].collision == true) {
			//entity.collisionOn = true;
			if(tile == 2) {//DESTROY TILE AND RANDOMIZE PU DROP
				tileMgr.setTile(0, x, y);
				int drop=randomDrop();
				if(drop!=0) {
					asset.setObject(drop, x*tileSize, y*tileSize);
				}
				
			}
			else {
				empty = false;
			}
		}
		else {//REMOVE HIT POWER UPS
			System.out.println(obj.size());
			for(int i=0;i<obj.size();i++) {
				System.out.println("X: " + x + " Y: " + y);
				System.out.println("OBJ X: " + obj.get(i).getX()/tileSize + "OBJ Y: " + obj.get(i).getY()/tileSize);
				if(x==(obj.get(i).getX())/tileSize && y==(obj.get(i).getY())/tileSize) {
					obj.get(i).addHits();
					boolean remove = obj.get(i).checkHits();
					if(remove) {
						obj.remove(i);
					}
				}
			}
			
			 *boolean = assetPresent[x][y]; --> in AssetSetter
			 *OR
			 *for(inti = 0; i<obj,size; obj++SuperObject obj: obj){
			 * if get(x),get(y) = x,y
			 *obj.remove
			 *
			 *
			 *}
			 *
		}
		return empty;
	}*/
	
	public int randomDrop() {
		int drop=0;
		Random random = new Random();
		int i = random.nextInt(100)+1;//from 1-4
		if(i>=1 && i<=15) {
			drop = 1;
			System.out.println(drop+": CAPACITY PU");
		}
		else if(i>=16 && i<=30) {
			drop = 2;
			System.out.println(drop+": RANGE PU");
		}
		return drop;
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
		//long timeStart = 0;
		//timeStart = System.nanoTime();
		
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
		//long timeEnd = System.nanoTime();
		//long passed = timeEnd - timeStart;
		//System.out.println(passed);
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
		// TODO Auto-generated method stub
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
