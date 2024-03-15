package com.example.demo;

import android.os.Bundle;

import android.view.View;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.sql.SQLException;



public class CardDetailActivity extends AppCompatActivity {
    ImageView back_btn;

    private static String url = "jdbc:mysql://rm-2ze740g8q9yaf3v06co.mysql.rds.aliyuncs.com:3296/mydesign"
            + "?useUnicode=true&characterEncoding=utf8";    // mysql 数据库连接 url
    private static String user = "au";    // 用户名
    private static String password = "Jzc4211315"; // 密码
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_detail);

        // 获取传递的卡片ID
        String cardId = getIntent().getStringExtra("card_id");
        loadCardDetailFromDatabase(cardId);
        if (!cardId.equals("")) {

        }
        Toast.makeText(CardDetailActivity.this, cardId, Toast.LENGTH_SHORT).show();
        back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
    private void loadCardDetailFromDatabase(String cardId) {

        try (Connection connection = DriverManager.getConnection(url, user, password)) {

            // 准备 SQL 查询语句
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM Images WHERE id = ?");
            statement.setString(1, cardId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {

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

                Toast.makeText(this, "ID: " + id + ", URL: " + url + ", Landmark ID: " + landmarkId + ", Width: " + width + ", Height: " + height + ", Author: " + author + ", Title: " + title, Toast.LENGTH_SHORT).show();
                updateUI(width, height, url, author, title);
            }


        } catch (SQLException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();

            e.printStackTrace();
        }
    }

    // 更新 UI
    private void updateUI(int width, int height, String url, String author, String title) {
        ImageView imageView = findViewById(R.id.imageView);
        TextView  card_author = findViewById(R.id.card_author);
        card_author.setText(author);
        Glide.with(this)
                .load(url)
                .error(R.drawable.rounded_box) // 加载失败显示的图像
                .into(imageView);
//        cardDetailTextView.setText("Width: " + width + "\n" +
//                "Height: " + height + "\n" +
//                "URL: " + url + "\n" +
//                "Author: " + author + "\n" +
//                "Title: " + title);
    }

}