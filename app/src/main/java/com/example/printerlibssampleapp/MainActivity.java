package com.example.printerlibssampleapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.printerlibs.PrinterManager;

public class MainActivity extends AppCompatActivity {

    PrinterManager printerManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setPrinterManager();
        initialisePrinter();


        Button btnPrint = findViewById(R.id.btnPrint);
        btnPrint.setOnClickListener(v -> print());
    }

    public void setPrinterManager() {
        printerManager = new PrinterManager(this);
    }

    public void initialisePrinter() {
        printerManager.getModel();
        printerManager.initialise();
    }

    public void print() {
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round);
        printerManager.print(icon, this);
    }
}