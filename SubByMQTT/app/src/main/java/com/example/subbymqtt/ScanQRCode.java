package com.example.subbymqtt;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class ScanQRCode extends AppCompatActivity {
    private final static int RESULT_CODE_ADD_QR = 9;
    private PreviewView previewView;
    String getValues;
    private ListenableFuture<ProcessCameraProvider> cameraProviderListenableFuture;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qrcode);

        previewView = findViewById(R.id.Camera);


        //check camera permission
        if(ContextCompat.checkSelfPermission(ScanQRCode.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            init();
        }
        else{
            ActivityCompat.requestPermissions(ScanQRCode.this,new String[]{Manifest.permission.CAMERA},1);
        }
    }
    private void init(){
        cameraProviderListenableFuture = ProcessCameraProvider.getInstance(ScanQRCode.this);
        cameraProviderListenableFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderListenableFuture.get();
                    bindImageAnalysis(cameraProvider);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        },ContextCompat.getMainExecutor(ScanQRCode.this));
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[]grantResult){
        super.onRequestPermissionsResult(requestCode,permissions,grantResult);

        if(grantResult.length > 0 && grantResult[0] == PackageManager.PERMISSION_GRANTED){
            init();
        }
        else {
            Toast.makeText(ScanQRCode.this,"Permissions Denied",Toast.LENGTH_SHORT).show();
        }
    }

    private void bindImageAnalysis(ProcessCameraProvider processCameraProvider){
        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder().setTargetResolution(new Size(1280,720))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build();

        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(ScanQRCode.this), new ImageAnalysis.Analyzer() {
            @Override
            public void analyze(@NonNull ImageProxy image) {
                Image mediaImage =  image.getImage();

                if(mediaImage!=null){
                    InputImage image2 = InputImage.fromMediaImage(mediaImage,image.getImageInfo().getRotationDegrees());

                    BarcodeScanner scanner = BarcodeScanning.getClient();
                    Task<List<Barcode>> result = scanner.process(image2);
                    result.addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                        @Override
                        public void onSuccess(List<Barcode> barcodes) {
                            for(Barcode barcode : barcodes ){
                                final String getValue = barcode.getRawValue();
                                Intent intent = new Intent(ScanQRCode.this,Add_Device.class);
                                intent.putExtra("QRMessage",getValue);
                                setResult(RESULT_CODE_ADD_QR,intent);
                                Log.d("Mapd",getValue);
                                finish();
                                return;

                            }
                            image.close();
                            mediaImage.close();
                        }
                    });
                }
            }
        });
        Preview preview = new Preview.Builder().build();
        CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        processCameraProvider.bindToLifecycle(this,cameraSelector,imageAnalysis,preview);
    }

}