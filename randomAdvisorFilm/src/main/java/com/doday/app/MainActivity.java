
package com.doday.app;

import android.net.http.HttpResponseCache;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.doday.app.adapter.ImageAdapter;
import com.doday.app.network.ConfigurationAsyncLoader;
import com.doday.app.network.DownloaderLoader;
import com.doday.app.network.MyHttpCache;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class MainActivity extends ActionBarActivity implements DownloaderLoader.LoadingImageListener {

    private static final String TAG = "MainActivity";
    private TextView hello;
    public static final String API_KEY_MOVIE_DB = "189ec91ba809cb4d27ef56780e4aa516";//TODO a mettre dans un fichier de configuration
    private static final String BASE_URL_CONFIGURATION = "http://api.themoviedb.org/3/movie/now_playing"; //TODO a mettre dans un fichier de configuration
    GridView gridView;
    private ConfigurationAsyncLoader asyncLoader;
    private MyHttpCache myHttpCache;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myHttpCache = new MyHttpCache();
        myHttpCache.enableHttpResponseCache(getApplicationContext());

        asyncLoader = new ConfigurationAsyncLoader(BASE_URL_CONFIGURATION +
                "?api_key=" +API_KEY_MOVIE_DB,this);
        asyncLoader.executeConfigurationRequest();//TODO asyncLoader.executeConfigurationRequest(traiementConfiguration(url),traitementImage);

        setContentView(R.layout.activity_main);
        gridView = (GridView)findViewById(R.id.grid);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myHttpCache.desableHttpResponseCache();
        asyncLoader.cancel();
    }
    ByteArrayOutputStream[] tabCinemaThumb = new ByteArrayOutputStream[20];
    int cpt = 0;

    @Override
    public void loadingComplete(URL url, ByteArrayOutputStream image) {
        tabCinemaThumb[cpt] = image;
        cpt++;
        if(19 == cpt){
            cpt = 0;
            initializeGridView(tabCinemaThumb);
        }
    }

    @Override
    public void onError(final String errorFormatted) {
        runOnUiThread(new Runnable() {
                          @Override
                          public void run() {
                              Toast.makeText(MainActivity.this,errorFormatted,Toast.LENGTH_LONG).show();
                          }
                      });
    }

    private void initializeGridView(final ByteArrayOutputStream[] tabCinemaThumb) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                gridView.setAdapter(new ImageAdapter(MainActivity.this, tabCinemaThumb));
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                        Toast.makeText(MainActivity.this, "Hello " + position, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_actions,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_clear_cache:
                myHttpCache.clearCache(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
