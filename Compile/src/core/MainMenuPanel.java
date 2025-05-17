package core;

import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JButton;

public class MainMenuPanel extends AbstractGamePanel implements Runnable{
	private Thread gameThread;
	public final int FPS = 60;
	private MainWindow mainWindow;
	private Config config = Config.getInstance(this);
    public MainMenuPanel(MainWindow window) {
    	this.mainWindow = window;
        JButton bombermanButton = new JButton("Play Bomberman");
        //JButton pacmanButton = new JButton("Play Pacman");

        bombermanButton.addActionListener(e -> playBM());
        //pacmanButton.addActionListener(e -> window.startPacman());

        this.add(bombermanButton);
        //this.add(pacmanButton);
        this.addKeyListener(keyH);
		this.addMouseListener(mouseH);
		this.addMouseMotionListener(mouseH);
		this.setFocusable(true);
		//playMusic(0);
		System.out.println("set after first");
		startGameThread();
        setMainMenuState();
    }
    
    public void saveConfig() {config.saveConfig();}
	public void loadConfig() {config.loadConfig();}
	public void adjustSFX(int adj) {sfx.adjScale(adj);}
	public void adjustBGM(int adj) {music.adjScale(adj);music.checkVol();}
	public void setBGMScale(int scale) {music.setScale(scale);}
	public void setSFXScale(int scale) {sfx.setScale(scale);}
    public void setSettingsState() {gamestateH.setState(new MainSettingsState(ui));}
	public void setMainLeaderState() {gamestateH.setState(new MainLeaderboardState(ui));System.out.println("MAINLEADERBOAED");}
	public void setMainMenuState() {gamestateH.setState(new MainMenuState(ui));}
	public void run() {
		double drawInterval = 1000000000/FPS;
		double delta = 0;
		long lastTime = System.nanoTime();
		long currentTime;
		while(gameThread != null) {
			currentTime = System.nanoTime();
			delta += (currentTime - lastTime) / drawInterval;
			lastTime = currentTime;
			if (delta>=1) {
				update();
				repaint();
				delta--;
			}
		}
	}
	public void startGameThread() {
		gameThread = new Thread(this);
		gameThread.start();
	}
	public void update() {System.out.println("BGM: " + getBGMScale() + " SFX: " + getSFXScale());gamestateH.request();}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		//RUNTIME
		//long timeStart = 0;
		//timeStart = System.nanoTime();
		ui.draw(g2);
		
		//RUNTIME
		//long timeEnd = System.nanoTime();
		//long passed = timeEnd - timeStart;
		//System.out.println(passed);
		g2.dispose();
	}
	
	public void returnToMenu() {
        mainWindow.returnToMenu();
    }
	
	public void playBM() {
		stopMusic();
		mainWindow.startBomberman();
	}
		
}
