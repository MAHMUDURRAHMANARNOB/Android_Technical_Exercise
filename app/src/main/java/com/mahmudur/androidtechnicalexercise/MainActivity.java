package com.mahmudur.androidtechnicalexercise;

import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.LruCache;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    Button GenerateImageBtn, BanglaBtn, EnglishBtn, ErrorText;
    ImageView RandomImageView;
    Context context;

    String ErrorbtnTxt;
    Resources resources;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BanglaBtn = findViewById(R.id.btnbn);
        EnglishBtn = findViewById(R.id.btnEn);

        EnglishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context = LocalHelper.setLocale(MainActivity.this, "en");
                resources = context.getResources();
                GenerateImageBtn.setText(resources.getString(R.string.btnGenerateImg));
                ErrorText.setText(resources.getString(R.string.noconnectionmessage));
            }
        });
        BanglaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context = LocalHelper.setLocale(MainActivity.this, "bn");
                resources = context.getResources();
                GenerateImageBtn.setText(resources.getString(R.string.btnGenerateImg));
                ErrorText.setText(resources.getString(R.string.noconnectionmessage));
            }
        });

        GenerateImageBtn = (Button) findViewById(R.id.btnGeneratePicture);
        RandomImageView = (ImageView) findViewById(R.id.imgRandomImage);
        ErrorText = (Button) findViewById(R.id.ErrorText);
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String imageUrl = prefs.getString("image_url", null);
        if (imageUrl != null) {

            GeneratedImage(imageUrl);
        } else {

            Toast.makeText(MainActivity.this, " Click the button to see new pictures ", Toast.LENGTH_SHORT).show();

        }
        GenerateImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkAvailable()) {
                    final int randomNum1 = new Random().nextInt(500) + 200;
                    final int randomNum2 = new Random().nextInt(600) + 100;
                    GeneratedImage(String.valueOf(randomNum1),String.valueOf(randomNum2));
                    Toast.makeText(MainActivity.this, " Wait a bit ", Toast.LENGTH_SHORT).show();
                    ErrorText.setVisibility(View.GONE);

                }
                else{
                    ErrorText.setVisibility(View.VISIBLE);
//                    ErrorText.setText(resources.getString(R.string.noconnectionmessage));
                }

            }
        });
    }


    public void GeneratedImage(String url){
        String urlBuilder = url;

        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
        Network network = new BasicNetwork(new HurlStack());
        RequestQueue requestQueue = new RequestQueue(cache, network);
        requestQueue.start();

        ImageLoader imageLoader = new ImageLoader(requestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(20);

            @Override
            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url, bitmap);
            }
        });

        imageLoader.get(urlBuilder, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                if (response.getBitmap() != null) {
                    RandomImageView.setImageBitmap(response.getBitmap());
                }
            }
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(MainActivity.this, "No Image connection", Toast.LENGTH_SHORT).show();
            }
        });

    }
    public void GeneratedImage(String a, String b){

        String urlBuilder = "https://picsum.photos/"+a+"/"+b;;


        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
        Network network = new BasicNetwork(new HurlStack());
        RequestQueue requestQueue = new RequestQueue(cache, network);
        requestQueue.start();

        SharedPreferences.Editor editor = getSharedPreferences("MyPrefs", MODE_PRIVATE).edit();
        editor.putString("image_url", urlBuilder);
        editor.apply();

        ImageLoader imageLoader = new ImageLoader(requestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(20);

            @Override
            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url, bitmap);
            }
        });

        imageLoader.get(urlBuilder, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                if (response.getBitmap() != null) {

                    RandomImageView.setImageBitmap(response.getBitmap());
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(MainActivity.this, "No Image connection", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /*for checking internet connectivity*/
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}