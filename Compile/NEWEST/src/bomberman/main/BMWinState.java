package bomberman.main;

import java.awt.Cursor;
import java.awt.Point;

import core.GameState;
import core.UI;

public class BMWinState implements GameState{
	private BMGamePanel gp;
	private UI ui;
	private int cnt;
	public BMWinState(BMGamePanel gp, UI ui) {
		this.gp = gp;
		this.ui = ui;
		cnt=0;
	}
	@Override
	public void update() {
		// TODO Auto-generated method stub
		cnt++;
		if(cnt>=100) {
			cnt=0;
			gp.updateLevel();
			gp.getScoreHandler().addScoreLvl();
			gp.newLevel();
		}
	}

	@Override
	public void draw(boolean clickedThisFrame, Point clickPoint) {
		// TODO Auto-generated method stub
		ui.drawHUD();
		ui.drawBPUCounter();
		ui.drawWinScreen(clickedThisFrame, clickPoint);
	}
	@Override
	public void pause() {}
	@Override
	public void hoverHandler(Point p) {gp.setCursor(Cursor.getDefaultCursor());}

}
