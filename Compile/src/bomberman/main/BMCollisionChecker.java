package bomberman.main;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Random;

import bomberman.entity.BMEnemy;
import bomberman.entity.BMEntity;
import bomberman.entity.Explosion;
import bomberman.entity.BMPlayer;
import bomberman.object.BMPowerUp;
import bomberman.tile.BMTileManager;

public class BMCollisionChecker{
	
	private BMGamePanel gp;
	private BMTileManager tileMgr;
	private BMAssetSetter asset;
	private Rectangle playerBox;
	private boolean hit;
	private ArrayList<BMPowerUp> obj;
	private ArrayList<Explosion> explosions;
	
	public BMCollisionChecker(BMGamePanel gp, BMAssetSetter asset, BMTileManager tileMgr, ArrayList<Explosion> explosions) {
		this.gp = gp;
		this.asset = asset;
		this.tileMgr = tileMgr;
		this.explosions = explosions;
	}
	
	public void checkTile(BMEntity entity) {
		int entityLeft = entity.worldX + entity.collisionBox.x;
		int entityRight  = entity.worldX + entity.collisionBox.x + entity.collisionBox.width;
		int entityTop = entity.worldY + entity.collisionBox.y;
		int entityBottom = entity.worldY + entity.collisionBox.y + entity.collisionBox.height;
		
		int entityLeftCol = entityLeft/gp.tileSize;
		int entityRightCol = entityRight/gp.tileSize;
		int entityTopRow = entityTop/gp.tileSize;
		int entityBottomRow = entityBottom/gp.tileSize;
		
		int tile1, tile2;
		
		switch(entity.direction) {
		case "up":
			entityTopRow = (entityTop - entity.speed)/gp.tileSize;
			tile1 = tileMgr.getMapTileNum(entityLeftCol, entityTopRow);//TOP LEFT
			tile2 = tileMgr.getMapTileNum(entityRightCol, entityTopRow);//TOP RIGHT
			if(tileMgr.getTileCollision(tile1) == true || tileMgr.getTileCollision(tile2) == true) {
				entity.collisionOn = true;
			}
			break;
		case "down":
			entityBottomRow = (entityBottom + entity.speed)/gp.tileSize;
			tile1 = tileMgr.getMapTileNum(entityLeftCol, entityBottomRow);//BOT LEFT
			tile2 = tileMgr.getMapTileNum(entityRightCol, entityBottomRow);//BOT RIGHT
			if(tileMgr.getTileCollision(tile1) == true || tileMgr.getTileCollision(tile2) == true) {
				entity.collisionOn = true;
			}
			break;
		case "left":
			entityLeftCol = (entityLeft - entity.speed)/gp.tileSize;
			tile1 = tileMgr.getMapTileNum(entityLeftCol, entityTopRow);//TOP LEFT
			tile2 = tileMgr.getMapTileNum(entityLeftCol, entityBottomRow);//BOT LEFT
			if(tileMgr.getTileCollision(tile1) == true || tileMgr.getTileCollision(tile2) == true) {
				entity.collisionOn = true;
			}
			break;
		case "right":
			entityRightCol = (entityRight + entity.speed)/gp.tileSize;
			tile1 = tileMgr.getMapTileNum(entityRightCol, entityTopRow);//TOP RIGHT
			tile2 = tileMgr.getMapTileNum(entityRightCol, entityBottomRow);//BOT RIGHT
			if(tileMgr.getTileCollision(tile1) == true || tileMgr.getTileCollision(tile2) == true) {
				entity.collisionOn = true;
			}
			break;
		}
	}
	
	public boolean checkEnemy(BMEntity entity, ArrayList<BMEnemy> targets) {
		
		boolean hit = false;
		for(int i=0; i<targets.size(); i++) {
			if(targets.get(i) != null) {
				entity.collisionBox.x = entity.worldX + entity.collisionBox.x;
				entity.collisionBox.y = entity.worldY + entity.collisionBox.y;
				targets.get(i).collisionBox.x = targets.get(i).collisionBox.x + targets.get(i).worldX;
				targets.get(i).collisionBox.y = targets.get(i).collisionBox.y + targets.get(i).worldY;
				
				switch(entity.direction) {
				case "up":
					entity.collisionBox.y -= entity.speed;
					if(entity.collisionBox.intersects(targets.get(i).collisionBox)) {
						//entity.collisionOn = true;
						hit = true;
					}
					break;
				case "down":
					entity.collisionBox.y += entity.speed;
					if(entity.collisionBox.intersects(targets.get(i).collisionBox)) {
						//entity.collisionOn = true;
						hit = true;
					}
					break;
				case "left":
					entity.collisionBox.x -= entity.speed;
					if(entity.collisionBox.intersects(targets.get(i).collisionBox)) {
						//entity.collisionOn = true;
						hit = true;
					}
					break;
				case "right":
					entity.collisionBox.x += entity.speed;
					if(entity.collisionBox.intersects(targets.get(i).collisionBox)) {
						//entity.collisionOn = true;
						hit = true;
					}
					break;
				}
				//RESET BOX VALUES
				entity.collisionBox.x = entity.collisionBoxDefaultX;
				entity.collisionBox.y = entity.collisionBoxDefaultY;
				targets.get(i).collisionBox.x = targets.get(i).collisionBoxDefaultX;
				targets.get(i).collisionBox.y = targets.get(i).collisionBoxDefaultY;
			}
		}
		return hit;
	}
	
