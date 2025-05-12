package main;

public class InsState implements GameState{
	UI ui;
	public InsState(UI ui) {
		this.ui = ui;
	}
	@Override
	public void update() {}

	@Override
	public void draw() {
		ui.drawHUD();
		ui.drawInstruction();
	}
	@Override
	public void pause() {}

}
