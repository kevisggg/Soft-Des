package main;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
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
	private boolean usernameRequested = false;
	
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
	
	public void draw(Graphics2D g2) {
		this.g2 = g2;
		this.originalComposite = g2.getComposite();
		gp.getGameState().draw();
	}
	
	public void setTextBG(int recScreenX, int recScreenY, int width, int height) {
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
		g2.setColor(colorBack);
		g2.drawString(text, x+indent, y+indent);
		g2.setColor(colorFront);
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
		for(int i=0; i<gp.getPlayer().getLives(); i++) {
			g2.drawImage(life, x, y, null);
			x += gp.tileSize;
		}
	}
	
	public void drawLevel() {
		setTextBG(571, 535, gp.tileSize*4, 36);
		g2.setColor(defaultBlue);
		g2.setFont(hud_20);
		g2.drawString("Level:", 576, 564);
		g2.drawString(gp.getLvl(), 696, 564);
	}
	
	public void drawScore() {
		setTextBG(379, 5, gp.tileSize*8, 36);
		g2.setColor(defaultBlue);
		g2.setFont(hud_20);
		g2.drawString("Score:", 384, 34);
		g2.drawString(scoreH.getScore(), 504, 34);
	}
	
	public void drawBPUCounter() {
		setTextBG(gp.tileSize-5, 535, gp.tileSize*3+40, 36);
		g2.setFont(hud_13);
		g2.setColor(Color.WHITE);
		g2.drawImage(bomb, gp.tileSize, 535, null);
		g2.drawString(gp.getPlayer().getBombsAvail()+"x", gp.tileSize+20, 551);
		g2.drawImage(PUrange, gp.tileSize*2+10, 536, null);
		g2.drawString(gp.getPlayer().getPURadius()+"x", gp.tileSize*2+30, 551);
		g2.drawImage(PUcap, gp.tileSize*3+20, 536, null);
		g2.drawString(gp.getPlayer().getPUcap()+"x", gp.tileSize*3+40, 551);
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
	            gp.playSFX(5);
	            gp.setPlayState();
	        }
			mouseH.resetClick();
		}
	}
	
	public void drawPauseScreen() {
		setBG();
		g2.setFont(menu_60);
		text = "PAUSED";
		x = getTextX(text);
		y = gp.screenHeight/3;
		addShadow(5, text, backColor, frontColor, x, y);
		g2.setColor(Color.white);
		g2.setFont(hud_20);
		g2.drawString("RESUME", resumeBounds.x, resumeBounds.y+resumeBounds.height);
		g2.drawString("RESTART", restartBounds.x, restartBounds.y+restartBounds.height);
		g2.drawString("EXIT", exitBounds.x, exitBounds.y+exitBounds.height);
		if(mouseH.getClicked()) {
			Point p = mouseH.getPoint();
			if (resumeBounds.contains(p)) {
	            System.out.println("RESUME");
	            gp.playSFX(5);
	            gp.playMusic();
	            gp.setPlayState();
	        } else if (restartBounds.contains(p)) {
	            System.out.println("RESTART");
	            gp.playSFX(5);
	            gp.restartGame();
	        }
			else if (exitBounds.contains(p)) {
				System.out.println("EXIT TO MENU");
				gp.playSFX(5);
				//insert exit to menu
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
	}
	
	private void returnToMenu() {
		g2.drawString("RETURN TO MENU", returnMenuBounds.x, returnMenuBounds.y+returnMenuBounds.height);
		text = "RETURN TO MENU";
		x = getTextX(text);
		y = gp.screenHeight/2+120;
		g2.setColor(Color.white);
		
		if(mouseH.getClicked()) {
			Point p = mouseH.getPoint();
			if (returnMenuBounds.contains(p)) {
	            System.out.println("RETURN");
	            gp.playSFX(5);
	            gp.restartGame();//CHANGE TO EXIT TO MENU
	            usernameRequested = false;
	            gp.saveData();
	        }
			mouseH.resetClick();
		}
	}

	public void createDialogBox() {
		if(!usernameRequested) {
			usernameRequested = true;
	        SwingUtilities.invokeLater(() -> {
	        	UsernameDialog dialog = new UsernameDialog((JFrame) SwingUtilities.getWindowAncestor(gp));
	        	do {
	        		
		            dialog.setVisible(true);

		            String username = dialog.getUsername();
		            if (username != null) {
		                gp.setCurPlayerName(username);
		            }
	        	}while(!dialog.getUserValid());
	        });
		}
	}
	
	public void drawWinScreen() {
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
		g2.setColor(Color.white);
		g2.drawString(text, x, y);
	}
	
	public int getTextX(String text) {
		int length = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth();
		x = gp.screenWidth/2 - length/2;
		return x;
	}
	
	public Color getColor(String hex) {
		Color color = Color.decode(hex);
		return color;
	}
}
