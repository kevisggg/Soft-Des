package core;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import bomberman.main.state.BMPlayState;
import core.AbstractGamePanel;

public class MouseHandler implements MouseListener, MouseMotionListener{
	private boolean clicked;
	private Point p;
	private AbstractGamePanel gp;
	
	
	public MouseHandler(AbstractGamePanel gp) {
		// TODO Auto-generated constructor stub
		this.gp=gp;
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		/*if(!(gp.getGameState() instanceof PlayState)) {
			p = e.getPoint();
			clicked = true;
			System.out.println(p);
		}*/
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		if(!(gp.getGameState() instanceof BMPlayState)) {//add AND NOT PMPLAYSTATE and SI
			p = e.getPoint();
			clicked = true;
			System.out.println(p);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		//System.out.println("You Entered");
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		 p = e.getPoint();
		 gp.getGameState().hoverHandler(p);
		 //System.out.println(p);
        // Rectangle previous = hoveredButton;
		 //hoveredButton = null;
		 //gp.repaint();
		 
	}
	
	public boolean getClicked() {
		// TODO Auto-generated method stub
		return clicked;
	}
	
	public Point getPoint() {
		// TODO Auto-generated method stub
		return p;
	}
	
	public void resetClick() {
	    clicked = false;
	    p = null;
	}
}
