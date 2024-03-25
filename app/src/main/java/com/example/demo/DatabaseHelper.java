package com.example.demo;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseHelper {
    private static String url = "jdbc:mysql://rm-2ze740g8q9yaf3v06co.mysql.rds.aliyuncs.com:3296/mydesign"
            + "?useUnicode=true&characterEncoding=utf8";    // mysql 数据库连接 url
    private static String user = "au";    // 用户名
    private static String password = "Jzc4211315"; // 密码

    public static void likeContent(String contentId, String userId, Context context) {
        new LikeAsyncTask(contentId, userId, context).execute();
    }

    private static class LikeAsyncTask extends AsyncTask<Void, Void, Boolean> {

        private String contentId;
        private String userId;
        private Context context;

        public LikeAsyncTask(String contentId, String userId, Context context) {
            this.contentId = contentId;
            this.userId = userId;
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try (Connection connection = DriverManager.getConnection(url, user, password)) {
                String sql = "INSERT INTO Like_Table (user_id, content_id) VALUES (?, ?)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    preparedStatement.setString(1, userId);
                    preparedStatement.setString(2, contentId); // 使用传入的 contentId
                    // 执行插入操作
                    int rowsInserted = preparedStatement.executeUpdate();
                    return rowsInserted > 0;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                // 插入成功，显示成功的 Toast 消息
                //Toast.makeText(context, "点赞成功！", Toast.LENGTH_SHORT).show();
            } else {
                // 插入失败，显示失败的 Toast 消息
                Toast.makeText(context, "点赞失败", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
