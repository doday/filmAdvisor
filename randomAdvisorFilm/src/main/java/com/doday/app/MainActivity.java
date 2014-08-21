
package com.doday.app;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.doday.app.adapter.ImageAdapter;
import com.doday.app.image.MyBitmapFactory;
import com.doday.app.network.ConfigurationAsyncLoader;
import com.doday.app.network.DownloaderLoader;
import com.doday.app.network.FromApi8HttpCache;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends ActionBarActivity implements DownloaderLoader.LoadingImageListener, Animator.AnimatorListener {

    private static final String TAG = "MainActivity";
    public static final String API_KEY_MOVIE_DB = "189ec91ba809cb4d27ef56780e4aa516";//TODO a mettre dans un fichier de configuration
    private static final String BASE_URL_CONFIGURATION = "http://api.themoviedb.org/3/movie/now_playing"; //TODO a mettre dans un fichier de configuration
    private GridView gridView;
    private ConfigurationAsyncLoader asyncLoader;
    private FromApi8HttpCache myHttpCache;
    private ArrayList<Bitmap> listCinemaThumb;//quel est le plus lours ? des bitmap ou des  ByteArrayOutputStream ou même des bytes ?
    private ImageAdapter adapter;
    private int currentFilm;
    private ArrayList<Integer> returnedImage = new ArrayList<Integer>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gridView = (GridView)findViewById(R.id.grid);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                currentFilm = position;
                ObjectAnimator animRotate = ObjectAnimator.ofFloat(view,"rotationY",0f,540f);
                animRotate.setDuration(500);
                animRotate.addListener(MainActivity.this);
                animRotate.start();


            }
        });
        listCinemaThumb = new ArrayList<Bitmap>();
        adapter = new ImageAdapter(this, listCinemaThumb);
        gridView.setAdapter(adapter);


        myHttpCache = new FromApi8HttpCache();
        myHttpCache.enableHttpResponseCache(getApplicationContext());
        asyncLoader = new ConfigurationAsyncLoader(BASE_URL_CONFIGURATION +
                "?api_key=" +API_KEY_MOVIE_DB,this);
        asyncLoader.executeConfigurationRequest();//TODO asyncLoader.executeConfigurationRequest(traiementConfiguration(url),traitementImage);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_actions, menu);
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

    @Override
    public void onLoadingCompleted(URL url, ByteArrayOutputStream image) {
        initializeGridView(image);
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

    private void initializeGridView(final ByteArrayOutputStream image) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final Bitmap bitmap = MyBitmapFactory.getBitmapAtDimensions(image, ImageAdapter.thumbSize,
                        ImageAdapter.thumbSize);
                listCinemaThumb.add(bitmap);
                adapter.notifyDataSetChanged();

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myHttpCache.desableHttpResponseCache();
        asyncLoader.cancel();
    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {


        //if(!returnedImage.contains(currentFilm)) {
            final Bitmap bitmap = MyBitmapFactory.
                    getBitmapAtDimensions(getResources(),
                            R.drawable.thumb_black_pink_generated, ImageAdapter.thumbSize,
                            ImageAdapter.thumbSize);
            listCinemaThumb.set(currentFilm, bitmap);
            returnedImage.add(currentFilm);
        /*}else{
            final Bitmap bitmap2 = MyBitmapFactory.
                    getBitmapAtDimensions(getResources(),
                            R.drawable.ic_launcher, ImageAdapter.thumbSize,
                            ImageAdapter.thumbSize);
            listCinemaThumb.set(currentFilm,bitmap2);
            returnedImage.remove(Integer.valueOf(currentFilm));
        }*/
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }
}
