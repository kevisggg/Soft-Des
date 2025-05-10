package main;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class BMLeaderboardSorter implements Serializable{
	private static final long serialVersionUID = 1L;
	private ArrayList<BMPlayerLeaderboard> players = new ArrayList<>();
	
	public void addPlayer(BMPlayerLeaderboard player) {
		players.add(player);
	}
	
	public void sort() {
		Collections.sort(players, Comparator.comparingInt(BMPlayerLeaderboard::getScore).reversed());
		setRank();
	}
	
	public void setRank() {
		for (int i = 0; i<players.size(); i++) {
			players.get(i).setRank(i+1);
		}
	}
	
	public void out() {
		for(BMPlayerLeaderboard player : players) {
			System.out.println(player.getName() + " - " + player.getScore() + " - " + player.getRank());
		}
	}
}
