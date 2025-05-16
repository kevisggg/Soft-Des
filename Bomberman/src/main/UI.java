package main;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class UI {
	
	private GamePanel gp;
	private MouseHandler mouseH;
	private ScoreHandler scoreH;
	private Graphics2D g2;
	private UIUtility util = new UIUtility();//?
	private Font menu_70, menu_50, hud_40, hud_20;
	private BufferedImage life, bomb, PUcap, PUrange, ins;
	private final Rectangle resumeBounds = new Rectangle(308, 230, 152, 30);
	private final Rectangle restartBounds = new Rectangle(295, 280, 178, 30);
	private final Rectangle returnMenuBounds = new Rectangle(224, 408, 321, 30);
	private final Rectangle restartGameOverBounds = new Rectangle(295, 348, 178, 30);
	private final Rectangle viewLeaderboardBounds = new Rectangle(189, 378, 390, 30);
	private final Rectangle exitBounds = new Rectangle(333, 330, 102, 30);
	private final Rectangle okBounds = new Rectangle(356, 500, 55, 25);
	private final Rectangle backBounds = new Rectangle(10, 10, 104, 20);
	private final Rectangle BMLBounds = new Rectangle(10, 52, 96, 20);
	private final Rectangle PMLBounds = new Rectangle(106, 52, 67, 20);
	private final Rectangle SILBounds = new Rectangle(173, 52, 134, 20);
	private Rectangle hoveredButton;
	private final String sbackColor = "#CC6600";
	private final String sfrontColor = "#FFD966";
	private final String sdefaultBlue = "#3162C3";
	private final String sdefaultPurple = "#b48cc8";
	private final String sdarkGray = "#1e1e1e";
	private final String slightGray = "#323232";
	
	private final Color backColor, frontColor, defaultBlue, defaultPurple, darkGray, lightGray;
	private String text;
	
	private int x,y;
	private int col1X, col2X, col3X, tableStartY, rowHeight, tableMargin;
	private boolean BMLselect, PMLselect, SILselect;
	
	
	public UI(GamePanel gp, MouseHandler mouseH, ScoreHandler scoreH) {
		this.gp = gp;
		this.mouseH = mouseH;
		this.scoreH = scoreH;
		menu_70 = util.createFont(70);
		menu_50 = util.createFont(50);
		hud_40 = util.createFont(40);
		hud_20 = util.createFont(20);
		backColor = util.getColor(sbackColor);
		frontColor = util.getColor(sfrontColor);
		defaultBlue = util.getColor(sdefaultBlue);
		defaultPurple = util.getColor(sdefaultPurple);
		darkGray = util.getColor(sdarkGray);
		lightGray = util.getColor(slightGray);
		life = util.setupImage("/objects/life.png", 15);
		PUcap = util.setupImage("/objects/PUcapacity.png", 15);
		PUrange = util.setupImage("/objects/PUrange.png", 15);
		bomb = util.setupImage("/objects/dynamite1.png", 10);
		col1X = GamePanel.tileSize;
		col2X = GamePanel.tileSize * 5;
	    col3X = GamePanel.screenWidth - GamePanel.tileSize * 5;
	    tableStartY = GamePanel.tileSize/2*3;
	    rowHeight = GamePanel.tileSize/2;
	    tableMargin = 17;
		try {
			ins = ImageIO.read(getClass().getResourceAsStream("/objects/instructions.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*public BufferedImage setupImage(String path, int sizeDiff) {
		image = null;
		s = new ImageScaler();
		try {
			image = ImageIO.read(getClass().getResourceAsStream(path));
			image = s.scale(GamePanel.tileSize-sizeDiff, GamePanel.tileSize-sizeDiff, image);
		}catch(IOException e) {
			e.printStackTrace();
		}
		return image;
	}*/
	
	/*public void resetToggleLeaderboard() {
		toggleLeaderboard = false;
		back = false;
	}*/
	public UIUtility getUIUtil() {
		return util;
	}
	public void hoverPause(Point p) {
		hoveredButton = null;
		if(resumeBounds.contains(p)) {
			hoveredButton = resumeBounds;
		} else if(restartBounds.contains(p)) {
			hoveredButton = restartBounds;
		} else if(exitBounds.contains(p)) {
			hoveredButton = exitBounds;
		}
		
		if (hoveredButton != null) {
            gp.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else {
        	gp.setCursor(Cursor.getDefaultCursor());
        }
	}
	
	public void hoverGameOver(Point p) {
		hoveredButton = null;
		
		if(!util.getToggleLeaderboard()) {
			if(restartGameOverBounds.contains(p)) {
				hoveredButton = restartGameOverBounds;
			} else if(viewLeaderboardBounds.contains(p)) {
				hoveredButton = viewLeaderboardBounds;
			} else if(returnMenuBounds.contains(p)) {
				hoveredButton = returnMenuBounds;
			}
		} else {
			if(backBounds.contains(p)) {
				hoveredButton = backBounds;
			}
		}
		if (hoveredButton != null) {
            gp.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else {
        	gp.setCursor(Cursor.getDefaultCursor());
        }
	}
	
	public void hoverInstructions(Point p) {
		hoveredButton = null;
		if(okBounds.contains(p)) {
			hoveredButton = okBounds;
		}
		if (hoveredButton != null) {
            gp.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else {
        	gp.setCursor(Cursor.getDefaultCursor());
        }
	}
	
	public void hoverMainLeaderboard(Point p) {
		hoveredButton = null;
		if(backBounds.contains(p)) {
			hoveredButton = backBounds;
		} else if(BMLBounds.contains(p)) {
			hoveredButton = BMLBounds;
		} else if(PMLBounds.contains(p)) {
			hoveredButton = PMLBounds;
		} else if(SILBounds.contains(p)) {
			hoveredButton = SILBounds;
		}
		
		
		if (hoveredButton != null) {
            gp.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else {
        	gp.setCursor(Cursor.getDefaultCursor());
        }
	}
	
	public void draw(Graphics2D g2) {
		this.g2 = g2;
		util.setComposite(g2.getComposite());
		boolean clickedThisFrame = mouseH.getClicked();
		Point clickPoint = mouseH.getPoint();
		gp.getGameState().draw(clickedThisFrame, clickPoint);
		//System.out.println(gp.getGameState());
	}
	
	public void drawHUD() {
		drawLife();
		drawLevel();
		drawScore();
		drawBPUCounter();
	}
	
	public void drawLife() {
		util.setTextBG(g2, 5, 5, GamePanel.tileSize*6, 36);
		g2.setColor(defaultBlue);
		g2.setFont(hud_40);
		g2.drawString("Lives:", 10, 34);
		x = GamePanel.tileSize*3;
		y = 5;
		for(int i=0; i<gp.getPlayer().getLives(); i++) {
			g2.drawImage(life, x, y, null);
			x += GamePanel.tileSize;
		}
	}
	
	public void drawLevel() {
		util.setTextBG(g2, 571, 535, GamePanel.tileSize*4, 36);
		g2.setColor(defaultBlue);
		g2.setFont(hud_40);
		g2.drawString("Level:", 576, 564);
		g2.drawString(gp.getLvl(), 696, 564);
	}
	
	public void drawScore() {
		util.setTextBG(g2, 379, 5, GamePanel.tileSize*8, 36);
		g2.setColor(defaultBlue);
		g2.setFont(hud_40);
		g2.drawString("Score:", 384, 34);
		g2.drawString(scoreH.getScore(), 504, 34);
	}
	
	public void drawBPUCounter() {
		util.setTextBG(g2, GamePanel.tileSize-5, 535, GamePanel.tileSize*3+40, 36);
		g2.setFont(hud_20);
		g2.setColor(Color.WHITE);
		g2.drawImage(bomb, GamePanel.tileSize, 535, null);
		g2.drawString(gp.getPlayer().getBombsAvail()+"x", GamePanel.tileSize+20, 551);
		g2.drawImage(PUrange, GamePanel.tileSize*2+10, 536, null);
		g2.drawString(gp.getPlayer().getPURadius()+"x", GamePanel.tileSize*2+30, 551);
		g2.drawImage(PUcap, GamePanel.tileSize*3+20, 536, null);
		g2.drawString(gp.getPlayer().getPUcap()+"x", GamePanel.tileSize*3+40, 551);
	}
	
	public void drawInstruction(boolean clickedThisFrame, Point clickPoint) {
		util.setBG(g2);
		g2.drawImage(ins, 0, 0, null);
		g2.setColor(Color.white);
		g2.setFont(menu_50);
		text = "OK";
		x = util.getTextX(g2, text);
		y = okBounds.y;
		g2.setColor(hoveredButton == okBounds ? Color.cyan : Color.white);
		g2.drawString(text, x, y+okBounds.height);
		//g2.draw(okBounds);
		if(clickedThisFrame) {
			if (okBounds.contains(clickPoint)) {
	            gp.playSFX(5);
	            gp.setPlayState();
	        }
			mouseH.resetClick();
		}
	}
	
	public void drawPauseScreen(boolean clickedThisFrame, Point clickPoint) {
		util.setBG(g2);
		g2.setFont(menu_70);
		text = "PAUSED";
		x = util.getTextX(g2, text);
		y = GamePanel.screenHeight/3;
		util.addShadow(g2, 5, text, backColor, frontColor, x, y);
		
		g2.setFont(menu_50);
		text = "RESUME";
		x = util.getTextX(g2, text);
		g2.setColor(hoveredButton == resumeBounds ? Color.cyan : Color.white);
		g2.drawString("RESUME", resumeBounds.x, resumeBounds.y+resumeBounds.height);
		text = "RESTART";
		x = util.getTextX(g2, text);
		g2.setColor(hoveredButton == restartBounds ? Color.cyan : Color.white);
		g2.drawString("RESTART", restartBounds.x, restartBounds.y+restartBounds.height);
		text = "EXIT";
		x = util.getTextX(g2, text);
		g2.setColor(hoveredButton == exitBounds ? Color.cyan : Color.white);
		g2.drawString("EXIT", exitBounds.x, exitBounds.y+exitBounds.height);
		//g2.draw(resumeBounds);
		//g2.draw(restartBounds);
		//g2.draw(exitBounds);
		if(mouseH.getClicked()) {
			if (resumeBounds.contains(clickPoint)) {
	            System.out.println("RESUME");
	            gp.playSFX(5);
	            gp.playMusic();
	            gp.setPlayState();
	        } else if (restartBounds.contains(clickPoint)) {
	            System.out.println("RESTART");
	            gp.playSFX(5);
	            gp.restartGame();
	        }
			else if (exitBounds.contains(clickPoint)) {
				System.out.println("EXIT TO MENU");
				gp.playSFX(5);
				//insert exit to menu
			}
			mouseH.resetClick();
		}
	}
	
	public void drawGameoverScreen(boolean clickedThisFrame, Point clickPoint) {
		
		util.setBG(g2);
		
		//GAME OVER TITLE
		g2.setFont(menu_70);
		text = "GAME OVER";
		x = util.getTextX(g2, text);
		y = GamePanel.screenHeight/3;
		util.addShadow(g2, 5, text, backColor, frontColor, x, y);
		g2.setFont(menu_50);
		
		//SCORE
		text = scoreH.out();
		x = util.getTextX(g2, text);
		y = GamePanel.screenHeight/3+50;
		g2.setColor(Color.white);
		g2.drawString(text, x, y);
		
		//RANK
		text = "RANK: "+gp.getCurPlayerRank();
		x = util.getTextX(g2, text);
		y = GamePanel.screenHeight/3+80;
		g2.setColor(Color.white);
		g2.drawString(text, x, y);
		
		gameOverButtons(clickedThisFrame, clickPoint);
		
        //DIALOG BOX
		if(!gp.getCurPlayerRank().equals("-")) {//doesnt require username if player not ranked
			drawEnterName();
			if(gp.getNameEntered() && !util.getBack()) {
		        //System.out.println("SAVING TOGGLE");
		       	 //gp.saveData();
		       	 util.setToggleLeaderboard(true);
		        }
		}
		if(util.getToggleLeaderboard()) {
			drawLeaderboard(clickedThisFrame, clickPoint);
		}
	}
	
	public void drawEnterName() {
		 g2.setFont(menu_50);
        g2.setColor(Color.CYAN);
        g2.setColor(!gp.getNameEntered() ? Color.cyan : Color.white);
        String nameLine = "ENTER NAME: " + gp.getNameInput() + (gp.getNameEntered() ? "" : "_");
        x = util.getTextX(g2, nameLine);
		 y = GamePanel.screenHeight/3+110;
        g2.drawString(nameLine, x, y);
       // System.out.println("NAME ENTERED: " + gp.getNameEntered());
        
	}
	
	public void drawLeaderboard(boolean clickedThisFrame, Point clickPoint) {
		//TABLE BACKGROUND
		g2.setColor(Color.DARK_GRAY);
		g2.fillRect(0, 0, GamePanel.screenWidth, GamePanel.screenHeight);
		//BACK and LEADERBOARD text
		//g2.setColor(Color.white);
		//g2.draw(backBounds);
		
		// DRAW BACK BUTTON AND LEADERBOARD HEADER
		g2.setFont(hud_40);
		text = "< BACK";
		g2.setColor(hoveredButton == backBounds ? Color.cyan : Color.white);
		g2.drawString(text, backBounds.x, backBounds.y+backBounds.height);	
		text = "LEADERBOARD";
		x = util.getTextX(g2, text);
		y = 10;
		util.addShadow(g2, 2, text, backColor, frontColor, x, 30);
		y = tableStartY + rowHeight;
		g2.setFont(hud_20);
		g2.setFont(g2.getFont().deriveFont(Font.PLAIN));
		drawLeaderboardHeader();
        drawBMLeaderboard();
		//System.out.println("CLICK: " + mouseH.getClicked());
		if(clickedThisFrame) {
			System.out.println("POINT");
			//Point point = mouseH.getPoint();
			if (backBounds.contains(clickPoint)) {
	            System.out.println("BACK");
	            gp.playSFX(5);
	            util.setToggleLeaderboard(false);//CHANGE TO EXIT TO MENU
	            util.setBack(true);
	            //gp.saveData();
	        }
			mouseH.resetClick();
		}
		//System.out.println("CLICK: " + mouseH.getClicked());
	}
	
	private void gameOverButtons(boolean clickedThisFrame, Point clickPoint) {
		g2.setFont(menu_50);
		text = "RESTART";
		x = util.getTextX(g2, text);
		y = GamePanel.screenHeight/2+60+restartGameOverBounds.height;
		//g2.draw(restartGameOverBounds);
		g2.setColor(hoveredButton == restartGameOverBounds ? Color.cyan : Color.white);
		g2.drawString(text, x, y);
		
		text = "VIEW LEADERBOARD";
		x = util.getTextX(g2, text);
		y = GamePanel.screenHeight/2+90+viewLeaderboardBounds.height;
		//g2.draw(viewLeaderboardBounds);
		g2.setColor(hoveredButton == viewLeaderboardBounds ? Color.cyan : Color.white);
		g2.drawString(text, x, y);
		
		text = "RETURN TO MENU";
		x = util.getTextX(g2, text);
		y = GamePanel.screenHeight/2+120+returnMenuBounds.height;
		//g2.draw(returnMenuBounds);
		g2.setColor(hoveredButton == returnMenuBounds ? Color.cyan : Color.white);
		g2.drawString(text, x, y);
		if(clickedThisFrame && (gp.getNameEntered() || gp.getCurPlayerRank().equals("-"))) {
			if (returnMenuBounds.contains(clickPoint) && !util.getToggleLeaderboard()) {
	            System.out.println("RETURN");
	            gp.playSFX(5);
	            gp.restartGame();//CHANGE TO EXIT TO MENU
	            //gp.saveData();
	        }
			else if (viewLeaderboardBounds.contains(clickPoint) && !util.getToggleLeaderboard()) {
	            System.out.println("leaderboard");
	            gp.playSFX(5);
	            util.setToggleLeaderboard(true);
	        }
			else if (restartGameOverBounds.contains(clickPoint) && !util.getToggleLeaderboard()) {
				System.out.println("RESTART GO");
				gp.playSFX(5);
				gp.restartGame();
				//insert exit to menu
			}
			mouseH.resetClick();
		}
	}
	
	public void drawWinScreen(boolean clickedThisFrame, Point clickPoint) {
		util.setBG(g2);
		g2.setFont(menu_50);
		text = "LEVEL COMPLETE";
		x = util.getTextX(g2, text);
		y = GamePanel.screenHeight/2;
		util.addShadow(g2, 5, text, backColor, frontColor, x, y);
		
		g2.setFont(hud_40);
		text="Loading Next Level...";
		x = util.getTextX(g2, text);
		y = (GamePanel.screenHeight/2)+50;
		g2.setColor(Color.white);
		g2.drawString(text, x, y);
	}
	
	public void drawMainLeaderboard(boolean clickedThisFrame, Point clickPoint) {
		g2.setColor(Color.DARK_GRAY);
		g2.fillRect(0, 0, GamePanel.screenWidth, GamePanel.screenHeight);
		g2.setFont(hud_40);
		text = "< BACK";
		g2.setColor(hoveredButton == backBounds ? Color.cyan : Color.white);
		g2.drawString(text, backBounds.x, backBounds.y+backBounds.height);
		
		text = "LEADERBOARD";
		x = util.getTextX(g2, text);
		y = 10;
		util.addShadow(g2, 2, text, backColor, frontColor, x, 30);
		y = tableStartY + rowHeight;
		if(BMLselect) {g2.setColor(defaultPurple);} else { g2.setColor(hoveredButton == BMLBounds ? defaultPurple : Color.black);}
		g2.fillRect(BMLBounds.x, BMLBounds.y, BMLBounds.width, BMLBounds.height);
		if(PMLselect) {g2.setColor(defaultPurple);} else { g2.setColor(hoveredButton == PMLBounds ? defaultPurple : Color.black);}
		g2.fillRect(PMLBounds.x, PMLBounds.y, PMLBounds.width, PMLBounds.height);
		if(SILselect) {g2.setColor(defaultPurple);} else { g2.setColor(hoveredButton == SILBounds ? defaultPurple : Color.black);}
		g2.fillRect(SILBounds.x, SILBounds.y, SILBounds.width, SILBounds.height);
		g2.setColor(Color.white);
		g2.draw(BMLBounds);
		g2.draw(PMLBounds);
		g2.draw(SILBounds);
		g2.setFont(hud_20);
		g2.setFont(g2.getFont().deriveFont(Font.PLAIN));
		
		text = "BomberMan";
		x = util.getTextX(g2, text);
		g2.drawString(text, BMLBounds.x+5, BMLBounds.y+BMLBounds.height-5);
		text = "PacMan";
		g2.drawString(text, PMLBounds.x+5, PMLBounds.y+PMLBounds.height-5);
		x = util.getTextX(g2, text);
		text = "SpaceInvaders";
		g2.drawString(text, SILBounds.x+5, SILBounds.y+SILBounds.height-5);
		x = util.getTextX(g2, text);
		if(!BMLselect && !PMLselect && !SILselect) {
			g2.setColor(darkGray);
			g2.fillRect(0, tableStartY, GamePanel.screenWidth, rowHeight*21);
			text="No Game Leaderboard Selected";
			x=util.getTextX(g2, text);
			g2.setColor(Color.white);
			g2.drawString(text, x, GamePanel.screenHeight/2);
		}
		else {
			drawLeaderboardHeader();
			if (BMLselect) drawBMLeaderboard();
	        if (PMLselect) drawPMLeaderboard();
	        if (SILselect) drawSILeaderboard();
		}

		if(clickedThisFrame) {
			if (BMLBounds.contains(clickPoint)) {
	            System.out.println("RETURN");
	            gp.playSFX(5);
	            SILselect=false;
	            PMLselect=false;
	            BMLselect=true;
	        }
			else if (PMLBounds.contains(clickPoint)) {
	            System.out.println("leaderboard");
	            gp.playSFX(5);
	            SILselect=false;
	            BMLselect=false;
	            PMLselect=true;
	        }
			else if (SILBounds.contains(clickPoint)) {
				System.out.println("RESTART GO");
				gp.playSFX(5);
	            BMLselect=false;
				PMLselect=false;
				SILselect=true;
			}
			if (backBounds.contains(clickPoint)) {
	            System.out.println("BACK");
	            gp.playSFX(5);
	            BMLselect=false;
				PMLselect=false;
				SILselect=false;
	            // ADD BACK TO MAIN MENU
	        }
			mouseH.resetClick();
		}
	}

	private void drawSILeaderboard() {
		// TODO Auto-generated method stub
		BMPlayerLeaderboard p = null;
		for (int i = 0; i < Math.min(20, GamePanel.bml.getPlayersSize()); i++) {
            p = GamePanel.bml.getPlayer(i);

            // Alternate row colors
            if (i % 2 == 0) g2.setColor(darkGray);
            else g2.setColor(lightGray);
            g2.fillRect(0, y, GamePanel.screenWidth, rowHeight);

            // Text
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawString(String.valueOf(p.getRank())+"SI", col1X, y + tableMargin);
            g2.drawString(p.getName(), col2X, y + tableMargin);
            g2.drawString(String.valueOf(p.getScore()), col3X, y + tableMargin);

            y += rowHeight;
        }
	}

	private void drawPMLeaderboard() {
		// TODO Auto-generated method stub
		BMPlayerLeaderboard p = null;
        
		for (int i = 0; i < Math.min(20, GamePanel.bml.getPlayersSize()); i++) {
            p = GamePanel.bml.getPlayer(i);

            // Alternate row colors
            if (i % 2 == 0) g2.setColor(darkGray);
            else g2.setColor(lightGray);
            g2.fillRect(0, y, GamePanel.screenWidth, rowHeight);

            // Text
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawString(String.valueOf(p.getRank())+"PM", col1X, y + tableMargin);
            g2.drawString(p.getName(), col2X, y + tableMargin);
            g2.drawString(String.valueOf(p.getScore()), col3X, y + tableMargin);

            y += rowHeight;
        }
	}

	private void drawBMLeaderboard() {
		// TODO Auto-generated method stub
		BMPlayerLeaderboard p = null;
        
		for (int i = 0; i < Math.min(20, GamePanel.bml.getPlayersSize()); i++) {
            p = GamePanel.bml.getPlayer(i);

            // Alternate row colors
            if (i % 2 == 0) g2.setColor(darkGray);
            else g2.setColor(lightGray);
            g2.fillRect(0, y, GamePanel.screenWidth, rowHeight);

            // Text
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawString(String.valueOf(p.getRank()), col1X, y + tableMargin);
            g2.drawString(p.getName(), col2X, y + tableMargin);
            g2.drawString(String.valueOf(p.getScore()), col3X, y + tableMargin);

            y += rowHeight;
        }
		//System.out.println("drawingBML");
	}
	
	private void drawLeaderboardHeader() {
		g2.setColor(defaultPurple);
		g2.fillRect(0, tableStartY, GamePanel.screenWidth, rowHeight);
		g2.setColor(Color.black);
		g2.drawString("RANK", col1X, tableStartY + tableMargin);
        g2.drawString("NAME", col2X, tableStartY + tableMargin);
        g2.drawString("SCORE", col3X, tableStartY + tableMargin);
	}
	
}
