package com.mahmudur.androidtechnicalexercise;

import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.LruCache;

import android.content.Context;
import android.content.SharedPreferences;
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

    Button GenerateImageBtn;
    ImageView RandomImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GenerateImageBtn = (Button) findViewById(R.id.btnGeneratePicture);
        RandomImageView = (ImageView) findViewById(R.id.imgRandomImage);
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String imageUrl = prefs.getString("image_url", null);
        if (imageUrl != null) {
            // Load the image using the URL
            GeneratedImage(imageUrl);
        } else {
            // No URL saved in SharedPreferences, load the default image
            Toast.makeText(MainActivity.this, "Click the button to see new pictures", Toast.LENGTH_SHORT).show();

        }
        GenerateImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkAvailable()) {
                     // [0, 60] + 20 => [20, 80]
                    final int randomNum1 = new Random().nextInt(500) + 200; // [0, 60] + 20 => [20, 80]
                    final int randomNum2 = new Random().nextInt(600) + 100;
                    GeneratedImage(String.valueOf(randomNum1),String.valueOf(randomNum2));
                    Toast.makeText(MainActivity.this, "Wait a Bit", Toast.LENGTH_SHORT).show();

                }
                else{
                    Toast.makeText(MainActivity.this, "No network connection", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    public void GeneratedImage(String url){
        String urlBuilder = url;

        // Set up the cache
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
        Network network = new BasicNetwork(new HurlStack());
        RequestQueue requestQueue = new RequestQueue(cache, network);
        requestQueue.start();

        // Set up the ImageLoader with the same RequestQueue
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
                    // Image loaded from cache or network
                    RandomImageView.setImageBitmap(response.getBitmap());
                }
            }
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle error
                Toast.makeText(MainActivity.this, "No Image connection", Toast.LENGTH_SHORT).show();
            }
        });

    }
    public void GeneratedImage(String a, String b){

        String urlBuilder = "https://picsum.photos/"+a+"/"+b;;

        // Set up the cache
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
                    // Image loaded from cache or network
                    RandomImageView.setImageBitmap(response.getBitmap());
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle error
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