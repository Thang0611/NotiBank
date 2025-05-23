package com.NT.banknotireader;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.concurrent.atomic.AtomicReference;
public class BankNotificationListener extends NotificationListenerService {
    private static AudioManager audioManager;
    private TextToSpeech tts;
    private DBHelper dbHelper;
    private SharedPreferences prefs;


    private SoundHelper soundHelper;
    @Override
    public void onCreate() {
        super.onCreate();

        dbHelper = new DBHelper(this);
        prefs = getSharedPreferences("settings", MODE_PRIVATE);
//        tts = new TextToSpeech(getApplicationContext(), status -> {
//            if (status == TextToSpeech.SUCCESS) {
//                tts.setLanguage(new Locale("vi","VN"));
//            }
//        });
        tts = new TextToSpeech(getApplicationContext(), status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(new Locale("vi", "VN"));

                // Đặt streamType theo SharedPreferences
                SharedPreferences prefs = getApplicationContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
                int streamType = prefs.getInt("audio_output_stream", AudioManager.STREAM_RING);
                tts.setAudioAttributes(new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .setLegacyStreamType(streamType)
                        .build());
            }
        });

        soundHelper = new SoundHelper(getApplicationContext());
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        if (!prefs.getBoolean("listener_enabled", true)) return;

        try {
            Bundle extras = sbn.getNotification().extras;
            String title = String.valueOf(extras.getCharSequence(NotificationCompat.EXTRA_TITLE));
            String text = String.valueOf(extras.getCharSequence(NotificationCompat.EXTRA_TEXT));
            String packageName = sbn.getPackageName();
            if (packageName.equals("com.zing.zalo") || packageName.equals("com.facebook.orca")) return;

            boolean isMoney = isMoneyText(text) || isMoneyText(title);
//            Log.i("NHT", "onNotificationPosted: "+isMoneyText(text+title) + isMoneyText(title));
            boolean isBankPackage = isBankNotification(packageName);
//            Log.i("NHT", "onNotificationPosted: "+text );
//            Log.i("NHT", title);


            if (isMoney||isBankPackage ) {


                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    String content = parseTransaction(packageName, title, text);
//                    Log.d("NHT", "content"+content);
                    if (!content.isEmpty()) {
                        speak(this,content);
                        saveTransaction(title, text);
//                        Log.i("NHT", "onNotificationPosted: "+content);
                    }
                }, 1000);
            }

        } catch (Exception e) {
//            Log.e("BankNotification", "Lỗi xử lý thông báo: ", e);
        }
    }

    private boolean isMoneyText(String text) {
        if (text.isEmpty()) {
            return false;
        }
        if (text.contains("VND") || text.contains("₫")) {
            return text.contains("+") || text.contains("-");
        }
        return false;
    }

    private boolean isBankNotification(String pkg) {
        return pkg.equals("com.techcombank.notiapp")
                || pkg.equals("com.vietqr.product")
                || pkg.contains("mb") || pkg.contains("vietcom") || pkg.contains("vib")
                || pkg.contains("tpb") || pkg.contains("vpbank") || pkg.contains("momo");
    }

    private String extractAmount(String text) {
        Pattern pattern = Pattern.compile("(\\+?\\d{1,3}(?:\\.\\d{3})*(?:,\\d{1,2})?|\\d{1,3}(?:,\\d{3})*(?:\\.\\d{1,2})?)(\\s?VND|\\s?₫|\\s?đ)?");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) return matcher.group().trim();
        return null;

    }
    private String extractAmount2(String text, String title) {
        String combined = text + " " + title;
        Pattern pattern = Pattern.compile("([+-]?\\d{1,3}(?:[.,]\\d{3})*(?:[.,]\\d{1,2})?)\\s?(VND|₫|đ)?", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(combined);

        while (matcher.find()) {
            String rawAmount = matcher.group(1);

            // Xóa dấu + hoặc - nếu có
            String cleanAmount = rawAmount.replaceAll("[^\\d]", "");

            try {
                long amount = Long.parseLong(cleanAmount);
                return String.format("%,d", amount); // Format lại dạng 2,000,000
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        return null;
    }


    private long getDaysUntilExpire() {
        String expireDateStr = prefs.getString("expire_date", "01-01-2000");
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate expireDate = LocalDate.parse(expireDateStr, formatter);
            return ChronoUnit.DAYS.between(LocalDate.now(), expireDate);
        } catch (Exception e) {
            return -1;
        }
    }

    private void sendNotification(String title, String message) {
        // Tùy bạn cài NotificationCompat.Builder ở đây nếu muốn thông báo cho người dùng.
    }

    private void saveTransaction(String title, String text) {
//        String amount = extractAmount(text);
        dbHelper.insertTransaction(title , text, System.currentTimeMillis());

        Intent intent = new Intent("com.NT.banknotireader.NEW_TRANSACTION");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private String parseTransaction(String pkg, String title, String text) {
        String result = "";
        if(pkg.equals("com.VCB")){
            result = TransactionParser.VietcomBank(text);
        }
        else if (pkg.equals("vn.com.techcombank.bb.app")) {
            result = TransactionParser.TCB(title);
        } else if (pkg.equals("vn.com.msb.smartBanking")) {
            result = TransactionParser.MSB(text);
        } else if (pkg.equals("com.tpb.mb.gprsandroid")) {
            result = TransactionParser.TPB(text);
            if (result.isEmpty()) result = TransactionParser.TPB2(text);
        } else if (pkg.equals("com.vib.myvib2")) {
            result = TransactionParser.VIB(text);
        } else if (pkg.equals("com.mservice.momotransfer")) {
            result = TransactionParser.Momo(text);
        } else if (pkg.equals("com.techcombank.notiapp")) {
            result = TransactionParser.ShareNoti(text);
        } else if (pkg.equals("com.bplus.vtpay")) {
            result = TransactionParser.ViettelPay(text);
            if (result.isEmpty()) result = TransactionParser.VietPay(text);
        } else if (pkg.equals("vn.com.lpb.lienviet24h")) {
            result = TransactionParser.LPBank(text);
        } else if (pkg.equals("com.vnpay.vpbankonline")) {
            result = TransactionParser.VPBank(title);
        } else if (pkg.equals("com.vnpay.coopbank")) {
            result = TransactionParser.CoopBank(text);
        } else if (pkg.equals("com.vietqr.product")) {
            result = TransactionParser.VietQR(text);
        } else if (pkg.equals("vn.abbank.retail")) {
            result = TransactionParser.ABBank(title);
        } else if (pkg.equals("vn.com.ocb.awe")) {
            result = TransactionParser.OCB(text);
        } else if (pkg.equals("com.vnpay.hdbank")) {
            result = TransactionParser.HDBank(text);
        } else {
            String amountRaw = extractAmount2(title, text);
            String amountSanitized = amountRaw.replace(",", "").replace(".", "");
            long amount = Long.parseLong(amountSanitized);
            result = "Giao dịch thành công: " + NumberToWordsConverter.convert(amount) + " đồng";
        }
//        Log.i("NHT",  result);
        return result;
    }

    private String today() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }
    private void speak(Context context, String msg) {
        if (tts == null) return;

        SharedPreferences prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        int streamType = prefs.getInt("audio_output_stream", AudioManager.STREAM_RING);
        boolean enableDucking = prefs.getBoolean("enable_ducking", false);

        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        int focusGain = enableDucking
                ? AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK
                : AudioManager.AUDIOFOCUS_GAIN_TRANSIENT;

        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .setLegacyStreamType(streamType)
                .build();

        AudioManager.OnAudioFocusChangeListener afChangeListener = focusChange -> {
            // Xử lý thay đổi focus nếu cần
        };

        final AtomicReference<AudioFocusRequest> focusRequestRef = new AtomicReference<>(null);
        int result;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AudioFocusRequest focusRequest = new AudioFocusRequest.Builder(focusGain)
                    .setAudioAttributes(attributes)
                    .setAcceptsDelayedFocusGain(false)
                    .setOnAudioFocusChangeListener(afChangeListener)
                    .build();
            focusRequestRef.set(focusRequest);
            result = audioManager.requestAudioFocus(focusRequest);
        } else {
            result = audioManager.requestAudioFocus(afChangeListener, streamType, focusGain);
        }

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            Runnable speakRunnable = () -> {
                if (tts != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        tts.setAudioAttributes(attributes);
                        tts.speak(msg, TextToSpeech.QUEUE_ADD, null, "ttsUtteranceId");
                    } else {
                        Bundle params = new Bundle();
                        params.putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, streamType);
                        tts.speak(msg, TextToSpeech.QUEUE_ADD, params, "ttsUtteranceId");
                    }
                }
            };

            soundHelper.playTingTing(context, () -> {
                speakRunnable.run();

                tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    @Override
                    public void onStart(String utteranceId) { }

                    @Override
                    public void onDone(String utteranceId) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            AudioFocusRequest focusRequest = focusRequestRef.get();
                            if (focusRequest != null) {
                                audioManager.abandonAudioFocusRequest(focusRequest);
                            }
                        } else {
                            audioManager.abandonAudioFocus(afChangeListener);
                        }
                    }

                    @Override
                    public void onError(String utteranceId) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            AudioFocusRequest focusRequest = focusRequestRef.get();
                            if (focusRequest != null) {
                                audioManager.abandonAudioFocusRequest(focusRequest);
                            }
                        } else {
                            audioManager.abandonAudioFocus(afChangeListener);
                        }
                    }
                });
            }, 500);
        } else {
            // Không được cấp focus, xử lý fallback nếu cần
        }
    }




