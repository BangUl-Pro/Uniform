package com.songjin.usum.entities;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class LikeEntity implements Parcelable {
    private static final String TAG = "LikeEntity";
    public static final String COLLECTION_NAME = "likes";

//    public static final String PROPERTY_UUID = BaasioBaseEntity.PROPERTY_UUID;
    public static final String PROPERTY_ID = "like_id";
    public static final String PROPERTY_timeline_id = "like_timeline_id";
    public static final String PROPERTY_user_id = "like_user_id";

//    public String uuid;
    public String id = "";
    public String timeline_id = "";
    public String user_id = "";

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
        this.timeline_id = "";
        this.user_id = "";
    }

    public LikeEntity(Parcel in) {
        set(in.readBundle());
    }

    public LikeEntity(JSONObject object) {
        set(object);
    }

    public void set(Bundle bundle) {
        this.id = bundle.getString(PROPERTY_ID);
        this.timeline_id = bundle.getString(PROPERTY_timeline_id);
        this.user_id = bundle.getString(PROPERTY_user_id);
    }

    public void set(JSONObject object) {
        try {
            if (!object.get(PROPERTY_ID).equals(null)) {
                this.id = object.getString(PROPERTY_ID);
                Log.d(TAG, "id = " + id);
            }
            if (object.getString(PROPERTY_timeline_id) != null) {
                this.timeline_id = object.getString(PROPERTY_timeline_id);
            }
            if (object.getString(PROPERTY_user_id) != null) {
                this.user_id = object.getString(PROPERTY_user_id);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Bundle getBundle() {
        Bundle bundle = new Bundle();
        bundle.putString(PROPERTY_ID, this.id);
        bundle.putString(PROPERTY_timeline_id, this.timeline_id);
        bundle.putString(PROPERTY_user_id, this.user_id);

        return bundle;
    }

//    public BaasioBaseEntity getBaasioBaseEntity() {
//        BaasioBaseEntity entity = new BaasioBaseEntity();
//        entity.setUuid(UUID.fromString(this.uuid));
//        entity.setProperty(PROPERTY_timeline_id, this.timeline_id);
//        entity.setProperty(PROPERTY_user_id, this.user_id);
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
