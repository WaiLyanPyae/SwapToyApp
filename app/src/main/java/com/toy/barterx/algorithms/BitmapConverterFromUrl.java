package com.toy.barterx.algorithms;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class BitmapConverterFromUrl {
    public static Bitmap drawImageToView(String imageUrl) throws IOException {
        Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(imageUrl).getContent());
        return bitmap;
    }
}
