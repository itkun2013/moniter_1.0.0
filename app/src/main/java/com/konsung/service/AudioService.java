package com.konsung.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.konsung.R;


/**
 * Created by chengminghui on 15/11/4.
 * 播放报警音的service
 */
public class AudioService extends Service implements MediaPlayer.OnCompletionListener {
    private MediaPlayer player;

    @Override
    public void onCompletion(MediaPlayer mp) {
        //是否需要循环播放
    }

    public class AudioServiceBinder extends Binder {
        public AudioService getService() {
            return AudioService.this;
        }
    }

    private final IBinder sasBinder = new AudioServiceBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return sasBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        player = MediaPlayer.create(this, R.raw.med_lev);
        player.setOnCompletionListener(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!player.isPlaying()) {
//            ToastAssit.showT(this, "play_music");
            player.start();
        }
        return super.onStartCommand(intent, flags, startId);

    }
    /**
     * 调用stopService停止服务时，会调用onDestroy()方法。
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (player.isPlaying()) {
//            ToastAssit.showT(this, "stop_music");
            player.stop();
        }
        player.release();
    }
}
