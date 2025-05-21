package leaderboard;
import java.io.Serializable;

public class PlayerScore implements Serializable {
    private String name;
    private int score;
    private int rank;

    public PlayerScore(String name, int score) {
        this.name = name;
        this.score = score;
        this.rank = 0;
    }

    public void setRank(int rank) {
        if (rank <= 20) {
            this.rank = rank;
        }
    }

    public int getScore() {
        return score;
    }

    public int getRank() {
        return rank;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
