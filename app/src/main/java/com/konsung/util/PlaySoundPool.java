package com.konsung.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;

import com.konsung.R;

import java.util.HashMap;


/**
 * @author noone
 */
public final class PlaySoundPool {
	
	public static final String HEIGHT = "hi_lev";
	public static final String LOW = "lo_lev";
	public static final String MED = "med_lev";

	private Context activity;
	HashMap<String,Integer> soundMap = null;
	SoundPool soundPool = null;
	public PlaySoundPool (Context activity) {
		this.activity = activity;
		initDatas();
	}

	@SuppressLint("UseSparseArrays")
	public void initDatas(){
		soundPool = new SoundPool(0,AudioManager.STREAM_MUSIC,0);
		soundMap = new HashMap<String, Integer>();
		soundMap.put(HEIGHT, soundPool.load(activity, R.raw.hi_lev, 1));
		soundMap.put(LOW, soundPool.load(activity, R.raw.lo_lev, 1));
		soundMap.put(MED, soundPool.load(activity, R.raw.med_lev, 1));

		soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
			
			@Override
			public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
//				Logs.i(getClass(), "sampleId:"+sampleId);
			}
		});
	}
	
	@SuppressWarnings("static-access")
	public void playSounds(String sound, int number) {
		
		AudioManager am = (AudioManager) activity.getSystemService(activity.AUDIO_SERVICE);
		float audioMaxVolumn = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		float audioCurrentVolumn = am.getStreamVolume(AudioManager.STREAM_MUSIC);
		float volumnRatio = audioCurrentVolumn / audioMaxVolumn;
//		Logs.i(getClass(), "audioMaxVolumn:"+audioMaxVolumn +"-audioCurrentVolumn:"+ audioCurrentVolumn+"-volumnRatio:"+volumnRatio);
		soundPool.play(soundMap.get(sound), volumnRatio, volumnRatio, 1, number, 1);
	}
}
