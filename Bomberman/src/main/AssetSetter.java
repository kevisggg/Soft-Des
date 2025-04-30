package main;

import entity.Enemy;
import object.PU_Capacity;
import object.PU_Range;

public class AssetSetter {

	GamePanel gp;
	int objectMax, objectCnt;
	
	public AssetSetter(GamePanel gp) {
		this.gp = gp;
		this.objectMax = 5;
		this.objectCnt = 0;
	}
	
	public void setObject(int i, int x, int y) {
		switch(i) {
		case 1: gp.obj.add(new PU_Capacity()); break;
		case 2: gp.obj.add(new PU_Range()); break;
		}
		objectCnt++;
		gp.obj.getLast().setX(x);
		gp.obj.getLast().setY(y);
		checkObjCnt();
		//gp.obj.add(new );
		/*gp.obj[0] = new PU_Capacity();
		gp.obj[0].worldX = gp.player.worldX;
		gp.obj[0].worldY = gp.player.worldY;*/
	}
	
	public void checkObjCnt() {
		if(objectCnt>objectMax) {
			gp.obj.remove(0);
			objectCnt--;
		}
	}
	
	public void setEnemy() {
		gp.enemies.add(new Enemy(gp, gp.tileSize, gp.tileSize*6));
		gp.enemies.add(new Enemy(gp, gp.tileSize*8, gp.tileSize));
		gp.enemies.add(new Enemy(gp, gp.tileSize*5, gp.tileSize*10));
		gp.enemies.add(new Enemy(gp, gp.tileSize*14, gp.tileSize*8));
		gp.enemies.add(new Enemy(gp, gp.tileSize*9, gp.tileSize*6));
	}
}
