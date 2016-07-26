package com.songjin.usum.dtos;

import android.content.ContentValues;
import android.database.Cursor;

import com.songjin.usum.entities.SchoolEntity;
import com.songjin.usum.entities.SchoolPointEntity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class SchoolRanking implements Serializable {
    private static final String TAG = "SchoolRanking";
    public static final String COLLECTION_NAME = "SchoolRanking";
    public static final String PROPERTY_ID = "school_id";
    public static final String PROPERTY_NAME = "schoolname";
    public static final String PROPERTY_ADDRESS = "address";
    public static final String PROPERTY_CITY = "city";
    public static final String PROPERTY_CATEGORY = "category";
    public static final String PROPERTY_GU = "gu";
    public static final String PROPERTY_POINT = "point";

    public long point;
    public int school_id;
    public String schoolname;
    public String address;
    public String city;
    public String category;
    public String gu;

    public SchoolRanking() {
    }

    public SchoolRanking(SchoolPointEntity schoolPointEntity, SchoolEntity schoolEntity) {
        set(schoolPointEntity, schoolEntity);
    }

    public SchoolRanking(JSONObject object) {
        set(object);
    }

    public SchoolRanking(Cursor cursor) {
        set(cursor);
    }

    public void set(Cursor cursor) {
        try {
            this.school_id = cursor.getInt(cursor.getColumnIndex(PROPERTY_ID));
            this.schoolname = cursor.getString(cursor.getColumnIndex(PROPERTY_NAME));
            this.address = cursor.getString(cursor.getColumnIndex(PROPERTY_ADDRESS));
            this.city = cursor.getString(cursor.getColumnIndex(PROPERTY_CITY));
            this.category = cursor.getString(cursor.getColumnIndex(PROPERTY_CATEGORY));
            this.gu = cursor.getString(cursor.getColumnIndex(PROPERTY_GU));
            this.point = cursor.getInt(cursor.getColumnIndex(PROPERTY_POINT));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void set(SchoolPointEntity schoolPointEntity, SchoolEntity schoolEntity) {
        this.point = schoolPointEntity.point;
        this.school_id = schoolEntity.id;
        this.schoolname = schoolEntity.schoolname;
        this.address = schoolEntity.address;
        this.city = schoolEntity.city;
        this.category = schoolEntity.category;
        this.gu = schoolEntity.gu;
    }

    public void set(JSONObject object) {
        try {
            if (object.get(SchoolPointEntity.PROPERTY_POINT).equals(null)) {
                this.point = 0;
            } else {
                this.point = object.getLong(SchoolPointEntity.PROPERTY_POINT);
            }
            this.school_id = object.getInt(SchoolEntity.PROPERTY_ID);
            this.schoolname = object.getString(SchoolEntity.PROPERTY_SCHOOLNAME);
            this.address = object.getString(SchoolEntity.PROPERTY_ADDRESS);
            this.city = object.getString(SchoolEntity.PROPERTY_CITY);
            this.category = object.getString(SchoolEntity.PROPERTY_CATEGORY);
            this.gu = object.getString(SchoolEntity.PROPERTY_GU);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getBaasioBaseEntity() {
        JSONObject object = new JSONObject();
        try {
            object.put(SchoolPointEntity.PROPERTY_POINT, this.point);
            object.put(SchoolEntity.PROPERTY_ID, this.school_id);
            object.put(SchoolEntity.PROPERTY_SCHOOLNAME, this.schoolname);
            object.put(SchoolEntity.PROPERTY_ADDRESS, this.address);
            object.put(SchoolEntity.PROPERTY_CITY, this.city);
            object.put(SchoolEntity.PROPERTY_CATEGORY, this.category);
            object.put(SchoolEntity.PROPERTY_GU, this.gu);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    public SchoolEntity getSchoolEntity() {
        SchoolEntity schoolEntity = new SchoolEntity();
        schoolEntity.id = this.school_id;
        schoolEntity.schoolname = this.schoolname;
        schoolEntity.address = this.address;
        schoolEntity.city = this.city;
        schoolEntity.category = this.category;
        schoolEntity.gu = this.gu;

        return schoolEntity;
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(PROPERTY_ID, this.school_id);
        values.put(PROPERTY_NAME, this.schoolname);
        values.put(PROPERTY_ADDRESS, this.address);
        values.put(PROPERTY_CITY, this.city);
        values.put(PROPERTY_CATEGORY, this.category);
        values.put(PROPERTY_GU, this.gu);
        values.put(PROPERTY_POINT, this.point);
        return values;
    }
}
