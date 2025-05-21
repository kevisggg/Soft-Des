package main;

import main.state.*;

public class Game {
    private GameState currentState;
    private final PlayState playState;
    private final GameOverState gameOverState;
    private final LeaderboardState leaderboardState;

    public Game() {
        leaderboardState = new LeaderboardState(this);
        playState = new PlayState(this, leaderboardState); // Pass observer
        gameOverState = new GameOverState(this);
        currentState = playState;
    }

    public void setState(GameState state) {
        currentState = state;
    }

    public GameState getCurrentState() {
        return currentState;
    }

    public PlayState getPlayState() {
        return playState;
    }

    public GameOverState getGameOverState() {
        return gameOverState;
    }

    public LeaderboardState getLeaderboardState() {
        return leaderboardState;
    }
}