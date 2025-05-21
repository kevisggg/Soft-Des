package core;

import java.awt.Point;

public class MainLeaderboardState implements GameState{
	private UI ui;
	public MainLeaderboardState(UI ui) {
		this.ui = ui;
	}
	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void draw(boolean clickedThisFrame, Point clickPoint) {
		// TODO Auto-generated method stub
		ui.drawMainLeaderboard(clickedThisFrame, clickPoint);
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hoverHandler(Point p) {
		// TODO Auto-generated method stub
		//ui.hoverGameOver(p);
		ui.hoverMainLeaderboard(p);
	}

}
