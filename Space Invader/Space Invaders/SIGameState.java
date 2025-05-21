public interface SIGameState {
    void enter();
    void exit();
    void update();
    void render(java.awt.Graphics2D g2);
    void handleKey(java.awt.event.KeyEvent e);
}
