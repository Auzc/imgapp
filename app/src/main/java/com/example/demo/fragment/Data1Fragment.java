package com.example.demo.fragment;

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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Data1Fragment extends Fragment {

    private RecyclerView mRecyclerView;
    private StaggeredGridAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private List<Card> mDataInfoList;
    private static final String url = "jdbc:mysql://rm-2ze740g8q9yaf3v06co.mysql.rds.aliyuncs.com:3296/mydesign" +
            "?useUnicode=true&characterEncoding=utf8&useSSL=false";

    // mysql 数据库连接 url
    private static String user = "au";    // 用户名
    private static String password = "Jzc4211315"; // 密码
    private int dataOffset = 0; // 数据偏移量，用于分页查询
    private static final int PAGE_SIZE = 20; // 每页数据数量

    private LoadDataAsyncTask loadDataAsyncTask; // 用于引用异步任务
    private int currentOffset = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_datalist, container, false);

        mRecyclerView = rootView.findViewById(R.id.recyclerView);
        mSwipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);

        // 设置RecyclerView布局管理器
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new StaggeredGridAdapter(requireContext(), 20);
        mRecyclerView.setAdapter(mAdapter);

        // 设置下拉刷新监听器
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 加载数据
                currentOffset = 0; // 重置偏移量
                loadDataFromDatabase();
            }
        });

        // 设置RecyclerView滚动监听
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int[] firstVisibleItems = layoutManager.findFirstVisibleItemPositions(null);
                int pastVisibleItems = firstVisibleItems[0];

                if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                    // 列表滚动到底部
                    if (!mSwipeRefreshLayout.isRefreshing()) {
                        // 加载更多数据
                        loadDataFromDatabase();
                    }
                }
            }
        });

        // 加载数据
        loadDataFromDatabase();

        return rootView;
    }



    private void loadDataFromDatabase() {
        // 创建新的异步任务
        loadDataAsyncTask = new LoadDataAsyncTask();
        // 执行异步任务
        loadDataAsyncTask.execute();
    }

    private void updateUI(List<Card> dataInfoList) {
        if (mDataInfoList == null) {
            mDataInfoList = new ArrayList<>();
        }
        mDataInfoList.addAll(dataInfoList);
        mAdapter.setCards(mDataInfoList);
        mAdapter.notifyDataSetChanged();
        mSwipeRefreshLayout.setRefreshing(false); // 停止刷新状态
    }

    private class LoadDataAsyncTask extends AsyncTask<Void, Void, List<Card>> {

        @Override
        protected List<Card> doInBackground(Void... voids) {
            List<Card> dataInfoList = new ArrayList<>();
            try (Connection connection = DriverManager.getConnection(url, user, password)) {
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT DISTINCT Images.id, Images.url, Images.landmark_id, Images.width, Images.height, Images.author, Images.title\n" +
                                "FROM history_table\n" +
                                "JOIN like_table ON history_table.content_id = like_table.content_id\n" +
                                "JOIN imagesimilarity ON like_table.content_id = imagesimilarity.Image_ID_1\n" +
                                "JOIN Images ON imagesimilarity.Image_ID_2 = Images.id\n" +
                                "LIMIT ? OFFSET ?;"
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

                // 更新当前偏移量
                currentOffset += PAGE_SIZE;

            } catch (Exception e) {
                Log.e("getData", "Error getData", e);
            }
            Collections.shuffle(dataInfoList);
            return dataInfoList;
        }

        @Override
        protected void onPostExecute(List<Card> dataInfoList) {
            super.onPostExecute(dataInfoList);
            updateUI(dataInfoList);
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        // 当Fragment重新获得焦点时，重新加载数据
        loadDataFromDatabase();
    }

}
