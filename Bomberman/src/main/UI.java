package main;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class UI {
	
	GamePanel gp;
	Graphics2D g2;
	float alpha50 = 0.5f;
	Font menu_60, hud_20;
	BufferedImage life;
	Rectangle resumeBounds = new Rectangle(300, 200, 120, 30);
	Rectangle restartBounds = new Rectangle(300, 250, 140, 30);
	Rectangle exitBounds = new Rectangle(300, 300, 80, 30);
	Composite originalComposite;
	
	public UI(GamePanel gp) {
		this.gp = gp;
		menu_60 = new Font ("Press Start 2P Regular", Font.PLAIN, 60);
		hud_20 = new Font ("Press Start 2P Regular", Font.PLAIN, 20);
		try {
			life = ImageIO.read(getClass().getResourceAsStream("/objects/life.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ImageScaler s = new ImageScaler();
		life = s.scale(gp.tileSize/2, gp.tileSize/2, life);
		
	}
	
	/*public static void setColor(Graphics2D g2, String hexColor) {
        Color color = Color.decode(hexColor);
        g2.setColor(color);
    }*/
	
	public void draw(Graphics2D g2) {
		this.g2 = g2;
		this.originalComposite = g2.getComposite();
		g2.setFont(menu_60);
		g2.setColor(Color.white);
		if(gp.gameState == gp.playState) {
			drawLife();
		}
		else if(gp.gameState == gp.pauseState) {
			drawLife();
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
		g2.setColor(Color.black);
		g2.fillRect(5, 5, gp.tileSize*6, 36);
		g2.setColor(Color.blue);
		g2.setFont(hud_20);
		g2.drawString("Lives:", 10, 34);
		//g2.rect
		int x = gp.tileSize*3;
		int y = 10;
		for(int i=0; i<gp.player.getLives(); i++) {
			g2.drawImage(life, x, y, null);
			x += gp.tileSize;
		}
	}
	public void drawPauseScreen() {
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha50));
		g2.setColor(Color.black);
		g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
		g2.setComposite(originalComposite);
		g2.setFont(menu_60);
		String text = "PAUSED";
		
		int x = getTextX(text);
		int y = gp.screenHeight/3;
		
		
		g2.setColor(getColor("#CC6600"));
		g2.drawString(text, x+5, y+5);
		g2.setColor(getColor("#FFD966"));
		g2.drawString(text, x, y);

		// In your paintComponent or draw method
		g2.setColor(Color.white);
		g2.setFont(hud_20);
		//g2.setFont(new Font("Arial", Font.BOLD, 24));
		g2.draw(resumeBounds);
		g2.draw(restartBounds);
		g2.draw(exitBounds);
		g2.drawString("RESUME", resumeBounds.x, resumeBounds.y+resumeBounds.height);
		g2.drawString("RESTART", resumeBounds.x, restartBounds.y+restartBounds.height);
		g2.drawString("EXIT", x, exitBounds.y);
		if(gp.mouseH.getClicked()) {
			Point p = gp.mouseH.getPoint();
			if (resumeBounds.contains(p)) {
	            System.out.println("RESUME");
	            gp.gameState=gp.playState;
	        } else if (restartBounds.contains(p)) {
	            System.out.println("RESTART");
	            gp.resetGame();
	        }
			gp.mouseH.resetClick();
		}
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
	
	public Color getColor(String hex) {
		Color color = Color.decode(hex);
		return color;
	}
}
