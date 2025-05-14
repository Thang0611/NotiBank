package com.example.banknotireader;


import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

public class AudioPlugin {
    private static final String TAG = "AudioPlugin";

    private MediaPlayer mediaPlayer;

    public AudioPlugin(Context context) {
        try {
            int resId = context.getResources().getIdentifier("ting_ting", "raw", context.getPackageName());
            if (resId != 0) {
                mediaPlayer = MediaPlayer.create(context, resId);
                if (mediaPlayer != null) {
                    mediaPlayer.setLooping(false);
                }
            } else {
                Log.e(TAG, "Không tìm thấy file ting_ting trong thư mục res/raw");
            }
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khởi tạo MediaPlayer", e);
        }
    }

    public void playAudio() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    public void stopAudio() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
