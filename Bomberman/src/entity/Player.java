package entity;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import main.CollisionChecker;
import main.GamePanel;
import main.KeyHandler;
import object.Bomb;
import object.PowerUp;

public class Player extends Entity{
	private KeyHandler keyH;
	private PowerUp obj;
	private int bombCnt, bombRadius, bombsPlaced, bombCooldown, lives, invincibleCnt, invincibleDuration, blinkInterval, collisionTile;
	private boolean  isInvincible, isVisible, bombAlreadyPlaced;
	
	
	public Player(GamePanel gp, KeyHandler keyH, CollisionChecker colCheck) {
		super(gp, colCheck);
		this.keyH = keyH;
		name = "Player";
		collisionBox = new Rectangle(10, 16, 28, 32);
		lives = 3;
		speed = 3;
		invincibleDuration = 180;//3.5 SECONDS
		blinkInterval = 5;
		
		setDefaultVal();
		getPlayerImage();
	}
	
	public void setDefaultVal() {
		worldX = GamePanel.tileSize;
		worldY = GamePanel.tileSize;
		collisionBoxDefaultX = collisionBox.x;
		collisionBoxDefaultY = collisionBox.y;
		direction = "down";
		bombCnt = 1;
		bombsPlaced = 0;
		bombRadius = 1;
		bombCooldown = 0;
		invincibleCnt = 0;
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
	
	public int getPURadius() {
		return bombRadius-1;
	}
	
	public int getPUcap() {
		return bombCnt-1;
	}
	
	public int getBombsAvail() {
		return bombCnt-bombsPlaced;
	}
	
	public int getBombCount() {
		return bombCnt;
	}
	
	public int getBombRadius() {
		return bombRadius;
	}
	
	public int getLives() {
		return lives;
	}
	
	public void setInvincible(boolean set) {
		this.isInvincible = set;
	}
	
	public boolean getInvincible() {
		return isInvincible;
	}
	
	public void dropBomb() {
		Bomb b = new Bomb((worldX + collisionBox.x + 5)/gp.tileSize*gp.tileSize, (worldY+collisionBox.y+10)/gp.tileSize*gp.tileSize, gp);
		gp.getBombs().add(b);
		bombsPlaced++;
		gp.playSFX(1);
		gp.playSFX(7);
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
	
	public void update() {
		bombCooldown++;
		if(lives == 0) {
			gp.updateLeaderboard();
			gp.setOverState();
			gp.playSFX(8);
		}
		if(bombCooldown > 12) {
			if(keyH.getSpace()==true) {
				//check if bombs available, MAKE METHOD
				collisionTile = gp.getTileManager().getMapTileNum((worldX + collisionBox.x+5)/GamePanel.tileSize, (worldY+collisionBox.y+10)/GamePanel.tileSize);
				if(bombsPlaced < bombCnt && !gp.getTileManager().getTileCollision(collisionTile)) {
					bombAlreadyPlaced = false;
					for(Bomb b: gp.getBombs()) {
						if((b.getX()+ collisionBox.x+5)/GamePanel.tileSize == (worldX + collisionBox.x+5)/GamePanel.tileSize && (b.getY()+ collisionBox.x+10)/GamePanel.tileSize == (worldY+collisionBox.y+10)/GamePanel.tileSize) {
							bombAlreadyPlaced = true;
							break;
						}
					}
					if(!bombAlreadyPlaced) {
						dropBomb();
					}
				}
			}
			bombCooldown=0;
		}
		if(keyH.getUp() == true ||keyH.getDown() == true ||keyH.getLeft() == true || keyH.getRight() == true) {
			if(keyH.getUp() == true) {
				direction = "up";
			}
			else if(keyH.getDown() == true) {
				direction = "down";
			}
			else if(keyH.getLeft() == true) {
				direction = "left";
			}
			else if(keyH.getRight() == true) {
				direction = "right";
			}
			
			//CHECK COLLISION
			collisionOn = false;
			colCheck.checkTile(this);
			
			//CHECK POWERUP PICKUP
			int objIndex = colCheck.checkObject(this);
			if(objIndex!=999) {//if powerup picked up
				//addScore(SCORE_PU);
				//System.out.println("PICKUP: " + gp.obj.get(objIndex).name);
				gp.minusObjCnt();
				obj = gp.getObj(objIndex);
				switch(obj.getName()) {
				case "PUcapacity":
					increaseCapacity();
					break;
				case "PUrange":
					increaseRange();
					break;
				}
				gp.removeObj(objIndex);
				gp.playSFX(4);
				gp.getScoreHandler().addScorePowerUp();
			}
			
			//CHECK ENEMY COLLISION
			if(!getInvincible()) {
				if(colCheck.checkEnemy(this, gp.getEnemies())) {
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
			//System.out.println("PLAYER HIT");
			lives--;
			//System.out.println("Lives: " + lives);
			
			entityHit = false;
			gp.playSFX(3);
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
	}
	
	
	private void increaseRange() {
		bombRadius++;
	}

	private void increaseCapacity() {
		bombCnt++;
	}

	public void collideEnemy() {
		setHit(true);
		setInvincible(true);
		gp.playSFX(3);
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
	    
		//COLLISION BOX TESTER
		/*g2.setColor(Color.RED);
		 * Rectangle actualCollisionBox = new Rectangle(
	        worldX + collisionBox.x,  // Absolute X position on screen
	        worldY + collisionBox.y,  // Absolute Y position on screen
	        collisionBox.width,
	        collisionBox.height
	    );
	    g2.draw(actualCollisionBox);*/
	}
}
