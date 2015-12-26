package com.songjin.usum.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.songjin.usum.entities.SchoolEntity;

public class SchoolDBAdapter extends SQLiteOpenHelper {
    private static final int VERSION = 1;

    public SchoolDBAdapter(Context context) {
        super(context, SchoolEntity.COLLECTION_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + SchoolEntity.COLLECTION_NAME + " ( " +
                        SchoolEntity.PROPERTY_ID + " INTEGER, " +
//                        SchoolEntity.PROPERTY_UUID + " TEXT, " +
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
}
