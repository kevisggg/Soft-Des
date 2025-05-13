package entity;

import java.awt.Rectangle;
import java.util.Random;
import main.CollisionChecker;
import main.GamePanel;


public class Enemy extends Entity{
	private int movementLimit, movementCnt;
	public Enemy(GamePanel gp, int x, int y, CollisionChecker colCheck) {
		super(gp, colCheck);
		
		name = "Bot";
		speed = 1;
		worldX = x;
		worldY = y;
		direction = "down";
		collisionBox = new Rectangle(10, 16, 28, 38);
		collisionBoxDefaultX = collisionBox.x;
		collisionBoxDefaultY = collisionBox.y;
		movementLimit = 120;
		movementCnt = 0;
		
		getEnemyImage();
	}
	
	public void getEnemyImage() {
		down2 = setupImage("/enemy/edown2.png");
		down1 = setupImage("/enemy/edown1.png");
		right1 = setupImage("/enemy/eright1.png");
		right2 = setupImage("/enemy/eright2.png");
		left1 = setupImage("/enemy/eleft1.png");
		left2 = setupImage("/enemy/eleft2.png");
		up1 = setupImage("/enemy/eup1.png");
		up2 = setupImage("/enemy/eup2.png");
	}
	
	public void setMovement() {
		movementCnt++;
		//WITH MOVEMENT LIMIT OR JUST COLLISION
		if(movementCnt==movementLimit || collisionOn == true) {
			Random random = new Random();
			int i = random.nextInt(4)+1;//from 1-4
			switch(i) {
			case 1: direction = "up"; break;
			case 2: direction = "down"; break;
			case 3: direction = "left"; break;
			case 4: direction = "right"; break;
			}
			movementCnt = 0;
		}
		
	}
}
