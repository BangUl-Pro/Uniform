package com.songjin.usum.entities;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class FileEntity implements Parcelable {
    public static final String COLLECTION_NAME = "files";

//    public static final String PROPERTY_UUID = BaasioBaseEntity.PROPERTY_UUID;
    public static final String PROPERTY_ID = "file_id";
    public static final String PROPERTY_PARENT_UUID = "file_parent_id";
    public static final String PROPERTY_TYPE = "file_type";

    public static final int PRODUCT = 1;
    public static final int TIMELINE = 2;

//    public String uuid;
    public String id;
    public String parent_uuid;
    public int type;

    public static final Creator<FileEntity> CREATOR = new Creator<FileEntity>() {
        public FileEntity createFromParcel(Parcel in) {
            return new FileEntity(in);
        }

        public FileEntity[] newArray(int size) {
            return new FileEntity[size];
        }
    };

    public FileEntity() {

    }

    public FileEntity(Parcel in) {
        set(in.readBundle());
    }

    public FileEntity(JSONObject object) {
        set(object);
    }

    public void set(Bundle bundle) {
//        this.uuid = bundle.getString(PROPERTY_UUID);
        this.id = bundle.getString(PROPERTY_ID);
        this.parent_uuid = bundle.getString(PROPERTY_PARENT_UUID);
        this.type = bundle.getInt(PROPERTY_TYPE);
    }

    public void set(JSONObject object) {
//        this.uuid = object.getUuid().toString();
        try {
            if (!object.getString(PROPERTY_ID).equals(null)) {
                this.id = object.getString(PROPERTY_ID);
            }
            if (!object.get(PROPERTY_PARENT_UUID).equals(null)) {
                this.parent_uuid = object.getString(PROPERTY_PARENT_UUID);
            }
            if (!object.get(PROPERTY_TYPE).equals(null)) {
                this.type = object.getInt(PROPERTY_TYPE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Bundle getBundle() {
        Bundle bundle = new Bundle();
//        bundle.putString(PROPERTY_UUID, this.uuid);
        bundle.putString(PROPERTY_ID, this.id);
        bundle.putString(PROPERTY_PARENT_UUID, this.parent_uuid);
        bundle.putInt(PROPERTY_TYPE, this.type);

        return bundle;
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
