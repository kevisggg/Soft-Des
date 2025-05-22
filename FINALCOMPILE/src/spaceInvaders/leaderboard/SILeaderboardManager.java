package spaceInvaders.leaderboard;
import java.io.*;
import java.util.*;

public class SILeaderboardManager implements Serializable {
    private static final long serialVersionUID = 1L;
    private ArrayList<SIPlayerScore> players;
    public static final String FILE_PATH = "spaceinvaders_leaderboard.dat";

    public SILeaderboardManager() {
        players = load();
    }

    public void addPlayer(SIPlayerScore player) {
        players.add(player);
        sort();
        save();
    }
    public SIPlayerScore getPlayer(int i) {
		return players.get(i);
	}
	
	public int getPlayersSize() {
		return players.size();
	}
	
    private void sort() {
        players.sort(Comparator.comparingInt(SIPlayerScore::getScore).reversed());
        // Assign ranks: tied scores share the highest rank, next is rank + number of ties
        int rank = 1;
        for (int i = 0; i < players.size(); i++) {
            if (i > 0 && players.get(i).getScore() == players.get(i - 1).getScore()) {
                players.get(i).setRank(players.get(i - 1).getRank());
            } else {
                players.get(i).setRank(rank);
            }
            rank = i + 2;
        }
        if (players.size() > 20) {
            players = new ArrayList<>(players.subList(0, 20));
        }
    }

    private void save() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            out.writeObject(players);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private ArrayList<SIPlayerScore> load() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            return (ArrayList<SIPlayerScore>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    public List<SIPlayerScore> getTopPlayers() {
        return players;
    }

    public int getTotalScores() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getTotalScores'");
    }
}
