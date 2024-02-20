package com.example.printerlibs;


import static com.example.printerlibs.BitmapHandler.convertGreyImgByFloyd;
import static com.example.printerlibs.BitmapHandler.imageDecode;
import static com.example.printerlibs.BitmapHandler.scaleImage;
import static com.example.printerlibs.BitmapHandler.toGrayscale;
import static com.example.printerlibs.PrinterModel.Imin.Swift1;
import static com.example.printerlibs.PrinterModel.Sunmi.V2sNC_GL;
import static com.example.printerlibs.PrinterModel.Wiseasy.Nano6;
import static com.example.printerlibs.PrinterModel.Wiseasy.Wisenet5;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;

import com.imin.library.SystemPropManager;
import com.imin.printerlib.IminPrintUtils;
import com.sunmi.printerx.PrinterSdk;
import com.sunmi.printerx.SdkException;
import com.sunmi.printerx.api.CanvasApi;
import com.sunmi.printerx.api.LineApi;
import com.sunmi.printerx.api.PrintResult;
import com.sunmi.printerx.enums.Align;
import com.sunmi.printerx.enums.ImageAlgorithm;
import com.sunmi.printerx.enums.Shape;
import com.sunmi.printerx.style.AreaStyle;
import com.sunmi.printerx.style.BaseStyle;
import com.sunmi.printerx.style.BitmapStyle;
import com.wisepos.smartpos.InitPosSdkListener;
import com.wisepos.smartpos.WisePosException;
import com.wisepos.smartpos.WisePosSdk;
import com.wisepos.smartpos.printer.PrinterListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Objects;


public class PrinterManager implements Printer{

    public wangpos.sdk4.libbasebinder.Printer mPrinter;
    private String printerModel = "";
    private final Context mContext;
    public Response response = new Response();
    final int PRINT_STYLE_CENTER = 0x02;
    private static final String wiseNet5 = Wisenet5.name();
    private static final String swift1 = Swift1.name();
    private static final String nano6 = Nano6.name();
    private static final String sunmiv2 = V2sNC_GL.name();

    private PrinterSdk.Printer selectPrinter;

    private boolean isPrinterAvailable = false;

    public PrinterManager(Context context){
        this.mContext = context;
    }

    @Override
    public String getModel() {
        printerModel = Build.MODEL;
        printerModel = printerModel.replaceAll("\\s","");
        Log.d(Constant.TAG, "printerModel = " + printerModel);

        return printerModel;
    }

