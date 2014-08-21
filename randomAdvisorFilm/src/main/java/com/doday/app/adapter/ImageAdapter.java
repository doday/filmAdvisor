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
import java.util.ArrayList;

/**
* Created by Formation on 24/07/2014.
*/
public class ImageAdapter extends BaseAdapter {
    private final Context context ;
    private ArrayList<Bitmap> listFilmThumb;
    public static int thumbSize = 340;//TODO choisir la taille de la largeur (portrait et paysage) des vignettes dynamiquement

    public ImageAdapter(Context context) {
        this.context = context;
    }

    public ImageAdapter(Context context, ArrayList<Bitmap> listCinemaThumb) {
        this(context);
        this.listFilmThumb = listCinemaThumb;
    }

    @Override
    public int getCount() {
        return listFilmThumb.size();
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

        imageView.setImageBitmap(listFilmThumb.get(position)); //il faut alléger le plus possible getView car sinon cela provoquera des lage
        return imageView;
    }

}
