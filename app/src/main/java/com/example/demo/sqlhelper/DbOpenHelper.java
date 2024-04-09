package com.example.demo.sqlhelper;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DbOpenHelper {

    private static String driver = "com.mysql.jdbc.Driver";// mysql 驱动

    private static String url = "jdbc:mysql://rm-2ze740g8q9yaf3v06co.mysql.rds.aliyuncs.com:3296/mydesign"
            + "?useUnicode=true&characterEncoding=utf8";    // mysql 数据库连接 url
    private static String user = "au";    // 用户名
    private static String password = "Jzc4211315"; // 密码

    private static Connection sConnection;

    /**
     * 连接数据库
     */
    public static Connection getConnection() {
        if (sConnection == null) {
            try {
                Class.forName(driver);  // 获取 mysql 驱动
                sConnection = DriverManager.getConnection(url, user, password);   // 获取连接
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return sConnection;
    }

    /**
     * 关闭数据库
     */
    public static void closeConnection() {
        if (sConnection != null) {
            try {
                sConnection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
