package bomberman.entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import bomberman.main.BMCollisionChecker;
import bomberman.main.BomberManGamePanel;
import core.EntityInterface;
import core.ImageScaler;

//ABSTRACT store variables used in player npc classes
public class BMEntity implements EntityInterface {
	public BomberManGamePanel gp;
	public BMCollisionChecker colCheck;
	public int worldX, worldY;
	public int speed;
	public String name;
	public BufferedImage down2, down1, right1, right2, left1, left2, up1, up2;
	public String direction;
	
	public int spriteCounter = 0;
	public int spriteNum = 1;
	public Rectangle collisionBox = new Rectangle(0, 0, 48, 48);
	public boolean collisionOn = false, entityHit = false;
	public int collisionBoxDefaultX, collisionBoxDefaultY;
	
	public BMEntity(BomberManGamePanel gp, BMCollisionChecker colCheck) {
		this.gp = gp;
		this.colCheck = colCheck;
	}
	
	public int getX() {
		return worldX + collisionBox.x;
	}
	
	public int getY() {
		return worldY + collisionBox.y;
	}
	
	public int getWidth() {
		return collisionBox.width;
	}
	
	public int getHeight() {
		return collisionBox.height;
	}
	
	public void setHit(boolean hit) {
		this.entityHit = hit;
	}
	
	public boolean getHit() {
		return entityHit;
	}
	
	public void setMovement() {}
	
	public void update() {
		setMovement();
		
		collisionOn = false;
		colCheck.checkTile(this);
		BMPlayer player = (BMPlayer) gp.getPlayer();
		if(!gp.getPlayer().getInvincible()) {
			if(colCheck.checkPlayer(this, player)) {
				player.collideEnemy();
			}
		}
		
		//COLLISION FALSE, THEN PLAYER MOVE
		if(collisionOn == false) {
			switch(direction) {
			case "up": worldY -= speed; break;
			case "down": worldY += speed; break;
			case "left": worldX -= speed; break;
			case "right": worldX += speed; break;
			}
		}
		else {
			setMovement();
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
		};
	}
	public BufferedImage setupImage(String path) {
		BufferedImage image = null;
		ImageScaler s = new ImageScaler();
		try {
			image = ImageIO.read(getClass().getResourceAsStream(path));
			image = s.scale(BomberManGamePanel.tileSize, BomberManGamePanel.tileSize, image);
		}catch(IOException e) {
			e.printStackTrace();
		}
		return image;
	}
	
	public void draw(Graphics2D g2) {
		BufferedImage image = null;
		
		switch(direction) {
		case "up":
			if(spriteNum==1) {
				image = up1;
			}
			if(spriteNum==2) {
				image = up2;
			}
			break;
		case "down":
			if(spriteNum==1) {
				image = down1;
			}
			if(spriteNum==2) {
				image = down2;
			}
			break;
		case "left":
			if(spriteNum==1) {
				image = left1;
			}
			if(spriteNum==2) {
				image = left2;
			}
			break;
		case "right":
			if(spriteNum==1) {
				image = right1;
			}
			if(spriteNum==2) {
				image = right2;
			}
			break;
		}
		g2.drawImage(image, worldX, worldY, BomberManGamePanel.tileSize, BomberManGamePanel.tileSize, null);
	}
	
}
