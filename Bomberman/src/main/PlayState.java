package main;

import java.awt.Cursor;
import java.awt.Point;
import java.util.ArrayList;

import entity.Enemy;
import entity.Explosion;
import entity.Player;
import object.Bomb;

public class PlayState implements GameState {
	private GamePanel gp;
	private Player player;
	private CollisionChecker colCheck;
	private ArrayList<Bomb> bombs;
	private ArrayList<Explosion> explosions;
	private ArrayList<Enemy> enemies;
	private UI ui;
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
		updatePlayer();
		updateBombs();
		updateExplosions();
		updateEnemies();
		checkWin();
	}
	
	@Override
	public void draw(boolean clickedThisFrame, Point clickPoint) {
		ui.drawHUD();
	}
	
	public void pause() {
		gp.setPauseState();
		gp.playSFX(6);
		gp.pauseMusic();
	}
	
	private void checkWin() {
		if(enemies.size() == 0) {
			gp.setWinState();
			gp.playSFX(9);
		}
	}
	
	private void updateEnemies() {
		for(int i = 0; i<enemies.size(); i++) {
			enemies.get(i).update();
			for(Explosion e: explosions) {
				if(colCheck.checkEntityExp(enemies.get(i).getX(), enemies.get(i).getY(), enemies.get(i).getWidth(), enemies.get(i).getHeight())) {
					enemies.remove(i);
					gp.playSFX(10);
					gp.getScoreHandler().addScoreElim();
					break;
				}
			}
		}
	}
	
	private void updateExplosions() {
		for (int i = explosions.size() - 1; i >= 0; i--) {
            Explosion e = explosions.get(i);
            e.update();
            if (!e.getExpStatus()) {
                explosions.remove(i);
            }
        }
	}
	
	private void updateBombs() {
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
		}
	}
	
	private void updatePlayer() {
		player.update();
		if(!player.getInvincible()) {
			if(colCheck.checkEntityExp(player.getX(), player.getY(), player.getWidth(), player.getHeight())) {
				player.setHit(true);
		       	player.setInvincible(true);
			}
		}
	}
	
	private void explode(Bomb b) {
		int eX = b.getX()/gp.tileSize;
		int eY = b.getY()/gp.tileSize;
		boolean upEmpty = colCheck.checkTileExp(eX, eY-1, true);
		boolean downEmpty = colCheck.checkTileExp(eX, eY+1, true);
		boolean leftEmpty = colCheck.checkTileExp(eX-1, eY, true);
		boolean rightEmpty = colCheck.checkTileExp(eX+1, eY, true);
		gp.addExplosions(eX*gp.tileSize, eY*gp.tileSize);
		for(int i = 0; i <= player.getBombRadius(); i++) {
			if(eY-i>0 && upEmpty) {
				upEmpty = colCheck.checkTileExp(eX, eY-i, upEmpty);
				if(upEmpty) {
					gp.addExplosions(eX*gp.tileSize, (eY-i)*gp.tileSize);
				}
			}
			if(eY+i>0 && downEmpty) {
				downEmpty = colCheck.checkTileExp(eX, eY+i, downEmpty);
				if(downEmpty) {
					gp.addExplosions(eX*gp.tileSize, (eY+i)*gp.tileSize);
				}
			}
			if(eX-i>0 && leftEmpty) {
				leftEmpty = colCheck.checkTileExp(eX-i, eY, leftEmpty);
				if(leftEmpty) {
					gp.addExplosions((eX-i)*gp.tileSize, eY*gp.tileSize);
				}
			}
			if(eX+i>0 && rightEmpty) {
				rightEmpty = colCheck.checkTileExp(eX+i, eY, rightEmpty);
				if(rightEmpty) {
					gp.addExplosions((eX+i)*gp.tileSize, eY*gp.tileSize);
				}
			}
		}
		for(Explosion e: explosions) {
			e.setExpStatus(true);
		}
		gp.playSFX(2);
	}
	@Override
	public void hoverHandler(Point p) {gp.setCursor(Cursor.getDefaultCursor());}
}
