package bomberman.main.state;

import java.awt.Point;

import bomberman.main.BMGamePanel;
import core.GameState;
import core.UI;

public class BMPauseState implements GameState{
	private UI ui;
	private BMGamePanel gp;
	public BMPauseState(BMGamePanel gp,UI ui) {
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
		ui.drawBPUCounter();
		ui.drawPauseScreen(clickedThisFrame, clickPoint);
	}
	
	public void pause() {
		gp.setPlayState();
		gp.playSFX(6);
		gp.playMusic(0);
	}
	
	@Override
	public void hoverHandler(Point p) {
		// TODO Auto-generated method stub
		ui.hoverPause(p);
	}

}
