package com.callender.mayancal.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
/*
 *
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private final String TAG = "DatabaseHelper";

    private static final int    databaseVersion = 1;
    private static final String databaseName = "dbTest";
    private static final String TABLE_IMAGE = "ImageTable";

    // Image Table Columns names
    private static final String COL_ID = "col_id";
    private static final String IMAGE_ID = "image_id";
    private static final String IMAGE_BITMAP = "image_bitmap";

    DatabaseHelper(Context context) {
        super(context, databaseName, null, databaseVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String CREATE_IMAGE_TABLE = "CREATE TABLE " + TABLE_IMAGE + "("
                + COL_ID + " INTEGER PRIMARY KEY,"
                + IMAGE_ID + " TEXT,"
                + IMAGE_BITMAP + " TEXT)";

        Log.d(TAG, "** table create: " + CREATE_IMAGE_TABLE );

        sqLiteDatabase.execSQL(CREATE_IMAGE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Drop older table if existed
        Log.d(TAG, "** onUpgrade");

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGE);
        onCreate(sqLiteDatabase);
    }

    public boolean checkForTableExists() {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "select DISTINCT tbl_name from sqlite_master where tbl_name='" + TABLE_IMAGE + "'";
        Log.d(TAG, "sql: \"" + sql + "\"");
        Cursor mCursor = db.rawQuery(sql, null);
        if (mCursor.getCount() > 0) {
            //mCursor.close();
            return true;
        }
        mCursor.close();
        return false;
    }

    public void insetImage(String imageId, byte[] image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(IMAGE_ID, imageId);
        values.put(IMAGE_BITMAP, image);
        db.insert(TABLE_IMAGE, null, values);
        db.close();
    }

    public ImageHelper getImage(String imageId) {
        Log.d(TAG, "** getImage");
        SQLiteDatabase db = this.getWritableDatabase();

        String[] string = new String[] {COL_ID, IMAGE_ID, IMAGE_BITMAP};
        Log.d(TAG, "query: " + string[0] + ", " + string[1] + ", " + string[2]);

        Cursor cursor =
                db.query(TABLE_IMAGE, string, IMAGE_ID +
                        " LIKE '" + imageId + "%'",
                        null, null, null, null);

        ImageHelper imageHelper = new ImageHelper();

        if (cursor.moveToFirst()) {
            do {
                imageHelper.setImageId(cursor.getString(1));
                imageHelper.setImageByteArray(cursor.getBlob(2));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return imageHelper;
    }

}
