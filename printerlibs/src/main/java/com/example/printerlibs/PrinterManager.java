package com.example.printerlibs;


import static com.example.printerlibs.BitmapHandler.convertGreyImgByFloyd;
import static com.example.printerlibs.BitmapHandler.imageDecode;
import static com.example.printerlibs.BitmapHandler.toGrayscale;
import static com.example.printerlibs.PrinterModel.Imin.Swift1;
import static com.example.printerlibs.PrinterModel.Sunmi.V2sNC_GL;
import static com.example.printerlibs.PrinterModel.Wiseasy.Nano6;
import static com.example.printerlibs.PrinterModel.Wiseasy.Wisenet5;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.imin.library.SystemPropManager;
import com.imin.printerlib.IminPrintUtils;
import com.sunmi.printerx.PrinterSdk;
import com.sunmi.printerx.SdkException;
import com.sunmi.printerx.api.LineApi;
import com.sunmi.printerx.enums.Align;
import com.sunmi.printerx.enums.ImageAlgorithm;
import com.sunmi.printerx.style.BitmapStyle;
import com.wisepos.smartpos.InitPosSdkListener;
import com.wisepos.smartpos.WisePosException;
import com.wisepos.smartpos.WisePosSdk;
import com.wisepos.smartpos.errorcode.WisePosErrorCode;
import com.wisepos.smartpos.printer.PrinterListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.List;

import wangpos.sdk4.libbasebinder.Printer;


public class PrinterManager implements IPrinter {

    public wangpos.sdk4.libbasebinder.Printer mPrinter;
    private String printerModel = "";
    private final Activity mContext;
    final int PRINT_STYLE_CENTER = 0x02;
    private static final String wiseNet5 = Wisenet5.name();
    private static final String swift1 = Swift1.name();
    private static final String nano6 = Nano6.name();
    private static final String sunmiv2 = V2sNC_GL.name();

    private PrinterSdk.Printer selectPrinter;
    public com.wisepos.smartpos.printer.Printer printer;

    private final WisePosSdk wisePosSdk = WisePosSdk.getInstance();


    public PrinterManager(Activity activity){
        this.mContext = activity;
    }

    public interface PrinterCallback {
        void onPrinterInitSuccess();
        void onPrinterInitFailure();
        void onPrintSuccess();
        void onPrintFailure(String errorMessage);
    }

    @Override
    public String getModel() {
        printerModel = Build.MODEL;
        printerModel = printerModel.replaceAll("\\s","");
        Log.d(Constant.TAG, "printerModel = " + printerModel);

        return printerModel;
    }

