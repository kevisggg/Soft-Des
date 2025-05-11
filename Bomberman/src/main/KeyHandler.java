package main;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener{
	
	private boolean upPressed, downPressed, leftPressed, rightPressed, spacePressed;
	//int up, down, left, right, space;
	private GamePanel gp;
	
	public KeyHandler(GamePanel gp) {
		this.gp = gp;
	}
	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();
		
		if(code == 'W') {//change to up key,
			upPressed = true;
		}
		if(code == KeyEvent.VK_S) {
			downPressed = true;
		}
		if(code == KeyEvent.VK_A) {
			leftPressed = true;
		}
		if(code == KeyEvent.VK_D) {
			rightPressed = true;
		}
		if(code == KeyEvent.VK_SPACE) {
			spacePressed = true;
		}
		if(code == KeyEvent.VK_ESCAPE) {
			if(gp.getGameState() == gp.playState) {
				gp.setPauseState();
				gp.playSFX(6);
			}
			else if(gp.getGameState() == gp.pauseState) {
				gp.setPlayState();
				gp.playSFX(6);
			}
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int code = e.getKeyCode();
		
		if(code == KeyEvent.VK_W) {
			upPressed = false;
		}
		if(code == KeyEvent.VK_S) {
			downPressed = false;
		}
		if(code == KeyEvent.VK_A) {
			leftPressed = false;
		}
		if(code == KeyEvent.VK_D) {
			rightPressed = false;
		}
		if(code == KeyEvent.VK_SPACE) {
			spacePressed = false;
		}
		
	}
	
	public void resetKeys() {
		upPressed = false;
	    downPressed = false;
	    leftPressed = false;
	    rightPressed = false;
	    spacePressed = false;
	}
	
	public boolean getUp() {
		return upPressed;
	}
	
	public boolean getDown() {
		return downPressed;
	}
	
	public boolean getLeft() {
		return leftPressed;
	}
	
	public boolean getRight() {
		return rightPressed;
	}
	
	public boolean getSpace() {
		return spacePressed;
	}

}