    @Override
    public void initialise() {
        printerModel = getModel();
        if (Objects.equals(printerModel, wiseNet5)) {
            // initialise for WISENET5 model
            new Thread(() -> {
                Log.d(Constant.TAG, "initialisePrinter(): WISENET");
                try {
                    int result = -1;
                    String MODEL = Build.MODEL;
                    Log.d(Constant.TAG, "Model = " + MODEL);
                    if (mPrinter == null){
                        mPrinter = new wangpos.sdk4.libbasebinder.Printer(mContext);
                    }
                    result = mPrinter.setPrintType(0);
                    if (result == 0) {
                        result = mPrinter.printInit();
                        mPrinter.clearPrintDataCache();
                        if (result == 0) {
                            isPrinterAvailable = true;
                            response.onSuccess("Printer init success");
                        } else {
                            isPrinterAvailable = false;

                            response.onFailed("Printer init failed");
                        }
                    } else {
                        isPrinterAvailable = false;

                        response.onFailed("Printer init failed");
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                    response.onError(e);
                }
            }).start();
        } else if (Objects.equals(printerModel, swift1)) {
            // initialise for SWIFT1 model

            new Thread(() -> {
                Log.d(Constant.TAG, "initialisePrinter(): iMIN");

                IminPrintUtils iminPrintUtils = IminPrintUtils.getInstance(mContext);
                        String deviceModel = SystemPropManager.getModel();
                        Log.d(Constant.TAG, "iminPrintUtils = " + iminPrintUtils);

                        Log.d(Constant.TAG, "DEVICE MODEL = " + deviceModel);

                IminPrintUtils.PrintConnectType connectType;
                    if (deviceModel.contains("M2-203")
                            || deviceModel.contains("M2-202")
                            || deviceModel.contains("M2-Pro"))
                    {
                        connectType = IminPrintUtils.PrintConnectType.SPI;
                    } else {
                        connectType = IminPrintUtils.PrintConnectType.USB;
                    }
                    Log.d(Constant.TAG, "connectType = " + connectType);
    //
                    int printerStatus = iminPrintUtils.getPrinterStatus(connectType);

                    Log.d(Constant.TAG, "STATUS = " + printerStatus);
                    if (printerStatus == 0)
                    {
                        isPrinterAvailable = true;
                        response.onSuccess("Printer init success");
                    } else {
                        isPrinterAvailable = false;

                        response.onFailed("failed");
                        iminPrintUtils.initPrinter(connectType);
                        response.onSuccess("Printer init success");
                    }
            }).start();
        } else if (Objects.equals(printerModel, nano6)) {
            // initialise for NANO6 model
            Log.d(Constant.TAG, "initialisePrinter(): Nano6");

            WisePosSdk wisePosSdk = WisePosSdk.getInstance();
            wisePosSdk.initPosSdk(mContext, new InitPosSdkListener() {
                @Override
                public void onInitPosSuccess() {
                    isPrinterAvailable = true;

                    Log.d(Constant.TAG, "Nano6 SDK init success ");
                    response.onSuccess("Printer init success");

                }

                @Override
                public void onInitPosFail(int i) {
                    isPrinterAvailable = false;

                    response.onFailed("Printer init failed");

                    Log.d(Constant.TAG, "Nano6 SDK init failed ");
                }
            });

            new Thread(() -> response.onSuccess("Success")).start();
        } else if (Objects.equals(printerModel, sunmiv2)) {
            Log.d(Constant.TAG, "initialisePrinter(): Sunmi");

            try {
                PrinterSdk.getInstance().getPrinter(mContext, new PrinterSdk.PrinterListen() {
                    @Override
                    public void onDefPrinter(PrinterSdk.Printer printer) {
                        Log.d(Constant.TAG, "sunmi printer: "+printer);
                        response.onSuccess("Printer init success");
                        isPrinterAvailable = true;

                        selectPrinter = printer;
                    }

                    @Override
                    public void onPrinters(List<PrinterSdk.Printer> list) {
                        Log.d(Constant.TAG, "sunmi printer: "+list);
                        isPrinterAvailable = list.size() > 0;

                    }
                });
            } catch (SdkException e) {
                response.onError(e);
                throw new RuntimeException(e);
            }
        } else {
            response.onError(new Throwable("ERROR: Printer not found"));
        }
    }
    @Override
    public void print(Object args) {
        if (Objects.equals(printerModel, wiseNet5)) {
            printReceiptWiseEasy(args);
        } else if (Objects.equals(printerModel, swift1)) {
            printReceiptImin(args);
        } else if (Objects.equals(printerModel, nano6)) {
            printReceiptNano6(args);
        } else if (Objects.equals(printerModel, sunmiv2)) {
            printReceiptSunmiV2(args);
        }
    }

    public void printReceiptImin(Object args) {
        new Thread(() -> {
            Looper.prepare();
            Log.d(Constant.TAG, "printReceiptImin()");
            try {
//                    InputStream inputStream = mContext.getAssets().open("ic_launcher_round.png");
//                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                Bitmap bitmap = imageDecode(args);

                IminPrintUtils.getInstance(mContext).printSingleBitmap(bitmap);
                IminPrintUtils.getInstance(mContext).printAndFeedPaper(255);
                response.onSuccess("printReceiptImin print success");

            } catch (Exception e) {
                Log.d(Constant.TAG, "ERROR printReceiptImin(): "+e);

                e.printStackTrace();
                response.onError(e);
            }
            Looper.loop();
        }).start();
    }

    public void printReceiptWiseEasy(Object args) {
        new Thread(() -> {
            Looper.prepare();
            Log.d(Constant.TAG, "printReceiptWiseEasy()");
            try {
                int result = -1;
                Bitmap image = imageDecode(args);
                JSONObject obj = new JSONObject(String.valueOf(args));

                mPrinter.setGrayLevel(1);
                result = mPrinter.printImageBase(image, 450, Integer.parseInt(obj.getString("pageHeight")), wangpos.sdk4.libbasebinder.Printer.Align.CENTER, 0);
                result = mPrinter.printPaper(80);
//                    image.recycle();
                if (result == 0) {
                    result= mPrinter.printFinish();
                    if (result == 0) {
                        response.onSuccess("Success");
                    } else {
                        response.onFailed("Failed");
                    }
                } else {
                    response.onFailed("Failed");
                }
                response.onSuccess("printReceiptWiseEasy Success");

            } catch (RemoteException | JSONException e) {
                throw new RuntimeException(e);
            }
            Looper.loop();
        }).start();
    }

    public void printReceiptNano6(Object args) {
        com.wisepos.smartpos.printer.Printer printer = WisePosSdk.getInstance().getPrinter();
        final int[] ret = {printer.initPrinter()};

        new Thread(() -> {
            Looper.prepare();
            Log.d(Constant.TAG, "printReceiptNano6()");
            try {

                if (ret[0] != 0) {
                    Log.d(Constant.TAG, "initPrinter() failed!!!" + String.format(" errCode = 0x%x\n", ret[0]));

                    return;
                }

                ret[0] = printer.setGrayLevel(1);
                if (ret[0] != 0){
                    Log.d(Constant.TAG, "initPrinter() failed!!!" + String.format(" errCode = 0x%x\n", ret[0]));
                    return;
                }
//                    InputStream inputStream = mContext.getAssets().open("ic_launcher_round.png");
//                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                Bitmap bitmap = imageDecode(args);
//                InputStream inputStream = decodedString(args);
//                bitmap = BitmapFactory.decodeStream(inputStream);
                assert bitmap != null;
                bitmap = toGrayscale(bitmap);
                bitmap = convertGreyImgByFloyd(bitmap);
                //Add a bitmap image to the canvas.
                printer.setGrayLevel(1);
                printer.addPicture(PRINT_STYLE_CENTER, bitmap);
                printer.feedPaper(32);

                Bundle printerOption = new Bundle();

                printer.startPrinting(printerOption, new PrinterListener() {
                    @Override
                    public void onError(int i) {
                        response.onError(new Throwable("ERROR: "+i));
                    }

                    @Override
                    public void onFinish() {
                        try {
                            printer.feedPaper(32);
                        } catch (WisePosException e) {
                            e.printStackTrace();
                            response.onError(e);
                        }
                    }

                    @Override
                    public void onReport(int i) {
                        //The callback method is reserved and does not need to be implemented

                    }
                });

                response.onSuccess("printReceiptNano6 Success");
            } catch (WisePosException e) {
                throw new RuntimeException(e);
            }
            Looper.loop();
        }).start();
    }

    public void printReceiptSunmiV2(Object args) {
        com.wisepos.smartpos.printer.Printer printer = WisePosSdk.getInstance().getPrinter();
        final int[] ret = {printer.initPrinter()};

        new Thread(() -> {
            Looper.prepare();
            Log.d(Constant.TAG, "printReceiptSunmiV2()");
            try {
                android.util.Log.e("logPrint" , "print image clicked > try");
                LineApi api = selectPrinter.lineApi();
                Bitmap bitmap = imageDecode(args);
                if(bitmap != null) {
                    api.printBitmap(bitmap, BitmapStyle.getStyle().setAlign(Align.CENTER).setAlgorithm(ImageAlgorithm.DITHERING).setWidth(384).setHeight(bitmap.getHeight()));
                    api.autoOut();
                    android.util.Log.e("logPrint" , "print image clicked > try");
                    response.onSuccess("printReceiptNano6 Done Success");

                } else {
                    response.onSuccess("printReceiptNano6 Success");

                    throw new RuntimeException();
                }
            } catch (SdkException e) {
                throw new RuntimeException(e);
            }
            Looper.loop();
        }).start();
    }

}