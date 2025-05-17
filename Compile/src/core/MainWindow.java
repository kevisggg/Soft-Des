package core;

import javax.swing.*;
import bomberman.main.BomberManGamePanel;
//import pacman.main.PacmanGamePanel;

public class MainWindow extends JFrame {
    public MainWindow() {
        setTitle("Arcade Launcher");
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
        mp.playMusic(0);
        revalidate();
        repaint();
    }

    public void startBomberman() {
        BomberManGamePanel gp = new BomberManGamePanel(this);
        //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//setResizable(false);
		setTitle("BomberMan");
		//add(window);
		
		
		//setLocationRelativeTo(null);
		//gp.setVisible(true);
		gp.setupGame();
		//gp.startGameThread();
        setContentPane(gp);
        pack();
        revalidate();
        repaint();
        gp.requestFocusInWindow();
        gp.startGameThread(); // start Bomberman
        System.out.println("BMG STARTED");
    }

    /*public void startPacMan() {
        PacManGamePanel gp = new PacManGamePanel(this);
        //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//setResizable(false);
		setTitle("PacMan");
		//add(window);
		
		
		//setLocationRelativeTo(null);
		//gp.setVisible(true);
		gp.setupGame();
		//gp.startGameThread();
        setContentPane(gp);
        pack();
        revalidate();
        repaint();
        gp.requestFocusInWindow();
        gp.startGameThread(); // start Bomberman
        System.out.println("BMG STARTED");
    }*/
    
    public void returnToMenu() {
        MainMenuPanel menu = new MainMenuPanel(this); // recreate menu or reuse if persistent
        setContentPane(menu);
        revalidate();
        repaint();
        menu.requestFocusInWindow(); // in case menu has any keyboard input
        menu.playMusic(0);
    }
}
