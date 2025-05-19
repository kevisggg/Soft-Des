package bomberman.object;

import java.io.IOException;

import javax.imageio.ImageIO;

public class PU_Range extends BMPowerUp{
	
	public PU_Range() {
		
		name = "PUrange";
		hits = 0;
		remove = false;
		collisionBoxDefaultX = 0;
		collisionBoxDefaultY = 0;
		try {
			image = ImageIO.read(getClass().getResourceAsStream("/BM/objects/PUrange.png"));
			
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
}
