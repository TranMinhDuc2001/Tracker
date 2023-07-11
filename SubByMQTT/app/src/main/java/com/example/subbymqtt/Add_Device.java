package com.example.subbymqtt;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class Add_Device extends AppCompatActivity {
    private final static int RESULT_CODE_ADD_QR = 9;
    EditText IDVih,ActiveSim,PhoneOfSim;
    Button btn_addVehicles,btn_qrcode;
    Connection connection;

    String IDUser_TXT;

    private ActivityResultLauncher<Intent> myActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == RESULT_CODE_ADD_QR){
                        Intent intent = result.getData();
                        if(intent != null){
                            String getDataBack = intent.getStringExtra("QRMessage");
                            IDVih.setText(getDataBack.split(",")[0]);
                            ActiveSim.setText(getDataBack.split(",")[1]);
                            PhoneOfSim.setText(getDataBack.split(",")[2]);
                            Toast.makeText(Add_Device.this, "Received data: " + getDataBack, Toast.LENGTH_SHORT).show();
                        }
                    }
            }
        });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);

        Init();

        btn_qrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentToScanQRCode = new Intent(Add_Device.this,ScanQRCode.class);
                myActivityResultLauncher.launch(intentToScanQRCode);
            }
        });
        btn_addVehicles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new Add_Device.onClickAddVehicles().execute("");

            }
        });
    }


    public void Init(){
        IDVih = (EditText) findViewById(R.id.edt_IDVih);
        ActiveSim = (EditText) findViewById(R.id.edt_ActiveSim);
        PhoneOfSim = (EditText) findViewById(R.id.edt_PhoneOfSim);
        btn_addVehicles = (Button) findViewById(R.id.btn_addVehicles);
        btn_qrcode = (Button) findViewById(R.id.ScanQRCode);
    }


    public class onClickAddVehicles extends AsyncTask<String,String,String>{

        String IDVih_TXT = IDVih.getText().toString().trim();
        String ActiveSim_TXT = ActiveSim.getText().toString().trim();
        String PhoneOfSim_TXT = PhoneOfSim.getText().toString().trim();


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



            Intent intent = getIntent();
            String Email = intent.getStringExtra("Email");
            if(connection == null){
                Toast.makeText(Add_Device.this,"Check Internet Connection",Toast.LENGTH_SHORT).show();
            }
            else{
                try{
                    String SQL = "Select * from Vehicles where IDVih = '"+IDVih_TXT+"' ";
                    Statement statement = connection.createStatement();
                    ResultSet rs = statement.executeQuery(SQL);
                    if(!rs.next()){
                        String SQL_TakeIDUser = "Select IDUser,Email from AccountUser Where Email = '"+Email+"'";
                        ResultSet rsTakeIDUser = statement.executeQuery(SQL_TakeIDUser);
                        if(rsTakeIDUser.next()){
                            IDUser_TXT = rsTakeIDUser.getString("IDUser");
                            Log.d("mapd",IDUser_TXT);
                        }
                        String SQL_InsertToVehicles = "Insert Into Vehicles(IDVih,ActiveSim,Firmware,PhoneOfSim,IDUser) Values"
                                +"('"+IDVih_TXT+"','"+ActiveSim_TXT+"',NULL,'"+PhoneOfSim_TXT+"','"+IDUser_TXT+"')";
                        PreparedStatement statement_InsertToVehicles = connection.prepareStatement(SQL_InsertToVehicles);
                        statement_InsertToVehicles.executeUpdate();
                        Toast.makeText(Add_Device.this, "New Device Has Added", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        IDVih.setText("");
                        ActiveSim.setText("");
                        PhoneOfSim.setText("");
                    }

                }catch (Exception e){
                    Log.e("SQL ERROR : ", e.getMessage());
                }
            }

            return null;
        }
    }
}