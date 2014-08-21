package com.doday.app.image;

import android.graphics.Bitmap;

/**
 * Created by Formation on 21/08/2014.
 */
public class MyBitmapThumb  {

    private final Bitmap bitamp;
    private boolean returned = false;

    public MyBitmapThumb(Bitmap bitamp) {
        super();
        this.bitamp = bitamp;
    }

    public boolean isReturned() {
        return returned;
    }

    public void returned() {
        this.returned = !returned;
    }

    public Bitmap getBitamp() {
        return bitamp;
    }


}
