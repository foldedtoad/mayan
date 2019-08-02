package com.callender.mayannum.db;

import android.annotation.SuppressLint;
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
    private com.callender.mayannum.db.DatabaseHelper databaseHelper;
    private ImageView imageView;

    private TextView textViewTitle;
    private TextView textViewLatin;
    private TextView textViewMayan;

    boolean firstTap = false;
    long tapTime;
    float  swipe_x1,swipe_x2;
    static final int MIN_DISTANCE = 150; // x-coordinate must travel to indicate a swipe

    int index = 0;
    String image_id;
    String image_name_format;
    byte[] sound_clip;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        tapTime = System.currentTimeMillis();

        mainLayout = findViewById(R.id.main_layout);

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
        String json;

        try {
            InputStream inStream = this.getAssets().open(getString(R.string.glyphs_filename));

            int size = inStream.available();

            byte[] buffer = new byte[size];

            byteCount = inStream.read(buffer);
            if (byteCount == 0) { return null; }

            inStream.close();

            json = new String(buffer, "UTF-8");

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private void loadGlyphsJSON() {
        try {
            String json = loadJSONFromAsset();
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.names();

            for (int i = 0; i < jsonArray.length(); i++) {
                String name = jsonArray.getString(i);
                String string = jsonObject.getJSONObject(name).toString();
                JSONObject jo_inside = new JSONObject(string);

                String _image  = jo_inside.getString("image");
                String _sound  = jo_inside.getString("sound");
                String mayan = jo_inside.getString("mayan");
                String latin = jo_inside.getString("latin");

                byte[] image = Base64.decode(_image.getBytes(), Base64.DEFAULT);
                byte[] sound = Base64.decode(_sound.getBytes(), Base64.DEFAULT);

                //Log.d(TAG,"** Details: name: " + name + ", len=" +
                //        ", mayan=" + mayan + ", latin=" + latin);

                databaseHelper.insertImage(name, image, sound, mayan, latin);

            }
        } catch (JSONException e) {
            Log.d(TAG, "** loadGlyphsJSON exception: " + e.getMessage());
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch(event.getAction()) {

            // Finger down (start of gesture)
            case MotionEvent.ACTION_DOWN:

                // double tap --> play sound clip
                if (firstTap && (System.currentTimeMillis() - tapTime) <= 300) {
                    // do stuff here for double tap
                    Log.d(TAG, "** DOUBLE TAP ** second tap ");
                    playSoundClip();
                    firstTap = false;
                }
                else {
                    firstTap = true;
                    tapTime = System.currentTimeMillis();
                }

                // detect initial swipe action
                swipe_x1 = event.getX();
                break;

            // Finger up (end of gesture)
            case MotionEvent.ACTION_UP:
                swipe_x2 = event.getX();
                float deltaX = swipe_x2 - swipe_x1;

                if (Math.abs(deltaX) > MIN_DISTANCE) {
 
                    // Left-to-Right swipe direction
                    if (swipe_x2 > swipe_x1) {
                        //Log.d(TAG, "Swipe Direction [Previous]");
                        image_id = String.format(image_name_format, index);
                        //Log.d(TAG, "image_id:" + image_id);
                        new LoadImageFromDatabaseTask().execute(0);
                        if (index > 0)  // smallest Mayan digit: 0
                            index--;
                    }
                    // Right-to-left swipe direction
                    else {
                        //Log.d(TAG, "Swipe Direction [Next]");
                        image_id = String.format(image_name_format, index);
                        //Log.d(TAG, "image_id:" + image_id);
                        new LoadImageFromDatabaseTask().execute(0);
                        if (index < 19)  // largest Mayan digit: 19
                            index++;
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    @SuppressLint("StaticFieldLeak")
    private class LoadDatabaseTask extends AsyncTask<Integer, Integer, com.callender.mayannum.db.ImageHelper> {

        private final ProgressDialog LoadProgressDialog = new ProgressDialog(MainActivity.this);

        protected void onPreExecute() {
            this.LoadProgressDialog.setMessage("Loading Image Database...");
            this.LoadProgressDialog.show();
        }

        @Override
        protected com.callender.mayannum.db.ImageHelper doInBackground(Integer... integers) {
            loadGlyphsJSON();
            return databaseHelper.getImage(image_id);
        }

        protected void onPostExecute(com.callender.mayannum.db.ImageHelper imageHelper) {
            if (this.LoadProgressDialog.isShowing()) {
                this.LoadProgressDialog.dismiss();
            }
            mainLayout.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class LoadImageFromDatabaseTask extends AsyncTask<Integer, Integer, com.callender.mayannum.db.ImageHelper> {

        protected void onPreExecute() { }

        @Override
        protected com.callender.mayannum.db.ImageHelper doInBackground(Integer... integers) {
            return databaseHelper.getImage(image_id);
        }

        protected void onPostExecute(com.callender.mayannum.db.ImageHelper imageHelper) {
            if (imageHelper.getImageId() != null) {
                byte[] image = imageHelper.getImageByteArray();
                String mayan = imageHelper.getMayanText();
                String latin = imageHelper.getLatinText();
                setUpImage(image, mayan, latin);

                sound_clip  = imageHelper.getSoundByteArray();

            }
            else {
                Log.d(TAG, "** onPostExecute: ImageID: null");
            }
        }
    }

    private void setUpImage(byte[] image, String mayan, String latin) {

        textViewTitle.setText(image_id);
        textViewMayan.setText(mayan);
        textViewLatin.setText(latin);

        Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);

        imageView.setImageBitmap(bitmap);
    }

    private void playSoundClip() {
        Log.d(TAG, "play sound clip");

        if (sound_clip != null && sound_clip.length > 10) {
            SoundHelper soundHelper = new SoundHelper();
            soundHelper.prepare(sound_clip);
            soundHelper.play();
        }
    }

}