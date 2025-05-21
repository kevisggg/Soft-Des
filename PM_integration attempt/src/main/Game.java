package pacman.src.main;

import core.Sound;
import pacman.src.main.state.*;

public class Game {
    private Sound sound;
    private PMLeaderboardObserver leaderboardObserver;
    private PMPlayState pacmanPlayState;
    // Add these fields
    private PMGameState currentState;
    private PMLeaderboardState leaderboardState;
    private PMGameOverState gameOverState;
    
    public Game() {
        sound = new Sound();
        leaderboardObserver = new PMLeaderboardObserver(this);
        pacmanPlayState = new PMPlayState(this);
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