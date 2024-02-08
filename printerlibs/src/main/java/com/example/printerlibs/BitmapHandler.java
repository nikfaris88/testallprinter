package com.example.printerlibs;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class BitmapHandler {

    InputStream decodedString(Object args) {
        try {
            JSONObject obj = new JSONObject(String.valueOf(args));
            Object imageReceipt = obj.has("imageReceipt") ? obj.getString("imageReceipt") : "";
            byte[] decodedString = Base64.decode(imageReceipt.toString(), Base64.DEFAULT);

            return new ByteArrayInputStream(decodedString);

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }

    Bitmap imageDecode(Object args) {
        try {
            JSONObject obj = new JSONObject(String.valueOf(args));
            Object imageReceipt = obj.has("imageReceipt") ? obj.getString("imageReceipt") : "";
            byte[] decodedString = Base64.decode(imageReceipt.toString(), Base64.DEFAULT);

            return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        } catch (JSONException e) {
            e.printStackTrace();

            return null;
        }
    }

    public static Bitmap toGrayscale(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();


        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }
    public static Bitmap convertGreyImgByFloyd(Bitmap img) {
        int width = img.getWidth();         //Obtain the width of the bitmap
        int height = img.getHeight();       //Obtain the height of the bitmap

        int[] pixels = new int[width * height]; //Create a pixel array based on the size of the bitmap
        img.getPixels(pixels, 0, width, 0, 0, width, height);
        int[] gray=new int[height*width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int grey = pixels[width * i + j];
                int red = ((grey  & 0x00FF0000 ) >> 16);
                gray[width*i+j]=red;
            }
        }

        int e=0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int g=gray[width*i+j];
                if (g>=128) {
                    pixels[width*i+j]=0xffffffff;
                    e=g-255;
                }else {
                    pixels[width*i+j]=0xff000000;
                    e=g-0;
                }
                if (j<width-1&&i<height-1) {
                    gray[width*i+j+1]+=3*e/8;
                    gray[width*(i+1)+j]+=3*e/8;
                    gray[width*(i+1)+j+1]+=e/4;
                }else if (j==width-1&&i<height-1) {
                    gray[width*(i+1)+j]+=3*e/8;
                }else if (j<width-1&&i==height-1) {
                    gray[width*(i)+j+1]+=e/4;
                }
            }
        }
        Bitmap mBitmap=Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        mBitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return mBitmap;
    }


    public static Bitmap scaleImage(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int newWidth = (width*7)/12;

        float scaleWidth = ((float) newWidth)/width;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, 1);

        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
    }
}
