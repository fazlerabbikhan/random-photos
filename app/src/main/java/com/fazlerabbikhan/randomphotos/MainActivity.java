package com.fazlerabbikhan.randomphotos;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final String LAST_FETCHED_IMAGE = "last_fetched_image";

    private ImageView imagePlaceholder;
    private TextView errorMessage;

    private Bitmap lastFetchedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button fetchButton = findViewById(R.id.button_fetch);
        imagePlaceholder = findViewById(R.id.image_placeholder);
        errorMessage = findViewById(R.id.error_message);

        fetchButton.setOnClickListener(v -> {
            if (isNetworkConnected()) {
                errorMessage.setVisibility(View.GONE);
                new FetchImageTask().execute();
            } else {
                showErrorMessage();
            }
        });

        if (savedInstanceState != null) {
            lastFetchedImage = savedInstanceState.getParcelable(LAST_FETCHED_IMAGE);
        }

        // Checking if a previously fetched image exists and setting it as the placeholder
        if (lastFetchedImage != null) {
            imagePlaceholder.setImageBitmap(lastFetchedImage);
        }
    }

    // Keeping the state of activity even if I rotate my screen
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(LAST_FETCHED_IMAGE, lastFetchedImage);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        lastFetchedImage = savedInstanceState.getParcelable(LAST_FETCHED_IMAGE);
    }

    // Checking if internet is available
    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    // Showing error message
    private void showErrorMessage() {
        Toast.makeText(this, R.string.error_message_text, Toast.LENGTH_SHORT).show();
        errorMessage.setVisibility(View.VISIBLE);
    }

    @SuppressLint("StaticFieldLeak")
    private class FetchImageTask extends AsyncTask<Void, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URL url = new URL("https://picsum.photos/200");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                return BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                lastFetchedImage = bitmap;
                imagePlaceholder.setImageBitmap(bitmap);
            }
        }
    }
}