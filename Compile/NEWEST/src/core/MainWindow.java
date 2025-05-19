package core;

import javax.swing.*;
import bomberman.main.BMGamePanel;
//import pacman.main.PacmanGamePanel;
//import spaceInvaders.game.Settings;
//import spaceInvaders.main.SIGamePanel;
import spaceInvaders.main.SpaceInvaders;

public class MainWindow extends JFrame {
	
	public static void main(String[] args) {
		new MainWindow();
		//System.out.println("NEW WINDOW");
	}
	
    public MainWindow() {
        setTitle("ArcadeGames");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(768, 576); // match your game resolution
        setLocationRelativeTo(null);
        MainMenuPanel mp = new MainMenuPanel(this);
    	mp.loadConfig();
        showMainMenu(mp);
        setVisible(true);
    }

    public void showMainMenu(MainMenuPanel mp) {
        setContentPane(mp);
        mp.requestFocusInWindow();
        mp.randomizeMusic();
        revalidate();
        repaint();
    }

    public void startBomberman() {
        BMGamePanel gp = new BMGamePanel(this);
		setTitle("BomberMan");
		gp.setupGame();
        setContentPane(gp);
        pack();
        revalidate();
        repaint();
        gp.requestFocusInWindow();
        gp.startGameThread(); //start Bomberman
        System.out.println("BMG STARTED");
    }

    public void startSpaceInvaders() {
    	SpaceInvaders gp = new SpaceInvaders(this);
    	setTitle("SpaceInvaders");
        setContentPane(gp);
        pack();
        revalidate();
        repaint();
        gp.requestFocusInWindow();
        System.out.println("SIG STARTED");
    }
    
    public void returnToMenu() {
        MainMenuPanel menu = new MainMenuPanel(this); // recreate menu or reuse if persistent
        setTitle("ArcadeGames");
        setContentPane(menu);
        pack();
        revalidate();
        repaint();
        menu.requestFocusInWindow(); // in case menu has any keyboard input
        menu.playMusic(0);
    }
}