    @Override
    public void initialise(PrinterCallback callback) {
        printerModel = getModel();
        // initialise for WISENET5 model
        if (printerModel.equalsIgnoreCase(wiseNet5)) {
            Log.d(Constant.TAG, "Printer init wiseNet5");

            new Thread() {
                @Override
                public void run() {
                    try {
                        int result;
                        Log.d(Constant.TAG, "Printer init 1");

                        if (mPrinter == null){
                            Log.d(Constant.TAG, "Printer init 2");
                            mPrinter = new Printer(mContext);
                        }
                        Log.d(Constant.TAG, "Printer init 3");

                        result = mPrinter.setPrintType(0);
                        if (result == 0) {
                            Log.d(Constant.TAG, "Printer init 4" + result);

                            result = mPrinter.printInit();
                            mPrinter.clearPrintDataCache();
                            Log.d(Constant.TAG, "Printer init 5" +result);

                            if (result == 0) {
                                Log.d(Constant.TAG, "Printer init 6" +result);

                                Log.d(Constant.TAG, "Printer init success");
                                mContext.runOnUiThread(callback::onPrinterInitSuccess);
                            } else {
                                Log.d(Constant.TAG, "Printer init 7" +result);

                                Log.d(Constant.TAG, "Printer init failed");
                                mContext.runOnUiThread(callback::onPrinterInitFailure);

                            }
                        } else {
                            Log.d(Constant.TAG, "Printer init 8" + result);

                            Log.d(Constant.TAG, "Printer init failed");
                            mContext.runOnUiThread(callback::onPrinterInitFailure);
                        }
                    } catch (Exception e) {
                        Log.d(Constant.TAG, "Printer init 9");
                        throw new RuntimeException(e);
                    }                }
            }.start();



        } else if (printerModel.equalsIgnoreCase(swift1)) {
            // initialise for SWIFT1 model
             Log.d(Constant.TAG, "initialisePrinter(): iMIN");

            IminPrintUtils iminPrintUtils = IminPrintUtils.getInstance(mContext);
            String deviceModel = SystemPropManager.getModel();
            Log.d(Constant.TAG, "iminPrintUtils = " + iminPrintUtils);

            Log.d(Constant.TAG, "DEVICE MODEL = " + deviceModel);

            IminPrintUtils.PrintConnectType connectType;
            if (deviceModel.contains("M")) {
                connectType = IminPrintUtils.PrintConnectType.SPI;
            }
            else {
                connectType = IminPrintUtils.PrintConnectType.USB;
            }
            Log.d(Constant.TAG, "connectType = " + connectType);
//
            int printerStatus = iminPrintUtils.getPrinterStatus(connectType);

            Log.d(Constant.TAG, "STATUS = " + printerStatus);
            if (printerStatus == 0)
            {
                callback.onPrinterInitSuccess();

            } else {
                callback.onPrinterInitFailure();
                iminPrintUtils.initPrinter(connectType);
                callback.onPrinterInitSuccess();

            }
        } else if (printerModel.equalsIgnoreCase(nano6)) {

            if (wisePosSdk.getPrinter().initPrinter() != 0) {
                Log.d(Constant.TAG, "initialisePrinter(): Nano6");
                Log.d(Constant.TAG, "wisePosSdk.getPrinter().getPrinterStatus(): "+wisePosSdk.getPrinter().initPrinter());
                Log.d(Constant.TAG, "MASUUUKKK  1");

                wisePosSdk.initPosSdk(mContext, new InitPosSdkListener() {

                    @Override
                    public void onInitPosSuccess() {

                        Log.d(Constant.TAG, "Nano6 SDK init success ");
                        mContext.runOnUiThread(callback::onPrinterInitSuccess);

                    }

                    @Override
                    public void onInitPosFail(int i) {
                        mContext.runOnUiThread(callback::onPrinterInitFailure);
                        Log.d(Constant.TAG, "Nano6 SDK init failed ");
                    }
                });
            } else {
                mContext.runOnUiThread(callback::onPrinterInitSuccess);
            }

//            new Thread() {
//                @Override
//                public void run() {
//                    Log.d(Constant.TAG, "Nano6 SDK init success ");
//                    mContext.runOnUiThread(callback::onPrinterInitSuccess);
//                }
//            }.start();
        } else if (printerModel.equalsIgnoreCase(sunmiv2)) {
            Log.d(Constant.TAG, "initialisePrinter(): Sunmi");
            new Thread() {
                @Override
                public void run() {
                    try {
                        PrinterSdk.getInstance().getPrinter(mContext, new PrinterSdk.PrinterListen() {
                            @Override
                            public void onDefPrinter(PrinterSdk.Printer printer) {
                                Log.d(Constant.TAG, "sunmi printer: "+printer);
                                callback.onPrinterInitSuccess();

                                selectPrinter = printer;
                            }

                            @Override
                            public void onPrinters(List<PrinterSdk.Printer> list) {
                                Log.d(Constant.TAG, "sunmi printer: "+list);

                            }
                        });
                    } catch (SdkException e) {
                        callback.onPrinterInitFailure();
                        throw new RuntimeException(e);
                    }
                }
            }.start();

        } else {
            callback.onPrinterInitFailure();
        }
    }
    @Override
    public void print(Object args,PrinterCallback callback) {
        if (printerModel.equalsIgnoreCase(wiseNet5)) {
            printReceiptWiseEasy(args, callback);
        } else if (printerModel.equalsIgnoreCase( swift1)) {
            printReceiptImin(args, callback);
        } else if (printerModel.equalsIgnoreCase(nano6)) {
            printReceiptNano6(args, callback);
        } else if (printerModel.equalsIgnoreCase(sunmiv2)) {
            printReceiptSunmiV2(args, callback);
        }
    }

    public void printReceiptImin(Object args, PrinterCallback callback) {
        new Thread(() -> {
            Log.d(Constant.TAG, "printReceiptImin()");
            try {

                Bitmap bitmap = imageDecode(args);

                IminPrintUtils.getInstance(mContext).printSingleBitmap(bitmap);
                IminPrintUtils.getInstance(mContext).printAndFeedPaper(255);
                mContext.runOnUiThread(callback::onPrintSuccess);
            } catch (Exception e) {
                Log.d(Constant.TAG, "ERROR printReceiptImin(): "+e);
                mContext.runOnUiThread(() -> callback.onPrintFailure("ERROR printReceiptImin(): "+e));
            }
        }).start();
    }

