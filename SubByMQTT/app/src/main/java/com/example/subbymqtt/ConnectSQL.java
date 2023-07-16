package com.example.subbymqtt;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectSQL {


    Connection connection = null;
    private static String ip = "Your IP Address", port = "1433", db = "Your DataBase Name", username = "Your UserName", password = "Your Password";
    //to find your ip address, first go to cmd then write ipconfig in cmd and press enter Your IP Address is IP V4

    @SuppressLint("NewAPI")
    public Connection ConnectClass() {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");

            String ConnectURL = "jdbc:jtds:sqlserver://" + ip +":"+port+";"
                    + "databaseName=" + db + ";user=" + username + ";password="
                    + password + ";";

            connection = DriverManager.getConnection(ConnectURL);



        } catch (SQLException e) {
            Log.d("mapD", e.getMessage());
        } catch (ClassNotFoundException e) {
            Log.i("mapd", e.getMessage());

        }
        return connection;
    }
}
