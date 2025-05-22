package pacman.main;

import core.MainWindow;
import pacman.main.state.PMLeaderboardObserver;
//import pacman.main.state.*;
//import pacman.main.state.PMGameOverState;
//import pacman.main.state.PMLeaderboardState;
//import pacman.main.state.PMPlayState;

/*public class Game {
    private PMGameState currentState;
    //private MainWindow mainWindow;
    private final PMPlayState playState;
    private final PMGameOverState gameOverState;
    private final PMLeaderboardState leaderboardState;

    public Game(MainWindow mainWindow) {
        leaderboardState = new PMLeaderboardState(this);
        playState = new PMPlayState(this, leaderboardState, mainWindow); // Pass observer
        gameOverState = new PMGameOverState(this);
        currentState = playState;
    }

    public void setState(PMGameState state) {
        currentState = state;
    }

    public PMGameState getCurrentState() {
        return currentState;
    }

    public PMPlayState getPlayState() {
        return playState;
    }

    public PMGameOverState getGameOverState() {
        return gameOverState;
    }

    public PMLeaderboardState getLeaderboardState() {
        return leaderboardState;
    }
}*/

//package pacman.main;

import core.Sound;
import pacman.main.state.*;

public class Game {
    private Sound sound;
    private PMLeaderboardObserver leaderboardObserver;
    private PMPlayState pacmanPlayState;
    // Add these fields
    private PMGameState currentState;
    private PMLeaderboardState leaderboardState;
    private PMGameOverState gameOverState;
    
    public Game(MainWindow mainWindow) {
        sound = new Sound();
        leaderboardObserver = new PMLeaderboardObserver(this);
        pacmanPlayState = new PMPlayState(this, mainWindow);
        leaderboardState = new PMLeaderboardState(this); // Initialize leaderboard state
        gameOverState = new PMGameOverState(this);
        currentState = pacmanPlayState; // Start with play state
    }
    
    public PMPlayState getPlayState() {
        return pacmanPlayState;
    }
    
    public Sound getSound() {
        return sound;
    }
    
    public PMLeaderboardObserver getLeaderboardObserver() {
        return leaderboardObserver;
    }
    // Add these methods
    public void setState(PMGameState state) {
        this.currentState = state;
    }
    
    public PMGameState getCurrentState() {
        return currentState;
    }
    
    public PMGameOverState getGameOverState() {
        return gameOverState;
    }
    
    public PMLeaderboardState getLeaderboardState() {
        return leaderboardState;
    }
}


