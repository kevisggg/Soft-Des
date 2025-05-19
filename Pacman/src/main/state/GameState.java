package main.state;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;

public interface GameState {
    void update();
    void draw(Graphics2D g);
    void keyPressed(KeyEvent e);
    void keyReleased(KeyEvent e);
    void mouseWheelMoved(MouseWheelEvent e);
}
