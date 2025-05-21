package bomberman.tile;

import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.imageio.ImageIO;
import bomberman.main.BMGamePanel;
import core.ImageScaler;

public class BMTileManager {
	private Tile[] tile;
	private int mapTileNum[][];
	
	public BMTileManager() {
		tile = new Tile[3];//TILE TYPES
		mapTileNum = new int[BMGamePanel.maxScreenCol][BMGamePanel.maxScreenRow];
		getTileImage();
		loadMap("/BM/maps/mapblank.txt");
	}
	
	public void getTileImage() {
		System.out.println("getting image");
		setupImage(0, "/BM/tile/floor.png", false);
		setupImage(1, "/BM/tile/wall.jpg", true);
		setupImage(2, "/BM/tile/box.png", true);
		System.out.println("got tiles");
	}
	
	public int getMapTileNum(int x, int y) { //tile int at coords x,y
		return mapTileNum[x][y];
	}
	
	public void setupImage(int index, String path, boolean collision) {
		ImageScaler s = new ImageScaler();
		try {
			tile[index] = new Tile();
			System.out.println("NEW TILE");
			tile[index].setImage(ImageIO.read(getClass().getResourceAsStream(path)));
			System.out.println("GET RESOURCE");
			tile[index].setImage(s.scale(BMGamePanel.tileSize, BMGamePanel.tileSize, tile[index].getImage()));
			tile[index].setCollision(collision);
		}catch(IOException e) {
			e.printStackTrace();
		}
		System.out.println("DONE SETUP");
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
			
			while(col<BMGamePanel.maxScreenCol && row<BMGamePanel.maxScreenRow) {
				String line = br.readLine();
				
				while(col<BMGamePanel.maxScreenCol) {
					String numbers[] = line.split(" ");
					int num = Integer.parseInt(numbers[col]);
					mapTileNum[col][row] = num;
					col++;
				}
				if(col == BMGamePanel.maxScreenCol) {
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
		return tile[i].getCollision();
	}
	
	public void draw(Graphics2D g2) {//draw tiles onto map area
		//g2.drawImage(tile[0].image, 0, 0, BomberManGamePanel.tileSize, BomberManGamePanel.tileSize, null);
		//g2.drawImage(tile[1].image, 48, 0, BomberManGamePanel.tileSize, BomberManGamePanel.tileSize, null);
		
		int col=0;
		int row=0;
		int x=0;
		int y=0;
		
		while(col<BMGamePanel.maxScreenCol && row<BMGamePanel.maxScreenRow) {
			int tileIndex = mapTileNum[col][row];
			
			g2.drawImage(tile[tileIndex].getImage(), x, y, null);
			col++;
			x+=BMGamePanel.tileSize;
			
			if(col==BMGamePanel.maxScreenCol) {
				col =0;
				x=0;
				row++;
				y+=BMGamePanel.tileSize;
			}
		}
	}

}
