package entity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import main.GamePanel;
import main.ImageScaler;
import object.Bomb;

public class Explosion{
	public int worldX, worldY, timeCount, timer;
	GamePanel gp;
	BufferedImage exp;
	private boolean expActive, expStart, hasHitPlayer;
	public Explosion(int x, int y, GamePanel gp) {
		worldX = x;
		worldY = y;
		this.gp = gp;
		timer = 60;
		expActive = true;
		expStart = false;
		//hasHitPlayer = false;
		exp = setupImage("/objects/explosion.png");
	}
	
	public BufferedImage setupImage(String path) {
		BufferedImage image = null;
		ImageScaler s = new ImageScaler();
		try {
			image = ImageIO.read(getClass().getResourceAsStream(path));
			image = s.scale(gp.tileSize, gp.tileSize, image);
		}catch(IOException e) {
			e.printStackTrace();
		}
		return image;
	}
	
	public void getExpArea() {
		//for(Bomb b: gp.bombs) {
			//int expX = 
		//}
	}
	
	public int getX() {
		return this.worldX;
	}
	
	public int getY() {
		return this.worldY;
	}
	
	public void update() {
		if (expActive) {
			timeCount++;
			if(timeCount > timer) {
				timeCount=0;
				expActive = false;
			}
		}
	}
	
	public boolean getExpStatus() {
		return expActive;
	}
	
	public boolean getExpStart() {
		return expStart;
	}
	
	/*public boolean getHasHit() {
		return hasHitPlayer;
	}*/
	
	public void setExpStatus(boolean set) {
		this.expStart = set;
	}
	
	/*public void setHit(boolean set) {
		this.hasHitPlayer = set;
	}*/
	
	public void draw(Graphics2D g2, GamePanel gp){
		if(expActive) {
			g2.drawImage(exp, worldX, worldY, null);
		}
	}
}
