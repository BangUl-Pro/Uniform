package com.songjin.usum.managers;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.songjin.usum.dtos.SchoolRanking;
import com.songjin.usum.sqlite.SchoolRankingDBAdapter;

import java.util.ArrayList;

public class SchoolRankingManager {
    private static final String TAG = "SchoolRankingManager";
    private SchoolRankingDBAdapter adapter;

    public SchoolRankingManager(Context context) {
        adapter = new SchoolRankingDBAdapter(context);
    }

    public void insertSchoolRankings(ArrayList<SchoolRanking> schoolRankingEntities) {
        SQLiteDatabase db = adapter.getWritableDatabase();

        for (int i = 0; i < 30; i++) {
            db.insert(SchoolRanking.COLLECTION_NAME, null, schoolRankingEntities.get(i).getContentValues());
        }
        db.close();
    }

    public ArrayList<SchoolRanking> selectSchoolRankings() {
        SQLiteDatabase db = adapter.getReadableDatabase();

        Cursor cursor = db.query(SchoolRanking.COLLECTION_NAME, null, null, null, null, null, null, null);
        ArrayList<SchoolRanking> schoolEntities = new ArrayList<>();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            schoolEntities.add(new SchoolRanking(cursor));
            cursor.moveToNext();
        }
        cursor.close();

        db.close();

        return schoolEntities;
    }

    public SchoolRanking selectSchool(int schoolId) {
        if (adapter == null)
            return null;
        SQLiteDatabase db = adapter.getReadableDatabase();

        Cursor cursor = db.query(
                SchoolRanking.COLLECTION_NAME,
                null,
                SchoolRanking.PROPERTY_ID + "=?",
                new String[]{String.valueOf(schoolId)},
                null,
                null,
                null,
                null
        );

        cursor.moveToFirst();
        SchoolRanking schoolEntity = new SchoolRanking(cursor);
        cursor.close();

        db.close();

        return schoolEntity;
    }

    public void deleteAllSchoolRankings() {
        SQLiteDatabase db = adapter.getWritableDatabase();

        db.delete(SchoolRanking.COLLECTION_NAME, null, null);
        db.close();
    }
}
