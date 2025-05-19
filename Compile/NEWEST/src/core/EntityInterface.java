package core;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public interface EntityInterface {
	void update();
	BufferedImage setupImage(String path);
	void draw(Graphics2D g2);
}
