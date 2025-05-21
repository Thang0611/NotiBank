package com.NT.banknotireader;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;



public class SettingsFragment extends Fragment {

    private Switch switchNotifyInDnd, switchDuckOtherApps, switchBoostVolume;
    private RadioGroup radioGroupOutput;
    private AudioManager audioManager;

    private LinearLayout layoutVoiceSettings, layoutNotificationAccess;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        SharedPreferences prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        // Khởi tạo AudioManager
        audioManager = (AudioManager) requireContext().getSystemService(Context.AUDIO_SERVICE);

        // Ánh xạ Switch
//        switchNotifyInDnd = view.findViewById(R.id.switch_notify_in_dnd);
        switchDuckOtherApps = view.findViewById(R.id.switch_duck_other_apps);
        switchBoostVolume = view.findViewById(R.id.switch_boost_volume);

        // Ánh xạ RadioGroup
        radioGroupOutput = view.findViewById(R.id.radio_output);
        radioGroupOutput.check(R.id.radio_ringtone);
        // Ánh xạ các mục cài đặt giọng nói và truy cập thông báo
        layoutVoiceSettings = view.findViewById(R.id.layout_voice_settings);
        layoutNotificationAccess = view.findViewById(R.id.layout_notification_access);

        // Xử lý: Cài đặt giọng nói
        layoutVoiceSettings.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction("com.android.settings.TTS_SETTINGS");
            startActivity(intent);
        });

        // Xử lý: Quyền truy cập thông báo
        layoutNotificationAccess.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
            startActivity(intent);
        });

        // 1. Cho phép phát thông báo khi đang ở chế độ im lặng (DND)
//        switchNotifyInDnd.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            if (isChecked && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
//                if (!notificationManager.isNotificationPolicyAccessGranted()) {
//                    Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
//                    startActivity(intent);
//                    Toast.makeText(getContext(), "Cần cấp quyền Không làm phiền", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(getContext(), "Đã bật phát thông báo trong chế độ im lặng", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });


        // 2. Giảm âm lượng ứng dụng khác khi phát thông báo
        switchDuckOtherApps.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Ghi nhớ trạng thái cho TTS hoặc MediaPlayer sau này
            if (isChecked) {
                Toast.makeText(getContext(), "Âm lượng ứng dụng khác sẽ bị giảm khi phát thông báo", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Tắt giảm âm lượng ứng dụng khác", Toast.LENGTH_SHORT).show();
            }
        });

        // 3. Tăng âm lượng tối đa khi phát thông báo
        switchBoostVolume.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                audioManager.setStreamVolume(
                        AudioManager.STREAM_MUSIC,
                        audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                        0
                );
                Toast.makeText(getContext(), "Đã tăng âm lượng tối đa", Toast.LENGTH_SHORT).show();
            }
        });
        int streamType=prefs.getInt("audio_output_stream",AudioManager.STREAM_RING);
        if(streamType==AudioManager.STREAM_NOTIFICATION){
            radioGroupOutput.check(R.id.radio_notification);
        }
        else if(streamType==AudioManager.STREAM_MUSIC){
            radioGroupOutput.check(R.id.radio_media);

        }else if(streamType==AudioManager.STREAM_RING){
            radioGroupOutput.check(R.id.radio_ringtone);

        }
// 4. Xử lý chọn đầu ra âm thanh
        radioGroupOutput.setOnCheckedChangeListener((group, checkedId) -> {

            int _streamType =prefs.getInt("audio_output_stream",AudioManager.STREAM_RING);

            if (checkedId == R.id.radio_notification) {
                _streamType = AudioManager.STREAM_NOTIFICATION;
            } else if (checkedId == R.id.radio_media) {
                _streamType = AudioManager.STREAM_MUSIC;
            } else if (checkedId == R.id.radio_ringtone) {
                _streamType = AudioManager.STREAM_RING;
            }

            Toast.makeText(getContext(), "Đã chọn đầu ra: " + streamTypeToString(_streamType), Toast.LENGTH_SHORT).show();

            prefs.edit().putInt("audio_output_stream", _streamType).apply();
        });
        return view;
    }
    // Hàm tiện ích chuyển stream type sang tên dễ đọc
    private String streamTypeToString(int streamType) {
        switch (streamType) {
            case AudioManager.STREAM_NOTIFICATION: return "Thông báo";
            case AudioManager.STREAM_MUSIC: return "Media";
            case AudioManager.STREAM_RING: return "Nhạc chuông";
            default: return "Không xác định";
        }
    }
}
