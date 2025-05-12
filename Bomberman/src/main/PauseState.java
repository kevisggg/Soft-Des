package main;

public class PauseState implements GameState{
	UI ui;
	GamePanel gp;
	public PauseState(GamePanel gp,UI ui) {
		this.gp = gp;
		this.ui = ui;
	}
	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void draw() {
		// TODO Auto-generated method stub
		ui.drawHUD();
		ui.drawPauseScreen();
	}
	
	public void pause() {
		gp.setPlayState();
		gp.playSFX(6);
	}

}
