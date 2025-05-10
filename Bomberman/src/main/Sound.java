package main;

import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Sound {
	
	Clip clip;
	URL sounds[] = new URL[30];
	
	public Sound() {
		sounds[0] = getClass().getResource("/sound/BMmusic.wav");
		sounds[1] = getClass().getResource("/sound/placeBomb.wav");
		sounds[2] = getClass().getResource("/sound/explosion.wav");
		sounds[3] = getClass().getResource("/sound/hitHurt.wav");
		sounds[4] = getClass().getResource("/sound/powerUp.wav");
		sounds[5] = getClass().getResource("/sound/select.wav");
		sounds[6] = getClass().getResource("/sound/pause.wav");
		sounds[7] = getClass().getResource("/sound/hissBomb.wav");
		sounds[8] = getClass().getResource("/sound/gameover.wav");
	}
	
	public void setFile(int i) {
		try {
			//OPEN AUDIO FILE IN JAVA
			AudioInputStream ais = AudioSystem.getAudioInputStream(sounds[i]);
			clip = AudioSystem.getClip();
			clip.open(ais);
			
		}catch(Exception e) {
		}
	}
	
	public void play() {
		clip.start();
	}
	
	public void loop() {
		clip.loop(Clip.LOOP_CONTINUOUSLY);
	}
	
	public void stop() {
		clip.stop();
	}
	
}
