package core;

import javax.swing.JPanel;

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
	
	public void setInsState() {}
	public void setPlayState() {}
	public void setPauseState() {}
	public void setOverState() {}
	public void setWinState() {}
	public void restartGame() {}
	public void update() {gamestateH.request();}
	public GameState getGameState() {return gamestateH.getState();}
	public String getLvl() {
		return null;
	}
	public void setCurPlayerName(String name) {}
	
	//FILE HANDLING
		public LeaderboardSorter loadData() {
			return ls;
	    }
		
		public void saveData() {
			//ls.out();
		}	
}
