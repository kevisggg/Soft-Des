package bomberman.object;

import java.io.IOException;

import javax.imageio.ImageIO;

public class PU_Capacity extends BMPowerUp{
	
	public PU_Capacity() {
		
		name = "PUcapacity";
		hits = 0;
		remove = false;
		collisionBoxDefaultX = 0;
		collisionBoxDefaultY = 0;
		try {
			image = ImageIO.read(getClass().getResourceAsStream("/BM/objects/PUcapacity.png"));
			
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
}
