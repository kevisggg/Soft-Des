package pacman.main.state;

import java.awt.*;
import java.awt.event.*;
import pacman.main.Game;
import pacman.leaderboard.LeaderboardManager;
import pacman.leaderboard.PlayerScore;
import core.LeaderboardSorter;

public class PMLeaderboardState implements PMGameState {
    private final Game game;
    private final LeaderboardSorter manager;

    public PMLeaderboardState(Game game) {
        this.game = game;
        this.manager = new LeaderboardSorter();
    }

    @Override
    public void update() {
        // Not needed
    }

    @Override
    public void draw(Graphics2D g) {
        // No need for duplicate leaderboard drawing
        // The Pacman class handles this with drawLeaderboardScreen()
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // No need for this as Pacman class handles all input
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {}
}