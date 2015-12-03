package com.ironfactory.donation.entities;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class LikeEntity implements Parcelable {
    public static final String COLLECTION_NAME = "likes";

//    public static final String PROPERTY_UUID = BaasioBaseEntity.PROPERTY_UUID;
    public static final String PROPERTY_ID = "like_id";
    public static final String PROPERTY_TIMELINE_UUID = "like_timeline_id";
    public static final String PROPERTY_USER_UUID = "like_user_id";

//    public String uuid;
    public String id = "";
    public String timeline_uuid = "";
    public String user_uuid = "";

    public static final Creator<LikeEntity> CREATOR = new Creator<LikeEntity>() {
        public LikeEntity createFromParcel(Parcel in) {
            return new LikeEntity(in);
        }

        public LikeEntity[] newArray(int size) {
            return new LikeEntity[size];
        }
    };

    public LikeEntity() {
        this.id = "";
        this.timeline_uuid = "";
        this.user_uuid = "";
    }

    public LikeEntity(Parcel in) {
        set(in.readBundle());
    }

    public LikeEntity(JSONObject object) {
        set(object);
    }

    public void set(Bundle bundle) {
        this.id = bundle.getString(PROPERTY_ID);
        this.timeline_uuid = bundle.getString(PROPERTY_TIMELINE_UUID);
        this.user_uuid = bundle.getString(PROPERTY_USER_UUID);
    }

    public void set(JSONObject object) {
        try {
            if (object.getString(PROPERTY_ID) != null) {
                this.id = object.getString(PROPERTY_ID);
            }
            if (object.getString(PROPERTY_TIMELINE_UUID) != null) {
                this.timeline_uuid = object.getString(PROPERTY_TIMELINE_UUID);
            }
            if (object.getString(PROPERTY_USER_UUID) != null) {
                this.user_uuid = object.getString(PROPERTY_USER_UUID);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Bundle getBundle() {
        Bundle bundle = new Bundle();
        bundle.putString(PROPERTY_ID, this.id);
        bundle.putString(PROPERTY_TIMELINE_UUID, this.timeline_uuid);
        bundle.putString(PROPERTY_USER_UUID, this.user_uuid);

        return bundle;
    }

//    public BaasioBaseEntity getBaasioBaseEntity() {
//        BaasioBaseEntity entity = new BaasioBaseEntity();
//        entity.setUuid(UUID.fromString(this.uuid));
//        entity.setProperty(PROPERTY_TIMELINE_UUID, this.timeline_uuid);
//        entity.setProperty(PROPERTY_USER_UUID, this.user_uuid);
//
//        return entity;
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
