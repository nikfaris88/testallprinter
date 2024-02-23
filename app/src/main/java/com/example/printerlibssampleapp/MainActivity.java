package com.example.printerlibssampleapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

public class MainActivity extends AppCompatActivity {

    private ActivityViewModel printerViewModel;
    private TextView txtModel;
    private TextView txtStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtModel = findViewById(R.id.txtModelName);
        txtStatus = findViewById(R.id.txtStatus);
        Button btnPrint = findViewById(R.id.btnPrint);

        printerViewModel = new ViewModelProvider(this,
                new PrinterViewModelFactory()).get(ActivityViewModel.class);
        printerViewModel.initPrintManager(this);
        initialisePrinter();


        btnPrint.setOnClickListener(v -> print(R.drawable.ic_launcher_foreground));

        showStatus();

    }

    @Override
    protected void onStart() {
        super.onStart();
        showStatus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showStatus();
    }

    @Override
    protected void onStop() {
        super.onStop();
        printerViewModel.getPrinterInitStatus().removeObservers(this);
        printerViewModel.getPrinterModel().removeObservers(this);
    }

    public void initialisePrinter() {
        printerViewModel.initializePrinter();
    }

    public void print(Integer args) {
        printerViewModel.onPrint(args);
    }

    public void showStatus(){
        printerViewModel.getPrinterInitStatus().observe(this, result -> {
            txtStatus.setText("Status: "+result);
        });
        printerViewModel.getPrinterModel().observe(this, result -> {
            txtModel.setText(result);
        });
    }
}