package main.state;

import java.awt.*;
import java.awt.event.*;
import main.Game;
import leaderboard.LeaderboardManager;
import leaderboard.PlayerScore;

public class LeaderboardState implements GameState, LeaderboardObserver {
    private final Game game;
    private final LeaderboardManager manager;

    public LeaderboardState(Game game) {
        this.game = game;
        this.manager = new LeaderboardManager();
    }

    @Override
    public void onNewScore() {
        manager.load(); // Reload latest scores if modified
    }

    @Override
    public void update() {}

    @Override
    public void draw(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 640, 480);
        g.setColor(Color.YELLOW);
        g.drawString("Leaderboard", 270, 50);

        int y = 80;
        for (PlayerScore score : manager.getTopPlayers()) {
            g.drawString(score.getRank() + ". " + score.getName() + " - " + score.getScore(), 200, y);
            y += 20;
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            game.setState(game.getPlayState());
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {}
}