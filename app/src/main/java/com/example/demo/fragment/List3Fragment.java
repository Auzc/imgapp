package com.example.demo.fragment;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.demo.R;
import com.example.demo.adapter.StaggeredGridAdapter;
import com.example.demo.data.Card;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public class List3Fragment extends Fragment {

    private RecyclerView mRecyclerView;
    private StaggeredGridAdapter mAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<Card> mylist = new ArrayList<>();
    private Stack<Card> cardStack = new Stack<>();
    private boolean isLoading = false;
    private int currentOffset = 0;
    private static final int PAGE_SIZE = 10; // Number of items to load per page

    private static final String url = "jdbc:mysql://rm-2ze740g8q9yaf3v06co.mysql.rds.aliyuncs.com:3296/mydesign" +
            "?useUnicode=true&characterEncoding=utf8&useSSL=false";  // mysql 数据库连接 url
    private static String user = "au";    // 用户名
    private static String password = "Jzc4211315"; // 密码

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_datalist, container, false);
        mRecyclerView = view.findViewById(R.id.recyclerView);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        // 获取当前日期
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // Month starts from 0, so adding 1 to match normal convention
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // 将年月日转换为偏移量
        currentOffset = convertDateToOffset(year, month, day);

        // 加载数据
        loadDataFromDatabase();

        int space = 20;
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new StaggeredGridAdapter(requireContext(), space);
        mRecyclerView.setAdapter(mAdapter);

        setupRecyclerViewScrollListener();

        swipeRefreshLayout.setOnRefreshListener(() -> {
            currentOffset = convertDateToOffset(year, month, day);
            loadDataFromDatabase();
        });

        // Load data initially
        loadDataFromDatabase();

        return view;
    }
    // 方法：将年月日转换为偏移量，并添加随机数
    private int convertDateToOffset(int year, int month, int day) {
        // 假设一页加载 10 条数据
        int pageSize = 10;
        // 计算基础偏移量
        int baseOffset = (year - 2000) * 12 * 30 + (month - 1) * 30 + day * pageSize;
        // 生成随机数（1到1000之间）
        int randomOffset = new SecureRandom().nextInt(999) + 1; // 保证随机数不为0

        int re = (baseOffset * randomOffset) % 2000000;
        // 返回基础偏移量除以随机数
        return re;
    }

    private void loadDataFromDatabase() {
        if (!isLoading) {
            new LoadDataAsyncTask().execute();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class LoadDataAsyncTask extends AsyncTask<Void, Void, List<Card>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isLoading = true;
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected List<Card> doInBackground(Void... voids) {
            List<Card> dataInfoList = new ArrayList<>();
            try (Connection connection = DriverManager.getConnection(url, user, password)) {
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT * FROM Images LIMIT ? OFFSET ?"
                );
                statement.setInt(1, PAGE_SIZE);
                statement.setInt(2, currentOffset);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    String id = resultSet.getString("id");
                    String url = resultSet.getString("url");
                    String landmarkId = resultSet.getString("landmark_id");
                    int width = resultSet.getInt("width");
                    int height = resultSet.getInt("height");
                    String author = resultSet.getString("author");
                    String title = resultSet.getString("title");
                    if (title.length() > 5) {
                        title = title.substring(5);
                    }
                    if (title.length() > 4) {
                        title = title.substring(0, title.length() - 4);
                    }
                    Card sizeInfo = new Card(title, author, id, url, width, height, landmarkId);
                    dataInfoList.add(sizeInfo);
                }
            } catch (Exception e) {
                Log.e("getData", "Error getData", e);
            }
            return dataInfoList;
        }

        @Override
        protected void onPostExecute(List<Card> dataInfoList) {
            super.onPostExecute(dataInfoList);
            isLoading = false;
            swipeRefreshLayout.setRefreshing(false);
            currentOffset += PAGE_SIZE;
            updateUI(dataInfoList);
        }
    }

    private void updateUI(List<Card> dataInfoList) {
        mylist.addAll(dataInfoList);
        mAdapter.setCards(mylist);
        mAdapter.notifyDataSetChanged();
    }

    private void setupRecyclerViewScrollListener() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) recyclerView.getLayoutManager();

                // Check if scrolled to the bottom and not refreshing
                int[] lastVisibleItemPositions = layoutManager.findLastVisibleItemPositions(null);
                int lastVisibleItem = getLastVisibleItem(lastVisibleItemPositions);

                if (!isLoading && lastVisibleItem == mylist.size() - 1 && !swipeRefreshLayout.isRefreshing()) {
                    // Start loading more data
                    loadDataFromDatabase();
                }
            }
        });
    }

    private int getLastVisibleItem(int[] lastVisibleItemPositions) {
        int max = lastVisibleItemPositions[0];
        for (int position : lastVisibleItemPositions) {
            if (position > max) {
                max = position;
            }
        }
        return max;
    }
    @Override
    public void onResume() {
        super.onResume();
        // 当Fragment重新获得焦点时，重新加载数据
        loadDataFromDatabase();
    }


}
