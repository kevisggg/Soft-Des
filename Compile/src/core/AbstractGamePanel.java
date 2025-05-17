package core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.JPanel;

import bomberman.entity.BMPlayer;
import bomberman.main.BMCollisionChecker;

public class AbstractGamePanel  extends JPanel{
	
	public MainWindow mainWindow;
	public static Sound sfx = new Sound();
	public static Sound music = new Sound();
	public LeaderboardPlayer currentPlayer;
	public LeaderboardSorter ls = new LeaderboardSorter();
	public GameStateHandler gamestateH = new GameStateHandler();
	public KeyHandler keyH = new KeyHandler(this);
	public MouseHandler mouseH = new MouseHandler(this);
	public ScoreHandlerInterface scoreH;
	public UI ui = new UI(this, mouseH);
	//public PlayerInterface player;
	//public BMCollisionChecker colCheck = new BMCollisionChecker(this, asset, tileMgr, explosions);
	//public Player player = new Player(this, keyH, colCheck);
	
	public void returnToMenu() {
		stopMusic();
        mainWindow.returnToMenu();
    }
	public String getNameInput() {return keyH.getName();}
	public boolean getNameEntered() {return keyH.getNameEntered();}
	public String getCurPlayerRank() {
		String rank = String.valueOf(currentPlayer.getRank());
		if(rank.equals("0")) {
			rank = "-";
		}
		return rank;
	}
	public int getBGMScale() {return music.getScale();}
	public int getSFXScale() {return sfx.getScale();}
	public void pauseMusic() {music.pause();}
	public void stopMusic() {music.stop();}
	public void playMusic(int file) {
		music.setFile(file);
		System.out.println("SET MUSIC");
		music.play();
		music.loop();
	}
	
	public void playSFX(int i) {
		sfx.setFile(i);
		sfx.play();
	}
	public void setPlayState() {
		// TODO Auto-generated method stub
		
	}
	public void restartGame() {
		// TODO Auto-generated method stub
		
	}
	
	public void update() {gamestateH.request();}
	//public BMPlayer getPlayer() {return null;}
	public GameState getGameState() {return gamestateH.getState();}
	
	/*public PlayerInterface getPlayer() {
		return player;
	}*/
	
	
	//FILE HANDLING
		public LeaderboardSorter loadData() {
			return ls;
	    }
		
		public void saveData() {
			//ls.out();
		}
		public String getLvl() {
			// TODO Auto-generated method stub
			return null;
		}
		public void setCurPlayerName(String name) {
			// TODO Auto-generated method stub
			
		}
}
