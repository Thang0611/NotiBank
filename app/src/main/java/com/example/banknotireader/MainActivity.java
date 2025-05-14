package com.example.banknotireader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.banknotireader.models.Transaction;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Switch switchListener;
    private RecyclerView recyclerView;
    private TransactionAdapter adapter;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        switchListener = findViewById(R.id.switchListener);
        recyclerView = findViewById(R.id.recyclerView);

        dbHelper = new DBHelper(this);
        adapter = new TransactionAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        boolean isEnabled = prefs.getBoolean("listener_enabled", false);
        switchListener.setChecked(isEnabled);

        // Kiểm tra nếu đây là lần đầu tiên mở ứng dụng
        SharedPreferences firstRunPrefs = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        boolean isFirstRun = firstRunPrefs.getBoolean("isFirstRun", true);

        if (isFirstRun) {
            // Kiểm tra quyền thông báo
            if (!isNotificationPermissionGranted()) {
                // Nếu chưa cấp quyền, yêu cầu người dùng cấp quyền
                requestNotificationPermission();
            }

            // Đánh dấu ứng dụng đã chạy lần đầu
            SharedPreferences.Editor editor = firstRunPrefs.edit();
            editor.putBoolean("isFirstRun", false);
            editor.apply();
        }

        switchListener.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("listener_enabled", isChecked);

            editor.apply();
        });

        loadTransactions();
        LocalBroadcastManager.getInstance(this).registerReceiver(
                transactionReceiver, new IntentFilter("com.example.banknotireader.NEW_TRANSACTION")
        );
    }
    private final BroadcastReceiver transactionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadTransactions(); // Gọi lại DB và cập nhật danh sách
        }
    };
    private void loadTransactions() {
        List<Transaction> transactions = dbHelper.getAllTransactions();
        adapter.setTransactions(transactions);
    }

    private boolean isNotificationPermissionGranted() {
        // Kiểm tra nếu quyền thông báo đã được cấp
        return android.provider.Settings.Secure.getString(getContentResolver(), "enabled_notification_listeners")
                .contains(getPackageName());
    }

    private void requestNotificationPermission() {
        // Yêu cầu người dùng cấp quyền đọc thông báo
        Toast.makeText(this, "Please enable notification access", Toast.LENGTH_LONG).show();
        startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
    }
}
