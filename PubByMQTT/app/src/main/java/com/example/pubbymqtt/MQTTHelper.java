package com.example.pubbymqtt;

import android.content.Context;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;


import info.mqtt.android.service.Ack;
import info.mqtt.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;



public class MQTTHelper extends AppCompatActivity {
    int successCount = 0;
    String sp_Longitude,sp_Latitude,sp_Local;
    private static final String TAG = "MQTT";


    public MqttAndroidClient mqttAndroidClient;

    final String clientId = "ductran143 ";
    final String username = "ductran143";
    final String password = "aio_RZJk91JHEtENAre4WuApti2yrjo9";//thay doi

    final String serverUri = "tcp://io.adafruit.com:1883";//thay doi





    public MQTTHelper(Context context) {
        mqttAndroidClient = new MqttAndroidClient(context, serverUri, clientId, Ack.AUTO_ACK);
        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
                Log.w("mqtt", s);
            }

            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) {
                Log.d(TAG, mqttMessage.toString());
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
        connect();
    }

    public void setCallback(MqttCallbackExtended callback) {
        mqttAndroidClient.setCallback(callback);
    }

    private void connect() {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);
        mqttConnectOptions.setUserName(username);
        mqttConnectOptions.setPassword(password.toCharArray());

        mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
            @Override

            public void onSuccess(IMqttToken asyncActionToken) {
                successCount++;
                if (successCount == 1) {
                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
                    Log.d("MQTT","Success");
                    pub(sp_Local+","+GetTimestamp());
                }
            }
            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                successCount++;
                if (successCount == 1) {
                    Log.w(TAG, "Failed to connect to: " + serverUri + exception.toString());
                }
            }
        });
    }
    public void receiveData(double g_Longitude,double g_Latitude){
       sp_Longitude = String.valueOf(g_Longitude);
       sp_Latitude = String.valueOf((g_Latitude));
       sp_Local = sp_Latitude + "," + sp_Longitude;
       Log.d("Mapd",sp_Local);
    }
    void pub(String content){
        String topic = "ductran143/feeds/device2";
        byte[] encodedPayload;
        try {
            encodedPayload = content.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            mqttAndroidClient.publish(topic,message);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private String GetTimestamp(){
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        return (hour+":"+minute+":"+second);
    }
}
