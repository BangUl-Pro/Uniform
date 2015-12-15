package com.ironfactory.donation.managers;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ironfactory.donation.entities.SchoolEntity;
import com.ironfactory.donation.sqlite.SchoolDBAdapter;

import java.util.ArrayList;

public class SchoolManager {
    private static final String TAG = "SchoolManager";
    private SchoolDBAdapter adapter;

    public SchoolManager(Context context) {
        adapter = new SchoolDBAdapter(context);
    }

    public void insertSchools(ArrayList<SchoolEntity> schoolEntities) {
        SQLiteDatabase db = adapter.getWritableDatabase();

        for (SchoolEntity schoolEntity : schoolEntities) {
            db.insert(SchoolEntity.COLLECTION_NAME, null, schoolEntity.getContentValues());
        }

        db.close();
    }

    public ArrayList<SchoolEntity> selectSchools() {
        SQLiteDatabase db = adapter.getReadableDatabase();

        Cursor cursor = db.query(SchoolEntity.COLLECTION_NAME, null, null, null, null, null, null, null);
        ArrayList<SchoolEntity> schoolEntities = new ArrayList<>();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            schoolEntities.add(new SchoolEntity(cursor));
            cursor.moveToNext();
        }
        cursor.close();

        db.close();

        return schoolEntities;
    }

    public SchoolEntity selectSchool(int schoolId) {
        SQLiteDatabase db = adapter.getReadableDatabase();
        Log.d(TAG, "schoolId = " + schoolId);

        Cursor cursor = db.query(
                SchoolEntity.COLLECTION_NAME,
                null,
                SchoolEntity.PROPERTY_ID + "=?",
                new String[]{String.valueOf(schoolId)},
                null,
                null,
                null,
                null
        );

        cursor.moveToFirst();
        SchoolEntity schoolEntity = new SchoolEntity(cursor);
        cursor.close();

        db.close();

        return schoolEntity;
    }

    public void deleteAllSchools() {
        SQLiteDatabase db = adapter.getWritableDatabase();

        db.delete(SchoolEntity.COLLECTION_NAME, null, null);
        db.close();
    }

    public boolean isEmptyTable() {
        SQLiteDatabase db = adapter.getReadableDatabase();

        int count = 0;
        Cursor cursor = db.query(SchoolEntity.COLLECTION_NAME, null, null, null, null, null, null, "1");
        if (cursor != null) {
            count = cursor.getCount();
            cursor.close();
        }

        db.close();

        return !(0 < count);
    }
}
