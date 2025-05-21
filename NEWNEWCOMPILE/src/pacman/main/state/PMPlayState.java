package pacman.main.state;

import java.awt.Graphics2D;
import java.awt.event.*;

import core.MainWindow;
import core.Sound;
import pacman.main.Game;
import pacman.main.Pacman;

public class PMPlayState implements PMGameState {
    private Pacman pacman;
    private Game game;
    
    public PMPlayState(Game game, MainWindow mainWindow) {
        this.game = game;
        Sound soundSystem = game.getSound();
        // Pass null for mainWindow, it will be set later
        pacman = new Pacman(game, game.getLeaderboardObserver(), soundSystem, mainWindow);
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