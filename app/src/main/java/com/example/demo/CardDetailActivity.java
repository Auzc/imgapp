package com.example.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.mysql.jdbc.Connection;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CardDetailActivity extends AppCompatActivity {
    ImageView back_btn;
    private Connection connection;
    private String jdbcUrl = "jdbc:mysql://bj-cynosdbmysql-grp-abgij5yo.sql.tencentcdb.com:29115/train";
    private String user = "admin";
    private String password = "Jzc123456";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_detail);

        // 获取传递的卡片ID
        String cardId = getIntent().getStringExtra("card_id");
        if (!cardId.equals("")) {
            loadCardDetailFromDatabase(cardId);
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
        try {
            // 连接到数据库
            connection = (Connection) DriverManager.getConnection(jdbcUrl, user, password);

            // 准备 SQL 查询语句
            String sql = "SELECT width, height, url_x, author, title FROM myData WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, cardId);

            // 执行查询
            ResultSet resultSet = statement.executeQuery();

            // 更新 UI
            if (resultSet.next()) {
                int width = resultSet.getInt("width");
                int height = resultSet.getInt("height");
                String url = resultSet.getString("url_x");
                String author = resultSet.getString("author");
                String title = resultSet.getString("title");
                Toast.makeText(CardDetailActivity.this, author, Toast.LENGTH_SHORT).show();
                updateUI(width, height, url, author, title);
            }

            // 关闭连接
            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
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