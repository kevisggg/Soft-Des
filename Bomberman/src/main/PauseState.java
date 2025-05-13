package main;

import java.awt.Point;

public class PauseState implements GameState{
	private UI ui;
	private GamePanel gp;
	public PauseState(GamePanel gp,UI ui) {
		this.gp = gp;
		this.ui = ui;
	}
	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void draw(boolean clickedThisFrame, Point clickPoint) {
		// TODO Auto-generated method stub
		ui.drawHUD();
		ui.drawPauseScreen(clickedThisFrame, clickPoint);
	}
	
	public void pause() {
		gp.setPlayState();
		gp.playSFX(6);
		gp.playMusic();
	}

}
