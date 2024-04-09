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
import com.example.demo.data.Card;
import com.example.demo.adapter.StaggeredGridAdapter;

//import java.sql.Connection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public class LikeFragment extends Fragment {

    private List<Card> mylist = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private StaggeredGridAdapter mAdapter;
    private List<Card> mCards;

    private static final String url = "jdbc:mysql://rm-2ze740g8q9yaf3v06co.mysql.rds.aliyuncs.com:3296/mydesign" +
            "?useUnicode=true&characterEncoding=utf8&useSSL=false";
    private static String user = "au";    // 用户名
    private static String password = "Jzc4211315"; // 密码
    private boolean isLoading = false;

    private Stack<Card> cardStack = new Stack<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private int currentOffset = new Random().nextInt(2000);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_like, container, false);
        mRecyclerView = view.findViewById(R.id.recyclerView);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        int space = 20;
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new StaggeredGridAdapter(requireContext(), space);
        mRecyclerView.setAdapter(mAdapter);

        setupRecyclerViewScrollListener();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 将 AsyncTask 的执行移到这里
                new LikeFragment.InfoAsyncTask().execute();
            }
        });

        // 只有在堆栈为空时才执行 AsyncTask
        if (cardStack.isEmpty()) {
            new LikeFragment.InfoAsyncTask().execute();
        }

        return view;
    }

    public List<Card> getData(int offset, int limit) {
        List<Card> dataInfoList = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, user, password)) {

            PreparedStatement statement = connection.prepareStatement(
                    "SELECT i.id, i.url, i.landmark_id, i.width, i.height, i.author, " +
                            "SUBSTRING(LEFT(i.title, LENGTH(i.title) - 4), 6) AS title " +
                            "FROM Images i " +
                            "INNER JOIN like_table h ON i.id = h.content_id " +
                            "WHERE h.user_id = ?"
            );
            statement.setString(1, "user123");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String url = resultSet.getString("url");
                String landmarkId = resultSet.getString("landmark_id");
                int width = resultSet.getInt("width");
                int height = resultSet.getInt("height");
                String author = resultSet.getString("author");
                String title = resultSet.getString("title");

                Card sizeInfo = new Card(title, author, id, url, width, height, landmarkId);
                dataInfoList.add(sizeInfo);
            }
        }catch (Exception e) {
            Log.e("getData", "Error getData", e);
        }
        return dataInfoList;
}

@SuppressLint("StaticFieldLeak")
public class InfoAsyncTask extends AsyncTask<Void, Void, List<Card>> {

    @Override
    protected List<Card> doInBackground(Void... voids) {
        List<Card> stackItems = new ArrayList<>();
        if (cardStack.isEmpty()) {
            cardStack.addAll(getData(currentOffset, 10));
            currentOffset += 10; // Update the offset for the next query
        }
        int itemsToLoad = Math.min(10, cardStack.size());
        for (int i = 0; i < itemsToLoad; i++) {
            stackItems.add(cardStack.pop());
        }

        return stackItems;
    }

    @Override
    protected void onPostExecute(List<Card> stackItems) {
        updateDataAndRefresh(stackItems);
        isLoading = false;
        swipeRefreshLayout.setRefreshing(false);
    }
}



    private void updateDataAndRefresh(List<Card> sizeInfoList) {

        // If loading more data, add to existing data
        if (isLoading) {
            // Iterate over new data, add only unique items
            for (Card newItem : sizeInfoList) {
                if (!mylist.contains(newItem)) {
                    mylist.add(newItem);
                }
            }
        } else {
            // Otherwise, replace existing data
            mylist.clear();
            mylist.addAll(sizeInfoList);
        }

        // Generate new mock data and update adapter
        mCards = generateMockData();
        mAdapter.setCards(mCards);

        // Notify adapter that data has changed
        mAdapter.notifyDataSetChanged();
    }

    private List<Card> generateMockData() {
        List<Card> mockData = new ArrayList<>();
        mockData.addAll(mylist);

        // If loading more, add loading item
        if (isLoading) {
            mockData.add(null);
        }

        return mockData;
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

                if (!isLoading && lastVisibleItem == mCards.size() - 1 && !swipeRefreshLayout.isRefreshing()) {
                    // Start loading more data
                    isLoading = true;
                    new LikeFragment.InfoAsyncTask().execute();
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
}
