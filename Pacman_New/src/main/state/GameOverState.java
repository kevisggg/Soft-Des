package main.state;

import java.awt.*;
import java.awt.event.*;
import main.Game;

public class GameOverState implements GameState {
    private final Game game;

    public GameOverState(Game game) {
        this.game = game;
    }

    @Override
    public void update() {}

    @Override
    public void draw(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 640, 480);
        g.setColor(Color.WHITE);
        g.drawString("Game Over", 280, 200);
        g.drawString("Press Enter to view Leaderboard", 220, 230);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            game.setState(game.getLeaderboardState());
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {}
}