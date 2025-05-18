package core;

import java.awt.Point;

public class MainSelectState implements GameState{
	UI ui;
	public MainSelectState(UI ui) {
		this.ui = ui;
	}
	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void draw(boolean clickedThisFrame, Point clickPoint) {
		// TODO Auto-generated method stub
		ui.drawGameSelect(clickedThisFrame, clickPoint);
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hoverHandler(Point p) {
		// TODO Auto-generated method stub
		ui.hoverSelect(p);
	}

}
