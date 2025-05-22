package pacman.leaderboard;
import java.io.*;
import java.util.*;
//import PlayerScore;

import core.LeaderboardPlayer;

public class LeaderboardManager implements Serializable {
    private static final long serialVersionUID = 1L;
    private ArrayList<PlayerScore> players;

    public LeaderboardManager() {
        players = load();
    }

    public void addPlayer(PlayerScore player) {
        players.add(player);
        sort();
        save();
    }
    public PlayerScore getPlayer(int i) {
		return players.get(i);
	}
	
	public int getPlayersSize() {
		return players.size();
	}
    private void sort() {
        players.sort(Comparator.comparingInt(PlayerScore::getScore).reversed());
        for (int i = 0; i < players.size(); i++) {
            players.get(i).setRank(i + 1);
        }
        if (players.size() > 20) {
            players = new ArrayList<>(players.subList(0, 20));
        }
    }

    private void save() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("leaderboard.dat"))) {
            out.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public ArrayList<PlayerScore> load() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("leaderboard.dat"))) {
            return (ArrayList<PlayerScore>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    public List<PlayerScore> getTopPlayers() {
        return players;
    }
}
