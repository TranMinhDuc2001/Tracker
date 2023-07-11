package com.example.subbymqtt;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectSQL {


    Connection connection = null;
    private static String ip = "10.0.0.183", port = "1433", db = "Tracking", username = "sa", password = "1";

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
