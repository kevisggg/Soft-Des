package core;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import bomberman.main.BMGamePanel;

public class UIUtility {
	private boolean toggleLeaderboard = false, back = false;
	private int x, length;
	private final float alpha50 = 0.5f;
	private final float alpha70 = 0.7f;
	//private Graphics2D g2;
	private Composite originalComposite;
	public static final String gameFont = "Pixeboy";
	private Stroke defaultStroke;
	
	/*public UIUtility(Graphics2D g2) {
		this.g2 = g2;
	}*/
	
	public BufferedImage setupImage(String path, int sizeDiff) {
		BufferedImage image = null;
		ImageScaler s = new ImageScaler();
		try {
			image = ImageIO.read(getClass().getResourceAsStream(path));
			image = s.scale(BMGamePanel.tileSize-sizeDiff, BMGamePanel.tileSize-sizeDiff, image);
		}catch(IOException e) {
			e.printStackTrace();
		}
		return image;
	}
	
	public BufferedImage renderRegImage(String path) {
		BufferedImage image = null;
		try {
			image = ImageIO.read(getClass().getResourceAsStream(path));
		}catch(IOException e) {
			e.printStackTrace();
		}
		return image;
	}
	
	public void resetToggleLeaderboard() {
		toggleLeaderboard = false;
		back = false;
	}
	
	public boolean getToggleLeaderboard() {
		return toggleLeaderboard;
	}
	
	public void setToggleLeaderboard(boolean set) {
		this.toggleLeaderboard = set;
	}
	
	public boolean getBack() {
		return back;
	}
	
	public void setBack(boolean set) {
		this.back = set;
	}
	
	public void addShadow(Graphics2D g2, int indent, String text, Color colorBack, Color colorFront, int x, int y) {
		g2.setColor(colorBack);
		g2.drawString(text, x+indent, y+indent);
		g2.setColor(colorFront);
		g2.drawString(text, x, y);
	}
	
	public int getTextX(Graphics2D g2, String text) {
		length = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth();
		x = BMGamePanel.screenWidth/2 - length/2;
		System.out.println(text +" - x " + x + " length:" + length);
		return x;
	}
	
	public Color getColor(String hex) {
		Color color = Color.decode(hex);
		return color;
	}
	
	public void setTextBG(Graphics2D g2, int recScreenX, int recScreenY, int width, int height) {
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha70));
		g2.setColor(Color.black);
		g2.fillRect(recScreenX, recScreenY, width, height);
		g2.setComposite(originalComposite);
	}
	
	public void setBG(Graphics2D g2) {
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha50));
		g2.setColor(Color.black);
		g2.fillRect(0, 0, BMGamePanel.screenWidth, BMGamePanel.screenHeight);
		g2.setComposite(originalComposite);
	}

	public void setComposite(Composite composite) {
		// TODO Auto-generated method stub
		this.originalComposite = composite;
	}
	
	public Font createFont(int size) {
		Font font = new Font (gameFont, Font.BOLD, size);
		return font;
	}
	
	/*public void setStroke(Graphics2D g2, int stroke) {
		g2.setStroke(new BasicStroke(stroke));
	}*/
	
	public void setDefaultStroke(Stroke stroke) {
		defaultStroke = stroke;
	}
	
	public Stroke getDefaultStroke() {
		return defaultStroke;
	}
}
