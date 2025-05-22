package bomberman.tile;

import java.awt.image.BufferedImage;

public class Tile {//tile class. make object for each different tile
	
	private BufferedImage image;
	private boolean collision = false;
	
	public BufferedImage getImage() {
		return image;
	}
	
	public void setImage(BufferedImage image) {
		this.image = image;
	}
	
	public boolean getCollision() {
		return collision;
	}
	
	public void setCollision(boolean collision) {
		this.collision = collision;
	}
}
