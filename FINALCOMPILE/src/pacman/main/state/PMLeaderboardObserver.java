package pacman.main.state;

import core.LeaderboardPlayer;
import core.LeaderboardReader;
import core.LeaderboardSorter;
import pacman.main.Game;

public class PMLeaderboardObserver {
    private Game game;
    private LeaderboardSorter leaderboardSorter;
    private static final String LEADERBOARD_FILE = "pacman_leaderboard.dat";
    
    public PMLeaderboardObserver(Game game) {
        this.game = game;
        this.leaderboardSorter = LeaderboardReader.loadLeaderboardFromFile(LEADERBOARD_FILE);
        if (this.leaderboardSorter == null) {
            this.leaderboardSorter = new LeaderboardSorter();
        }
    }
    
    public void addNewScore(LeaderboardPlayer player) {
        leaderboardSorter.addPlayer(player);
        leaderboardSorter.sort();
    }

    public LeaderboardSorter getLeaderboard() {
        return leaderboardSorter;
    }
}