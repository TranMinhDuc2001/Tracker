package com.example.subbymqtt;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class SignUpActivity extends AppCompatActivity {
    private final static int RESULT_CODE_SignIn_SignUp = 8;
    EditText IDUser,CCCD,UserName,Password,PhoneNumber,Email,Address;
    private Button btn_signup;

    Connection connection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initUi();
        initListener();
    }

    private void initUi(){
        IDUser = findViewById(R.id.edt_IDUser);
        CCCD = findViewById(R.id.edt_CCCD);
        UserName = findViewById(R.id.edt_UserName);
        PhoneNumber = findViewById(R.id.edt_PhoneNumber);
        Email = findViewById(R.id.edt_Email);
        Address = findViewById(R.id.edt_Address);
        Password = findViewById(R.id.edt_Password);
        btn_signup = findViewById(R.id.btn_signup);
    }
    private void initListener(){
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SignUpActivity.onClickSignUp().execute("");
            }
        });
    }

    public class onClickSignUp extends AsyncTask<String,String,String> {
        String ID_User_TXT = IDUser.getText().toString().trim();
        String CCCD_TXT = CCCD.getText().toString().trim();
        String UserName_TXT = UserName.getText().toString().trim();
        String Password_TXT = Password.getText().toString().trim();
        String PhoneNumber_TXT = PhoneNumber.getText().toString().trim();
        String Email_TXT = Email.getText().toString().trim();
        String Address_TXT = Address.getText().toString().trim();

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
                Toast.makeText(SignUpActivity.this,"Check Internet Connection",Toast.LENGTH_SHORT).show();
            }
            else {
                try {
                    String SQL = "Select * from AccountUser where Email = '" + Email_TXT + "' and Password = '" + Password_TXT + "' ";
                    Statement statement = connection.createStatement();
                    ResultSet rs = statement.executeQuery(SQL);
                    if (!rs.next()) {
                        String SQL_InsertToAccountUser = "INSERT INTO AccountUser(IDUser,CCCD,UserName,PhoneNumber,Email,Password,Address) VALUES" +
                            "('"+ID_User_TXT+"','"+CCCD_TXT+"','"+UserName_TXT+"','"+PhoneNumber_TXT+"','"+Email_TXT+"','"+Password_TXT+"','"+Address_TXT+"')";
                        PreparedStatement statement_InsertData = connection.prepareStatement(SQL_InsertToAccountUser);
                        statement_InsertData.executeUpdate();
                        Intent intentBackSignIn = new Intent(SignUpActivity.this, SignInActivity.class);
                        intentBackSignIn.putExtra("SignUpSuccess","SignUpSuccess");
                        setResult(RESULT_CODE_SignIn_SignUp,intentBackSignIn);
                        finish();
                    } else {
                        Toast.makeText(SignUpActivity.this,"Account Is Already Exist",Toast.LENGTH_SHORT).show();
                        CCCD.setText("");
                        UserName.setText("");
                        PhoneNumber.setText("");
                        Email.setText("");
                        Password.setText("");
                        Address.setText("");
                    }

                } catch (Exception e) {
                    Log.e("SQL ERROR : ", e.getMessage());
                }
            }




            return null;
        }
    }
}