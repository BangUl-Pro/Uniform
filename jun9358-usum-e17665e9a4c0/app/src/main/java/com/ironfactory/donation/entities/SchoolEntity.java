package com.ironfactory.donation.entities;


import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

import java.util.HashMap;

public class SchoolEntity implements Parcelable {
    public static final String COLLECTION_NAME = "schools";
    public static final String PROPERTY_ID = "school_id";
//    public static final String PROPERTY_UUID = "uuid";
    public static final String PROPERTY_SCHOOLNAME = "school_schoolname";
    public static final String PROPERTY_ADDRESS = "school_address";
    public static final String PROPERTY_CITY = "school_city";
    public static final String PROPERTY_CATEGORY = "school_category";
    public static final String PROPERTY_GU = "school_gu";
    public static final Creator<SchoolEntity> CREATOR = new Creator<SchoolEntity>() {
        public SchoolEntity createFromParcel(Parcel in) {
            return new SchoolEntity(in);
        }

        public SchoolEntity[] newArray(int size) {
            return new SchoolEntity[size];
        }
    };
    public int id;
//    public String uuid;
    public String schoolname;
    public String address;
    public String city;
    public String category;
    public String gu;

    public SchoolEntity() {
    }

    public SchoolEntity(HashMap<String, String> map) {
        set(map);
    }


    public SchoolEntity(JSONObject object) {
        set(object);
    }
//    public SchoolEntity(BaasioBaseEntity entity) {
//        set(entity);
//    }

    public SchoolEntity(ContentValues values) {
        set(values);
    }

    public SchoolEntity(Cursor cursor) {
        set(cursor);
    }

    public SchoolEntity(Parcel parcel) {
        set(parcel.readBundle());
    }

