package com.example.banknotireader;


import android.content.*;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.view.*;
import android.widget.Switch;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.*;
import com.example.banknotireader.models.Transaction;
import java.util.*;

public class MainFragment extends Fragment {
    private Switch switchListener;
    private RecyclerView recyclerView;
    private TransactionAdapter adapter;
    private DBHelper dbHelper;
    private TextToSpeech tts;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        dbHelper = new DBHelper(requireContext());
        tts = new TextToSpeech(getContext(), status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(new Locale("vi", "VN"));
            }
        });

        switchListener = view.findViewById(R.id.switchListener);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TransactionAdapter();
        recyclerView.setAdapter(adapter);

        SharedPreferences prefs = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        boolean isEnabled = prefs.getBoolean("listener_enabled", false);
        switchListener.setChecked(isEnabled);

        // First run
        SharedPreferences firstRunPrefs = requireContext().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        if (firstRunPrefs.getBoolean("isFirstRun", true)) {
            if (!isNotificationPermissionGranted()) {
                requestNotificationPermission();
            }
            firstRunPrefs.edit().putBoolean("isFirstRun", false).apply();
        }

        switchListener.setOnCheckedChangeListener((btn, isChecked) -> {
            prefs.edit().putBoolean("listener_enabled", isChecked).apply();
            String msg = isChecked ? "Đã bật thông báo chuyển khoản" : "Đã tắt thông báo chuyển khoản";
            tts.speak(msg, TextToSpeech.QUEUE_ADD, null, null);
        });

        loadTransactions();

        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
                transactionReceiver, new IntentFilter("com.example.banknotireader.NEW_TRANSACTION"));

        return view;
    }

    private final BroadcastReceiver transactionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadTransactions();
        }
    };

    private void loadTransactions() {
        List<Transaction> transactions = dbHelper.getAllTransactions();
        adapter.setTransactions(transactions);
    }

    private boolean isNotificationPermissionGranted() {
        return Settings.Secure.getString(requireContext().getContentResolver(),
                "enabled_notification_listeners").contains(requireContext().getPackageName());
    }

    private void requestNotificationPermission() {
        Toast.makeText(getContext(), "Please enable notification access", Toast.LENGTH_LONG).show();
        startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
    }
}
