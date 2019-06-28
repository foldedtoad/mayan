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
import android.widget.ImageView;
import android.widget.TextView;

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

    private com.callender.mayancal.db.DatabaseHelper databaseHelper;
    private ImageView imageView;
    private TextView textViewTitle;
    private TextView textViewLatin;
    private TextView textViewMayan;

    JSONArray jsonGlyphsArray;

    float  swipe_x1,swipe_x2;
    static final int MIN_DISTANCE = 150;

    int index = 0;
    String IMAGE_ID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        databaseHelper = new DatabaseHelper(this);

        textViewTitle = findViewById(R.id.textView_title);
        imageView     = findViewById(R.id.imageView);
        textViewLatin = findViewById(R.id.textView_latin);
        textViewMayan = findViewById(R.id.textView_mayan);

        new LoadDatabaseTask().execute(0);
    }

    public String loadJSONFromAsset() {

        int byteCount;
        Log.d(TAG, "** loadJSONFromAsset");

        String json;
        try {
            InputStream inStream = MainActivity.this.getAssets().open("glyphs.json");

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

            jsonGlyphsArray = jsonObject.getJSONArray("glyphs");

            for (int i = 0; i < jsonGlyphsArray.length(); i++) {

                JSONObject jo_inside = jsonGlyphsArray.getJSONObject(i);

                String name = jo_inside.getString("name");
                int    len  = jo_inside.getInt("len");
                String data = jo_inside.getString("data");

                byte[] image = Base64.decode(data.getBytes(), Base64.DEFAULT);

                Log.d(TAG,"** Details: name: " + name + ", len=" + len);

                databaseHelper.insetImage(name, image);

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
                    if (index < 0)  index = 0;
                    if (index > 19) index = 19;
                    Log.d(TAG, "index:" + index);
                    IMAGE_ID = String.format("mayan_%02d", index);

                    // Left-to-Right swipe direction
                    if (swipe_x2 > swipe_x1) {
                        Log.d(TAG, "Swipe [Previous]");
                        index--;
                        new LoadImageFromDatabaseTask().execute(0);
                    }
                    // Right-to-left swipe direction
                    else {
                        Log.d(TAG, "Swipe [Next]");
                        index++;
                        new LoadImageFromDatabaseTask().execute(0);
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
            String IMAGE_ID = "mayan_creation";
            loadGlyphsJSON();
            return databaseHelper.getImage(IMAGE_ID);
        }

        protected void onPostExecute(com.callender.mayancal.db.ImageHelper imageHelper) {
            Log.d(TAG, "** onPostExecute: ImageID " + imageHelper.getImageId());
            if (this.LoadProgressDialog.isShowing()) {
                this.LoadProgressDialog.dismiss();
            }
            setUpImage(imageHelper.getImageByteArray());
        }
    }


    private class LoadImageFromDatabaseTask extends AsyncTask<Integer, Integer, com.callender.mayancal.db.ImageHelper> {

        protected void onPreExecute() {
            Log.d(TAG, "** onPreExecute");
        }

        @Override
        protected com.callender.mayancal.db.ImageHelper doInBackground(Integer... integers) {
            Log.d(TAG, "** doInBackground");
            return databaseHelper.getImage(IMAGE_ID);
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

        textViewTitle.setText(IMAGE_ID);

        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        imageView.setImageBitmap(bitmap);
    }

}