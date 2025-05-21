package core;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import bomberman.main.BMGamePanel;
import pacman.main.Pacman;
import spaceInvaders.leaderboard.SILeaderboardManager;
import spaceInvaders.leaderboard.SIPlayerScore;
import spaceInvaders.main.SpaceInvaders;
import pacman.leaderboard.*;


public class UI {
	
	private AbstractGamePanel gp;
	private MouseHandler mouseH;
	//private PlayerInterface player;
	private Graphics2D g2;
	private UIUtility util = new UIUtility();//?
	//BOMBERMAN UI COMPONENTS
	private Font menu_70, menu_50, hud_40, hud_20;
	private BufferedImage life, bomb, PUcap, PUrange, ins, BMgame, PMgame, SIgame, MBG;
	 private static final int CARD_WIDTH = 170;
	    private static final int CARD_HEIGHT = 380;
	    private static final int CARD_Y = 120;
	    private static final int SPACING = 49;
	    private static final int CARD_X1 = 80;
	    private static final int CARD_X2 = CARD_X1 + CARD_WIDTH + SPACING; // ≈ 299
	    private static final int CARD_X3 = CARD_X2 + CARD_WIDTH + SPACING;
	private final Rectangle playBMBounds = new Rectangle(126, 440, 78, 20);
	private final Rectangle playPMBounds = new Rectangle(345, 440, 78, 20);
	private final Rectangle playSIBounds = new Rectangle(564, 440, 78, 20);
	private final Rectangle resumeBounds = new Rectangle(308, 230, 152, 30);
	private final Rectangle restartBounds = new Rectangle(295, 280, 178, 30);
	private final Rectangle returnMenuBounds = new Rectangle(224, 408, 321, 30);
	private final Rectangle restartGameOverBounds = new Rectangle(295, 348, 178, 30);
	private final Rectangle selectGameBounds = new Rectangle(253, 258, 263, 30);
	private final Rectangle settingsBounds = new Rectangle(283, 318, 203, 30);
	private final Rectangle viewLeaderboardBounds = new Rectangle(189, 378, 390, 30);
	private final Rectangle quitGameBounds = new Rectangle(332, 438, 104, 30);
	private final Rectangle exitBounds = new Rectangle(333, 330, 102, 30);
	private final Rectangle okBounds = new Rectangle(356, 500, 55, 25);
	private final Rectangle backBounds = new Rectangle(10, 10, 104, 20);
	private final Rectangle BMLBounds = new Rectangle(10, 52, 96, 20);
	private final Rectangle PMLBounds = new Rectangle(106, 52, 67, 20);
	private final Rectangle SILBounds = new Rectangle(173, 52, 134, 20);
	private final Rectangle volSFXSlider = new Rectangle(548, 126, 150, 24);
	private final Rectangle volSFXPlus = new Rectangle(698, 126, 24, 24);
	private final Rectangle volSFXMinus = new Rectangle(524, 126, 24, 24);
	private final Rectangle volBGMSlider = new Rectangle(548, 174, 150, 24);
	private final Rectangle volBGMPlus = new Rectangle(698, 174, 24, 24);
	private final Rectangle volBGMMinus = new Rectangle(524, 174, 24, 24);
	private final Rectangle controlTabArea = new Rectangle(40, 300, 688, 200);
    private final Rectangle BMTabBounds = new Rectangle(40, 300, 229, 40);
    private final Rectangle PMTabBounds = new Rectangle(269, 300, 229, 40);
    private final Rectangle SITabBounds = new Rectangle(498, 300, 229, 40);
    private String selectedControlTab = "BM"; // Default to Bomberman controls
	private Rectangle hoveredButton;
	private final String sbackColor = "#CC6600";
	private final String sfrontColor = "#FFD966";
	private final String sdefaultBlue = "#3162C3";
	private final String sdefaultLBlue = "#99CCFF";
	private final String sdefaultPurple = "#b48cc8";
	private final String sdarkGray = "#1e1e1e";
	private final String slightGray = "#323232";
	private final Color backColor, frontColor, defaultBlue, defaultLBlue, defaultPurple, darkGray, lightGray;
	private String text;
	private int x,y,col1X, col2X, col3X, tableStartY, rowHeight, tableMargin;
	private boolean BMLselect, PMLselect, SILselect;


	
	
