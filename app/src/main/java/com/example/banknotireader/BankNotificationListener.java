package com.example.banknotireader;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BankNotificationListener extends NotificationListenerService {

    private TextToSpeech tts;
    private DBHelper dbHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        dbHelper = new DBHelper(this);
        tts = new TextToSpeech(getApplicationContext(), status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(new Locale("vi"));
            }
        });
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        // Kiểm tra trạng thái bật/tắt của listener
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        if (!prefs.getBoolean("listener_enabled", true)) return;

        // Lấy nội dung thông báo và tiêu đề
        Bundle extras = sbn.getNotification().extras;
        String title = extras.getCharSequence("android.title") + "";
        String text = extras.getCharSequence("android.text") + "";

        // Kiểm tra xem tiêu đề có chứa các từ khóa liên quan đến giao dịch ngân hàng
        Log.i("NHT", "onNotificationPosted: "+text);
        if (!containsBankKeywords(title, text)) {
            return; // Nếu không có từ khóa liên quan, bỏ qua
        }

        // Kiểm tra xem thông báo có chứa số tiền hay không
        String amount = extractAmount(text);
        if (amount != null) {
            speak("Bạn vừa nhận được " + amount);
            dbHelper.insertTransaction(amount, text, System.currentTimeMillis());
            // Gửi broadcast để cập nhật giao diện
            Intent intent = new Intent("com.example.banknotireader.NEW_TRANSACTION");
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }

    // Kiểm tra tiêu đề và nội dung thông báo có chứa từ khóa ngân hàng
    private boolean containsBankKeywords(String title, String text) {
        // Các từ khóa liên quan đến giao dịch ngân hàng
        String[] bankKeywords = {
                "Giao dịch", "Số dư", "Thanh toán", "Chuyển khoản", "Chuyển tiền", "Thanh toán", "VND", "₫", "đ"
        };
        String _title = removeVietnameseTone(title);
        String _text = removeVietnameseTone(text);
        // Kiểm tra nếu tiêu đề hoặc nội dung chứa bất kỳ từ khóa nào trong danh sách
        for (String keyword : bankKeywords) {
            if (title.contains(keyword) || text.contains(keyword)||title.contains(removeVietnameseTone(keyword)) || text.contains(removeVietnameseTone(keyword))) {
                return true; // Nếu có từ khóa, xác nhận là thông báo ngân hàng
            }
        }

        return false; // Không có từ khóa ngân hàng
    }
    private String removeVietnameseTone(String str) {
        return Normalizer.normalize(str, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");
    }
    // Tìm số tiền trong nội dung thông báo
    private String extractAmount(String text) {
        // Regex tìm số tiền theo định dạng VND, ₫, hoặc đ
        Pattern pattern = Pattern.compile(
                "(\\+?\\d{1,3}(?:\\.\\d{3})*(?:,\\d{1,2})?|\\d{1,3}(?:,\\d{3})*(?:\\.\\d{1,2})?)(\\s?VND|\\s?₫|\\s?đ)?"
        );
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group().trim(); // Trả về kết quả đã tìm được
        }
        return null; // Không tìm thấy số tiền trong thông báo
    }

    private void speak(String message) {
        if (tts != null) {
            // Phát âm thông báo
            tts.speak(message, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    @Override
    public void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}