//    private void speak(Context context, String msg) {
//        if (tts != null) {
//            SharedPreferences prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
//            int streamType = prefs.getInt("audio_output_stream", AudioManager.STREAM_RING);
//
//            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
//            int currentVolume = audioManager != null ? audioManager.getStreamVolume(streamType) : 0;
//
//            AudioAttributes attributes = new AudioAttributes.Builder()
//                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY)
//                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
//                    .setLegacyStreamType(streamType)
//                    .build();
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                soundHelper.playTingTing(context, new Runnable() {
//                    @Override
//                    public void run() {
//                        if (tts != null) {
//                            tts.setAudioAttributes(attributes);
//                            if (audioManager != null) {
//                                audioManager.setStreamVolume(streamType, currentVolume, 0);
//                            }
//                            tts.speak(msg, TextToSpeech.QUEUE_ADD, null, "ttsUtteranceId");
//                        }
//                    }
//                }, 500);
//            } else {
//                Bundle params = new Bundle();
//                params.putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, streamType);
//                soundHelper.playTingTing(context, new Runnable() {
//                    @Override
//                    public void run() {
//                        if (tts != null) {
//                            tts.speak(msg, TextToSpeech.QUEUE_ADD, params, "ttsUtteranceId");
//                        }
//                    }
//                }, 500);
//            }
//        }
//    }


    @Override
    public void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
            soundHelper.release();
        }
        super.onDestroy();
    }

}
