package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class UI {
	
	GamePanel gp;
	Graphics2D g2;
	Font arial_40;
	BufferedImage life;
	
	public UI(GamePanel gp) {
		this.gp = gp;
		arial_40 = new Font ("Press Start 2P Regular", Font.PLAIN, 40);
		try {
			life = ImageIO.read(getClass().getResourceAsStream("/objects/dynamite1.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ImageScaler s = new ImageScaler();
		life = s.scale(gp.tileSize, gp.tileSize, life);
		
	}
	
	/*public static void setColor(Graphics2D g2, String hexColor) {
        Color color = Color.decode(hexColor);
        g2.setColor(color);
    }*/
	
	public void draw(Graphics2D g2) {
		this.g2 = g2;
		
		g2.setFont(arial_40);
		g2.setColor(Color.white);
		if(gp.gameState == gp.playState) {
			drawLife();
		}
		else if(gp.gameState == gp.pauseState) {
			drawPauseScreen();
		}
		else if(gp.gameState == gp.gameoverState) {
			drawGameoverScreen();
		}
		else if(gp.gameState == gp.winState) {
			drawWinScreen();
		}
	}
	
	public void drawLife() {
		int x = gp.tileSize/2;
		int y = gp.tileSize/2;
		for(int i=0; i<gp.player.getLives(); i++) {
			g2.drawImage(life, x, y, null);
			x += gp.tileSize;
		}
	}
	public void drawPauseScreen() {
		String text = "PAUSED";
		
		int x = getTextX(text);
		int y = gp.screenHeight/2;
		
		g2.setColor(Color.green);
		g2.drawString(text, x+3, y+3);
		g2.setColor(Color.white);
		g2.drawString(text, x, y);
		
	}
	
	public void drawGameoverScreen() {
		String text = "GAME OVER";
		
		int x = getTextX(text);
		int y = gp.screenHeight/2;
		
		g2.drawString(text, x, y);
	}
	
	public void drawWinScreen() {
		String text = "LEVEL COMPLETE";
		
		int x = getTextX(text);
		int y = gp.screenHeight/2;
		
		g2.drawString(text, x, y);
	}
	
	public int getTextX(String text) {
		int length = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth(); //CREATE TEXT LENGTH FUNCTION
		int x = gp.screenWidth/2 - length/2;
		return x;
	}
}
