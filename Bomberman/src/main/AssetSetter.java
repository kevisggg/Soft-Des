package main;

import entity.Enemy;
import object.OBJ_Bomb;

public class AssetSetter {

	GamePanel gp;
	
	public AssetSetter(GamePanel gp) {
		this.gp = gp;
	}
	
	public void setObject() {
		gp.obj[0] = new OBJ_Bomb();
		gp.obj[0].worldX = gp.player.worldX;
		gp.obj[0].worldY = gp.player.worldY;
	}
	
	public void setEnemy() {
		gp.enemies.add(new Enemy(gp, gp.tileSize, gp.tileSize*6));
		gp.enemies.add(new Enemy(gp, gp.tileSize*8, gp.tileSize));
		gp.enemies.add(new Enemy(gp, gp.tileSize*5, gp.tileSize*10));
		gp.enemies.add(new Enemy(gp, gp.tileSize*14, gp.tileSize*8));
		gp.enemies.add(new Enemy(gp, gp.tileSize*9, gp.tileSize*6));
	}
}
