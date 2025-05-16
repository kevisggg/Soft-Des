package main;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener{
	
	private boolean upPressed, downPressed, leftPressed, rightPressed, spacePressed;
	private boolean nameEntered;
	//int up, down, left, right, space;
	private GamePanel gp;
	private String name = "";
	
	public KeyHandler(GamePanel gp) {
		this.gp = gp;
	}
	@Override
	public void keyTyped(KeyEvent e) {
		if(gp.getGameState() instanceof GameOverState && !nameEntered) {
			//System.out.println("GETTING KEY TYPED");
			System.out.println(name + " - " + nameEntered);
			char c = e.getKeyChar();
	          if (Character.isLetterOrDigit(c) && name.length() < 10) {
	        	 // System.out.println("ADD CHAR");
	        	  name += Character.toUpperCase(c);
	          } else if (c == '\b' && name.length() > 0) {
	        	  //System.out.println("BACK CHAR");
	              name = name.substring(0, name.length() - 1);
	          } else if (c == '\n' && name.length() > 0) {
	        	  gp.setCurPlayerName(name);
	        	  gp.playSFX(5);
	              setNameEntered(true);
	              gp.saveData();
	          }
	          //gp.repaint();
		}  
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
			gp.getGameState().pause();
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
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setNameEntered(boolean nameEntered) {
		this.nameEntered = nameEntered;
	}
	
	public boolean getNameEntered() {
		return nameEntered;
	}

}