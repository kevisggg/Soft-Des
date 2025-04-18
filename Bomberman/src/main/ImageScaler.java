package main;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ImageScaler { //REDUCE RUNTIME DRAW BY PRESCALING
	public BufferedImage scale(int height, int width, BufferedImage image) {
		BufferedImage scaled = new BufferedImage(width, height, image.getType());
		Graphics2D g2 = scaled.createGraphics();
		g2.drawImage(image, 0, 0, width, height, null);
		g2.dispose();
		return scaled;
	}
}