    public void printReceiptWiseEasy(Object args, PrinterCallback callback) {
            Log.d(Constant.TAG, "printReceiptWiseEasy()");
            try {
                int result = -1;
                JSONObject obj = new JSONObject(String.valueOf(args));
                Bitmap image = imageDecode(args);
                Log.d(Constant.TAG, "Printer printing 1");

                assert image != null;
                mPrinter.setGrayLevel(1);
                result = mPrinter.printImageBase(image, 450, Integer.parseInt(obj.getString("pageHeight")), wangpos.sdk4.libbasebinder.Printer.Align.CENTER, 0);
                result = mPrinter.printPaper(80);
                Log.d(Constant.TAG, "Printer printing 2");
                image.recycle();
                if (result == 0) {
                    Log.d(Constant.TAG, "Printer printing 3");

                    result= mPrinter.printFinish();
                    if (result == 0) {
                        Log.d(Constant.TAG, "Printer printing 4" + result);

                        mContext.runOnUiThread(callback::onPrintSuccess);
                    } else {
                        Log.d(Constant.TAG, "Printer printing 5" + result);

                        mContext.runOnUiThread(()->callback.onPrintFailure("ERROR Print: ")) ;
                    }
                } else {
                    Log.d(Constant.TAG, "Printer printing 6");
                    mContext.runOnUiThread(()->callback.onPrintFailure("ERROR Print: ")) ;
                }

            } catch (RemoteException | JSONException e) {
                mContext.runOnUiThread(()->callback.onPrintFailure("ERROR Print: "+e)) ;

                throw new RuntimeException(e);
            }
    }

    public void printReceiptNano6(Object args, PrinterCallback callback) {

       int gray = 3;
       new Thread() {
           int ret;
           @Override
           public void run() {
                Log.d(Constant.TAG, "printReceiptNano6()");

               try {
                   InputStream inputStream = null;
                   Bitmap bitmap = null;
                   printer = WisePosSdk.getInstance().getPrinter();
                   ret = printer.initPrinter();
                   Log.d(Constant.TAG, "INIT PRINTER");

                   if (ret != 0) {
                       Log.d(Constant.TAG, "initPrinter failed = " + String.format(" errCode = 0x%x\n", ret));
                       return;
                   }

                   ret = printer.setGrayLevel(1);
                   if (ret != 0) {
                       Log.d(Constant.TAG, "startCaching failed" + String.format(" errCode = 0x%x\n", ret));
                       return;
                   }

                   bitmap = imageDecode(args);
                   assert bitmap != null;
                   bitmap = toGrayscale(bitmap);
                   bitmap = convertGreyImgByFloyd(bitmap);

                   ret = printer.initPrinter();
                   printer.addPicture(PRINT_STYLE_CENTER, bitmap);
                   printer.feedPaper(32);
                   Bundle printerOption = new Bundle();

                   printer.startPrinting(printerOption, new PrinterListener() {
                       @Override
                       public void onError(int i) {
                       }

                       @Override
                       public void onFinish() {
                           try {
                               //After printing, Feed the paper.
                               printer.feedPaper(32);
                           } catch (WisePosException e) {
                               mContext.runOnUiThread(()-> callback.onPrintFailure("Failed"));

                               throw new RuntimeException(e);                           }
                       }
                       @Override
                       public void onReport(int i) {
                           //The callback method is reserved and does not need to be implemented
                       }

                   });

               }  catch (WisePosException e) {
                   mContext.runOnUiThread(()-> callback.onPrintFailure("Failed"));

                   throw new RuntimeException(e);               }
           }
       }.start();
    }

    public void printReceiptSunmiV2(Object args, PrinterCallback callback) {
        new Thread() {
            public void run() {
                Log.d(Constant.TAG, "printReceiptSunmiV2()");
                try {
                    Log.e("logPrint" , "print image clicked > try");
                    LineApi api = selectPrinter.lineApi();
                    Bitmap bitmap = imageDecode(args);
                    if(bitmap != null) {
                        api.printBitmap(bitmap, BitmapStyle.getStyle().setAlign(Align.CENTER).setAlgorithm(ImageAlgorithm.DITHERING).setWidth(384).setHeight(bitmap.getHeight()));
                        api.autoOut();
                        Log.e("logPrint" , "print image clicked > try");
                        mContext.runOnUiThread(callback::onPrintSuccess);


                    } else {
                        mContext.runOnUiThread(()-> callback.onPrintFailure("Failed"));

                        throw new RuntimeException();
                    }
                } catch (SdkException e) {
                    mContext.runOnUiThread(()-> callback.onPrintFailure("Failed"));

                    throw new RuntimeException(e);
                }
            }
        }.start();
    }

}