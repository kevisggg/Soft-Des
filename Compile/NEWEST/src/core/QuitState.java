package core;

import java.awt.Point;

public class QuitState implements GameState{

	@Override
	public void update() {
		// TODO Auto-generated method stub
		System.exit(0);
	}

	@Override
	public void draw(boolean clickedThisFrame, Point clickPoint) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hoverHandler(Point p) {
		// TODO Auto-generated method stub
		
	}

}
