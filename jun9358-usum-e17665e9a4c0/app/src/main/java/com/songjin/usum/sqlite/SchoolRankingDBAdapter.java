package com.songjin.usum.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.songjin.usum.dtos.SchoolRanking;

public class SchoolRankingDBAdapter extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String TAG = "SchoolDBAdapter";
    private SQLiteDatabase db;
    private Context context;

    public SchoolRankingDBAdapter(Context context) {
        super(context, SchoolRanking.COLLECTION_NAME, null, VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + SchoolRanking.COLLECTION_NAME + " ( " +
                        SchoolRanking.PROPERTY_ID + " INTEGER, " +
                        SchoolRanking.PROPERTY_POINT + " INTEGER, " +
                        SchoolRanking.PROPERTY_NAME + " TEXT, " +
                        SchoolRanking.PROPERTY_ADDRESS + " TEXT, " +
                        SchoolRanking.PROPERTY_CITY + " TEXT, " +
                        SchoolRanking.PROPERTY_CATEGORY + " TEXT, " +
                        SchoolRanking.PROPERTY_GU + " TEXT " +
                        ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + SchoolRanking.COLLECTION_NAME);
        onCreate(db);
    }

    public void openDB() {
        String dbPath = context.getDatabasePath(SchoolRanking.COLLECTION_NAME).getPath();
        if (db != null && db.isOpen())
            return;
        db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    public void closeDB() {
        if (db == null)
            return;
        db.close();
    }
}
