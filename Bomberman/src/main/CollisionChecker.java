package main;

import java.util.ArrayList;

import entity.Enemy;
import entity.Entity;

public class CollisionChecker {
	
	GamePanel gp;
	
	public CollisionChecker(GamePanel gp) {
		this.gp = gp;
	}
	
	public void checkTile(Entity entity) {
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
			tile1 = gp.tileMgr.mapTileNum[entityLeftCol][entityTopRow];//TOP LEFT
			tile2 = gp.tileMgr.mapTileNum[entityRightCol][entityTopRow];//TOP RIGHT
			if(gp.tileMgr.tile[tile1].collision == true || gp.tileMgr.tile[tile2].collision == true) {
				entity.collisionOn = true;
			}
			break;
		case "down":
			entityBottomRow = (entityBottom + entity.speed)/gp.tileSize;
			tile1 = gp.tileMgr.mapTileNum[entityLeftCol][entityBottomRow];//BOT LEFT
			tile2 = gp.tileMgr.mapTileNum[entityRightCol][entityBottomRow];//BOT RIGHT
			if(gp.tileMgr.tile[tile1].collision == true || gp.tileMgr.tile[tile2].collision == true) {
				entity.collisionOn = true;
			}
			break;
		case "left":
			entityLeftCol = (entityLeft - entity.speed)/gp.tileSize;
			tile1 = gp.tileMgr.mapTileNum[entityLeftCol][entityTopRow];//TOP LEFT
			tile2 = gp.tileMgr.mapTileNum[entityLeftCol][entityBottomRow];//BOT LEFT
			if(gp.tileMgr.tile[tile1].collision == true || gp.tileMgr.tile[tile2].collision == true) {
				entity.collisionOn = true;
			}
			break;
		case "right":
			entityRightCol = (entityRight + entity.speed)/gp.tileSize;
			tile1 = gp.tileMgr.mapTileNum[entityRightCol][entityTopRow];//TOP RIGHT
			tile2 = gp.tileMgr.mapTileNum[entityRightCol][entityBottomRow];//BOT RIGHT
			if(gp.tileMgr.tile[tile1].collision == true || gp.tileMgr.tile[tile2].collision == true) {
				entity.collisionOn = true;
			}
			break;
		}
	}
	
	public boolean checkEnemy(Entity entity, ArrayList<Enemy> targets) {
		
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
				entity.collisionBox.y = entity.collisionBoxDefaultX;
				targets.get(i).collisionBox.x = targets.get(i).collisionBoxDefaultX;
				targets.get(i).collisionBox.y = targets.get(i).collisionBoxDefaultY;
			}
		}
		return hit;
	}
	
	public boolean checkPlayer(Entity entity) {
		//for()
		boolean hit = false;
		entity.collisionBox.x = entity.worldX + entity.collisionBox.x;
		entity.collisionBox.y = entity.worldY + entity.collisionBox.y;
		gp.player.collisionBox.x = gp.player.collisionBox.x + gp.player.worldX;
		gp.player.collisionBox.y = gp.player.collisionBox.y + gp.player.worldY;
		
		switch(entity.direction) {
		case "up":
			entity.collisionBox.y -= entity.speed;
			if(entity.collisionBox.intersects(gp.player.collisionBox)) {
				//entity.collisionOn = true;
				hit = true;
			}
			break;
		case "down":
			entity.collisionBox.y += entity.speed;
			if(entity.collisionBox.intersects(gp.player.collisionBox)) {
				//entity.collisionOn = true;
				hit = true;
			}
			break;
		case "left":
			entity.collisionBox.x -= entity.speed;
			if(entity.collisionBox.intersects(gp.player.collisionBox)) {
				//entity.collisionOn = true;
				hit = true;
			}
			break;
		case "right":
			entity.collisionBox.x += entity.speed;
			if(entity.collisionBox.intersects(gp.player.collisionBox)) {
				//entity.collisionOn = true;
				hit = true;
			}
			break;
		}
		//RESET BOX VALUES
		entity.collisionBox.x = entity.collisionBoxDefaultX;
		entity.collisionBox.y = entity.collisionBoxDefaultX;
		gp.player.collisionBox.x = gp.player.collisionBoxDefaultX;
		gp.player.collisionBox.y = gp.player.collisionBoxDefaultY;
		return hit;
	}
	
	public int checkObject(Entity entity) {
		int index=999;
		
		for(int i =0; i<gp.obj.size(); i++) {
			if(gp.obj.get(i) != null) {
				entity.collisionBox.x = entity.worldX + entity.collisionBox.x;
				entity.collisionBox.y = entity.worldY + entity.collisionBox.y;
				
				gp.obj.get(i).collisionBox.x = gp.obj.get(i).collisionBox.x + gp.obj.get(i).getX();
				gp.obj.get(i).collisionBox.y = gp.obj.get(i).collisionBox.y + gp.obj.get(i).getY();
			
				switch(entity.direction) {
				case "up":
					entity.collisionBox.y -= entity.speed;
					if(entity.collisionBox.intersects(gp.obj.get(i).collisionBox)) {
						//entity.collisionOn = true;
						//hit = true;
						index = i;
					}
					break;
				case "down":
					entity.collisionBox.y += entity.speed;
					if(entity.collisionBox.intersects(gp.obj.get(i).collisionBox)) {
						//entity.collisionOn = true;
						//hit = true;
						index = i;
					}
					break;
				case "left":
					entity.collisionBox.x -= entity.speed;
					if(entity.collisionBox.intersects(gp.obj.get(i).collisionBox)) {
						//entity.collisionOn = true;
						//hit = true;
						index = i;
					}
					break;
				case "right":
					entity.collisionBox.x += entity.speed;
					if(entity.collisionBox.intersects(gp.obj.get(i).collisionBox)) {
						//entity.collisionOn = true;
						//hit = true;
						index = i;
					}
					break;
				}
				entity.collisionBox.x = entity.collisionBoxDefaultX;
				entity.collisionBox.y = entity.collisionBoxDefaultX;
				gp.obj.get(i).collisionBox.x = gp.obj.get(i).collisionBoxDefaultX;
				gp.obj.get(i).collisionBox.y = gp.obj.get(i).collisionBoxDefaultY;
			}
			
		}
		
		return index;
	}
}
