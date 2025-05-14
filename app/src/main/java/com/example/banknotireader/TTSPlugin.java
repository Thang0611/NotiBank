package com.example.banknotireader;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

public class TTSPlugin {
    private static boolean isInitialized = false;
    private static TextToSpeech tts;
    private static Context unityContext;

    public static void setContext(Context context) {
        unityContext = context;
    }

    public static void initializeTTS() {
        if (unityContext != null) {
            tts = new TextToSpeech(unityContext, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status == TextToSpeech.SUCCESS) {
                        int result = tts.setLanguage(new Locale("vi", "VN"));
                        isInitialized = (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED);
                        if (!isInitialized) {
                            Log.e("TTS", "Language not supported or missing data");
                        }
                    } else {
                        Log.e("TTS", "Initialization failed");
                    }
                }
            });
        }
    }

    public static void speak(String text) {
        if (isInitialized) {
            tts.speak(text, TextToSpeech.QUEUE_ADD, null, null);
        } else {
            Log.e("TTS", "TTS not initialized");
        }
    }

    public static void stop() {
        if (tts != null) {
            tts.stop();
        }
    }

    public static void shutdown() {
        if (tts != null) {
            tts.shutdown();
        }
    }
}
