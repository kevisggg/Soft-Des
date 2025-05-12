package main;

public class WinState implements GameState{
	private GamePanel gp;
	private UI ui;
	private int cnt;
	public WinState(GamePanel gp, UI ui) {
		this.gp = gp;
		this.ui = ui;
		cnt=0;
	}
	@Override
	public void update() {
		// TODO Auto-generated method stub
		cnt++;
		if(cnt>=100) {
			cnt=0;
			gp.updateLevel();
			gp.getScoreHandler().addScoreLvl();
			gp.newLevel();
		}
	}

	@Override
	public void draw() {
		// TODO Auto-generated method stub
		ui.drawHUD();
		ui.drawWinScreen();
	}
	@Override
	public void pause() {}

}
