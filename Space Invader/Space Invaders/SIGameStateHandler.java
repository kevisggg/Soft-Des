public class SIGameStateHandler {
    private SIGameState currentState;

    public void setState(SIGameState state) {
        if (currentState != null) currentState.exit();
        currentState = state;
        if (currentState != null) currentState.enter();
    }

    public SIGameState getState() {
        return currentState;
    }

    public void update() {
        if (currentState != null) currentState.update();
    }

    public void render(java.awt.Graphics2D g2) {
        if (currentState != null) currentState.render(g2);
    }

    public void handleKey(java.awt.event.KeyEvent e) {
        if (currentState != null) currentState.handleKey(e);
    }
}
