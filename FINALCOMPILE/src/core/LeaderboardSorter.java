package core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class LeaderboardSorter implements Serializable{ //SORTS ARRAYLIST OF PLAYERS
	private static final long serialVersionUID = 1L;
	private ArrayList<LeaderboardPlayer> players = new ArrayList<>();
	
	public LeaderboardPlayer getPlayer(int i) {
		return players.get(i);
	}
	
	public int getPlayersSize() {
		return players.size();
	}
	
	public void addPlayer(LeaderboardPlayer player) {
		players.add(player);
	}
	
	public void sort() {//sort from highest (index 0) to lowest (index 19)
		Collections.sort(players, Comparator.comparingInt(LeaderboardPlayer::getScore).reversed());
		setRank();
		
		//delete scores beyond top 20
		if(players.size()>20) {
			players.remove(20);
		}
	}
	
	public void setRank() {
		for (int i = 0; i<players.size(); i++) {
			players.get(i).setRank(i+1);
		}
	}
	
	public void out() {
		for(LeaderboardPlayer player : players) {
			System.out.println(player.getName() + " - " + player.getScore() + " - " + player.getRank());
		}
	}
}
