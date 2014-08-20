package com.doday.app.network;

import android.net.http.HttpResponseCache;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;


/**
 * Created by Formation on 20/08/2014.
 */
public class MyHttpCache <T extends Object> {

    private static final String TAG = "MyHttpCache";
    private HttpResponseCache iceCreamSandwichCache;
    private com.integralblue.httpresponsecache.HttpResponseCache preIceCreamSandwichCache;

    public void enableHttpResponseCache(android.content.Context context) {
        long httpCacheSize = 10 * 1024 * 1024; // 10 MiB
        File httpCacheDir = new File(context.getCacheDir(), "http");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            try {
                HttpResponseCache.install(httpCacheDir, httpCacheSize);
                iceCreamSandwichCache = HttpResponseCache.getInstalled();
            }catch (IOException e) {
                Log.e(TAG, "HTTP response post ICS iceCreamSandwichCache installation failed:" + e);
            }
        }else{
            try {
                com.integralblue.httpresponsecache.HttpResponseCache.install(httpCacheDir, httpCacheSize);
                preIceCreamSandwichCache = com.integralblue.httpresponsecache.HttpResponseCache.getInstalled();
            } catch (IOException e) {
                Log.e(TAG, "HTTP response pre ICS iceCreamSandwichCache installation failed:" + e);
            }
        }
    }

    public void desableHttpResponseCache() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                if (iceCreamSandwichCache != null) {
                    iceCreamSandwichCache.flush();
                }
            } else {
                if (preIceCreamSandwichCache != null) {
                    preIceCreamSandwichCache.flush();
                }
            }
        }catch(IllegalStateException e){
            Log.i(TAG,String.format("The cache is closed, it cant be flushed %s %s",e.getMessage(),e.getStackTrace()));
        }
    }

    public void clearCache(android.content.Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            Toast.makeText(context, "Clear iceCreamSandwichCache !!", Toast.LENGTH_LONG).show();
            try {
                if (iceCreamSandwichCache != null) {
                    iceCreamSandwichCache.delete();
                }
            } catch (IOException e) {
                Log.e(TAG, String.format("%s \n %s", e.getMessage(), e.getStackTrace()));
            }
        }else{
            try {
                preIceCreamSandwichCache.delete();
            } catch (IOException e) {
                Log.e(TAG, String.format("%s \n %s", e.getMessage(), e.getStackTrace()));
            }
        }
    }



}
