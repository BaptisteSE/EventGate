package com.example.qrcode;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;



import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class scanner_View extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    ZXingScannerView scannerView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    // Firestore
    private FirebaseFirestore firestore;
    // Document reference
    DocumentReference reference;
    // Context
    Context context = scanner_View.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);


        Dexter.withContext(getApplicationContext())
                .withPermission(Manifest.permission.CAMERA)
        .withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                scannerView.startCamera();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.cancelPermissionRequest();
            }
        }).check();




    }



    @Override
    public void handleResult(Result rawResult) {
        firestore = FirebaseFirestore.getInstance();
        Intent intent = new Intent(getApplicationContext(), scanner_View.class);

        if(rawResult.getText()!=null){
            reference = firestore.collection("qrcodes").document(rawResult.getText());
            reference.get().addOnCompleteListener(task -> {
                if (task.getResult().exists()) {
                    String libelle = task.getResult().getString("libelle");
                    Toast.makeText(context, "Le code est bien valide",Toast.LENGTH_SHORT).show();
                    MainActivity.scanText.setText("Code : "+rawResult.getText());
                    if(libelle==null){
                        MainActivity.scanText2.setText("");
                    }else{
                        MainActivity.scanText2.setText("Libelle : "+libelle);
                    }
                    onBackPressed(); // autometic execute
                } else {
                    Toast.makeText(context, "Le code incorrect",Toast.LENGTH_SHORT).show();
                    onResume();
                    //MainActivity.scanText.setText("Le code n'est pas valide");
                    //startActivity(intent);
                    //if(MainActivity.scanText.getText()!=null){
                    //    onBackPressed();
                    //}

                }
            });
        }

            //
    }


    @Override
    protected void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();

        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }
}