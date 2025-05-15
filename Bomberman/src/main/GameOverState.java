package main;

import java.awt.Point;

public class GameOverState implements GameState{
	private UI ui;
	public GameOverState(UI ui) {
		this.ui = ui;
	}
	@Override
	public void update() {
		// TODO Auto-generated method stub
		//gp.stopMusic();
	}
	@Override
	public void draw(boolean clickedThisFrame, Point clickPoint) {
		// TODO Auto-generated method stub
		ui.drawHUD();
		ui.drawGameoverScreen(clickedThisFrame, clickPoint);
	}
	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void hoverHandler(Point p) {
		// TODO Auto-generated method stub
		ui.hoverGameOver(p);
	}
}
