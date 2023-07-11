package com.example.subbymqtt;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class SignInActivity extends AppCompatActivity {
    private final static int RESULT_CODE_SignIn_SignUp = 8;
    private LinearLayout layoutSignUp;
    private Button btn_SignIn;
    EditText Email,Password;
    Boolean isSuccess = false;
    Connection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        Email = findViewById(R.id.edt_user);
        Password = findViewById(R.id.edt_pass);
        btn_SignIn = findViewById(R.id.btn_signin);
        layoutSignUp = findViewById(R.id.layout_sign_up);


        SignUp();



        btn_SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isConnected()) {
                    new SignInActivity.checkSignin().execute("");
                }
                else{
                    Toast.makeText(SignInActivity.this,"No Internet Connection",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public class checkSignin extends AsyncTask<String, String, String>{

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
                Toast.makeText(SignInActivity.this,"Check Internet Connection",Toast.LENGTH_SHORT).show();
            }
            else {
                try {
                    String SQL = "Select * from AccountUser where Email = '" + Email.getText() + "' and Password = '" + Password.getText() + "' ";
                    Statement statement = connection.createStatement();
                    ResultSet rs = statement.executeQuery(SQL);
                    if (rs.next()) {
                        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                        intent.putExtra("Email",Email.getText().toString());
                        startActivity(intent);
                    } else {
                        Email.setText("");
                        Password.setText("");
                    }

                } catch (Exception e) {
                    isSuccess = false;
                    Log.e("SQL ERROR : ", e.getMessage());
                }
            }
            return null;
        }
    }
    private ActivityResultLauncher<Intent> myActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == RESULT_CODE_SignIn_SignUp){
                        Intent intent = result.getData();
                        if(intent != null){
                            Toast.makeText(SignInActivity.this,"Sign Up Success",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });


    public void SignUp(){
        layoutSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isConnected()) {
                    Intent intentToSignUp = new Intent(SignInActivity.this, SignUpActivity.class);
                    myActivityResultLauncher.launch(intentToSignUp);
                }
                else{
                    Toast.makeText(SignInActivity.this,"No Internet Connection",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
        return false;
    }

}