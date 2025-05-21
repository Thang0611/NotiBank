package com.NT.banknotireader;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;

import java.io.IOException;

public class SoundHelper {

    private MediaPlayer mediaPlayer;
    private final Context context;

    public SoundHelper(Context context) {
        // Dùng application context để đảm bảo không bị leak context của Service
        this.context = context.getApplicationContext();
    }
    public void playTingTing(Context context, Runnable callback, int delayMillis) {
        release(); // release mediaPlayer trước đó nếu có

        SharedPreferences prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        int streamType = prefs.getInt("audio_output_stream", AudioManager.STREAM_RING); // lấy stream type

        int usage = getUsageFromStreamType(streamType); // ánh xạ sang usage tương ứng

        try {
            AssetFileDescriptor afd = context.getResources().openRawResourceFd(R.raw.tingting);
            if (afd == null) return;

            mediaPlayer = new MediaPlayer();

            // Gán đúng AudioAttributes với streamType và usage
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(usage)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setLegacyStreamType(streamType)
                    .build();

            mediaPlayer.setAudioAttributes(attributes);
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();

            mediaPlayer.setOnPreparedListener(mp -> mp.start());

            mediaPlayer.setOnCompletionListener(mp -> {
                release();
                if (callback != null) {
                    new Handler(Looper.getMainLooper()).postDelayed(callback, delayMillis);
                }
            });

            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
            release();
        }
    }




//    public void playTingTing(Context context, Runnable callback, int delayMillis) {
//        release();
//
//        SharedPreferences prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
//        int streamType = prefs.getInt("audio_output_stream", AudioManager.STREAM_RING);
//
//        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
//        int currentVolume = audioManager.getStreamVolume(streamType);
//        int maxVolume = audioManager.getStreamMaxVolume(streamType);
//        float volumeRatio = (float) currentVolume / maxVolume;
//
//        mediaPlayer = MediaPlayer.create(context, R.raw.tingting);
//        if (mediaPlayer == null) return;
//
//        AudioAttributes attributes = new AudioAttributes.Builder()
//                .setUsage(getUsageFromStreamType(streamType))
//                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
//                .build();
//        mediaPlayer.setAudioAttributes(attributes);
//
//        mediaPlayer.setVolume(volumeRatio, volumeRatio);
//
//        mediaPlayer.setOnCompletionListener(mp -> {
//            release();
//            if (callback != null) {
//                new Handler(Looper.getMainLooper()).postDelayed(callback, delayMillis);
//            }
//        });
//
//        mediaPlayer.start();
//    }

    private int getUsageFromStreamType(int streamType) {
        switch (streamType) {
            case AudioManager.STREAM_NOTIFICATION:
                return AudioAttributes.USAGE_NOTIFICATION;
            case AudioManager.STREAM_RING:
                return AudioAttributes.USAGE_NOTIFICATION_RINGTONE;
            case AudioManager.STREAM_MUSIC:
            default:
                return AudioAttributes.USAGE_MEDIA;
        }
    }

    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
