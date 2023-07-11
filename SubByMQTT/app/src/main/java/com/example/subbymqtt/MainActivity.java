package com.example.subbymqtt;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private final static int RESULT_CODE_MAIN_MAP = 10;
    String Email,IDUser;
    Button btn_addDevice,btn_getDevice,btn_refresh;
    Connection connection;

    private ListView listView;
    private ArrayList<String> dataList;
    private ArrayAdapter<String> adapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intentGetEmail = getIntent();
        Email = intentGetEmail.getStringExtra("Email");

        initUI();

        btn_addDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(MainActivity.this,Add_Device.class);
                intent1.putExtra("Email",Email);
                startActivity(intent1);
            }
        });

        btn_getDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MainActivity.OnClickGetDevice().execute("");

            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intentToMaps = new Intent(MainActivity.this,MapsActivity.class);
                Log.d("mapD",String.valueOf(position));
                intentToMaps.putExtra("selectedItem",position);
                myActivityResultLauncher.launch(intentToMaps);
            }
        });

        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dataList == null){
                    Toast.makeText(MainActivity.this,"No Data Entry",Toast.LENGTH_SHORT).show();
                }
                else {
                    adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, dataList);
                    adapter.notifyDataSetChanged();
                    listView.setAdapter(adapter);
                }
            }
        });
    }

    private ActivityResultLauncher<Intent> myActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == RESULT_CODE_MAIN_MAP){
                        Intent intent = result.getData();
                        if(intent != null){

                        }
                    }
                }
            });

    private void initUI(){
        btn_addDevice =  findViewById(R.id.btn_addDevice);
        btn_getDevice =  findViewById(R.id.btn_getDevice);
        listView = findViewById(R.id.listView);
        btn_refresh = findViewById(R.id.btn_refresh);
    }

    public class OnClickGetDevice extends AsyncTask<String,String,String>{

        @Override
        protected void onPreExecute(){
        }

        @Override
        protected void onPostExecute(String s){
        }

        @Override
        protected String doInBackground(String... strings) {

            ConnectSQL connectSQL = new ConnectSQL();
            connection = connectSQL.ConnectClass();
            if(connection == null){
                Toast.makeText(MainActivity.this,"Check Internet Connection",Toast.LENGTH_SHORT).show();
            }
            else{
                try{
                    String SQL_TakeIDUser = "Select AccountUser.Email,Vehicles.IDUser from AccountUser Join Vehicles on Vehicles.IDUser = AccountUser.IDUser" +
                            " where AccountUser.Email = '"+Email+"';";
                    Statement statement_TakeIDUser = connection.createStatement();
                    ResultSet rs_TakeIDUser = statement_TakeIDUser.executeQuery(SQL_TakeIDUser);
                    if(rs_TakeIDUser.next()){
                        IDUser = rs_TakeIDUser.getString("IDUser");
                        String SQL_TakeDevice = "Select * from Vehicles where IDUser = '"+IDUser+"';";
                        Statement statement_TakeDevice = connection.createStatement();
                        ResultSet rs_TakeDevice = statement_TakeDevice.executeQuery(SQL_TakeDevice);
                        dataList = new ArrayList<>();
                        while(rs_TakeDevice.next()){
                            dataList.add(rs_TakeDevice.getString("IDVih"));
                        }
//                        adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, dataList);
//                        adapter.notifyDataSetChanged();
//                        listView.setAdapter(adapter);
                    }

                }catch (Exception e){

                }
            }
            return null;
        }
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
    }

}