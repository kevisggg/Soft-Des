package object;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import main.GamePanel;

public class PowerUp {
	
	public BufferedImage image;
	public String name;
	public int worldX, worldY, hits;
	public boolean remove;
	public Rectangle collisionBox = new Rectangle(0,0,48,48);
	public int collisionBoxDefaultX, collisionBoxDefaultY;
	
	public void setX(int x) {
		this.worldX = x;
	}
	
	public void setY(int y) {
		this.worldY = y;
	}
	
	public int getX() {
		return worldX;
	}
	
	public int getY() {
		return worldY;
	}
	
	public String getName() {
		return name;
	}

	public void addHits() {
		hits++;
	}

	public boolean checkHits() {
		if(hits>=2) {
			remove=true;
		}
		return remove;
	}
	
	public void draw(Graphics2D g2, GamePanel gp) {
		g2.drawImage(image, worldX, worldY, gp.tileSize, gp.tileSize, null);
	}
}
