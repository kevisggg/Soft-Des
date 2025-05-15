package main;

import java.awt.Point;

public interface GameState {
	void update();
	void draw(boolean clickedThisFrame, Point clickPoint);
	void pause();
	void hoverHandler(Point p);
}
