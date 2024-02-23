package com.example.printerlibssampleapp;

import android.app.Activity;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.printerlibs.PrinterManager;

public class ActivityViewModel extends ViewModel implements PrinterManager.PrinterCallback {

    private PrinterManager printerManager;
    private final MutableLiveData<Boolean> printerInitStatus = new MutableLiveData<>();
    private final MutableLiveData<String> printStatus = new MutableLiveData<>();
    private final MutableLiveData<String> printerModel = new MutableLiveData<>();

    public ActivityViewModel(){
    }

    public void initPrintManager(Activity activity) {
        printerManager = new PrinterManager(activity);
    }

    public void initializePrinter() {
        if(printerManager != null) {
            printerManager.initialise(this);
        } else {
            printerInitStatus.setValue(false);
        }
    }

    public void onPrint(Object args) {
        if(printerManager != null && Boolean.TRUE.equals(printerInitStatus.getValue())) {
            printerManager.print(args, this);
        } else {
            printerInitStatus.setValue(false);
            printStatus.setValue("Failed to print! Printer Manager not initialized");
        }
    }

    @Override
    public void onPrinterInitSuccess() {
        printerInitStatus.setValue(true);
        printerModel.setValue(printerManager.getModel());

    }

    @Override
    public void onPrinterInitFailure() {
        printerInitStatus.postValue(false);
    }

    @Override
    public void onPrintSuccess() {
        printStatus.setValue("Printing Completed");

    }

    @Override
    public void onPrintFailure(String s) {
        printStatus.setValue(s);
    }

    public LiveData<Boolean> getPrinterInitStatus() {
        return printerInitStatus;
    }

    public LiveData<String> getPrinterModel() {
        return printerModel;
    }

}
