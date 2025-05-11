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
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class UI {
	
	GamePanel gp;
	MouseHandler mouseH;
	ScoreHandler scoreH;
	Graphics2D g2;
	float alpha50 = 0.5f;
	float alpha70 = 0.7f;
	Font menu_60, menu_50, menu_30, hud_20, hud_10;
	BufferedImage life, bomb, PUcap, PUrange, image, ins;
	ImageScaler s;
	Rectangle resumeBounds = new Rectangle(324, 200, 120, 30);
	Rectangle restartBounds = new Rectangle(314, 250, 140, 30);
	Rectangle returnMenuBounds = new Rectangle(166, 408, 436, 30);
	Rectangle exitBounds = new Rectangle(344, 300, 80, 30);
	Rectangle okBounds = new Rectangle(353, 500, 62, 30);
	Composite originalComposite;
	
	public UI(GamePanel gp, MouseHandler mouseH, ScoreHandler scoreH) {
		this.gp = gp;
		this.mouseH = mouseH;
		this.scoreH = scoreH;
		menu_60 = new Font ("Press Start 2P Regular", Font.BOLD, 60);
		menu_50 = new Font ("Press Start 2P Regular", Font.BOLD, 50);
		menu_30 = new Font ("Press Start 2P Regular", Font.BOLD, 30);
		hud_20 = new Font ("Press Start 2P Regular", Font.BOLD, 20);
		hud_10 = new Font ("Press Start 2P Regular", Font.BOLD, 13);
		life = setupImage("/objects/life.png", 15);
		PUcap = setupImage("/objects/PUcapacity.png", 15);
		PUrange = setupImage("/objects/PUrange.png", 15);
		bomb = setupImage("/objects/dynamite1.png", 10);
		try {
			ins = ImageIO.read(getClass().getResourceAsStream("/objects/instructions.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public BufferedImage setupImage(String path, int sizeDiff) {
		image = null;
		s = new ImageScaler();
		try {
			image = ImageIO.read(getClass().getResourceAsStream(path));
			image = s.scale(gp.tileSize-sizeDiff, gp.tileSize-sizeDiff, image);
		}catch(IOException e) {
			e.printStackTrace();
		}
		return image;
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
		drawHUD();
		if(gp.getGameState() == gp.instructionState) {
			drawInstruction();
		}
		else if(gp.getGameState() == gp.playState) {
			
		}
		else if(gp.getGameState() == gp.pauseState) {
			
			drawPauseScreen();
		}
		else if(gp.getGameState() == gp.gameoverState) {
			drawGameoverScreen();
		}
		else if(gp.getGameState() == gp.winState) {
			
			drawWinScreen();
			
		}
	}
	
	/*public void setComp(float alpha) {
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
	}*/
	
	public void setTextBG(int recScreenX, int recScreenY, int width, int height) {
		//setComp(alpha70);
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha70));
		g2.setColor(Color.black);
		g2.fillRect(recScreenX, recScreenY, width, height);
		g2.setComposite(originalComposite);
	}
	
	public void setBG() {
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha50));
		g2.setColor(Color.black);
		g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
		g2.setComposite(originalComposite);
	}
	
	public void addShadow(int indent, String text, String colorBack, String colorFront, int x, int y) {
		g2.setColor(getColor(colorBack));//colorback
		g2.drawString(text, x+indent, y+indent);
		g2.setColor(getColor(colorFront));//colorfront
		g2.drawString(text, x, y);
	}
	
	public void drawHUD() {
		drawLife();
		drawLevel();
		drawScore();
		drawBPUCounter();
	}
	
	public void drawLife() {
		//g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha70));
		//setComp(alpha70);
		/*g2.setColor(Color.black);
		g2.fillRect(5, 5, gp.tileSize*6, 36);
		g2.setComposite(originalComposite);*/
		setTextBG(5, 5, gp.tileSize*6, 36);
		g2.setColor(getColor("#3162C3"));
		g2.setFont(hud_20);
		g2.drawString("Lives:", 10, 34);
		//g2.rect
		int x = gp.tileSize*3;
		int y = 5;
		for(int i=0; i<gp.player.getLives(); i++) {
			g2.drawImage(life, x, y, null);
			x += gp.tileSize;
		}
	}
	
	public void drawLevel() {
		//g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha70));
		/*setComp(alpha70);
		g2.setColor(Color.black);
		g2.fillRect(585, 535, gp.tileSize*3+32, 36);
		g2.setComposite(originalComposite);*/
		setTextBG(571, 535, gp.tileSize*4, 36);
		g2.setColor(getColor("#3162C3"));
		g2.setFont(hud_20);
		g2.drawString("Level:", 576, 564);
		g2.drawString(gp.getLvl(), 696, 564);
	}
	
	public void drawScore() {//768
		setTextBG(379, 5, gp.tileSize*8, 36);
		g2.setColor(getColor("#3162C3"));
		g2.setFont(hud_20);
		g2.drawString("Score:", 384, 34);
		//int h = getTextX("Score:");
		g2.drawString(scoreH.getScore(), 504, 34);
	}
	
	public void drawBPUCounter() {
		setTextBG(gp.tileSize-5, 535, gp.tileSize*3+40, 36);
		g2.setFont(hud_10);
		g2.setColor(Color.WHITE);
		g2.drawImage(bomb, gp.tileSize, 535, null);
		g2.drawString(gp.player.getBombsAvail()+"x", gp.tileSize+20, 551);
		g2.drawImage(PUrange, gp.tileSize*2+10, 536, null);
		g2.drawString(gp.player.getPURadius()+"x", gp.tileSize*2+30, 551);
		g2.drawImage(PUcap, gp.tileSize*3+20, 536, null);
		g2.drawString(gp.player.getPUcap()+"x", gp.tileSize*3+40, 551);
	}
	
	public void drawInstruction() {
		setBG();
		g2.drawImage(ins, 0, 0, null);
		g2.setColor(Color.white);
		g2.setFont(menu_30);
		String text = "OK";
		int x = getTextX(text);
		int y = 500;
		addShadow(3, text, "#CC6600", "#FFD966", x, y+okBounds.height);
		if(mouseH.getClicked()) {
			Point p = mouseH.getPoint();
			if (okBounds.contains(p)) {
	            System.out.println("OK");
	            gp.playSFX(5);
	            gp.setPlayState();
	        }
			mouseH.resetClick();
		}
	}
	
	public void drawPauseScreen() {
		//g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha50));
		//setComp(alpha50);
		setBG();
		g2.setFont(menu_60);
		String text = "PAUSED";
		
		int x = getTextX(text);
		int y = gp.screenHeight/3;
		
		addShadow(5, text, "#CC6600", "#FFD966", x, y);
		/*g2.setColor(getColor("#CC6600"));
		g2.drawString(text, x+5, y+5);
		g2.setColor(getColor("#FFD966"));
		g2.drawString(text, x, y);*/

		// In your paintComponent or draw method
		g2.setColor(Color.white);
		g2.setFont(hud_20);
		//g2.setFont(new Font("Arial", Font.BOLD, 24));
		//g2.draw(resumeBounds);
		//g2.draw(restartBounds);
		//g2.draw(exitBounds);
		g2.drawString("RESUME", resumeBounds.x, resumeBounds.y+resumeBounds.height);
		g2.drawString("RESTART", restartBounds.x, restartBounds.y+restartBounds.height);
		g2.drawString("EXIT", exitBounds.x, exitBounds.y+exitBounds.height);
		//int hh = getTextX("RESUME");
		//System.out.println(hh);
		//hh = getTextX("RESTART");
		//System.out.println(hh);
		//hh = getTextX("EXIT");
		//System.out.println(hh);
		/*RESUME X LENGTH: 120
324
RESTART X LENGTH: 140
314
EXIT X LENGTH: 80
344*/
		if(mouseH.getClicked()) {
			Point p = mouseH.getPoint();
			if (resumeBounds.contains(p)) {
	            System.out.println("RESUME");
	            gp.playSFX(5);
	            gp.setPlayState();
	        } else if (restartBounds.contains(p)) {
	            System.out.println("RESTART");
	            gp.playSFX(5);
	            gp.restartGame();
	        }
			else if (exitBounds.contains(p)) {
				System.out.println("EXIT TO MENU");
				gp.playSFX(5);
			}
			mouseH.resetClick();
		}
	}
	
	public void drawGameoverScreen() {
		setBG();
		
		//GAME OVER TITLE
		g2.setFont(menu_50);
		String text = "GAME OVER";
		int x = getTextX(text);
		int y = gp.screenHeight/3;
		addShadow(5, text, "#CC6600", "#FFD966", x, y);
		//g2.drawString(text, x, y);
		
		g2.setFont(menu_30);
		//SCORE
		text = scoreH.out();
		x = getTextX(text);
		y = gp.screenHeight/3+50;
		g2.setColor(Color.white);//colorfront
		g2.drawString(text, x, y);
		
		//RANK
		text = "RANK: "+gp.getCurPlayerRank();
		x = getTextX(text);
		y = gp.screenHeight/3+80;
		g2.setColor(Color.white);//colorfront
		g2.drawString(text, x, y);
	
        //DIALOG BOX
		if(!gp.usernameRequested) {
			gp.usernameRequested = true;
			//JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(gp);
	        SwingUtilities.invokeLater(() -> {
	        	UsernameDialog dialog = new UsernameDialog((JFrame) SwingUtilities.getWindowAncestor(gp));
	            dialog.setVisible(true); // Blocks until closed

	            String username = dialog.getUsername();
	            if (username != null) {
	                gp.saveUsername(username);
	                gp.setCurPlayerName(username);
	                //gp.restartGame(); // Reset the game
	            } else {
	                // Optional: Force retry or exit
	                System.exit(0); // Example: Close the game if they refuse
	            }
	            
	        });
		}
		
		//RETURN TO MENU
		g2.drawString("RETURN TO MENU", returnMenuBounds.x, returnMenuBounds.y+returnMenuBounds.height);
		//g2.draw(returnMenuBounds);
		text = "RETURN TO MENU";
		x = getTextX(text);
		//System.out.println("GET TEXT X " + x);
		y = gp.screenHeight/2+120;
		g2.setColor(Color.white);//colorfront
		//g2.drawString(text, x, y);
		
		if(mouseH.getClicked()) {
			Point p = mouseH.getPoint();
			if (returnMenuBounds.contains(p)) {
	            System.out.println("RETURN");
	            gp.playSFX(5);
	            gp.usernameRequested = false;
	            gp.restartGame();
	            gp.setPlayState();
	            gp.saveData();
	        }
			mouseH.resetClick();
		}
		
		
	}
	
	/*public void showUsernameDialog() {
        SwingUtilities.invokeLater(() -> {
            UsernameDialog dialog = new UsernameDialog(
                (JFrame) SwingUtilities.getWindowAncestor(gp)
            );
            dialog.setVisible(true);
            handleDialogResult(dialog);
        });
    }

    private void handleDialogResult(UsernameDialog dialog) {
        if(dialog.getUsername() != null) {
            gp.saveUsername(dialog.getUsername());
            gp.restartGame();
        }
    }*/
	
	public void drawWinScreen() {
		//g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha50));
		setBG();
		g2.setFont(menu_50);
		String text = "LEVEL COMPLETE";
		
		int x = getTextX(text);
		int y = gp.screenHeight/2;
		//SHADOW METHOD(pass indentation, text, colorBack, colorFront, x, y)
		addShadow(5, text, "#CC6600", "#FFD966", x, y);
		/*g2.setColor(getColor("#CC6600"));//colorback
		g2.drawString(text, x+5, y+5);
		g2.setColor(getColor("#FFD966"));//colorfront
		g2.drawString(text, x, y);*/
		
		g2.setFont(hud_20);
		text="Loading Next Level...";
		x = getTextX(text);
		y = (gp.screenHeight/2)+50;
		//g2.setColor(getColor("#CC6600"));
		//g2.drawString(text, x+2, y+2);
		g2.setColor(Color.white);
		g2.drawString(text, x, y);
	}
	
	public int getTextX(String text) {
		int length = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth(); //CREATE TEXT LENGTH FUNCTION
		//System.out.println(text+" X LENGTH: " + length);
		int x = gp.screenWidth/2 - length/2;
		return x;
	}
	
	public Color getColor(String hex) {
		Color color = Color.decode(hex);
		return color;
	}
}
