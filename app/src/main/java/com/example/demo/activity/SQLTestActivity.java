package com.example.demo.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.demo.R;
import com.example.demo.data.Card;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SQLTestActivity extends AppCompatActivity {

    private static String url = "jdbc:mysql://rm-2ze740g8q9yaf3v06co.mysql.rds.aliyuncs.com:3296/mydesign"
            + "?useUnicode=true&characterEncoding=utf8&useSSL=false";
    // mysql 数据库连接 url
    private static String user = "au";    // 用户名
    private static String password = "Jzc4211315"; // 密码
    private  Card card;
    private String cardId;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sql_test);
        btn = findViewById(R.id.btn);
        cardId = getIntent().getStringExtra("card_id");
        new DatabaseTask().execute();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 在点击按钮时，执行 AsyncTask 来执行数据库操作
                Toast.makeText(SQLTestActivity.this,card.getImg_url(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 创建 AsyncTask 来执行数据库操作
    private class DatabaseTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try (Connection connection1 = DriverManager.getConnection(url, user, password)) {
                PreparedStatement statement = connection1.prepareStatement("SELECT * FROM Images WHERE id = ?");
                statement.setString(1, cardId);
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
                    card = new Card(title,author,id,url,width,height,landmarkId);

                    // 在后台线程中不能直接更新 UI，因此使用 publishProgress 来更新 UI
//                    publishProgress(card);
                }
            } catch (Exception e) {
                Log.e("getData", "Error getData", e);
            }
            return null;
        }

//        private void publishProgress(Card card) {
//
//            Toast.makeText(SQLTestActivity.this,card.getImg_url(), Toast.LENGTH_SHORT).show();
//
//        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            // 在 UI 线程中显示 Toast，因为这个方法会在主线程中执行
            Toast.makeText(SQLTestActivity.this, values[0].toString(), Toast.LENGTH_SHORT).show();
        }
    }


}
