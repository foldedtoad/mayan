package com.callender.mayancal.db;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/*
 *
 */
public class MainActivity extends Activity {

    final String TAG = "MainActivity";

    private ConstraintLayout mainLayout;
    private com.callender.mayancal.db.DatabaseHelper databaseHelper;
    private ImageView imageView;

    private TextView textViewTitle;
    private TextView textViewLatin;
    private TextView textViewMayan;

    JSONArray jsonGlyphsArray;

    float  swipe_x1,swipe_x2;
    static final int MIN_DISTANCE = 150; // x-coordinate must travel to indicate a swipe

    int index = 0;
    String image_id;
    String image_name_format;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mainLayout = findViewById(R.id.main_layout);
        //mainLayout.setVisibility(View.INVISIBLE);

        image_name_format = getString(R.string.image_name_format);

        textViewTitle = findViewById(R.id.textView_title);
        imageView     = findViewById(R.id.imageView);
        textViewLatin = findViewById(R.id.textView_latin);
        textViewMayan = findViewById(R.id.textView_mayan);

        databaseHelper = new DatabaseHelper(this);

        textViewTitle.setText(R.string.splash_title);
        textViewMayan.setText("");
        textViewLatin.setText("");
        imageView.setImageResource(R.drawable.splash);

        new LoadDatabaseTask().execute(0);
    }

    public String loadJSONFromAsset() {

        int byteCount;
        Log.d(TAG, "** loadJSONFromAsset");

        String json;
        try {
            InputStream inStream = this.getAssets().open(getString(R.string.glyphs_filename));

            int size = inStream.available();

            byte[] buffer = new byte[size];

            byteCount = inStream.read(buffer);

            inStream.close();

            json = new String(buffer, "UTF-8");

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        Log.d(TAG, "** loadJSONFromAsset OK, byteCount " + byteCount);
        return json;
    }

    private void loadGlyphsJSON() {

        Log.d(TAG, "** loadGlyphsJSON");

        try {
            JSONObject jsonObject = new JSONObject(loadJSONFromAsset());

            jsonGlyphsArray = jsonObject.getJSONArray("");

            Log.d(TAG,"** json: len=" + jsonGlyphsArray.length());

            for (int i = 0; i < jsonGlyphsArray.length(); i++) {

                JSONObject jo_inside = jsonGlyphsArray.getJSONObject(i);

                String name  = jo_inside.getString("name");
                int    len   = jo_inside.getInt("len");
                String data  = jo_inside.getString("data");
                //String mayan = jo_inside.getString("mayan");
                //String latin = jo_inside.getString("latin");

                byte[] image = Base64.decode(data.getBytes(), Base64.DEFAULT);

                Log.d(TAG,"** Details: name: " + name + ", len=" + len); // +
                //        ", mayan=" + mayan + ", latin=" + latin);

                databaseHelper.insertImage(name, image); //, mayan, latin);

            }
        } catch (JSONException e) {
            Log.d(TAG, "** loadGlyphsJSON exception: " + e.getMessage());
            return;
        }

        Log.d(TAG, "** loadGlyphsJSON OK");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch(event.getAction()) {

            // Finger down (start of gesture)
            case MotionEvent.ACTION_DOWN:
                swipe_x1 = event.getX();
                break;

            // Finger up (end of gesture)
            case MotionEvent.ACTION_UP:
                swipe_x2 = event.getX();
                float deltaX = swipe_x2 - swipe_x1;

                if (Math.abs(deltaX) > MIN_DISTANCE) {
 
                    // Left-to-Right swipe direction
                    if (swipe_x2 > swipe_x1) {
                        Log.d(TAG, "Swipe Direction [Previous]");
                        image_id = String.format(image_name_format, index);
                        Log.d(TAG, "image_id:" + image_id);
                        new LoadImageFromDatabaseTask().execute(0);
                        if (index > 0)  // smallest Mayan digit: 0
                            index--;
                    }
                    // Right-to-left swipe direction
                    else {
                        Log.d(TAG, "Swipe Direction [Next]");
                        image_id = String.format(image_name_format, index);
                        Log.d(TAG, "image_id:" + image_id);
                        new LoadImageFromDatabaseTask().execute(0);
                        if (index < 19)  // largest Mayan digit: 19
                            index++;
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    private class LoadDatabaseTask extends AsyncTask<Integer, Integer, com.callender.mayancal.db.ImageHelper> {

        private final ProgressDialog LoadProgressDialog = new ProgressDialog(MainActivity.this);

        protected void onPreExecute() {
            Log.d(TAG, "** onPreExecute");
            this.LoadProgressDialog.setMessage("Loading Image Database...");
            this.LoadProgressDialog.show();
        }

        @Override
        protected com.callender.mayancal.db.ImageHelper doInBackground(Integer... integers) {
            Log.d(TAG, "** doInBackground");
            loadGlyphsJSON();
            return databaseHelper.getImage(image_id);
        }

        protected void onPostExecute(com.callender.mayancal.db.ImageHelper imageHelper) {
            Log.d(TAG, "** onPostExecute: ImageID " + imageHelper.getImageId());
            if (this.LoadProgressDialog.isShowing()) {
                this.LoadProgressDialog.dismiss();
            }
            mainLayout.setVisibility(View.VISIBLE);
        }
    }

    private class LoadImageFromDatabaseTask extends AsyncTask<Integer, Integer, com.callender.mayancal.db.ImageHelper> {

        protected void onPreExecute() {
            Log.d(TAG, "** onPreExecute");
        }

        @Override
        protected com.callender.mayancal.db.ImageHelper doInBackground(Integer... integers) {
            Log.d(TAG, "** doInBackground");
            return databaseHelper.getImage(image_id);
        }

        protected void onPostExecute(com.callender.mayancal.db.ImageHelper imageHelper) {
            if (imageHelper.getImageId() != null) {
                Log.d(TAG, "** onPostExecute: ImageID: " + imageHelper.getImageId());
                setUpImage(imageHelper.getImageByteArray());
            }
            else {
                Log.d(TAG, "** onPostExecute: ImageID: null");
            }
        }
    }

    private void setUpImage(byte[] bytes) {
        Log.d(TAG, "** setUpImage");

        textViewTitle.setText(image_id);
        textViewMayan.setText("mayan pronouncation");
        textViewLatin.setText("english annotation");

        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        imageView.setImageBitmap(bitmap);
    }

}