package main;

public class ScoreHandler {
	private final static int SCORE_ELIM = 100;
	private final static int SCORE_PU = 50;
	private final static int SCORE_LVL = 300;
	private int score;
	private String scoreStr;
	//GamePanel gp;
	
	public ScoreHandler() {
		//this.gp=gp;
		score = 0;
	}
	
	public void resetScore() {
		score = 0;
	}
	
	public void addScoreElim() {
		score = score+SCORE_ELIM;
		out();
	}
	
	public void addScorePowerUp() {
		score = score+SCORE_PU;
		out();
	}
	
	public void addScoreLvl() {
		score = score+SCORE_LVL;
		out();
	}
	
	public String out() {
		//System.out.println("SCORE: "+score);
		return "SCORE: "+score;
	}
	
	public String getScore() {
		scoreStr = String.valueOf(score);
		return scoreStr;
	}
}