    public void set(JSONObject object) {
        try {
            this.id = object.getInt(PROPERTY_ID);
//            this.uuid = object.getString(PROPERTY_UUID);
            this.schoolname = object.getString(PROPERTY_SCHOOLNAME);
            this.address = object.getString(PROPERTY_ADDRESS);
            this.city = object.getString(PROPERTY_CITY);
            this.category = object.getString(PROPERTY_CATEGORY);
            this.gu = object.getString(PROPERTY_GU);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public void set(BaasioBaseEntity entity) {
//        if (entity.getProperty(PROPERTY_ID) != null) {
//            this.id = entity.getProperty(PROPERTY_ID).asInt();
//        }
//        if (entity.getUuid() != null) {
//            this.uuid = entity.getUuid().toString();
//        }
//        if (entity.getProperty(PROPERTY_SCHOOLNAME) != null) {
//            this.schoolname = entity.getProperty(PROPERTY_SCHOOLNAME).asText();
//        }
//        if (entity.getProperty(PROPERTY_ADDRESS) != null) {
//            this.address = entity.getProperty(PROPERTY_ADDRESS).asText();
//        }
//        if (entity.getProperty(PROPERTY_CITY) != null) {
//            this.city = entity.getProperty(PROPERTY_CITY).asText();
//        }
//        if (entity.getProperty(PROPERTY_CATEGORY) != null) {
//            this.category = entity.getProperty(PROPERTY_CATEGORY).asText();
//        }
//        if (entity.getProperty(PROPERTY_GU) != null) {
//            this.gu = entity.getProperty(PROPERTY_GU).asText();
//        }
//    }

    public void set(HashMap<String, String> map) {
        this.id = Integer.valueOf(map.get(PROPERTY_ID));
//        this.uuid = String.valueOf(map.get(PROPERTY_UUID));
        this.schoolname = String.valueOf(map.get(PROPERTY_SCHOOLNAME));
        this.address = String.valueOf(map.get(PROPERTY_ADDRESS));
        this.city = String.valueOf(map.get(PROPERTY_CITY));
        this.category = String.valueOf(map.get(PROPERTY_CATEGORY));
        this.gu = String.valueOf(map.get(PROPERTY_GU));
    }

    public void set(ContentValues values) {
        this.id = values.getAsInteger(PROPERTY_ID);
//        this.uuid = values.getAsString(PROPERTY_UUID);
        this.schoolname = values.getAsString(PROPERTY_SCHOOLNAME);
        this.address = values.getAsString(PROPERTY_ADDRESS);
        this.city = values.getAsString(PROPERTY_CITY);
        this.category = values.getAsString(PROPERTY_CATEGORY);
        this.gu = values.getAsString(PROPERTY_GU);
    }

    public void set(Cursor cursor) {
        this.id = cursor.getInt(cursor.getColumnIndex(PROPERTY_ID));
//        this.uuid = cursor.getString(cursor.getColumnIndex(PROPERTY_UUID));
        this.schoolname = cursor.getString(cursor.getColumnIndex(PROPERTY_SCHOOLNAME));
        this.address = cursor.getString(cursor.getColumnIndex(PROPERTY_ADDRESS));
        this.city = cursor.getString(cursor.getColumnIndex(PROPERTY_CITY));
        this.category = cursor.getString(cursor.getColumnIndex(PROPERTY_CATEGORY));
        this.gu = cursor.getString(cursor.getColumnIndex(PROPERTY_GU));
    }

    public void set(Bundle bundle) {
        this.id = bundle.getInt(PROPERTY_ID);
//        this.uuid = bundle.getString(PROPERTY_UUID);
        this.schoolname = bundle.getString(PROPERTY_SCHOOLNAME);
        this.address = bundle.getString(PROPERTY_ADDRESS);
        this.city = bundle.getString(PROPERTY_CITY);
        this.category = bundle.getString(PROPERTY_CATEGORY);
        this.gu = bundle.getString(PROPERTY_GU);
    }

    public HashMap<String, String> getHashMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put(PROPERTY_ID, String.valueOf(this.id));
//        map.put(PROPERTY_UUID, String.valueOf(this.uuid));
        map.put(PROPERTY_SCHOOLNAME, String.valueOf(this.schoolname));
        map.put(PROPERTY_ADDRESS, String.valueOf(this.address));
        map.put(PROPERTY_CITY, String.valueOf(this.city));
        map.put(PROPERTY_CATEGORY, String.valueOf(this.category));
        map.put(PROPERTY_GU, String.valueOf(this.gu));

        return map;
    }

    public Bundle getBundle() {
        Bundle bundle = new Bundle();
        bundle.putInt(PROPERTY_ID, this.id);
//        bundle.putString(PROPERTY_UUID, this.uuid);
        bundle.putString(PROPERTY_SCHOOLNAME, this.schoolname);
        bundle.putString(PROPERTY_ADDRESS, this.address);
        bundle.putString(PROPERTY_CITY, this.city);
        bundle.putString(PROPERTY_CATEGORY, this.category);
        bundle.putString(PROPERTY_GU, this.gu);

        return bundle;
    }

//    public BaasioBaseEntity getBaasioBaseEntity() {
//        BaasioBaseEntity entity = new BaasioBaseEntity();
//        entity.setProperty(PROPERTY_ID, String.valueOf(this.id));
//        entity.setProperty(PROPERTY_UUID, String.valueOf(this.uuid));
//        entity.setProperty(PROPERTY_SCHOOLNAME, String.valueOf(this.schoolname));
//        entity.setProperty(PROPERTY_ADDRESS, String.valueOf(this.address));
//        entity.setProperty(PROPERTY_CITY, String.valueOf(this.city));
//        entity.setProperty(PROPERTY_CATEGORY, String.valueOf(this.category));
//        entity.setProperty(PROPERTY_GU, String.valueOf(this.gu));
//        return entity;
//    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(PROPERTY_ID, this.id);
//        values.put(PROPERTY_UUID, this.uuid);
        values.put(PROPERTY_SCHOOLNAME, this.schoolname);
        values.put(PROPERTY_ADDRESS, this.address);
        values.put(PROPERTY_CITY, this.city);
        values.put(PROPERTY_CATEGORY, this.category);
        values.put(PROPERTY_GU, this.gu);
        return values;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeBundle(getBundle());
    }
}
