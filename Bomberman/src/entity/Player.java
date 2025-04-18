package entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import main.GamePanel;
import main.KeyHandler;
import object.Bomb;

public class Player extends Entity{
	KeyHandler keyH;
	private int bombCnt, bombRadius, bombsPlaced, bombCooldown, lives, invincibleCnt, invincibleDuration, blinkInterval, collisionTile;
	boolean  isInvincible, isVisible, bombAlreadyPlaced;
	
	
	public Player(GamePanel gp, KeyHandler keyH) {
		super(gp);
		this.keyH = keyH;
		name = "Player";
		collisionBox = new Rectangle(10, 16, 28, 32);
		
		
		setDefaultVal();
		getPlayerImage();
	}
	
	public void setDefaultVal() {
		worldX = gp.tileSize;
		worldY = gp.tileSize;
		collisionBoxDefaultX = collisionBox.x;
		collisionBoxDefaultY = collisionBox.y;
		speed = 3;
		direction = "down";
		bombCnt = 3;
		bombsPlaced = 0;
		bombRadius = 1;
		bombCooldown = 0;
		lives = 3;
		invincibleCnt = 0;
		invincibleDuration = 180;//3.5 SECONDS
		blinkInterval = 5;
		//playerHit = false;
		isInvincible = false;
		isVisible = true;
		bombAlreadyPlaced = false;
		collisionTile = 0;
	}
	
	public void getPlayerImage() {
		down2 = setupImage("/player/down2.png");
		down1 = setupImage("/player/down1.png");
		right1 = setupImage("/player/right1.png");
		right2 = setupImage("/player/right2.png");
		left1 = setupImage("/player/left1.png");
		left2 = setupImage("/player/left2.png");
		up1 = setupImage("/player/up1.png");
		up2 = setupImage("/player/up2.png");
	}
	
	public void setBombCount(int bombCnt) {
		this.bombCnt = bombCnt;
	}
	
	public void setBombRadius(int bombRadius) {
		this.bombRadius = bombRadius;
	}
	
	public int getBombCount() {
		return bombCnt;
	}
	
	public int getBombRadius() {
		return bombRadius;
	}
	
	public void setInvincible(boolean set) {
		this.isInvincible = set;
	}
	
	public boolean getInvincible() {
		return isInvincible;
	}
	
	public void dropBomb() {
		Bomb b = new Bomb((worldX + gp.player.collisionBox.x + 5)/gp.tileSize*gp.tileSize, (worldY+gp.player.collisionBox.y+10)/gp.tileSize*gp.tileSize, gp);
		gp.bombs.add(b);
		bombsPlaced++;
	}
	
	public void removeBombsPlaced() {
		bombsPlaced--;
	}
	
	public void setVisibility(boolean set) {
		isVisible = set;
	}
	
	public void blinkVisibility() {
		if(invincibleCnt % blinkInterval == 0) {
			setVisibility(false);
		}
		else {
			setVisibility(true);
		}
	}
	//make lives = 0 checker?
	
	public void update() {
		bombCooldown++;
		if(lives == 0) {
			gp.gameState = gp.gameoverState;
		}
		
		if(bombCooldown > 12) {
			if(keyH.spacePressed==true) {
				//check if bombs available, MAKE METHOD
				collisionTile = gp.tileMgr.mapTileNum[(worldX + collisionBox.x+5)/gp.tileSize][(worldY+collisionBox.y+10)/gp.tileSize];
				System.out.println("=====" + worldX/gp.tileSize + "   " + worldY/gp.tileSize);
				if(bombsPlaced < bombCnt && !gp.tileMgr.getTileCollision(collisionTile)) {
					for(Bomb b: gp.bombs) {
						if((b.getX()+ collisionBox.x+5)/gp.tileSize == (worldX + collisionBox.x+5)/gp.tileSize && (b.getY()+ collisionBox.x+10)/gp.tileSize == (worldY+collisionBox.y+10)/gp.tileSize) {
							bombAlreadyPlaced = true;
							System.out.println("bombAlreadyPlaced TRUE");
							break;
						}
					}
					if(!bombAlreadyPlaced) {
						System.out.println("BOMB DROPPED");
						dropBomb();
					}
				}
			}
			bombCooldown=0;
		}
		if(keyH.upPressed == true ||keyH.downPressed == true ||keyH.leftPressed == true ||keyH.rightPressed == true) {
			if(keyH.upPressed == true) {
				direction = "up";
			}
			else if(keyH.downPressed == true) {
				direction = "down";
			}
			else if(keyH.leftPressed == true) {
				direction = "left";
			}
			else if(keyH.rightPressed == true) {
				direction = "right";
			}
			
			//CHECK COLLISION
			collisionOn = false;
			gp.colCheck.checkTile(this);
			
			//CHECK ENEMY COLLISION
			if(!getInvincible()) {
				if(gp.colCheck.checkEnemy(this, gp.enemies)) {
					collideEnemy();
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
		}
		if(entityHit) {
			System.out.println("PLAYER HIT");
			//damage
			lives--;
			System.out.println("Lives: " + lives);
			
			entityHit = false;
		}
		if(isInvincible) {
			invincibleCnt++;
			blinkVisibility();
			if(invincibleCnt >= invincibleDuration) {
				setInvincible(false);
				invincibleCnt = 0;
				setVisibility(true);
			}
		}
		//System.out.println(worldX + " " + worldY);///////////////////////////////
	}
	
	
	public void collideEnemy() {
		System.out.println("COLLISION");
		setHit(true);
		setInvincible(true);
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
		if(isVisible) {
			g2.drawImage(image, worldX, worldY, null);
		}
	}
}
