package com.example.printerlibssampleapp;

import static com.example.printerlibs.BitmapHandler.convertGreyImgByFloyd;
import static com.example.printerlibs.BitmapHandler.toGrayscale;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.printerlibs.PrinterManager;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    PrinterManager printerManager;

    private TextView txtModel;
    private TextView txtStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtModel = findViewById(R.id.txtModelName);
        txtStatus = findViewById(R.id.txtStatus);

        setPrinterManager();
        initialisePrinter();
        showStatus();


        Button btnPrint = findViewById(R.id.btnPrint);

        btnPrint.setOnClickListener(v -> print(R.drawable.ic_launcher_foreground));

    }

    public void setPrinterManager() {
        printerManager = new PrinterManager(this);
    }

    public void initialisePrinter() {
        printerManager.getModel();
        printerManager.initialise();
    }

    public void print(Object args) {
        printerManager.print(args, this);
    }

    public void showStatus(){
        txtModel.setText(printerManager.getModel());
        txtStatus.setText(printerManager.response.getResponse());
    }
}