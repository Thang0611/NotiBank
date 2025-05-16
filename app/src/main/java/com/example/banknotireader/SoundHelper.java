package com.example.banknotireader;

import android.content.Context;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Looper;

public class SoundHelper {

    private SoundPool soundPool;
    private int soundId;
    private boolean isLoaded = false;

    public SoundHelper(Context context) {
        soundPool = new SoundPool.Builder()
                .setMaxStreams(1)
                .build();
        soundId = soundPool.load(context, R.raw.tingting, 1);
        soundPool.setOnLoadCompleteListener((sp, id, status) -> {
            if (status == 0 && id == soundId) {
                isLoaded = true;
            }
        });
    }

    /**
     * Phát âm thanh "ting-ting" và gọi callback khi phát xong (khoảng delayMillis ms)
     * @param callback Runnable được gọi sau khi âm thanh phát xong
     * @param delayMillis thời gian delay sau khi play âm thanh, thường 500ms đủ để âm thanh phát xong
     */
    public void playTingTing(Runnable callback, int delayMillis) {
        if (!isLoaded) {
            // Nếu chưa load kịp thì chờ load xong rồi play, đơn giản có thể delay thêm chút
            new Handler(Looper.getMainLooper()).postDelayed(() -> playTingTing(callback, delayMillis), 100);
            return;
        }

        soundPool.play(soundId, 1f, 1f, 1, 0, 1f);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (callback != null) {
                callback.run();
            }
        }, delayMillis);
    }

    public void release() {
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }
}
