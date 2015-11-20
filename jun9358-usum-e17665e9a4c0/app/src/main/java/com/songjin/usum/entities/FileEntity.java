package com.songjin.usum.entities;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class FileEntity implements Parcelable {
    public static final String COLLECTION_NAME = "files";

//    public static final String PROPERTY_UUID = BaasioBaseEntity.PROPERTY_UUID;
    public static final String PROPERTY_PARENT_UUID = "parent_uuid";

//    public String uuid;
    public String parent_uuid;

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
        this.parent_uuid = bundle.getString(PROPERTY_PARENT_UUID);
    }

    public void set(JSONObject object) {
//        this.uuid = object.getUuid().toString();
        try {
            if (object.getString(PROPERTY_PARENT_UUID) != null) {
                this.parent_uuid = object.getString(PROPERTY_PARENT_UUID);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Bundle getBundle() {
        Bundle bundle = new Bundle();
//        bundle.putString(PROPERTY_UUID, this.uuid);
        bundle.putString(PROPERTY_PARENT_UUID, this.parent_uuid);

        return bundle;
    }

//    public BaasioBaseEntity getBaasioBaseEntity() {
//        BaasioBaseEntity entity = new BaasioBaseEntity();
//        entity.setUuid(UUID.fromString(this.uuid));
//        entity.setProperty(PROPERTY_PARENT_UUID, this.parent_uuid);
//
//        return entity;
//    }

//    public BaasioFile getBaasioFile() {
//        BaasioFile baasioFile = new BaasioFile();
//        baasioFile.setUuid(UUID.fromString(this.uuid));
//        baasioFile.setProperty(PROPERTY_PARENT_UUID, this.parent_uuid);
//        baasioFile.setFilename(this.uuid);
//
//        return baasioFile;
//    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeBundle(getBundle());
    }
}
