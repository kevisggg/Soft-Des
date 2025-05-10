package main;

import java.io.Serializable;

public class BMPlayerLeaderboard implements Serializable{
	private String name;
	private int score, rank;
	
	public BMPlayerLeaderboard(int score) {
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
