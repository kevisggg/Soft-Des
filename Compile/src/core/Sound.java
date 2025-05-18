package core;

import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class Sound {
	
	Clip clip;
	URL sounds[] = new URL[30];
	int frame;
	private FloatControl volControl;
	private int volumeScale = 3;
	private float volume;
	
	public Sound() {
		sounds[0] = getClass().getResource("/BM/sound/BMMusic.wav");
		sounds[1] = getClass().getResource("/BM/sound/placeBomb.wav");
		sounds[2] = getClass().getResource("/BM/sound/explosion.wav");
		sounds[3] = getClass().getResource("/BM/sound/hitHurt.wav");
		sounds[4] = getClass().getResource("/BM/sound/powerUp.wav");
		sounds[5] = getClass().getResource("/BM/sound/select.wav");
		sounds[6] = getClass().getResource("/BM/sound/pause.wav");
		sounds[7] = getClass().getResource("/BM/sound/hissBomb.wav");
		sounds[8] = getClass().getResource("/BM/sound/gameover.wav");
		sounds[9] = getClass().getResource("/BM/sound/win.wav");
		sounds[10] = getClass().getResource("/BM/sound/enemyHit.wav");
	}
	
	public void setFile(int i) {
		try {
			//OPEN AUDIO FILE IN JAVA
			AudioInputStream ais = AudioSystem.getAudioInputStream(sounds[i]);
			clip = AudioSystem.getClip();
			clip.open(ais);
			
			volControl = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
			checkVol();
			
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
	}
	
	public void stop() {
		clip.stop();
		frame = 0;
	}
	
	//=================================================
	
	public void checkVol() {
		switch(volumeScale) {
		case 0: volume = -80f; break;
		case 1: volume = -20f; break;
		case 2: volume = -12f; break;
		case 3: volume = -5f; break;
		case 4: volume = 1f; break;
		case 5: volume = 6f; break;
		}
		volControl.setValue(volume);
	}
	
	public void adjScale(int adj) {
		int adjScale = volumeScale + adj;
		if(adjScale <= 5 && adjScale >= 0) {
			volumeScale = adjScale;
		}
		System.out.println(volumeScale);
	}
	
	public void setScale(int scale) {
		System.out.println("PRESCALE: " + volumeScale);
		System.out.println("SCALESSET");
		volumeScale = scale;
		System.out.println("POSRSCALE: " + volumeScale);
		//checkVol();
	}
	
	public int getScale() {
		return volumeScale;
	}
	
	/*public void toggle(boolean toggle) {
		if(toggle) {
			volumeScale=3;
		} else {
			volumeScale=0;
		}
	}*/
}
