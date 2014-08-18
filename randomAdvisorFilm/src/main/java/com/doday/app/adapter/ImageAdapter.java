package com.doday.app.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.doday.app.image.MyBitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
* Created by Formation on 24/07/2014.
*/
public class ImageAdapter extends BaseAdapter {
    private final Context context ;
    private ByteArrayOutputStream[] tabCinemaThumb;
    private int thumbSize = 340;//choisir la taille de la largeur (portrait et paysage) des vignettes dynamiquement

    public ImageAdapter(Context context) {
        this.context = context;
    }

    public ImageAdapter(Context context,ByteArrayOutputStream[] tabCinemaThumb ) {
        this.context = context;
        this.tabCinemaThumb = tabCinemaThumb;
    }

    @Override
    public int getCount() {
        return tabCinemaThumb.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ImageView imageView;
        if(convertView == null){
            imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(thumbSize, thumbSize));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }else{
            imageView = (ImageView)convertView;
        }

        final Bitmap bitmapFromStreamAtDimensions = MyBitmapFactory.getBitmapFromStreamAtDimensions(tabCinemaThumb[position], thumbSize, thumbSize);
        imageView.setImageBitmap(bitmapFromStreamAtDimensions);
        return imageView;
    }

}
