package core;

import java.io.Serializable;

public class LeaderboardPlayer implements Serializable{
	private String name;
	private int score, rank;
	
	public LeaderboardPlayer(int score) {
		name = "temp";
		this.score = score;
		rank = 0;
	}
	
	public void setRank(int rank) {
		if(rank<=20) {
			this.rank = rank;
		}
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getScore() {
		return this.score;
	}
	
	public int getRank() {
		return this.rank;
	}
	
	public String getName() {
		return this.name;
	}
}
