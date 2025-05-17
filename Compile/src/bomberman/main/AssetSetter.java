package bomberman.main;

import bomberman.entity.Enemy;
import bomberman.object.PU_Capacity;
import bomberman.object.PU_Range;
//import bomberman.object.PowerUp;

public class AssetSetter {

	private BomberManGamePanel gp;
	private int objectMax, objectCnt;
	
	public AssetSetter(BomberManGamePanel gp) {
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
		objectCnt--;
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
