package com.example.printerlibs;


import static com.example.printerlibs.BitmapHandler.convertGreyImgByFloyd;
import static com.example.printerlibs.BitmapHandler.scaleImage;
import static com.example.printerlibs.BitmapHandler.toGrayscale;
import static com.example.printerlibs.PrinterModel.Imin.Swift1;
import static com.example.printerlibs.PrinterModel.Sunmi.V2sNC_GL;
import static com.example.printerlibs.PrinterModel.Wiseasy.Nano6;
import static com.example.printerlibs.PrinterModel.Wiseasy.Wisenet5;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.sunmi.printerx.api.PrintResult;
import com.sunmi.printerx.enums.ImageAlgorithm;
import com.sunmi.printerx.enums.Shape;
import com.sunmi.printerx.style.AreaStyle;
import com.sunmi.printerx.style.BaseStyle;
import com.sunmi.printerx.style.BitmapStyle;
import com.wisepos.smartpos.InitPosSdkListener;
import com.wisepos.smartpos.WisePosException;
import com.wisepos.smartpos.WisePosSdk;
import com.wisepos.smartpos.printer.PrinterListener;

import java.io.IOException;
import java.io.InputStream;
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

    PrinterManager(Context context){
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
        if (Objects.equals(printerModel, wiseNet5)) {
            // initialise for WISENET5 model
            new Thread() {
                @Override
                public void run() {
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
                                response.onSuccess("Success");
                            } else {
                                response.onFailed("Failed");
                            }
                        } else {
                            response.onFailed("Failed");
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                        response.onError(e);
                    }
                }
            }.start();
        } else if (Objects.equals(printerModel, swift1)) {
            // initialise for SWIFT1 model

//            new Thread() {
//                @Override
//                public void run() {
                    Log.d(Constant.TAG, "initialisePrinter(): iMIN");

            IminPrintUtils iminPrintUtils = IminPrintUtils.getInstance(mContext);
                    String deviceModel = SystemPropManager.getModel();
                    Log.d(Constant.TAG, "iminPrintUtils = " + iminPrintUtils);

                    Log.d(Constant.TAG, "DEVICE MODEL = " + deviceModel);

            IminPrintUtils.PrintConnectType connectType;
            if (deviceModel.contains("M2-203") || deviceModel.contains("M2-202") || deviceModel.contains("M2-Pro")) {
                        connectType = IminPrintUtils.PrintConnectType.SPI;
                    } else {
                        connectType = IminPrintUtils.PrintConnectType.USB;
                    }
                    Log.d(Constant.TAG, "connectType = " + connectType);
//
                    int status = iminPrintUtils.getPrinterStatus(connectType);

                    Log.d(Constant.TAG, "STATUS = " + status);
                    if (status == 0) {
                        response.onSuccess("Success");
                    } else {
                        response.onSuccess("failed");
                        iminPrintUtils.initPrinter(connectType);
                        response.onSuccess("Success");
                    }
//                }
//            }.start();
        } else if (Objects.equals(printerModel, nano6)) {
            // initialise for NANO6 model
            Log.d(Constant.TAG, "initialisePrinter(): Nano6");

            WisePosSdk wisePosSdk = WisePosSdk.getInstance();
            wisePosSdk.initPosSdk(mContext, new InitPosSdkListener() {
                @Override
                public void onInitPosSuccess() {
                    Log.d(Constant.TAG, "Nano6 SDK init success ");
                }

                @Override
                public void onInitPosFail(int i) {
                    Log.d(Constant.TAG, "Nano6 SDK init failed ");
                }
            });

            new Thread() {
                @Override
                public void run() {
                    response.onSuccess("Success");

                }
            }.start();
        } else if (Objects.equals(printerModel, sunmiv2)) {
            Log.d(Constant.TAG, "initialisePrinter(): Sunmi");

            try {
                PrinterSdk.getInstance().getPrinter(mContext, new PrinterSdk.PrinterListen() {
                    @Override
                    public void onDefPrinter(PrinterSdk.Printer printer) {
                        Log.d(Constant.TAG, "sunmi printer: "+printer);

                        selectPrinter = printer;
                    }

                    @Override
                    public void onPrinters(List<PrinterSdk.Printer> list) {
                        Log.d(Constant.TAG, "sunmi printer: "+list);

                    }
                });
            } catch (SdkException e) {
                response.onError(e);
                throw new RuntimeException(e);
            }
        }
    }
    @Override
    public void print(Object args, Context context) {
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
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Log.d(Constant.TAG, "printReceiptImin()");
                try {
                    InputStream inputStream = mContext.getAssets().open("ic_launcher_round.png");
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

//                    Bitmap image = bitmapHandler.imageDecode(args);

                    IminPrintUtils.getInstance(mContext).printSingleBitmap(bitmap);
                    IminPrintUtils.getInstance(mContext).printAndFeedPaper(255);
                    response.onSuccess("printReceiptImin print success");

                } catch (Exception e) {
                    Log.d(Constant.TAG, "ERROR printReceiptImin(): "+e);

                    e.printStackTrace();
                    response.onError(e);
                }
                Looper.loop();
            }
        }.start();
    }

    public void printReceiptWiseEasy(Object args) {
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Log.d(Constant.TAG, "printReceiptWiseEasy()");
                try {
                    int result = -1;
//                    Bitmap image = imageDecode(args);
//                    JSONObject obj = new JSONObject(String.valueOf(args));
                    InputStream inputStream = mContext.getAssets().open("ic_launcher_round.png");
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//                    bitmap = toGrayscale(bitmap);
//                    bitmap = convertGreyImgByFloyd(bitmap);
                    //Add a bitmap image to the canvas.
                    mPrinter.setGrayLevel(1);
//                    result = mPrinter.printImageBase(image, 450, Integer.parseInt(obj.getString("pageHeight")), wangpos.sdk4.libbasebinder.Printer.Align.CENTER, 0);
                    result = mPrinter.printImageBase(bitmap, 450, 400, wangpos.sdk4.libbasebinder.Printer.Align.CENTER, 0);
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

                } catch (RemoteException | IOException e) {
                    throw new RuntimeException(e);
                }
                Looper.loop();
            }
        }.start();
    }

    public void printReceiptNano6(Object args) {
        com.wisepos.smartpos.printer.Printer printer = WisePosSdk.getInstance().getPrinter();
        final int[] ret = {printer.initPrinter()};

        new Thread() {
            @Override
            public void run() {
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

//                    Bitmap image = imageDecode(args);
//                    JSONObject obj = new JSONObject(String.valueOf(args));
                    InputStream inputStream = mContext.getAssets().open("ic_launcher_round.png");
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
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
                } catch (IOException | WisePosException e) {
                    throw new RuntimeException(e);
                }
                Looper.loop();
            }
        }.start();
    }

    public void printReceiptSunmiV2(Object args) {
        com.wisepos.smartpos.printer.Printer printer = WisePosSdk.getInstance().getPrinter();
        final int[] ret = {printer.initPrinter()};

        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Log.d(Constant.TAG, "printReceiptSunmiV2()");
                try {
//                    Bitmap image = imageDecode(args);
//                    JSONObject obj = new JSONObject(String.valueOf(args));
                    InputStream inputStream = mContext.getAssets().open("ic_launcher_round.png");
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    bitmap = scaleImage(bitmap);
                    //Add a bitmap image to the canvas.
                    CanvasApi api = selectPrinter.canvasApi();
                    api.initCanvas(BaseStyle.getStyle().setWidth(450).setHeight(450));
                    api.renderArea(AreaStyle.getStyle().setStyle(Shape.BOX).setPosX(0).setPosY(0).setWidth(450).setHeight(450));
                    api.renderBitmap(bitmap, BitmapStyle.getStyle().setAlgorithm(ImageAlgorithm.DITHERING)
                            .setPosX(0).setPosY(0).setWidth(320).setHeight(320));
                    api.printCanvas(1, new PrintResult() {
                        @Override
                        public void onResult(int resultCode, String message) throws RemoteException {
                            if(resultCode == 0) {
                                //打印完成
                            } else {
                                //打印失败
                            }
                        }
                    });


                    response.onSuccess("printReceiptNano6 Success");
                } catch (IOException | SdkException e) {
                    throw new RuntimeException(e);
                }
                Looper.loop();
            }
        }.start();
    }

}