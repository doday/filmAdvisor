package com.doday.app.image;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MyBitmapFactory {

    private static final String TAG = "MyBitmapFactory";

    public static Bitmap getBitmapFromResourceAtDimensions(Resources res, int resId,
                                                           int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = calculateDimensInOptionFromBitmapAndSize(res, resId, reqWidth, reqHeight);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return android.graphics.BitmapFactory.decodeResource(res, resId, options);
    }

    public static Bitmap getBitmapFromStreamAtDimensions(ByteArrayOutputStream baos, int thumbWidth, int thumbHeight) {
        if(baos != null) {
            // Determine the new dimensions of image
            final BitmapFactory.Options options = calculateDimensInOptionFromInputStreamAndSize(baos, thumbWidth, thumbHeight);

            // Decode bitmap with thumb Size set
            InputStream inputStreamToDecode = new ByteArrayInputStream(baos.toByteArray());
            options.inJustDecodeBounds = false;
            final Bitmap bitmap = BitmapFactory.decodeStream(inputStreamToDecode, null, options);
            return bitmap;
        }
        return null;
    }

    private static BitmapFactory.Options calculateDimensInOptionFromInputStreamAndSize(ByteArrayOutputStream baos, int reqWidth, int reqHeight) {
        InputStream inputStream = new ByteArrayInputStream(baos.toByteArray());
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);//To avoid java.lang.OutOfMemory exceptions, check the dimensions of a bitmap before decoding it, unless you absolutely trust the source to provide you with predictably sized image data that comfortably fits within the available memory.

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        return options;
    }

    private static BitmapFactory.Options calculateDimensInOptionFromBitmapAndSize(Resources res, int resId, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        return options;
    }

    private static int calculateInSampleSize(
            android.graphics.BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
}