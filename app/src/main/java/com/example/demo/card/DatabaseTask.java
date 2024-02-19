package com.example.demo.card;

// Import statements

import android.os.AsyncTask;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseTask extends AsyncTask<Void, Void, Void> {

    @Override
    protected Void doInBackground(Void... voids) {
        // Database connection parameters
        String jdbcUrl = "jdbc:mysql://bj-cynosdbmysql-grp-abgij5yo.sql.tencentcdb.com:29115/pics";
        String user = "admin";
        String password = "Jzc123456";

        // JDBC objects
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            // Register the JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish the connection
            connection = DriverManager.getConnection(jdbcUrl, user, password);

            // SQL query
            String sqlQuery = "SELECT * FROM id";

            // Prepare and execute the query
            preparedStatement = connection.prepareStatement(sqlQuery);
            resultSet = preparedStatement.executeQuery();

            // Process the query results
            while (resultSet.next()) {
                // Assuming 'id' is an integer column, replace with the actual column name
                String id = resultSet.getString("id");
                // Add more variables for other columns if needed

                // Log the result (You can replace this with your preferred logging mechanism)
                Log.d("DatabaseTask", "ID: " + id);
            }

        } catch (ClassNotFoundException | SQLException ex) {
            Log.e("DatabaseTask", "Exception: " + ex.getMessage());
        } finally {
            // Close JDBC objects in the reverse order of their creation to avoid resource leaks
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException ex) {
                Log.e("DatabaseTask", "Exception during cleanup: " + ex.getMessage());
            }
        }

        return null;
    }
}

