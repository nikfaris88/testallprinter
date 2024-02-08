package com.example.printerlibs;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

//import androidx.appcompat.app.AppCompatActivity;

//public class MainActivity extends AppCompatActivity {
//
//    PrinterManager printerManager;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        printerManager = new PrinterManager(this);
//
//        printerManager.getModel();
//        printerManager.initialise();
//
//        Button btnPrint = findViewById(R.id.btnPrint);
//        TextView txt1 = findViewById(R.id.txt1);
//
//        txt1.setText(printerManager.response.getResponse());
//
//        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round);
//        btnPrint.setOnClickListener(v -> printerManager.print(icon, this));
//    }
//}