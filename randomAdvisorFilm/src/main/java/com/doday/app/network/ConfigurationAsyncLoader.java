package com.doday.app.network;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.MalformedURLException;
import java.net.URL;


/**
* Created by Formation on 25/07/2014.
*/
public class ConfigurationAsyncLoader implements DownloaderLoader.LoadingConfigurationListener {
    private static final String TAG = "ConfigurationAsyncLoader";
    private final String stringUrl;
    private static final String BASE_URL_PICTURE = "http://image.tmdb.org/t/p/w342/%s";//TODO a mettre dans un fichier de configuration
    private DownloaderLoader.LoadingImageListener activity;
    private DownloaderLoader downloaderLoader = new DownloaderLoader();
    private DownloaderLoader.TreatementPage treatementPage;
    private boolean isAborted = false;

    public ConfigurationAsyncLoader(String url, DownloaderLoader.LoadingImageListener activity) {
        this.stringUrl = url;
        this.activity = activity;
    }

    public void executeConfigurationRequest(){
        try {
            //Execute la conf
            treatementPage = new DownloaderLoader.TreatementPage();
            downloaderLoader.executeRequestAndReadStream(new URL(stringUrl), this, treatementPage);
        } catch (MalformedURLException e) {
            Log.e(TAG, String.format("Exception : %s %s",e.getMessage(), e.getStackTrace()));
        }
    }

    private void launchImagesRequest(String jsonConfiguration) throws JSONException, MalformedURLException {
        if (jsonConfiguration != null) {
            isAborted = false;
            Log.i(TAG, String.format("%s - %s",BASE_URL_PICTURE ,jsonConfiguration));
            JSONObject response = new JSONObject(jsonConfiguration);
            JSONArray results = response.getJSONArray("results");
            int i = 0;

            while (i < results.length() && !isAborted()) {
                JSONObject film = results.getJSONObject(i);
                String imageRessourceName = film.getString("backdrop_path");
                imageRessourceName = imageRessourceName.trim();
                if (imageRessourceName.equals("null") || imageRessourceName.length() == 0) {
                    imageRessourceName = film.getString("poster_path"); //TODO et si même le poster est a null, que faire ??
                }
                Log.i(TAG, String.format("image %d %s : ", i,imageRessourceName));
                imageRessourceName.trim();
                if(!imageRessourceName.trim().equals("null")) {
                    downloaderLoader.executeRequestAndReadStream(
                            new URL(String.format(BASE_URL_PICTURE, imageRessourceName)), activity, new DownloaderLoader.TreatementImage());
                }
                i++;
            }
            if(isAborted()){
                    Log.v(TAG,"Annulation de la boucle pour lancer le reste des requêtes d'images");
            }
            Log.i(TAG, String.format("Il y a %d images récupérées sur le serveur %s", i, BASE_URL_PICTURE));
        }
    }

    @Override
    public void onLoadingCompleted(URL url, String result) throws MalformedURLException, JSONException {
        launchImagesRequest(result);
    }

    @Override
    public void onError(String errorFormatted) {
        if(activity != null){
            activity.onError(errorFormatted);
        }
    }

    public void cancel() {
        Log.v(TAG,"Fermeture des connections");
        isAborted = true;
        downloaderLoader.cancel(); //annule le fait qu'on prévienne l'activity de l'arrivé de nouvelles images
        treatementPage.finish();//annule le traitement des requêtes
        activity = null;

    }

    public boolean isAborted() {
        return isAborted;
    }

}
