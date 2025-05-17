package core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Config {
	private MainMenuPanel gp;
	private static Config instance;
	
	private Config(MainMenuPanel gp) {
		this.gp = gp;
	}
	public static Config getInstance(MainMenuPanel gp) {
		if (instance == null) instance = new Config(gp);
        return instance;
	}
	
	public void saveConfig() {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter("config.txt"));
			
			//MUSIC VOL
			bw.write(String.valueOf(gp.getBGMScale()));
			bw.newLine();
			
			//SFX VOL
			bw.write(String.valueOf(gp.getSFXScale()));
			bw.newLine();
			
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void loadConfig() {
		try {
			BufferedReader br = new BufferedReader(new FileReader("config.txt"));
			
			//MUSIC VOL
			String s = br.readLine();
			gp.setBGMScale(Integer.parseInt(s));
			
			//SFX VOL
			s = br.readLine();
			gp.setSFXScale(Integer.parseInt(s));
			
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("loaded config");
	}
}
