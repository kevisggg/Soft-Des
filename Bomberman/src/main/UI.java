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
	
	private GamePanel gp;
	private MouseHandler mouseH;
	private ScoreHandler scoreH;
	private Graphics2D g2;
	private final float alpha50 = 0.5f;
	private final float alpha70 = 0.7f;
	private Font menu_60, menu_50, menu_30, hud_20, hud_13;
	private BufferedImage life, bomb, PUcap, PUrange, image, ins;
	private ImageScaler s;
	private final Rectangle resumeBounds = new Rectangle(324, 200, 120, 30);
	private final Rectangle restartBounds = new Rectangle(314, 250, 140, 30);
	private final Rectangle returnMenuBounds = new Rectangle(166, 408, 436, 30);
	private final Rectangle exitBounds = new Rectangle(344, 300, 80, 30);
	private final Rectangle okBounds = new Rectangle(353, 500, 62, 30);
	private final String sbackColor = "#CC6600";
	private final String sfrontColor = "#FFD966";
	private final String sdefaultBlue = "#3162C3";
	public static final String gameFont = "Press Start 2P Regular";
	private final Color backColor, frontColor, defaultBlue;
	private String text;
	private Composite originalComposite;
	private int x,y;
	
	public UI(GamePanel gp, MouseHandler mouseH, ScoreHandler scoreH) {
		this.gp = gp;
		this.mouseH = mouseH;
		this.scoreH = scoreH;
		menu_60 = createFont(60);
		menu_50 = createFont(50);
		menu_30 = createFont(30);
		hud_20 = createFont(20);
		hud_13 = createFont(13);
		backColor = getColor(sbackColor);
		frontColor = getColor(sfrontColor);
		defaultBlue = getColor(sdefaultBlue);
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
	
	public Font createFont(int size) {
		Font font = new Font (gameFont, Font.BOLD, size);
		return font;
	}
	
	/*public static void setColor(Graphics2D g2, String hexColor) {
        Color color = Color.decode(hexColor);
        g2.setColor(color);
    }*/
	
	public void draw(Graphics2D g2) {
		this.g2 = g2;
		this.originalComposite = g2.getComposite();
		//g2.setFont(menu_60);
		//g2.setColor(Color.white);
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
	
	public void addShadow(int indent, String text, Color colorBack, Color colorFront, int x, int y) {
		g2.setColor(colorBack);//colorback
		g2.drawString(text, x+indent, y+indent);
		g2.setColor(colorFront);//colorfront
		g2.drawString(text, x, y);
	}
	
	public void drawHUD() {
		drawLife();
		drawLevel();
		drawScore();
		drawBPUCounter();
	}
	
	public void drawLife() {
		setTextBG(5, 5, gp.tileSize*6, 36);
		g2.setColor(defaultBlue);
		g2.setFont(hud_20);
		g2.drawString("Lives:", 10, 34);
		x = gp.tileSize*3;
		y = 5;
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
		g2.setColor(defaultBlue);
		g2.setFont(hud_20);
		g2.drawString("Level:", 576, 564);
		g2.drawString(gp.getLvl(), 696, 564);
	}
	
	public void drawScore() {//768
		setTextBG(379, 5, gp.tileSize*8, 36);
		g2.setColor(defaultBlue);
		g2.setFont(hud_20);
		g2.drawString("Score:", 384, 34);
		//int h = getTextX("Score:");
		g2.drawString(scoreH.getScore(), 504, 34);
	}
	
	public void drawBPUCounter() {
		setTextBG(gp.tileSize-5, 535, gp.tileSize*3+40, 36);
		g2.setFont(hud_13);
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
		text = "OK";
		x = getTextX(text);
		y = 500;
		addShadow(3, text, backColor, frontColor, x, y+okBounds.height);
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
		text = "PAUSED";
		
		x = getTextX(text);
		y = gp.screenHeight/3;
		
		addShadow(5, text, backColor, frontColor, x, y);
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
		text = "GAME OVER";
		x = getTextX(text);
		y = gp.screenHeight/3;
		addShadow(5, text, backColor, frontColor, x, y);
		
		g2.setFont(menu_30);
		//SCORE
		text = scoreH.out();
		x = getTextX(text);
		y = gp.screenHeight/3+50;
		g2.setColor(Color.white);
		g2.drawString(text, x, y);
		
		//RANK
		text = "RANK: "+gp.getCurPlayerRank();
		x = getTextX(text);
		y = gp.screenHeight/3+80;
		g2.setColor(Color.white);
		g2.drawString(text, x, y);
	
        //DIALOG BOX
		if(!gp.getCurPlayerRank().equals("-")) {//doesnt require username if player not ranked
			createDialogBox();
		}
		
		returnToMenu();
		//RETURN TO MENU
		
	}
	
	private void returnToMenu() {
		// TODO Auto-generated method stub
		g2.drawString("RETURN TO MENU", returnMenuBounds.x, returnMenuBounds.y+returnMenuBounds.height);
		//g2.draw(returnMenuBounds);
		text = "RETURN TO MENU";
		x = getTextX(text);
		//System.out.println("GET TEXT X " + x);
		y = gp.screenHeight/2+120;
		g2.setColor(Color.white);
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

	public void createDialogBox() {
		if(!gp.usernameRequested) {
			gp.usernameRequested = true;
	        SwingUtilities.invokeLater(() -> {
	        	UsernameDialog dialog = new UsernameDialog((JFrame) SwingUtilities.getWindowAncestor(gp));
	        	do {
	        		
		            dialog.setVisible(true);

		            String username = dialog.getUsername();
		            if (username != null) {
		                //gp.saveUsername(username);
		                gp.setCurPlayerName(username);
		                //gp.restartGame(); // Reset the game
		            }
	        	}while(!dialog.getUserValid());
	        	
	            
	            //gp.restartGame(); // Reset the game
	        });
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
		text = "LEVEL COMPLETE";
		x = getTextX(text);
		y = gp.screenHeight/2;
		addShadow(5, text, backColor, frontColor, x, y);
		
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
		x = gp.screenWidth/2 - length/2;
		return x;
	}
	
	public Color getColor(String hex) {
		Color color = Color.decode(hex);
		return color;
	}
}
