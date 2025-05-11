package main;

import entity.Enemy;
import object.PU_Capacity;
import object.PU_Range;
import object.PowerUp;

public class AssetSetter {

	private GamePanel gp;
	private int objectMax, objectCnt;
	
	public AssetSetter(GamePanel gp) {
		this.gp = gp;
		this.objectMax = 5;
		this.objectCnt = 0;
	}
	
	public void setObject(int i, int x, int y) {
		switch(i) {
		case 1: gp.addObj(new PU_Capacity()); break;
		case 2: gp.addObj(new PU_Range()); break;
		}
		objectCnt++;
		gp.setObjXY(x, y);
		checkObjCnt();
	}
	
	public void minusCnt() {
		System.out.println("OLD OBJCOUNT: " + objectCnt);
		objectCnt--;
		System.out.println("NEW OBJCOUNT: " + objectCnt);
	}
	
	public void checkObjCnt() {
		if(objectCnt>objectMax) {
			gp.removeFirstObj();
			objectCnt--;
		}
	}
	
	public void setEnemy() {
		gp.addEnemy(new Enemy(gp, gp.tileSize, gp.tileSize*6, gp.getCollisionChecker()));
		gp.addEnemy(new Enemy(gp, gp.tileSize*8, gp.tileSize, gp.getCollisionChecker()));
		gp.addEnemy(new Enemy(gp, gp.tileSize*5, gp.tileSize*10, gp.getCollisionChecker()));
		gp.addEnemy(new Enemy(gp, gp.tileSize*14, gp.tileSize*8, gp.getCollisionChecker()));
		gp.addEnemy(new Enemy(gp, gp.tileSize*9, gp.tileSize*6, gp.getCollisionChecker()));
	}
	
	public void resetObjCnt() {
		objectCnt = 0;
	}
}
