package bomberman.main;

import bomberman.entity.BMEnemy;
import bomberman.object.PU_Capacity;
import bomberman.object.PU_Range;
//import bomberman.object.PowerUp;

public class BMAssetSetter {

	private BMGamePanel gp;
	private int objectMax, objectCnt;
	
	public BMAssetSetter(BMGamePanel gp) {
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
		gp.addEnemy(new BMEnemy(gp, BMGamePanel.tileSize, BMGamePanel.tileSize*6, gp.getCollisionChecker()));
		gp.addEnemy(new BMEnemy(gp, BMGamePanel.tileSize*8, BMGamePanel.tileSize, gp.getCollisionChecker()));
		gp.addEnemy(new BMEnemy(gp, BMGamePanel.tileSize*5, BMGamePanel.tileSize*10, gp.getCollisionChecker()));
		gp.addEnemy(new BMEnemy(gp, BMGamePanel.tileSize*14, BMGamePanel.tileSize*8, gp.getCollisionChecker()));
		gp.addEnemy(new BMEnemy(gp, BMGamePanel.tileSize*9, BMGamePanel.tileSize*6, gp.getCollisionChecker()));
	}
	
	public void resetObjCnt() {
		objectCnt = 0;
	}
}