	public UI(AbstractGamePanel gp, MouseHandler mouseH) {
		this.gp = gp;
		this.mouseH = mouseH;
		col1X = BMGamePanel.tileSize;
		col2X = BMGamePanel.tileSize * 5;
	    col3X = BMGamePanel.screenWidth - BMGamePanel.tileSize * 5;
	    tableStartY = BMGamePanel.tileSize/2*3;
	    rowHeight = BMGamePanel.tileSize/2;
	    tableMargin = 17;
		menu_70 = util.createFont(70);
		menu_50 = util.createFont(50);
		hud_40 = util.createFont(40);
		hud_20 = util.createFont(20);
		backColor = util.getColor(sbackColor);
		frontColor = util.getColor(sfrontColor);
		defaultBlue = util.getColor(sdefaultBlue);
		defaultLBlue = util.getColor(sdefaultLBlue);
		defaultPurple = util.getColor(sdefaultPurple);
		darkGray = util.getColor(sdarkGray);
		lightGray = util.getColor(slightGray);
		life = util.setupImage("/BM/objects/life.png", 15);
		PUcap = util.setupImage("/BM/objects/PUcapacity.png", 15);
		PUrange = util.setupImage("/BM/objects/PUrange.png", 15);
		bomb = util.setupImage("/BM/objects/dynamite1.png", 10);
	    MBG = util.renderRegImage("/MAIN/ArcadeGamesBG.jpg");
	    ins = util.renderRegImage("/BM/objects/instructions.png");
		BMgame = util.renderRegImage("/MAIN/bm.png");
		PMgame = util.renderRegImage("/MAIN/pm.png");
		SIgame = util.renderRegImage("/MAIN/si.png");
	}

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
		
