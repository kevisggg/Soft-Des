package pacman.src.main.state;

import java.awt.Graphics2D;
import java.awt.event.*;
import core.Sound;
import pacman.src.main.Game;
import pacman.src.main.Pacman;

public class PMPlayState implements PMGameState {
    private Pacman pacman;
    private Game game;
    
    public PMPlayState(Game game) {
        this.game = game;
        Sound soundSystem = game.getSound();
        // Pass null for mainWindow, it will be set later
        pacman = new Pacman(game, game.getLeaderboardObserver(), soundSystem);
    }

    @Override
    public void update() {
        pacman.updateGame();
    }

    @Override
    public void draw(Graphics2D g) {
        // Change from renderGame to draw
        pacman.draw(g);  // Use the existing draw method
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

    public Pacman getPacman() {
        return pacman;
    }
}