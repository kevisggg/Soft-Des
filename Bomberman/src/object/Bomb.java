package object;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import entity.Explosion;
import main.GamePanel;
import main.ImageScaler;

public class Bomb {
	GamePanel gp;
	public BufferedImage dynamite1, dynamite2;
	public String name;
	//public boolean collision;
	public int worldX, worldY, timer, timeCount, spriteCounter, spriteNum; //CHANGE TIMER TO SECS AND MULTIPLY BY FPS
	private boolean bombPlaced;
	//public ArrayList<Explosion> explosions = new ArrayList<>();
	
	public Bomb(int worldX, int worldY, GamePanel gp) {
		name = "Bomb";
		this.gp = gp;
		//collision = false;
		this.worldX = worldX;
		this.worldY = worldY;
		timer = 180;
		timeCount = 0;
		bombPlaced = true;
		//expActive = false;
		spriteCounter = 0;
		spriteNum = 1;
		dynamite1 = setupImage("/objects/dynamite1.png");
		dynamite2 = setupImage("/objects/dynamite2.png");
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
	/*public int[][] getCoords(){
		int[][] coords = {{worldX},{worldY}};
		return coords;
	}*/
	
	public int getX() {
		return this.worldX;
	}
	
	public int getY() {
		return this.worldY;
	}
	
	public boolean getBombStatus() {
		return this.bombPlaced;
	}
	
	/*public boolean getExpStatus() {
		return this.expActive;
	}*/
	
	
	
	public void update() {
		if (bombPlaced) {
			timeCount++;
			if(timeCount > timer) {
				timeCount=0;
				bombPlaced = false;
				//expActive = true;
			}
		
		//else {
			
		//}
		//CLASSIFY
		spriteCounter++;//ANIMATION
		if(spriteCounter > 30) {
			//System.out.println("spriteupdate");
			if(spriteNum == 1) {
				spriteNum = 2;
			}
			else if(spriteNum == 2) {
				spriteNum = 1;
			}
			spriteCounter = 0;
		}
		}
		/*else {
			if(expActive) {
				System.out.println("EXPLOSION");
				timeCount++;
				if(timeCount > timer) {
					timeCount=0;
					expActive = false;
				}
			}
		}*/
	}
	
	public void draw(Graphics2D g2) {
		//int x=0;
		//int y=0;
		if(bombPlaced == true) {
			BufferedImage image = null;
			if(spriteNum==1) {
				image = dynamite1;
			}
			if(spriteNum==2) {
				image = dynamite2;
			}
			g2.drawImage(image, worldX, worldY, null);
			//get center
			//make update
		}
	}
}
