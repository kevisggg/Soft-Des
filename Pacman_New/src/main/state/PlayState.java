package main.state;

import java.awt.*;
import java.awt.event.*;
import main.Game;
import main.Pacman;

public class PlayState implements GameState {
    private final Game game;
    private final Pacman pacman;
    public final int instructionState = 4;  // New state for instructions
    private Rectangle okButtonBounds;
    private boolean instructionsShown = false;

    public PlayState(Game game, LeaderboardState leaderboardObserver) {
        this.game = game;
        this.pacman = new Pacman(game, leaderboardObserver); // Observer-aware
    }

    public Pacman getPacman() {
        return pacman;
    }
    
    @Override
    public void update() {
        pacman.updateGame();
    }

    @Override
    public void draw(Graphics2D g) {
        pacman.renderGame(g);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        pacman.keyPressed(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        pacman.keyReleased(e);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        pacman.mouseWheelMoved(e);
    }
}