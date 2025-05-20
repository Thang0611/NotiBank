package com.NT.banknotireader;


import android.content.Context;
import android.media.AudioManager;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import java.util.Locale;

public class TextToSpeechPlugin  {
    private static final String TAG = "TextToSpeechPlugin";

    private final AudioManager audioManager;
    private final TextToSpeech textToSpeech;

    public TextToSpeechPlugin(final Context context) {
        this.audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        this.textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(new Locale("vi", "VN"));

                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(context, "Thiết bị chưa hỗ trợ tiếng Việt, hãy cài thêm giọng nói Google", Toast.LENGTH_LONG).show();
                        Log.e(TAG, "Ngôn ngữ tiếng Việt không được hỗ trợ");
                    } else {
                        Log.d(TAG, "Đã thiết lập TTS với tiếng Việt");
                    }
                } else {
                    Log.e(TAG, "Khởi tạo TTS thất bại");
                }
            }
        });
    }

    /**
     * Phát âm văn bản tiếng Việt
     */
    public void speak(String text) {
        // Đặt âm lượng kênh nhạc (STREAM_MUSIC) lên mức tối đa
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);

        textToSpeech.speak(text, TextToSpeech.QUEUE_ADD, null, null);
    }

    /**
     * Giải phóng tài nguyên TTS
     */
    public void shutdown() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }
}
