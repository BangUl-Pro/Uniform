package com.songjin.usum.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.songjin.usum.Global;
import com.songjin.usum.entities.SchoolEntity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class SchoolDBAdapter extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String TAG = "SchoolDBAdapter";
    private SQLiteDatabase db;
    private Context context;

    public SchoolDBAdapter(Context context) {
        super(context, SchoolEntity.COLLECTION_NAME, null, VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + SchoolEntity.COLLECTION_NAME + " ( " +
                        SchoolEntity.PROPERTY_ID + " INTEGER, " +
                        SchoolEntity.PROPERTY_SCHOOLNAME + " TEXT, " +
                        SchoolEntity.PROPERTY_ADDRESS + " TEXT, " +
                        SchoolEntity.PROPERTY_CITY + " TEXT, " +
                        SchoolEntity.PROPERTY_CATEGORY + " TEXT, " +
                        SchoolEntity.PROPERTY_GU + " TEXT " +
                        ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + SchoolEntity.COLLECTION_NAME);
        onCreate(db);
    }

    public void openDB() {
        String dbPath = context.getDatabasePath(SchoolEntity.COLLECTION_NAME).getPath();
        Log.d(TAG, "dbPath = " + dbPath);
        if (db != null && db.isOpen())
            return;
        db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    public void closeDB() {
        if (db == null)
            return;
        db.close();
    }

    public void copyDB() {
        try {
            InputStream is = context.getResources().getAssets().open(SchoolEntity.COLLECTION_NAME);
            File dir = new File("/data/data/" + Global.PACKAGE_NAME + "/databases/");
            File file = new File("/data/data/" + Global.PACKAGE_NAME + "/databases/" + SchoolEntity.COLLECTION_NAME);
            Log.d(TAG, "file Path = " + file.getPath());
            if (!dir.exists()) {
                Log.d(TAG, "파일 만듬");
                Log.d(TAG, "성공? = " + dir.mkdirs());
            }

            if (!file.exists()) {
                Log.d(TAG, "파일 만들기 = " + file.createNewFile());
            }

            if (!file.isFile()) {
                Log.d(TAG, "파일 삭제 = " + file.delete());
                Log.d(TAG, "폴더 -> 파일 = " + file.createNewFile());
            }

            String outFileName = file.getPath();
            OutputStream os = new FileOutputStream(outFileName);
            byte[] buffer = new byte[1024 * 8];
            int length = 0;
            while ((length = is.read(buffer)) > 0)
                os.write(buffer, 0, length);
            os.flush();
            os.close();
            is.close();
            Log.d(TAG, "COPY DB");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
