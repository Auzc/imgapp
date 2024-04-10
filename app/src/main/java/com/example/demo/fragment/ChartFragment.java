package com.example.demo.fragment;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.demo.R;
import com.example.demo.data.Card;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChartFragment extends Fragment {

    private static final String url = "jdbc:mysql://rm-2ze740g8q9yaf3v06co.mysql.rds.aliyuncs.com:3296/mydesign" +
            "?useUnicode=true&characterEncoding=utf8&useSSL=false";
    private static final String user = "au";
    private static final String password = "Jzc4211315";

    private Map<String, Float> chartData;
    private Map<String, Float> chartData2;
    private BarChart barChart;
    private RadarChart radarChart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chart, container, false);

        barChart = rootView.findViewById(R.id.bar_chart);
        radarChart = rootView.findViewById(R.id.radarChart);
        chartData = new HashMap<>();
        chartData2 = new HashMap<>();
        new LoadDataAsyncTask().execute();
        new LoadDataAsyncTask1().execute();
        return rootView;
    }

    private class LoadDataAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try (Connection connection = DriverManager.getConnection(url, user, password)) {
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT like_table.content_id, image_data.type1_inception, image_data.confidence1_inception, " +
                                "image_data.type2_inception, image_data.confidence2_inception, image_data.type3_inception, " +
                                "image_data.confidence3_inception, image_data.type1_resnet, image_data.confidence1_resnet, " +
                                "image_data.type2_resnet, image_data.confidence2_resnet, image_data.type3_resnet, " +
                                "image_data.confidence3_resnet " +
                                "FROM like_table " +
                                "INNER JOIN image_data ON like_table.content_id = image_data.image_id " +
                                "LIMIT 30"
                );

                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    String contentId = resultSet.getString("content_id");
                    String type1Inception = resultSet.getString("type1_inception");
                    float confidence1Inception = resultSet.getFloat("confidence1_inception");
                    String type2Inception = resultSet.getString("type2_inception");
                    float confidence2Inception = resultSet.getFloat("confidence2_inception");
                    String type3Inception = resultSet.getString("type3_inception");
                    float confidence3Inception = resultSet.getFloat("confidence3_inception");
                    String type1Resnet = resultSet.getString("type1_resnet");
                    float confidence1Resnet = resultSet.getFloat("confidence1_resnet");
                    String type2Resnet = resultSet.getString("type2_resnet");
                    float confidence2Resnet = resultSet.getFloat("confidence2_resnet");
                    String type3Resnet = resultSet.getString("type3_resnet");
                    float confidence3Resnet = resultSet.getFloat("confidence3_resnet");

                    // Log database query result
                    Log.d("ChartFragment", "contentId: " + contentId +
                            ", type1Inception: " + type1Inception +
                            ", confidence1Inception: " + confidence1Inception +
                            ", type2Inception: " + type2Inception +
                            ", confidence2Inception: " + confidence2Inception +
                            ", type3Inception: " + type3Inception +
                            ", confidence3Inception: " + confidence3Inception +
                            ", type1Resnet: " + type1Resnet +
                            ", confidence1Resnet: " + confidence1Resnet +
                            ", type2Resnet: " + type2Resnet +
                            ", confidence2Resnet: " + confidence2Resnet +
                            ", type3Resnet: " + type3Resnet +
                            ", confidence3Resnet: " + confidence3Resnet);

                    addToChartData(type1Inception, confidence1Inception);
                    addToChartData(type2Inception, confidence2Inception);
                    addToChartData(type3Inception, confidence3Inception);
                    addToChartData(type1Resnet, confidence1Resnet);
                    addToChartData(type2Resnet, confidence2Resnet);
                    addToChartData(type3Resnet, confidence3Resnet);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                Log.e("ChartFragment", "SQLException: " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // Here you can update your UI with the received data if needed
            // For example, you can display the chart with the chartData
            if (!chartData.isEmpty()) {

                setupRadarChart();
            } else {
                Toast.makeText(getContext(), "No data available", Toast.LENGTH_SHORT).show();
            }
        }

        private void addToChartData(String type, float confidence) {
            if (chartData.containsKey(type)) {
                float existingConfidence = chartData.get(type);
                chartData.put(type, existingConfidence + confidence);
            } else {
                chartData.put(type, confidence);
            }
        }


        private void setupRadarChart() {
            List<Map.Entry<String, Float>> sortedChartData = new ArrayList<>(chartData.entrySet());
            Collections.sort(sortedChartData, (entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

            List<Map.Entry<String, Float>> topEntries = sortedChartData.subList(0, Math.min(sortedChartData.size(), 10)); // 获取前十大的数据
            Collections.shuffle(topEntries); // 对前十大的数据进行打乱

            List<RadarEntry> entries = new ArrayList<>();
            List<String> labels = new ArrayList<>();

            int count = Math.min(topEntries.size(), 10); // Limiting to top 10 entries

            for (int i = 0; i < count; i++) {
                Map.Entry<String, Float> entry = topEntries.get(i);
                entries.add(new RadarEntry(entry.getValue()));
                labels.add(entry.getKey());
            }

            RadarDataSet dataSet = new RadarDataSet(entries, "Confidence");
            int colorValue = getResources().getColor(R.color.syscolor); // 获取颜色值
            dataSet.setColor(colorValue); // 设置颜色
            dataSet.setFillColor(colorValue); // 设置填充颜色

            dataSet.setDrawFilled(true);
            dataSet.setFillAlpha(180);
            dataSet.setLineWidth(2f);

            RadarData radarData = new RadarData(dataSet);
            radarChart.setData(radarData);

            XAxis xAxis = radarChart.getXAxis();
            xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
            xAxis.setTextSize(10f);
            int colorValue2 = getResources().getColor(R.color.RosyBrown); // 获取颜色值
            xAxis.setTextColor(colorValue2);
            YAxis yAxis = radarChart.getYAxis();
            yAxis.setAxisMinimum(0);

            yAxis.setEnabled(false); // 隐藏 Y 轴
            radarChart.getDescription().setEnabled(false);
            radarChart.setDrawWeb(false);
            radarChart.setScaleX(0.9f); // 设置水平缩放比例为80%
            radarChart.setScaleY(0.9f); // 设置垂直缩放比例为80%

            radarChart.animateXY(1000, 1000);
            radarChart.invalidate();
        }

    }
    private class LoadDataAsyncTask1 extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try (Connection connection = DriverManager.getConnection(url, user, password)) {
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT history_table.content_id, image_data.type1_inception, image_data.confidence1_inception, " +
                                "image_data.type2_inception, image_data.confidence2_inception, image_data.type3_inception, " +
                                "image_data.confidence3_inception, image_data.type1_resnet, image_data.confidence1_resnet, " +
                                "image_data.type2_resnet, image_data.confidence2_resnet, image_data.type3_resnet, " +
                                "image_data.confidence3_resnet " +
                                "FROM history_table " +
                                "INNER JOIN image_data ON history_table.content_id = image_data.image_id " +
                                "LIMIT 50"
                );
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    String contentId = resultSet.getString("content_id");
                    String type1Inception = resultSet.getString("type1_inception");
                    float confidence1Inception = resultSet.getFloat("confidence1_inception");
                    String type2Inception = resultSet.getString("type2_inception");
                    float confidence2Inception = resultSet.getFloat("confidence2_inception");
                    String type3Inception = resultSet.getString("type3_inception");
                    float confidence3Inception = resultSet.getFloat("confidence3_inception");
                    String type1Resnet = resultSet.getString("type1_resnet");
                    float confidence1Resnet = resultSet.getFloat("confidence1_resnet");
                    String type2Resnet = resultSet.getString("type2_resnet");
                    float confidence2Resnet = resultSet.getFloat("confidence2_resnet");
                    String type3Resnet = resultSet.getString("type3_resnet");
                    float confidence3Resnet = resultSet.getFloat("confidence3_resnet");

                    // Log database query result
                    Log.d("ChartFragment", "contentId: " + contentId +
                            ", type1Inception: " + type1Inception +
                            ", confidence1Inception: " + confidence1Inception +
                            ", type2Inception: " + type2Inception +
                            ", confidence2Inception: " + confidence2Inception +
                            ", type3Inception: " + type3Inception +
                            ", confidence3Inception: " + confidence3Inception +
                            ", type1Resnet: " + type1Resnet +
                            ", confidence1Resnet: " + confidence1Resnet +
                            ", type2Resnet: " + type2Resnet +
                            ", confidence2Resnet: " + confidence2Resnet +
                            ", type3Resnet: " + type3Resnet +
                            ", confidence3Resnet: " + confidence3Resnet);

                    addToChartData(type1Inception, confidence1Inception);
                    addToChartData(type2Inception, confidence2Inception);
                    addToChartData(type3Inception, confidence3Inception);
                    addToChartData(type1Resnet, confidence1Resnet);
                    addToChartData(type2Resnet, confidence2Resnet);
                    addToChartData(type3Resnet, confidence3Resnet);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                Log.e("ChartFragment", "SQLException: " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // Here you can update your UI with the received data if needed
            // For example, you can display the chart with the chartData
            if (!chartData2.isEmpty()) {
                setupBarChart();

            } else {
                Toast.makeText(getContext(), "No data available", Toast.LENGTH_SHORT).show();
            }
        }

        private void addToChartData(String type, float confidence) {
            if (chartData2.containsKey(type)) {
                float existingConfidence = chartData2.get(type);
                chartData2.put(type, existingConfidence + confidence);
            } else {
                chartData2.put(type, confidence);
            }
        }

        private void setupBarChart() {
            List<Map.Entry<String, Float>> sortedChartData = new ArrayList<>(chartData2.entrySet());
            Collections.sort(sortedChartData, (entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

            List<BarEntry> entries = new ArrayList<>();
            List<String> labels = new ArrayList<>();

            int count = Math.min(sortedChartData.size(), 10); // Limiting to top 10 entries

            Map.Entry<String, Float> entry;
            for (int i = 0; i < count; i++) {
                entry = sortedChartData.get(i);
                entries.add(new BarEntry(i, entry.getValue()));
                labels.add(entry.getKey());
            }

            //Collections.shuffle(entries); // 打乱数据
            BarDataSet dataSet = new BarDataSet(entries, "Confidence");
            int colorValue = getResources().getColor(R.color.syscolor); // 获取颜色值
            dataSet.setColor(colorValue); // 设置颜色


            BarData barData = new BarData(dataSet);
            barChart.setData(barData);

            XAxis xAxis = barChart.getXAxis();
            xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setGranularity(1f);
            xAxis.setGranularityEnabled(true);
            xAxis.setLabelRotationAngle(45);
            xAxis.setLabelCount(labels.size());

            int colorValue2 = getResources().getColor(R.color.RosyBrown); // 获取颜色值
            xAxis.setTextColor(colorValue2);
            YAxis leftAxis = barChart.getAxisLeft();
            leftAxis.setTextColor(colorValue2);
            leftAxis.setAxisMinimum(0);

            barChart.getDescription().setEnabled(false);
            barChart.setDrawGridBackground(false);
            barChart.setFitBars(true);
            barChart.animateY(1000);
            barChart.setScaleX(0.9f); // 设置水平缩放比例为80%
            barChart.setScaleY(0.9f); // 设置垂直缩放比例为80%

            barChart.invalidate();
        }
    }
}
