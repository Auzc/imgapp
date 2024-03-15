package com.example.demo;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.demo.R;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLTestActivity extends AppCompatActivity {

    private String jdbcUrl = "jdbc:mysql://bj-cynosdbmysql-grp-abgij5yo.sql.tencentcdb.com:29115/train";
    private String user = "admin";
    private String password = "Jzc123456";
    private String userId = "user123";
    private String contentId = "content456";
    String cardId;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sql_test);
        btn = findViewById(R.id.btn);
        cardId = getIntent().getStringExtra("card_id");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LikeAsyncTask().execute();
            }
        });

// 获取传递的卡片ID

        // 在这里调用 AsyncTask 来执行数据库操作

    }

    private class LikeAsyncTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            try (Connection connection = DriverManager.getConnection(jdbcUrl, user, password)) {
                String sql = "SELECT width, height, url_x, author, title FROM myData WHERE id = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, cardId);
                ResultSet resultSet = preparedStatement.executeQuery();

                // 通过 Toast 打印结果
                if (resultSet.next()) {
                    String width = resultSet.getString("width");
                    String height = resultSet.getString("height");
                    String url_x = resultSet.getString("url_x");
                    String author = resultSet.getString("author");
                    String title = resultSet.getString("title");
                    String message = "Width: " + width + ", Height: " + height ;
                    Toast.makeText(SQLTestActivity.this, message, Toast.LENGTH_SHORT).show();
                }

                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }


    }

}
