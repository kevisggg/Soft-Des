package main;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MouseHandler implements MouseListener{
	private boolean clicked;
	private Point p;
	private GamePanel gp;
	
	public MouseHandler(GamePanel gp) {
		// TODO Auto-generated constructor stub
		this.gp=gp;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(gp.getGameState()!=gp.playState) {
			p = e.getPoint();
			System.out.println(p);
			clicked = true;
			System.out.println(clicked);
		}
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
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
