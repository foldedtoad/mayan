package com.callender.mayancal.db;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        databaseHelper = new DatabaseHelper(this);

        textViewTitle = findViewById(R.id.textView_title);
        imageView     = findViewById(R.id.imageView);
        textViewLatin = findViewById(R.id.textView_latin);
        textViewMayan = findViewById(R.id.textView_mayan);

        new LoadImageFromDatabaseTask().execute(0);
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

    private class LoadImageFromDatabaseTask extends AsyncTask<Integer, Integer, com.callender.mayancal.db.ImageHelper> {

        private final ProgressDialog LoadImageProgressDialog = new ProgressDialog(MainActivity.this);

        protected void onPreExecute() {
            Log.d(TAG, "** onPreExecute");
            this.LoadImageProgressDialog.setMessage("Loading Image Database...");
            this.LoadImageProgressDialog.show();
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
            if (this.LoadImageProgressDialog.isShowing()) {
                this.LoadImageProgressDialog.dismiss();
            }
            setUpImage(imageHelper.getImageByteArray());
        }
    }

    private void setUpImage(byte[] bytes) {
        Log.d(TAG, "** setUpImage");

        String desc = "Mayan Creation";
        textViewTitle.setText(desc);

        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        imageView.setImageBitmap(bitmap);
    }

}