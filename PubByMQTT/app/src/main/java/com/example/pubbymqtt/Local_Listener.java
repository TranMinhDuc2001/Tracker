package com.example.pubbymqtt;


import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class Local_Listener extends AppCompatActivity implements LocationListener {
    public static double g_longitude,g_latitude;
    TextView txt1, txt2;
    LocationManager locationManager;
    MQTTHelper mqttHelper;
    Button btnConnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Location","onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_listener);
        //gán
        txt1 = (TextView) findViewById(R.id.txt1);
        txt2 = (TextView) findViewById(R.id.txt2);

        btnConnect = (Button)findViewById(R.id.btnConnect);
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(Local_Listener.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Local_Listener.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},100);
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, this);
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {
        // cap nhat vi tri vao textview
        g_longitude = location.getLongitude();
        g_latitude = location.getLatitude();
        txt1.setText(String.valueOf(g_longitude));
        Log.d("Longitude", String.valueOf(g_longitude));
        txt2.setText(String.valueOf(g_latitude));
        Log.d("Latitude", String.valueOf(g_latitude));
        startMQTT();
    }
    @Override
    public void onFlushComplete(int requestCode) {
        LocationListener.super.onFlushComplete(requestCode);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
    }
    public void startMQTT(){
        mqttHelper = new MQTTHelper(this);
        mqttHelper.receiveData(g_longitude,g_latitude);
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

            }

            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                Log.d("mqtt1",topic + "***" + message.toString());
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }


}
