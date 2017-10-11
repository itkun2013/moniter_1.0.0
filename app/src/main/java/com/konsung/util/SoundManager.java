package com.konsung.util;

import android.content.Context;

/**
 * 
 * @author Administrator
 * 
 */
public class SoundManager {
	private PlaySoundPool playSoundPool;


	private static SoundManager soundManager;

	public SoundManager(Context context) {
		playSoundPool = new PlaySoundPool(context);
	}

	public static SoundManager getInstance(Context context) {
		if (soundManager == null) {
			soundManager = new SoundManager(context);
		}
		return soundManager;
	}

	
	public void playHeightSound() {
		playSoundPool.playSounds(PlaySoundPool.HEIGHT, 0);
	}
	public void playLowSound() {
		playSoundPool.playSounds(PlaySoundPool.LOW, 0);
	}
	public void playMedSound() {
		playSoundPool.playSounds(PlaySoundPool.MED, 0);
	}

}
