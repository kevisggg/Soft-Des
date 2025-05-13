package main;

import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Sound {
	
	Clip clip;
	URL sounds[] = new URL[30];
	int frame;
	
	public Sound() {
		sounds[0] = getClass().getResource("/sound/BMMusic.wav");
		sounds[1] = getClass().getResource("/sound/placeBomb.wav");
		sounds[2] = getClass().getResource("/sound/explosion.wav");
		sounds[3] = getClass().getResource("/sound/hitHurt.wav");
		sounds[4] = getClass().getResource("/sound/powerUp.wav");
		sounds[5] = getClass().getResource("/sound/select.wav");
		sounds[6] = getClass().getResource("/sound/pause.wav");
		sounds[7] = getClass().getResource("/sound/hissBomb.wav");
		sounds[8] = getClass().getResource("/sound/gameover.wav");
		sounds[9] = getClass().getResource("/sound/win.wav");
		sounds[10] = getClass().getResource("/sound/enemyHit.wav");
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
		clip.setFramePosition(frame);
		clip.start();
	}
	
	public void loop() {
		clip.loop(Clip.LOOP_CONTINUOUSLY);
	}
	
	public void pause() {
		frame = clip.getFramePosition();
		clip.stop();
		//clip.close();
	}
	
	public void stop() {
		clip.stop();
		frame = 0;
	}
}
