package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

import main.CollisionChecker;
import main.GamePanel;
import main.ImageScaler;
import object.Bomb;

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
	
	/*public void update() {
			
			//CHECK COLLISION
			collisionOn = false;
			gp.colCheck.checkTile(this);
			
			//COLLISION FALSE, THEN PLAYER MOVE
			if(collisionOn == false) {
				switch(direction) {
				case "up": worldY -= speed; break;
				case "down": worldY += speed; break;
				case "left": worldX -= speed; break;
				case "right": worldX += speed; break;
				}
			}
			
			spriteCounter++;//ANIMATION
			if(spriteCounter > 12) {
				if(spriteNum == 1) {
					spriteNum = 2;
				}
				else if(spriteNum == 2) {
					spriteNum = 1;
				}
				spriteCounter = 0;
			}
		//System.out.println(worldX + " " + worldY);///////////////////////////////
	}*/
	
	
//	public void draw(Graphics2D g2) {
//		BufferedImage image = null;
//		
//		switch(direction) {
//		case "up":
//			if(spriteNum==1) {
//				image = up1;
//			}
//			if(spriteNum==2) {
//				image = up2;
//			}
//			break;
//		case "down":
//			if(spriteNum==1) {
//				image = down1;
//			}
//			if(spriteNum==2) {
//				image = down2;
//			}
//			break;
//		case "left":
//			if(spriteNum==1) {
//				image = left1;
//			}
//			if(spriteNum==2) {
//				image = left2;
//			}
//			break;
//		case "right":
//			if(spriteNum==1) {
//				image = right1;
//			}
//			if(spriteNum==2) {
//				image = right2;
//			}
//			break;
//		}
//		g2.drawImage(image, worldX, worldY, null);
//	}
}
