package core;

import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class Sound {
	
	Clip clip;
	URL sounds[] = new URL[40];
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
		
		sounds[11] = getClass().getResource("/SI/sound/themesong.wav");
		sounds[12] = getClass().getResource("/SI/sound/shoot.wav");
		sounds[13] = getClass().getResource("/SI/sound/shoot2.wav");
		sounds[14] = getClass().getResource("/SI/sound/invaderkilled.wav");
		sounds[15] = getClass().getResource("/SI/sound/mysterykilled.wav");
		sounds[16] = getClass().getResource("/SI/sound/shipexplosion.wav");
		sounds[17] = getClass().getResource("/SI/sound/mysteryentered.wav");
		sounds[18] = getClass().getResource("/SI/sound/0.wav");
		sounds[19] = getClass().getResource("/SI/sound/1.wav");
		sounds[20] = getClass().getResource("/SI/sound/2.wav");
		sounds[21] = getClass().getResource("/SI/sound/3.wav");
		
		sounds[22] = getClass().getResource("/PM/sounds/pacman_beginning.wav");
		sounds[23] = getClass().getResource("/PM/sounds/pacman_chomp.wav");
		sounds[24] = getClass().getResource("/PM/sounds/pacman_death.wav");
		sounds[25] = getClass().getResource("/PM/sounds/pacman_eatfruit.wav");
		sounds[26] = getClass().getResource("/PM/sounds/pacman_ghostBlue.wav");
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