		hoverCursor();
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
		hoverCursor();
	}
	
	public void hoverInstructions(Point p) {
		hoveredButton = null;
		if(okBounds.contains(p)) {
			hoveredButton = okBounds;
		}
		hoverCursor();
	}
	
	public void hoverMainLeaderboard(Point p) {
		hoveredButton = null;
		if(backBounds.contains(p)) {
			//System.out.println("CONTAINS BACK");
			hoveredButton = backBounds;
		} else if(BMLBounds.contains(p)) {
			hoveredButton = BMLBounds;
		} else if(PMLBounds.contains(p)) {
			hoveredButton = PMLBounds;
		} else if(SILBounds.contains(p)) {
			hoveredButton = SILBounds;
		}
		
		hoverCursor();
		
	}
	
	public void hoverSettings(Point p) {
		hoveredButton = null;
		if(backBounds.contains(p)) {
			hoveredButton = backBounds;
		} else if(volBGMPlus.contains(p)) {
			hoveredButton = volBGMPlus;
		} else if(volBGMMinus.contains(p)) {
			hoveredButton = volBGMMinus;
		} else if(volSFXPlus.contains(p)) {
			hoveredButton = volSFXPlus;
		} else if(volSFXMinus.contains(p)) {
			hoveredButton = volSFXMinus;
		} 
		hoverCursor();
	}
	
	public void hoverMainMenu(Point p) {
		hoveredButton = null;
		if(selectGameBounds.contains(p)) {
			hoveredButton = selectGameBounds;
		} else if(settingsBounds.contains(p)) {
			hoveredButton = settingsBounds;
		} else if(viewLeaderboardBounds.contains(p)) {
			hoveredButton = viewLeaderboardBounds;
		} else if(quitGameBounds.contains(p)) {
			hoveredButton = quitGameBounds;
		}
		hoverCursor();
	}
	
	public void hoverSelect(Point p) {
		hoveredButton = null;
		if(backBounds.contains(p)) {
			hoveredButton = backBounds;
		} else if(playBMBounds.contains(p)) {
			hoveredButton = playBMBounds;
		} else if(playPMBounds.contains(p)) {
			hoveredButton = playPMBounds;
		} else if(playSIBounds.contains(p)) {
			hoveredButton = playSIBounds;
		}
		hoverCursor();
	}
	
	public void hoverCursor() {
		if (hoveredButton != null) {
            gp.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else {
        	gp.setCursor(Cursor.getDefaultCursor());
        }
	}
	
	public void draw(Graphics2D g2) {
		this.g2 = g2;
		util.setComposite(g2.getComposite());
		util.setDefaultStroke(g2.getStroke());
		boolean clickedThisFrame = mouseH.getClicked();
		Point clickPoint = mouseH.getPoint();
		gp.getGameState().draw(clickedThisFrame, clickPoint);
		//System.out.println(gp.getGameState());
	}
	
	public void drawHUD() {
		drawLife();
		drawLevel();
		drawScore();
		//drawBPUCounter();
	}
	
	public void drawLife() {
		BMGamePanel bm = (BMGamePanel)gp;
		util.setTextBG(g2, 5, 5, BMGamePanel.tileSize*6, 36);
		g2.setColor(defaultBlue);
		g2.setFont(hud_40);
		g2.drawString("Lives:", 10, 34);
		x = BMGamePanel.tileSize*3;
		y = 5;
		for(int i=0; i<bm.getPlayer().getLives(); i++) {
			g2.drawImage(life, x, y, null);
			x += BMGamePanel.tileSize;
		}
	}
	
	public void drawLevel() {
		util.setTextBG(g2, 571, 535, BMGamePanel.tileSize*4, 36);
		g2.setColor(defaultBlue);
		g2.setFont(hud_40);
		g2.drawString("Level:", 576, 564);
		g2.drawString(gp.getLvl(), 696, 564);
	}
	
	public void drawScore() {
		BMGamePanel bm = (BMGamePanel) gp;
		util.setTextBG(g2, 379, 5, BMGamePanel.tileSize*8, 36);
		g2.setColor(defaultBlue);
		g2.setFont(hud_40);
		g2.drawString("Score:", 384, 34);
		g2.drawString(bm.getScoreH().getScore(), 504, 34);
	}
	
	public void drawBPUCounter() {
		BMGamePanel bm = (BMGamePanel) gp;
		util.setTextBG(g2, BMGamePanel.tileSize-5, 535, BMGamePanel.tileSize*3+40, 36);
		g2.setFont(hud_20);
		g2.setColor(Color.WHITE);
		g2.drawImage(bomb, BMGamePanel.tileSize, 535, null);
		g2.drawString(bm.getPlayer().getBombsAvail()+"x", BMGamePanel.tileSize+20, 551);
		g2.drawImage(PUrange, BMGamePanel.tileSize*2+10, 536, null);
		g2.drawString(bm.getPlayer().getPURadius()+"x", BMGamePanel.tileSize*2+30, 551);
		g2.drawImage(PUcap, BMGamePanel.tileSize*3+20, 536, null);
		g2.drawString(bm.getPlayer().getPUcap()+"x", BMGamePanel.tileSize*3+40, 551);
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
		y = BMGamePanel.screenHeight/3;
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
	            gp.playMusic(0);
	            gp.setPlayState();
	        } else if (restartBounds.contains(clickPoint)) {
	            System.out.println("RESTART");
	            gp.playSFX(5);
	            gp.restartGame();
	        }
			else if (exitBounds.contains(clickPoint)) {
				System.out.println("EXIT TO MENU");
				gp.playSFX(5);
				gp.returnToMenu();
				//insert exit to menu
			}
			mouseH.resetClick();
		}
	}
	
	public void drawGameoverScreen(boolean clickedThisFrame, Point clickPoint) {
		BMGamePanel bm = (BMGamePanel) gp;
		util.setBG(g2);
		
		//GAME OVER TITLE
		g2.setFont(menu_70);
		text = "GAME OVER";
		x = util.getTextX(g2, text);
		y = BMGamePanel.screenHeight/3;
		util.addShadow(g2, 5, text, backColor, frontColor, x, y);
		g2.setFont(menu_50);
		
		//SCORE
		text = bm.getScoreH().out();
		x = util.getTextX(g2, text);
		y = BMGamePanel.screenHeight/3+50;
		g2.setColor(Color.white);
		g2.drawString(text, x, y);
		
		//RANK
		text = "RANK: "+gp.getCurPlayerRank();
		x = util.getTextX(g2, text);
		y = BMGamePanel.screenHeight/3+80;
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
		y = BMGamePanel.screenHeight/3+110;
        g2.drawString(nameLine, x, y);
       // System.out.println("NAME ENTERED: " + gp.getNameEntered());
        
	}
	
	public void drawLeaderboard(boolean clickedThisFrame, Point clickPoint) {
		//TABLE BACKGROUND
		g2.setColor(Color.DARK_GRAY);
		g2.fillRect(0, 0, BMGamePanel.screenWidth, BMGamePanel.screenHeight);
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
		y = BMGamePanel.screenHeight/2+60+restartGameOverBounds.height;
		//g2.draw(restartGameOverBounds);
		g2.setColor(hoveredButton == restartGameOverBounds ? Color.cyan : Color.white);
		g2.drawString(text, x, y);
		
		text = "VIEW LEADERBOARD";
		x = util.getTextX(g2, text);
		y = BMGamePanel.screenHeight/2+90+viewLeaderboardBounds.height;
		//g2.draw(viewLeaderboardBounds);
		g2.setColor(hoveredButton == viewLeaderboardBounds ? Color.cyan : Color.white);
		g2.drawString(text, x, y);
		
		text = "RETURN TO MENU";
		x = util.getTextX(g2, text);
		y = BMGamePanel.screenHeight/2+120+returnMenuBounds.height;
		//g2.draw(returnMenuBounds);
		g2.setColor(hoveredButton == returnMenuBounds ? Color.cyan : Color.white);
		g2.drawString(text, x, y);
		if(clickedThisFrame && (gp.getNameEntered() || gp.getCurPlayerRank().equals("-"))) {
			if (returnMenuBounds.contains(clickPoint) && !util.getToggleLeaderboard()) {
	            System.out.println("RETURN");
	            gp.playSFX(5);
	            gp.returnToMenu();
	            //gp.restartGame();//CHANGE TO EXIT TO MENU
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
			}
			mouseH.resetClick();
		}
	}
	
	public void drawWinScreen(boolean clickedThisFrame, Point clickPoint) {
		util.setBG(g2);
		g2.setFont(menu_70);
		text = "LEVEL COMPLETE";
		x = util.getTextX(g2, text);
		y = BMGamePanel.screenHeight/2;
		util.addShadow(g2, 5, text, backColor, frontColor, x, y);
		
		g2.setFont(hud_40);
		text="Loading Next Level...";
		x = util.getTextX(g2, text);
		y = (BMGamePanel.screenHeight/2)+50;
		g2.setColor(Color.white);
		g2.drawString(text, x, y);
	}
	
	public void drawMainLeaderboard(boolean clickedThisFrame, Point clickPoint) {
		//g2.setColor(Color.DARK_GRAY);
		//g2.fillRect(0, 0, BMGamePanel.screenWidth, BMGamePanel.screenHeight);
		g2.drawImage(MBG, 0, 0, MainMenuPanel.MENU_WIDTH, MainMenuPanel.MENU_HEIGHT,null);
		g2.setFont(hud_40);
		text = "< BACK";
		g2.setColor(hoveredButton == backBounds ? Color.white : defaultLBlue);
		g2.drawString(text, backBounds.x, backBounds.y+backBounds.height);
		
		text = "LEADERBOARD";
		x = util.getTextX(g2, text);
		y = 10;
		util.addShadow(g2, 2, text, backColor, frontColor, x, 30);
		y = tableStartY + rowHeight;
		if(BMLselect) {g2.setColor(defaultPurple);} else { g2.setColor(hoveredButton == BMLBounds ? defaultPurple : Color.black);}
		g2.fill(BMLBounds);
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

		MainMenuPanel menu = (MainMenuPanel) gp;
		if(!BMLselect && !PMLselect && !SILselect) {
			g2.setColor(darkGray);
			g2.fillRect(0, tableStartY, BMGamePanel.screenWidth, rowHeight*21);
			text="No Game Leaderboard Selected";
			x=util.getTextX(g2, text);
			g2.setColor(Color.white);
			g2.drawString(text, x, BMGamePanel.screenHeight/2);
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
			} else if (backBounds.contains(clickPoint)) {
	            System.out.println("BACK");
	            gp.playSFX(5);
	            BMLselect=false;
				PMLselect=false;
				SILselect=false;
	            // ADD BACK TO MAIN MENU
				menu.setMainMenuState();
	        }
			mouseH.resetClick();
		}
	}

	private void drawSILeaderboard() {
		SIPlayerScore p = null;
		SILeaderboardManager ls = LeaderboardReader.loadSIManager(SILeaderboardManager.FILE_PATH);//CHANGE TO PMS
		if(ls!=null) {
			for (int i = 0; i < Math.min(20, ls.getPlayersSize()); i++) {
	            p = ls.getPlayer(i);

	            // Alternate row colors
	            if (i % 2 == 0) g2.setColor(darkGray);
	            else g2.setColor(lightGray);
	            g2.fillRect(0, y, BMGamePanel.screenWidth, rowHeight);

	            // Text
	            g2.setColor(Color.LIGHT_GRAY);
	            g2.drawString(String.valueOf(p.getRank())+"PM", col1X, y + tableMargin);
	            g2.drawString(p.getName(), col2X, y + tableMargin);
	            g2.drawString(String.valueOf(p.getScore()), col3X, y + tableMargin);

	            y += rowHeight;
	        }
		}
	}

	private void drawPMLeaderboard() {
		PlayerScore p = null;
		LeaderboardManager ls = LeaderboardReader.loadPMManager(Pacman.DATA_FILE);//CHANGE TO PMS
		if(ls!=null) {
			for (int i = 0; i < Math.min(20, ls.getPlayersSize()); i++) {
	            p = ls.getPlayer(i);

	            // Alternate row colors
	            if (i % 2 == 0) g2.setColor(darkGray);
	            else g2.setColor(lightGray);
	            g2.fillRect(0, y, BMGamePanel.screenWidth, rowHeight);

	            // Text
	            g2.setColor(Color.LIGHT_GRAY);
	            g2.drawString(String.valueOf(p.getRank())+"PM", col1X, y + tableMargin);
	            g2.drawString(p.getName(), col2X, y + tableMargin);
	            g2.drawString(String.valueOf(p.getScore()), col3X, y + tableMargin);

	            y += rowHeight;
	        }
		}
	}

	private void drawBMLeaderboard() {
		LeaderboardPlayer p = null;
		LeaderboardSorter ls = LeaderboardReader.loadLeaderboardFromFile(BMGamePanel.DATA_FILE);
		if(ls!=null) {
		for (int i = 0; i < Math.min(20, ls.getPlayersSize()); i++) {
            p = ls.getPlayer(i);

            // Alternate row colors
            if (i % 2 == 0) g2.setColor(darkGray);
            else g2.setColor(lightGray);
            g2.fillRect(0, y, BMGamePanel.screenWidth, rowHeight);

            // Text
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawString(String.valueOf(p.getRank()), col1X, y + tableMargin);
            g2.drawString(p.getName(), col2X, y + tableMargin);
            g2.drawString(String.valueOf(p.getScore()), col3X, y + tableMargin);

            y += rowHeight;
        	}
		}
	}
	
	
	
	private void drawLeaderboardHeader() {
		g2.setColor(defaultPurple);
		g2.fillRect(0, tableStartY, BMGamePanel.screenWidth, rowHeight);
		g2.setColor(Color.black);
		g2.drawString("RANK", col1X, tableStartY + tableMargin);
        g2.drawString("NAME", col2X, tableStartY + tableMargin);
        g2.drawString("SCORE", col3X, tableStartY + tableMargin);
	}

	public void drawSettings(boolean clickedThisFrame, Point clickPoint) {
		//g2.setColor(Color.BLACK);
		//g2.fillRect(0, 0, BMGamePanel.screenWidth, BMGamePanel.screenHeight);
		g2.drawImage(MBG, 0, 0, MainMenuPanel.MENU_WIDTH, MainMenuPanel.MENU_HEIGHT,null);
		g2.setFont(hud_40);
		text = "< BACK";
		g2.setColor(hoveredButton == backBounds ? Color.white : defaultLBlue);
		g2.drawString(text, backBounds.x, backBounds.y+backBounds.height);
		
		text = "SETTINGS";
		x = util.getTextX(g2, text);
		y = 10;
		util.addShadow(g2, 2, text, backColor, frontColor, x, 30);
		
		y=150;
		g2.drawString("SFX Volume:", 70, y);
		g2.setStroke(new BasicStroke(3));
		g2.draw(volSFXSlider);
		g2.fillRect(volSFXSlider.x, volSFXSlider.y, gp.getSFXScale()*30, volSFXSlider.height);
		g2.drawString("+",volSFXPlus.x+2,volSFXPlus.y+volSFXPlus.height-2);
		g2.drawString("-",volSFXMinus.x,volSFXMinus.y+volSFXMinus.height-2);
		//g2.draw(volMinus);
		y=198;
		g2.drawString("BGM Volume:", 70, y);
		g2.draw(volBGMSlider);
		g2.fillRect(volBGMSlider.x, volBGMSlider.y, gp.getBGMScale()*30, volBGMSlider.height);
		g2.drawString("+",volBGMPlus.x+2,volBGMPlus.y+volBGMPlus.height-2);
		g2.drawString("-",volBGMMinus.x,volBGMMinus.y+volBGMMinus.height-2);
		y=246;
		drawControlsSection(g2);

	    MainMenuPanel menu = (MainMenuPanel) gp;		
		if(clickedThisFrame) {
	          if (volSFXPlus.contains(clickPoint)) {
	            System.out.println("SFXPLUS");
	            gp.playSFX(5);
	            menu.adjustSFX(1);
	            
	        } else if (volSFXMinus.contains(clickPoint)) {
	            System.out.println("SFXMINUS");
	            gp.playSFX(5);
	            menu.adjustSFX(-1);
	        } else if (volBGMPlus.contains(clickPoint)) {
	            System.out.println("BGMPLUS");
	            gp.playSFX(5);
	            menu.adjustBGM(1);
	            
	        } else if (volBGMMinus.contains(clickPoint)) {
	            System.out.println("BGMMINUS");
	            gp.playSFX(5);
	            menu.adjustBGM(-1);
	        } else if (backBounds.contains(clickPoint)) {
	            System.out.println("BACK");
	            gp.playSFX(5);
	            menu.saveConfig();
	            g2.setStroke(util.getDefaultStroke());
	            // CHANGE TO BACK TO MAIN MENU
	            menu.setMainMenuState();
	        }
			
			// Add control tab click handling
			if (BMTabBounds.contains(clickPoint)) {
                selectedControlTab = "BM";
                gp.playSFX(5);
            } else if (PMTabBounds.contains(clickPoint)) {
                selectedControlTab = "PM";
                gp.playSFX(5);
            } else if (SITabBounds.contains(clickPoint)) {
                selectedControlTab = "SI";
                gp.playSFX(5);
            }
			mouseH.resetClick();
		}
	}

	
	public void drawMainMenu(boolean clickedThisFrame, Point clickPoint) {
		MainMenuPanel menu = (MainMenuPanel) gp;
		//g2.setColor(Color.BLACK);
		//g2.fillRect(0, 0, BMGamePanel.screenWidth, BMGamePanel.screenHeight);
		g2.drawImage(MBG, 0, 0, MainMenuPanel.MENU_WIDTH, MainMenuPanel.MENU_HEIGHT,null);
		g2.setFont(menu_70.deriveFont(130f));
		text = "ARCADE";
		x = util.getTextX(g2, text);
		util.addShadow(g2, 5, text, backColor, frontColor, x, 120);
		text = "GAMES";
		x = util.getTextX(g2, text);
		util.addShadow(g2, 5, text, backColor, frontColor, x, 210);
		//g2.drawString(text, x, 100);
		g2.setFont(menu_50);
		g2.setColor(hoveredButton == selectGameBounds ? Color.cyan : Color.white);
		text = "SELECT GAME";
		x = util.getTextX(g2, text);
		g2.drawString(text, x, selectGameBounds.y+selectGameBounds.height);
		g2.setColor(hoveredButton == settingsBounds ? Color.cyan : Color.white);
		text = "SETTINGS";
		x = util.getTextX(g2, text);
		g2.drawString(text, x, settingsBounds.y+settingsBounds.height);
		g2.setColor(hoveredButton == viewLeaderboardBounds ? Color.cyan : Color.white);
		text = "VIEW LEADERBOARD";
		x = util.getTextX(g2, text);
		g2.drawString(text, x, viewLeaderboardBounds.y+viewLeaderboardBounds.height);
		g2.setColor(hoveredButton == quitGameBounds ? Color.cyan : Color.white);
		text = "QUIT";
		x = util.getTextX(g2, text);
		g2.drawString(text, x, quitGameBounds.y+quitGameBounds.height);
		//g2.draw(selectGameBounds);
		//g2.draw(settingsBounds);
		//g2.draw(viewLeaderboardBounds);
		//g2.draw(quitGameBounds);
		if(clickedThisFrame) {
			if (viewLeaderboardBounds.contains(clickPoint)) {
	            System.out.println("leaderboard");
	            gp.playSFX(5);
	            menu.setMainLeaderState();
	        } else if (settingsBounds.contains(clickPoint)) {
	            System.out.println("settings");
	            gp.playSFX(5);
	            menu.setSettingsState();
	        } else if (quitGameBounds.contains(clickPoint)) {
	            System.out.println("quit");
	            gp.playSFX(5);
	            menu.setQuitState();
	        } else if (selectGameBounds.contains(clickPoint)) {
	            System.out.println("selectGame");
	            gp.playSFX(5);
	            menu.setSelectState();
	        }
			mouseH.resetClick();
		}
	}

	public void drawGameSelect(boolean clickedThisFrame, Point clickPoint) {
		// TODO Auto-generated method stub
		MainMenuPanel menu = (MainMenuPanel) gp;
		//g2.setColor(Color.BLACK);
		//g2.fillRect(0, 0, BMGamePanel.screenWidth, BMGamePanel.screenHeight);
		g2.drawImage(MBG, 0, 0, MainMenuPanel.MENU_WIDTH, MainMenuPanel.MENU_HEIGHT,null);
		g2.setFont(hud_40);
		text = "< BACK";
		g2.setColor(hoveredButton == backBounds ? Color.white : defaultLBlue);
		g2.drawString(text, backBounds.x, backBounds.y+backBounds.height);
		text = "SELECT GAME";
		x = util.getTextX(g2, text);
		y = 10;
		util.addShadow(g2, 2, text, backColor, frontColor, x, 30);
		
		text = "BomberMan";
		g2.drawString(text, CARD_X1+((CARD_WIDTH-176)/2), CARD_Y-20);
		text = "PacMan";
		x = util.getTextX(g2, text);
		g2.drawString(text, x, CARD_Y-20);
		text = "Space";
		g2.drawString(text, CARD_X3+((CARD_WIDTH-98)/2), CARD_Y-40);
		text = "Invaders";
		g2.drawString(text, CARD_X3+((CARD_WIDTH-157)/2), CARD_Y-10);
		text = "PLAY";
		x = util.getTextX(g2, text);
		drawCard(g2, CARD_X1, "BM");
	    drawCard(g2, CARD_X2, "PM");
	    drawCard(g2, CARD_X3, "SI");
	    g2.setColor(hoveredButton == playBMBounds ? Color.cyan : Color.white);
	    g2.drawString("PLAY", playBMBounds.x, playBMBounds.y+playBMBounds.height);
	    g2.setColor(hoveredButton == playPMBounds ? Color.cyan : Color.white);
	    g2.drawString("PLAY", playPMBounds.x, playPMBounds.y+playPMBounds.height);
	    g2.setColor(hoveredButton == playSIBounds ? Color.cyan : Color.white);
	    g2.drawString("PLAY", playSIBounds.x, playSIBounds.y+playSIBounds.height);
		if(clickedThisFrame) {
			if (backBounds.contains(clickPoint)) {
	            System.out.println("BACK");
	            gp.playSFX(5);
				menu.setMainMenuState();
	        } else if (playBMBounds.contains(clickPoint)) {
	            System.out.println("BACK");
	            gp.playSFX(5);
				menu.playBM();
	        } else if (playPMBounds.contains(clickPoint)) {
	            System.out.println("BACK");
	            gp.playSFX(5);
				menu.playPM();
	        } else if (playSIBounds.contains(clickPoint)) {
	            System.out.println("BACK");
	            gp.playSFX(5);
				menu.playSI();
	        }
			mouseH.resetClick();
		}
	}
	
	private void drawCard(Graphics2D g2, int x, String game) {
	 	g2.setColor(Color.BLACK);
        g2.fillRoundRect(x, CARD_Y, CARD_WIDTH, CARD_HEIGHT, 30, 30);   
        g2.setColor(Color.WHITE);
        g2.drawRoundRect(x, CARD_Y, CARD_WIDTH, CARD_HEIGHT, 30, 30);
        
        switch(game) {
        case "BM": g2.drawImage(BMgame, x+5, CARD_Y + 30, null);break;
        case "PM": g2.drawImage(PMgame, x+5, CARD_Y + 30, null);break;
        case "SI": g2.drawImage(SIgame, x+5, CARD_Y + 30, null);break;
        }
		
    }

	// Add method to draw controls section
	private void drawControlsSection(Graphics2D g2) {
	// Add "Controls" title
	g2.setFont(menu_50.deriveFont(24f));
	g2.setColor(Color.WHITE);
	String controlsText = "Controls";
	int controlsX = 40 + (688 - g2.getFontMetrics().stringWidth(controlsText)) / 2;
	g2.drawString(controlsText, controlsX, 285);

	// Draw tabs
	g2.setFont(menu_50.deriveFont(20f));
	drawControlTab(g2, "BM", BMTabBounds, "Bomberman");
	drawControlTab(g2, "PM", PMTabBounds, "Pacman");
	drawControlTab(g2, "SI", SITabBounds, "Space Invaders");

	// Draw controls content area with full width
	g2.setColor(darkGray);
	g2.fillRect(40, 340, 688, 160);

	// Draw controls with wider spread
	g2.setFont(menu_50.deriveFont(16f));
	g2.setColor(Color.WHITE);
	
	switch(selectedControlTab) {
		case "BM":
			drawBombermanControls(g2);
			break;
		case "PM":
			drawPacmanControls(g2);
			break;
		case "SI":
			drawSpaceInvadersControls(g2);
			break;
	}
}

