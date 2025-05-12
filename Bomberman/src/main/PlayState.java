package main;

import java.util.ArrayList;

import entity.Enemy;
import entity.Explosion;
import entity.Player;
import object.Bomb;

public class PlayState implements GameState {
	GamePanel gp;
	Player player;
	CollisionChecker colCheck;
	ArrayList<Bomb> bombs;
	ArrayList<Explosion> explosions;
	ArrayList<Enemy> enemies;
	UI ui;
	public PlayState(GamePanel gp, Player player, CollisionChecker colCheck, ArrayList<Bomb> bombs, ArrayList<Explosion> explosions, ArrayList<Enemy> enemies, UI ui) {
		this.gp = gp;
		this.player = player;
		this.colCheck = colCheck;
		this.bombs = bombs;
		this.explosions = explosions;
		this.enemies = enemies;
		this.ui = ui;
	}
	@Override
	public void update() {
		// TODO Auto-generated method stub
		player.update();
		//CREATE EXPLOSION HIT METHOD
		if(!player.getInvincible()) {
			/*for(Explosion e: explosions) {
				//if(e.getExpStatus() && !e.getHasHit()) {
					
				//}
				//System.out.println(player.getX()/gp.tileSize + "/" + e.getX()/gp.tileSize + "        " + player.getY()/gp.tileSize + "/" + e.getY()/gp.tileSize);
				if(player.getX()/gp.tileSize == e.getX()/tileSize && player.getY()/tileSize == e.getY()/tileSize ) {
					System.out.println("SETTING PLAYER HIT ======");
					player.setHit(true);
					//e.setHit(true);
					player.setInvincible(true);
					break;
				}
			}*/
			if(colCheck.checkEntityExp(player.getX(), player.getY(), player.getWidth(), player.getHeight())) {
				player.setHit(true);
		       	player.setInvincible(true);
			}
		}
		
		//System.out.println(bombs.size());/////////////////////
		for(int i = bombs.size() - 1; i >= 0; i--){
			Bomb b = bombs.get(i);
			if(b!=null) {
				b.update();
				if(!b.getBombStatus()) {
					explode(b);
					bombs.remove(i);
					player.removeBombsPlaced();
				}
			}
			//System.out.println("[" + (b.getX() + player.collisionBox.x)/tileSize + "] [" + (b.getY()+player.collisionBox.y+10)/tileSize + "]");
		}
		for (int i = explosions.size() - 1; i >= 0; i--) {
            Explosion e = explosions.get(i);
            e.update(); // Add an update method to Explosion for timing
            if (!e.getExpStatus()) { // Add an expiration check in Explosion
                explosions.remove(i);
            }
        }
		for(int i = 0; i<enemies.size(); i++) {
			//System.out.println("CHEKCING ENEMIES");
			enemies.get(i).update();
			for(Explosion e: explosions) {
				//if(e.getExpStatus() && !e.getHasHit()) {
				
				if(colCheck.checkEntityExp(enemies.get(i).getX(), enemies.get(i).getY(), enemies.get(i).getWidth(), enemies.get(i).getHeight())) {
					enemies.remove(i);
					gp.playSFX(10);
					gp.getScoreHandler().addScoreElim();
					break;
				}
				//}
				//System.out.println(player.getX()/tileSize + "/" + e.getX()/tileSize + "        " + player.getY()/tileSize + "/" + e.getY()/tileSize);
				/*if(enemies.get(i).getX()/tileSize == e.getX()/tileSize && enemies.get(i).getY()/tileSize == e.getY()/tileSize ) {
					System.out.println("SETTING ENEMY HIT ======");
					enemies.remove(i);
					//addScore()
					//player.addScore(i);
					scoreH.addScoreElim();
					//e.setHit(true);
					break;
				}*/
			}
		}
		if(enemies.size() == 0) {
			gp.setWinState();
			gp.playSFX(9);
		}
	}
	
	private void explode(Bomb b) {
		int eX = b.getX()/gp.tileSize;
		int eY = b.getY()/gp.tileSize;
		//System.out.println("x: " + eX + " y: " + eY);
		boolean upEmpty = colCheck.checkTileExp(eX, eY-1, true);
		boolean downEmpty = colCheck.checkTileExp(eX, eY+1, true);
		boolean leftEmpty = colCheck.checkTileExp(eX-1, eY, true);
		boolean rightEmpty = colCheck.checkTileExp(eX+1, eY, true);
		gp.addExplosions(eX*gp.tileSize, eY*gp.tileSize);
		for(int i = 0; i <= player.getBombRadius(); i++) {
			//System.out.println("ADDING EXPLOSIONS");
			if(eY-i>0 && upEmpty) {
				//System.out.println("UP EMPTY");
				
				upEmpty = colCheck.checkTileExp(eX, eY-i, upEmpty);
				if(upEmpty) {
					gp.addExplosions(eX*gp.tileSize, (eY-i)*gp.tileSize);
				}
			}
			if(eY+i>0 && downEmpty) {
				//System.out.println("DOWN EMPTY");
				downEmpty = colCheck.checkTileExp(eX, eY+i, downEmpty);
				if(downEmpty) {
					gp.addExplosions(eX*gp.tileSize, (eY+i)*gp.tileSize);
				}
			}
			if(eX-i>0 && leftEmpty) {
				//System.out.println("LEFT EMPTY");
				//gp.addExplosions((eX-i)*gp.tileSize, eY*gp.tileSize);
				leftEmpty = colCheck.checkTileExp(eX-i, eY, leftEmpty);
				if(leftEmpty) {
					gp.addExplosions((eX-i)*gp.tileSize, eY*gp.tileSize);
				}
			}
			if(eX+i>0 && rightEmpty) {
				//System.out.println("RIGHT EMPTY");
				//gp.addExplosions((eX+i)*gp.tileSize, eY*gp.tileSize);
				rightEmpty = colCheck.checkTileExp(eX+i, eY, rightEmpty);
				if(rightEmpty) {
					gp.addExplosions((eX+i)*gp.tileSize, eY*gp.tileSize);
				}
			}
		}
		//System.out.println("EXPLOSIONS: " + explosions.size());
		for(Explosion e: explosions) {
			//System.out.println("TRUE SET");
			e.setExpStatus(true);
		}
		gp.playSFX(2);
	}
	
	@Override
	public void draw() {
		ui.drawHUD();
	}
	
	public void pause() {
		gp.setPauseState();
		gp.playSFX(6);
	}
}
