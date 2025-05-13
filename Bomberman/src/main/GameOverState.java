package main;

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
	public void draw() {
		// TODO Auto-generated method stub
		ui.drawHUD();
		ui.drawGameoverScreen();
	}
	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}
}
