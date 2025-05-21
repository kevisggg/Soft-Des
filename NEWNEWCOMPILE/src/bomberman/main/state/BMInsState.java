package bomberman.main.state;

import java.awt.Point;

import core.GameState;
import core.UI;

public class BMInsState implements GameState{
	private UI ui;
	public BMInsState(UI ui) {
		this.ui = ui;
	}
	@Override
	public void update() {}

	@Override
	public void draw(boolean clickedThisFrame, Point clickPoint) {
		ui.drawHUD();
		ui.drawBPUCounter();
		ui.drawInstruction(clickedThisFrame, clickPoint);
	}
	@Override
	public void pause() {}
	@Override
	public void hoverHandler(Point p) {
		// TODO Auto-generated method stub
		ui.hoverInstructions(p);
	}

}
