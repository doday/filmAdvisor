package com.doday.app.network;

import android.net.http.AndroidHttpClient;
import android.util.Log;


import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Formation on 29/07/2014.
 */
public class DownloaderLoader{

    private static final String TAG = "DownloaderLoader";
    public static int DAY = 60 * 60 * 24;
    private AtomicBoolean isInterrupted;

    public void clearCache() {

    }

    enum DAY_IN_WEEK{
        SUNDAY(3),MONDAY(4),TUESDAY(5),WEDNESDAY(6),THURSDAY(0),FRIDAY(1),SATURDAY(2);

        final int indice;

        DAY_IN_WEEK(int indice) {
            this.indice = indice;
        }

        public int getIndice() {
            return indice;
        }

        public static DAY_IN_WEEK getEnumFromValue(int dayInWeek) {
            for(DAY_IN_WEEK enumDay : values()){
                if(enumDay.ordinal() == dayInWeek){
                    return enumDay;
                }
            }
            throw new IllegalArgumentException("Unknown indice of day. " +
                    "Only this indices are associated to a day : " +
                    "sunday = 0, monday = 1, tuesday = 2, wednesday = 3, " +
                    "thursday = 4, friday = 5, saturday = 6");
        }
    }



    public interface DownloadingListener<T> {
        void onLoadingCompleted(URL url, T result) throws MalformedURLException, JSONException;

        void onError(String errorFormatted);


    }
    public interface LoadingImageListener extends DownloadingListener<ByteArrayOutputStream>{
    }
    public interface LoadingConfigurationListener extends DownloadingListener<String>{
    }

    public DownloaderLoader() {
        isInterrupted = new AtomicBoolean(false);
    }

    public void cancel() {
        isInterrupted.set(true);
    }

    public AtomicBoolean isInterrupted() {
        return isInterrupted;
    }

    public void executeRequestAndReadStream(final URL firstUrlToRequest, final DownloadingListener delegate, final TreatmentResponse treatementPage) {
        isInterrupted.set(false);
        new Thread(new Runnable() {
            @Override
            public void run() {

                if (!isInterrupted().get()) {
                    try {
                        delegate.onLoadingCompleted(firstUrlToRequest, treatementPage.getResponseFromRequest(firstUrlToRequest));
                    } catch (Exception e) {
                        final String errorFormatted = String.format("%s Exception : %s \n %s",TAG,  e.getMessage(), e.getStackTrace());
                        Log.e(TAG, errorFormatted);
                        delegate.onError(errorFormatted);
                       // throw new RuntimeException(errorFormatted);
                    }
                }else{
                    Log.v(TAG,String.format("Annulation de la notification %s",firstUrlToRequest.toString()));
                }
            }
        }).start();
    }



     private static int calculateMaxStaleFromCurrentDate() {
        Date aujourdhui = new Date();
        SimpleDateFormat formaterDay = new SimpleDateFormat("dd");
        int day = Integer.parseInt(formaterDay.format(aujourdhui));
        SimpleDateFormat formaterMonth = new SimpleDateFormat("mm");
        int month = Integer.parseInt(formaterMonth.format(aujourdhui));
        SimpleDateFormat formaterYear = new SimpleDateFormat("yyyy");
        int year = Integer.parseInt(formaterYear.format(aujourdhui));
        return calculateMaxStale(day, month, year);
    }

    static int calculateMaxStale(int day, int month, int year) {
        DAY_IN_WEEK dayInWeek = getDayInWeekFromMikeKeithAlgorithme(day,month,year);
        int dayFromWednesday = Math.abs(DAY_IN_WEEK.WEDNESDAY.getIndice() - dayInWeek.getIndice());
        return DAY * dayFromWednesday ;
    }

    private static DAY_IN_WEEK getDayInWeekFromMikeKeithAlgorithme(int d,int m, int y) {
        int z = (m >= 3 ? y : (y - 1)), x = (m >= 3 ? 2 : 0);
        int dayInWeek = (((23 * m) / 9) + d + 4 + y + (z / 4) - (z / 100) + (z / 400) - x) % 7;
        return DAY_IN_WEEK.getEnumFromValue(dayInWeek);
    }

    public static abstract class TreatmentResponse <T>{

        private HttpURLConnection urlConnection;
        private InputStream inputStreamn;

        abstract T retrunResponse(InputStream in) throws IOException;
        private T downloadPageWithHttpUrlConnection(URL url) throws IOException {
            urlConnection = null;
            InputStream inputStreamn = null;
            try {
                urlConnection = (HttpURLConnection)url.openConnection();
                int maxStale = calculateMaxStaleFromCurrentDate();
                urlConnection.addRequestProperty("Cache-Control", "max-stale=" + maxStale);//utilisation du cache
                inputStreamn = urlConnection.getInputStream();
                Log.i("ConfigurationAsyncLoader","Le chargement de l'url  " + url + " à réussis ");
                return retrunResponse(inputStreamn);
            }finally {
                finish();
            }

        }

        private T downloadPageWithAndroidHttpClient(String stringUrl) throws IOException {
            HttpGet httpGet = new HttpGet(stringUrl);
            AndroidHttpClient androidHttpClient = AndroidHttpClient.newInstance(null);
            String result = null;
            try {
                httpGet.abort();
                result = androidHttpClient.execute(httpGet, new BasicResponseHandler());
                inputStreamn = new ByteArrayInputStream(result.getBytes());
                return retrunResponse(inputStreamn);
            }finally{
                androidHttpClient.close();
            }
        }

        private T getResponseFromRequest(final URL url) throws IOException {
            T stringResult = null;
            if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.FROYO) {
                stringResult = downloadPageWithAndroidHttpClient(url.toString()); //TODO IMPOSSIBLE DE TESTER
            }else {
                stringResult = downloadPageWithHttpUrlConnection(url);
            }
            return stringResult;
        }

        public void finish() {
            if(urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStreamn != null) {
                try {
                    inputStreamn.close();
                } catch (IOException e) {
                    Log.e(TAG, String.format("Exception : %s %s",e.getMessage(), e.getStackTrace()));
                }
            }
        }
    }

   static class TreatementPage extends TreatmentResponse<String> {
        @Override
        String retrunResponse(InputStream in) throws IOException {
            String result = null;
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line = null;
            try {
                line = reader.readLine();
                result = line;
                while ((line = reader.readLine()) != null) {
                    result += line + '\n';
                }
            }finally {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TAG, String.format("Exception : %s %s",e.getMessage(), e.getStackTrace()));
                }
            }
            return result;
        }
    }

   static class TreatementImage extends TreatmentResponse<ByteArrayOutputStream>{
        @Override
        ByteArrayOutputStream retrunResponse(InputStream inputStream) throws IOException {
            ByteArrayOutputStream baos = getByteArrayOutputStream(inputStream);
            return baos;
        }
   }


    private static ByteArrayOutputStream getByteArrayOutputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) > -1) {
                baos.write(buffer, 0, len);
            }
            baos.flush();

        return baos;
    }
}


