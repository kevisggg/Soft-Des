package main;

import java.awt.Point;

public class InsState implements GameState{
	private UI ui;
	public InsState(UI ui) {
		this.ui = ui;
	}
	@Override
	public void update() {}

	@Override
	public void draw(boolean clickedThisFrame, Point clickPoint) {
		ui.drawHUD();
		ui.drawInstruction(clickedThisFrame, clickPoint);
	}
	@Override
	public void pause() {}

}
