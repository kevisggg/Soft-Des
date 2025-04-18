package tile;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;

import main.GamePanel;
import main.ImageScaler;

public class TileManager {
	
	GamePanel gp;
	public Tile[] tile;
	public int mapTileNum[][];
	
	public TileManager(GamePanel gp) {
		this.gp = gp;

		tile = new Tile[3];//TILE TYPES
		mapTileNum = new int[gp.maxScreenCol][gp.maxScreenRow];
		getTileImage();
		loadMap("/maps/mapblank.txt");
	}
	
	public void getTileImage() {
		setupImage(0, "/tile/floor.png", false);
		setupImage(1, "/tile/wall.jpg", true);
		setupImage(2, "/tile/box.png", true);
	}
	
	public void setupImage(int index, String path, boolean collision) {
		ImageScaler s = new ImageScaler();
		try {
			tile[index] = new Tile();
			tile[index].image = ImageIO.read(getClass().getResourceAsStream(path));
			tile[index].image = s.scale(gp.tileSize, gp.tileSize, tile[index].image);
			tile[index].collision = collision;
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setTile(int index, int x, int y) {
		mapTileNum[x][y] = index;
	}
	
	public void loadMap(String path) {
		try {
			InputStream is = getClass().getResourceAsStream(path);
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			
			int col = 0;
			int row = 0;
			
			while(col<gp.maxScreenCol && row<gp.maxScreenRow) {
				String line = br.readLine();
				
				while(col<gp.maxScreenCol) {
					String numbers[] = line.split(" ");
					int num = Integer.parseInt(numbers[col]);
					mapTileNum[col][row] = num;
					col++;
				}
				if(col == gp.maxScreenCol) {
					col=0;
					row++;
				}
			}
			
			br.close();
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean getTileCollision(int i) {
		return tile[i].collision;
	}
	
	public void draw(Graphics2D g2) {
		//g2.drawImage(tile[0].image, 0, 0, gp.tileSize, gp.tileSize, null);
		//g2.drawImage(tile[1].image, 48, 0, gp.tileSize, gp.tileSize, null);
		
		int col=0;
		int row=0;
		int x=0;
		int y=0;
		
		while(col<gp.maxScreenCol && row<gp.maxScreenRow) {
			int tileIndex = mapTileNum[col][row];
			
			g2.drawImage(tile[tileIndex].image, x, y, null);
			col++;
			x+=gp.tileSize;
			
			if(col==gp.maxScreenCol) {
				col =0;
				x=0;
				row++;
				y+=gp.tileSize;
			}
		}
	}

}
