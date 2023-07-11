package com.example.subbymqtt;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.List;

import info.mqtt.android.service.MqttAndroidClient;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private final static int RESULT_CODE_MAIN_MAP = 10;
    double dLat,dLong;
    Button btn_back,btn_stop;
    public MqttAndroidClient mqttAndroidClient;
    String sLongitude,sLatitude;
    MQTTHelper mqttHelper;
    int iTopic_index;

    private MapView mapView;
    private Polyline polyline;

    private GoogleMap mMap;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mapView = findViewById(R.id.map);
        btn_back = findViewById(R.id.btn_back);
        btn_stop = findViewById(R.id.btn_Stop);

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(MapsActivity.this);



        Intent intent = getIntent();
        iTopic_index = intent.getIntExtra("selectedItem",-1);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(MapsActivity.this,MainActivity.class);
                setResult(RESULT_CODE_MAIN_MAP,intent1);
                finish();
            }
        });
        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mqttHelper.disconnect();
                Toast.makeText(MapsActivity.this, "Disconnected", Toast.LENGTH_SHORT).show();
            }
        });

        startMQTT();
    }




    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }


    public void startMQTT(){
        mqttHelper = new MQTTHelper(this);
        mqttHelper.ReceiveData(iTopic_index);
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

            }

            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                Log.d("Mapd",topic + "***" + message.toString());
                sLongitude = message.toString().split(",")[1];
                sLatitude = message.toString().split(",")[0];
                dLong = Double.parseDouble(sLongitude);
                dLat = Double.parseDouble(sLatitude);
                try {
                    LatLng myLocation = new LatLng(dLat, dLong);
                    mMap.addMarker(new MarkerOptions().position(myLocation).title("This is your device location"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));

//                    PolylineOptions polylineOptions = new PolylineOptions();
//                    polylineOptions.color(Color.BLUE);
//                    polylineOptions.width(5);
//                    polylineOptions.add(myLocation);
//                    polyline = mMap.addPolyline(polylineOptions);

                }
                catch (SecurityException e ){
                    e.printStackTrace();
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }


    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

}