private void drawControlTab(Graphics2D g2, String id, Rectangle bounds, String label) {
    if (selectedControlTab.equals(id)) {
        g2.setColor(darkGray);
    } else {
        g2.setColor(lightGray);
    }
    g2.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
    
    g2.setColor(selectedControlTab.equals(id) ? Color.WHITE : Color.LIGHT_GRAY);
    int textX = bounds.x + (bounds.width - g2.getFontMetrics().stringWidth(label)) / 2;
    g2.drawString(label, textX, bounds.y + 25);
}

private void drawBombermanControls(Graphics2D g2) {
	int y = 360;
	int leftCol = 50;
	int rightCol = 600;
	
	g2.drawString("MOVE UP", leftCol, y);
	g2.drawString("W, UP", rightCol, y);
	y += 20;
	g2.drawString("MOVE DOWN", leftCol, y);
	g2.drawString("S, DOWN", rightCol, y);
	y += 20;
	g2.drawString("MOVE LEFT", leftCol, y);
	g2.drawString("A, LEFT", rightCol, y);
	y += 20;
	g2.drawString("MOVE RIGHT", leftCol, y);
	g2.drawString("D, RIGHT", rightCol, y);
	y += 20;
	g2.drawString("PLACE BOMB", leftCol, y);
	g2.drawString("SPACE", rightCol, y);
	y += 20;
	g2.drawString("PAUSE", leftCol, y);
	g2.drawString("P, ESC", rightCol, y);
}

