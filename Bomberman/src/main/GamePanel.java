package main;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.JPanel;

import object.Bomb;
import entity.Enemy;
import entity.Explosion;
import entity.Player;
import object.SuperObject;
import tile.TileManager;

public class GamePanel extends JPanel implements Runnable{
	
	private final int ogTileSize = 16;
	private final int scale = 3;
	
	public final int tileSize = ogTileSize * scale;
	public final int maxScreenCol = 16;
	public final int maxScreenRow = 12;
	public final int screenWidth = tileSize * maxScreenCol;
	public final int screenHeight = tileSize * maxScreenRow;
	
	int FPS = 60;
	
	public TileManager tileMgr = new TileManager(this);
	KeyHandler keyH = new KeyHandler(this);
	Thread gameThread;
	public CollisionChecker colCheck = new CollisionChecker(this);
	public AssetSetter asset = new AssetSetter(this);
	public Player player = new Player(this, keyH);
	//public Enemy enemy[] = new Enemy[5];
	public ArrayList<Enemy> enemies = new ArrayList<>();
	public SuperObject obj[] = new SuperObject[10];
	public ArrayList<Bomb> bombs = new ArrayList<>();
	public ArrayList<Explosion> explosions = new ArrayList<>();
	public UI ui = new UI(this);
	
	// GAME STATE
	public int gameState;
	public final int playState = 1;
	public final int pauseState = 2;
	public final int gameoverState = 3;
	public final int winState = 4;
	
	
	public GamePanel() {
		
		this.setPreferredSize(new Dimension(screenWidth, screenHeight));
		this.setBackground(Color.blue);
		this.setDoubleBuffered(true);
		this.addKeyListener(keyH);
		this.setFocusable(true);
	}
	
	public void setupGame() {
		//asset.setObject();
		asset.setEnemy();
		gameState = playState;
	}
	
	public void resetGame() {
		player.setDefaultVal();
		bombs.clear();
		 explosions.clear();
		    enemies.clear();

		    // Reset tile map to its original state
		    tileMgr.loadMap("/maps/mapblank.txt"); // You'll need to implement this in TileManager

		//TRY CALL SETUPGAME() INSTEAD OF MANUAL SET ENEMY
		    // Re-initialize enemies and objects
		    asset.setEnemy();

		    // Reset game state
		    gameState = playState;
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
		if(gameState == playState) {
			player.update();
			//CREATE EXPLOSION HIT METHOD
			if(!player.getInvincible()) {
				for(Explosion e: explosions) {
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
				}
			}
			
			System.out.println(bombs.size());/////////////////////
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
				enemies.get(i).update();
				for(Explosion e: explosions) {
					//if(e.getExpStatus() && !e.getHasHit()) {
						
					//}
					//System.out.println(player.getX()/tileSize + "/" + e.getX()/tileSize + "        " + player.getY()/tileSize + "/" + e.getY()/tileSize);
					if(enemies.get(i).getX()/tileSize == e.getX()/tileSize && enemies.get(i).getY()/tileSize == e.getY()/tileSize ) {
						System.out.println("SETTING ENEMY HIT ======");
						enemies.remove(i);
						//e.setHit(true);
						break;
					}
				}
			}
			if(enemies.size() == 0) {
				gameState = winState;
			}
		}
		
		else if(gameState == pauseState) {
			
		}
		
		else if(gameState == gameoverState) {
			
		}
		else if(gameState == winState) {
			//timer
			resetGame();
		}
	}
	
	public void addExplosions(int x, int y) {
		explosions.add(new Explosion(x, y, this));
	}
	
	private void explode(Bomb b) {
		
		//for(Explosion e: explosions) {
		//	if(e.getExpStatus()) {
				
		//	}
		//}
			
		
		
		int eX = b.getX()/tileSize;
		int eY = b.getY()/tileSize;
		
		boolean upEmpty = checkTileExp(eX, eY-1, true);
		boolean downEmpty = checkTileExp(eX, eY+1, true);
		boolean leftEmpty = checkTileExp(eX-1, eY, true);
		boolean rightEmpty = checkTileExp(eX+1, eY, true);
		addExplosions(eX*tileSize, eY*tileSize);
		for(int i = 0; i <= player.getBombRadius(); i++) {
			System.out.println("ADDING EXPLOSIONS");
			if(upEmpty) {
				System.out.println("UP EMPTY");
				addExplosions(eX*tileSize, (eY-i)*tileSize);
				upEmpty = checkTileExp(eX, eY-i, upEmpty);
			}
			if(downEmpty) {
				System.out.println("DOWN EMPTY");
				addExplosions(eX*tileSize, (eY+i)*tileSize);
				downEmpty = checkTileExp(eX, eY+i, downEmpty);
			}
			if(leftEmpty) {
				System.out.println("LEFT EMPTY");
				addExplosions((eX-i)*tileSize, eY*tileSize);
				leftEmpty = checkTileExp(eX-i, eY, leftEmpty);
			}
			if(rightEmpty) {
				System.out.println("RIGHT EMPTY");
				addExplosions((eX+i)*tileSize, eY*tileSize);
				rightEmpty = checkTileExp(eX+i, eY, rightEmpty);
			}
		}
		System.out.println("EXPLOSIONS: " + explosions.size());
		for(Explosion e: explosions) {
			System.out.println("TRUE SET");
			e.setExpStatus(true);
		}
	}
	
	public boolean checkTileExp(int x, int y, boolean empty) {
		int tile = tileMgr.mapTileNum[x][y];
		if(tileMgr.tile[tile].collision == true) {
			//entity.collisionOn = true;
			if(tile == 2) {
				tileMgr.setTile(0, x, y);
			}
			else {
				empty = false;
			}
		}
		return empty;
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		//TILE
		//long timeStart = 0;
		//timeStart = System.nanoTime();
		tileMgr.draw(g2);
		
		//OBJECT
		/*for(int i=0; i<obj.length; i++) {
			if(obj[i]!= null) {
				obj[i].draw(g2, this);
			}
		}*/
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
	
}
