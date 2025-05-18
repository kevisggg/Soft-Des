package core;

public class GameStateHandler {
	 private GameState state;

	    public void setState(GameState state) {
	        this.state = state;
	    }
	    
	    public GameState getState() {
	    	return state;
	    }

	    public void request() {
	        state.update();
	    }
}