private void drawPacmanControls(Graphics2D g2) {
	int y = 360;
	int leftCol = 50;
	int rightCol = 550;
	
	g2.drawString("MOVE UP", leftCol, y);
	g2.drawString("W, UP ARROW", rightCol, y);
	y += 20;
	g2.drawString("MOVE DOWN", leftCol, y);
	g2.drawString("S, DOWN ARROW", rightCol, y);
	y += 20;
	g2.drawString("MOVE LEFT", leftCol, y);
	g2.drawString("A, LEFT ARROW", rightCol, y);
	y += 20;
	g2.drawString("MOVE RIGHT", leftCol, y);
	g2.drawString("D, RIGHT ARROW", rightCol, y);
	y += 20;
	g2.drawString("PAUSE", leftCol, y);
	g2.drawString("P, ESC", rightCol, y);
	y += 20;
}

private void drawSpaceInvadersControls(Graphics2D g2) {
	int y = 360;
	int leftCol = 50;
	int rightCol = 570;
	
	g2.drawString("MOVE LEFT", leftCol, y);
	g2.drawString("LEFT ARROW", rightCol, y);
	y += 20;
	g2.drawString("MOVE RIGHT", leftCol, y);
	g2.drawString("RIGHT ARROW", rightCol, y);
	y += 20;
	g2.drawString("SHOOT", leftCol, y);
	g2.drawString("SPACE", rightCol, y);
	y += 20;
	g2.drawString("PAUSE", leftCol, y);
	g2.drawString("P, ESC", rightCol, y);
	y += 20;
	g2.drawString("MENU", leftCol, y);
	g2.drawString("M", rightCol, y);
	y += 20;
	g2.drawString("RESTART", leftCol, y);
	g2.drawString("R", rightCol, y);
}


	
}