	public boolean checkPlayer(BMEntity entity, BMPlayer player) {
		//for()
		//this.player = player;
		boolean hit = false;
		entity.collisionBox.x = entity.worldX + entity.collisionBox.x;
		entity.collisionBox.y = entity.worldY + entity.collisionBox.y;
		player.collisionBox.x = player.collisionBox.x + player.worldX;
		player.collisionBox.y = player.collisionBox.y + player.worldY;
		
		switch(entity.direction) {
		case "up":
			entity.collisionBox.y -= entity.speed;
			if(entity.collisionBox.intersects(player.collisionBox)) {
				//entity.collisionOn = true;
				hit = true;
			}
			break;
		case "down":
			entity.collisionBox.y += entity.speed;
			if(entity.collisionBox.intersects(player.collisionBox)) {
				//entity.collisionOn = true;
				hit = true;
			}
			break;
		case "left":
			entity.collisionBox.x -= entity.speed;
			if(entity.collisionBox.intersects(player.collisionBox)) {
				//entity.collisionOn = true;
				hit = true;
			}
			break;
		case "right":
			entity.collisionBox.x += entity.speed;
			if(entity.collisionBox.intersects(player.collisionBox)) {
				//entity.collisionOn = true;
				hit = true;
			}
			break;
		}
		//RESET BOX VALUES
		entity.collisionBox.x = entity.collisionBoxDefaultX;
		entity.collisionBox.y = entity.collisionBoxDefaultY;
		player.collisionBox.x = player.collisionBoxDefaultX;
		player.collisionBox.y = player.collisionBoxDefaultY;
		return hit;
	}
	
	public int checkObject(BMEntity entity) {
		int index=999;
		obj = gp.getObjects();
		for(int i =0; i<obj.size(); i++) {
			if(obj.get(i) != null) {
				entity.collisionBox.x = entity.worldX + entity.collisionBox.x;
				entity.collisionBox.y = entity.worldY + entity.collisionBox.y;
				
				obj.get(i).collisionBox.x = obj.get(i).collisionBox.x + obj.get(i).getX();
				obj.get(i).collisionBox.y = obj.get(i).collisionBox.y + obj.get(i).getY();
			
				switch(entity.direction) {
				case "up":
					entity.collisionBox.y -= entity.speed;
					if(entity.collisionBox.intersects(obj.get(i).collisionBox)) {
						//entity.collisionOn = true;
						//hit = true;
						index = i;
					}
					break;
				case "down":
					entity.collisionBox.y += entity.speed;
					if(entity.collisionBox.intersects(obj.get(i).collisionBox)) {
						//entity.collisionOn = true;
						//hit = true;
						index = i;
					}
					break;
				case "left":
					entity.collisionBox.x -= entity.speed;
					if(entity.collisionBox.intersects(obj.get(i).collisionBox)) {
						//entity.collisionOn = true;
						//hit = true;
						index = i;
					}
					break;
				case "right":
					entity.collisionBox.x += entity.speed;
					if(entity.collisionBox.intersects(obj.get(i).collisionBox)) {
						//entity.collisionOn = true;
						//hit = true;
						index = i;
					}
					break;
				}
				entity.collisionBox.x = entity.collisionBoxDefaultX;
				entity.collisionBox.y = entity.collisionBoxDefaultY;
				obj.get(i).collisionBox.x = obj.get(i).collisionBoxDefaultX;
				obj.get(i).collisionBox.y = obj.get(i).collisionBoxDefaultY;
			}
			
		}
		
		return index;
	}
	
	public boolean checkTileExp(int x, int y, boolean empty) {
		int tile = tileMgr.getMapTileNum(x, y);
		obj = gp.getObjects();
		if(tileMgr.getTileCollision(tile) == true) {//if colliding tile
			//entity.collisionOn = true;
			if(tile == 2) {//DESTROY TILE AND RANDOMIZE PU DROP
				tileMgr.setTile(0, x, y);
				int drop=randomDrop();
				if(drop!=0) {
					asset.setObject(drop, x*gp.tileSize, y*gp.tileSize);
					//System.out.println("Setting drop " + drop + " at " + x + "," + y);
					//System.out.println("Total objects: " + obj.size());
				}
				
			}
			else {
				empty = false;
			}
		}
		else {//REMOVE HIT POWER UPS
			//System.out.println(obj.size());
			for(int i=0;i<obj.size();i++) {
				//System.out.println("X: " + x + " Y: " + y);
				//System.out.println("OBJ X: " + obj.get(i).getX()/tileSize + "OBJ Y: " + obj.get(i).getY()/tileSize);
				if(x==(obj.get(i).getX())/gp.tileSize && y==(obj.get(i).getY())/gp.tileSize) {
					obj.get(i).addHits();
					boolean remove = obj.get(i).checkHits();
					if(remove) {
						obj.remove(i);
						asset.minusCnt();
					}
				}
			}
		}
		return empty;
	}
	
	public boolean checkEntityExp(int x, int y, int width, int height) {
		playerBox = new Rectangle(x,y,width,height);
		hit=false;
		for (Explosion e : explosions) {
	        if (e.getCollisionBox().intersects(playerBox)) {
	        	//gp.player.setHit(true);
		       	//gp.player.setInvincible(true);
		       	hit = true;
	            break;
	        }
	    }
		return hit;    
	}
	
	public int randomDrop() {
		int drop=0;
		Random random = new Random();
		int i = random.nextInt(100)+1;//from 1-4
		if(i>=1 && i<=15) {
			drop = 1;
			//System.out.println(drop+": CAPACITY PU");
		}
		else if(i>=16 && i<=30) {
			drop = 2;
			//System.out.println(drop+": RANGE PU");
		}
		return drop;
	}
}
