package com.example.banknotireader;


import android.graphics.Color;
import android.os.Bundle;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.*;
import com.github.mikephil.charting.data.*;

import java.util.*;

public class ChartFragment extends Fragment {

    private LineChart lineChart;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chart, container, false);
        lineChart = view.findViewById(R.id.bar_chart);

        drawDummyData();

        return view;
    }

    private void drawDummyData() {
        List<Entry> entries = new ArrayList<>();
        for (int i = 1; i <= 7; i++) {
            entries.add(new Entry(i, (float) (Math.random() * 100000)));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Doanh thu 7 ngày");
        dataSet.setColor(Color.BLUE);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setCircleRadius(4f);
        dataSet.setLineWidth(2f);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.invalidate();
    }
}
