package com.songjin.usum.entities;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.kth.baasio.entity.BaasioBaseEntity;

import java.util.UUID;

public class LikeEntity implements Parcelable {
    public static final String COLLECTION_NAME = "likes";

    public static final String PROPERTY_UUID = BaasioBaseEntity.PROPERTY_UUID;
    public static final String PROPERTY_TIMELINE_UUID = "timeline_uuid";
    public static final String PROPERTY_USER_UUID = "user_uuid";

    public String uuid;
    public String timeline_uuid;
    public String user_uuid;

    public static final Creator<LikeEntity> CREATOR = new Creator<LikeEntity>() {
        public LikeEntity createFromParcel(Parcel in) {
            return new LikeEntity(in);
        }

        public LikeEntity[] newArray(int size) {
            return new LikeEntity[size];
        }
    };

    public LikeEntity() {
        this.uuid = "";
        this.timeline_uuid = "";
        this.user_uuid = "";
    }

    public LikeEntity(Parcel in) {
        set(in.readBundle());
    }

    public LikeEntity(BaasioBaseEntity entity) {
        set(entity);
    }

    public void set(Bundle bundle) {
        this.uuid = bundle.getString(PROPERTY_UUID);
        this.timeline_uuid = bundle.getString(PROPERTY_TIMELINE_UUID);
        this.user_uuid = bundle.getString(PROPERTY_USER_UUID);
    }

    public void set(BaasioBaseEntity entity) {
        this.uuid = entity.getUuid().toString();
        if (entity.getProperty(PROPERTY_TIMELINE_UUID) != null) {
            this.timeline_uuid = entity.getProperty(PROPERTY_TIMELINE_UUID).asText();
        }
        if (entity.getProperty(PROPERTY_USER_UUID) != null) {
            this.user_uuid = entity.getProperty(PROPERTY_USER_UUID).asText();
        }
    }

    public Bundle getBundle() {
        Bundle bundle = new Bundle();
        bundle.putString(PROPERTY_UUID, this.uuid);
        bundle.putString(PROPERTY_TIMELINE_UUID, this.timeline_uuid);
        bundle.putString(PROPERTY_USER_UUID, this.user_uuid);

        return bundle;
    }

    public BaasioBaseEntity getBaasioBaseEntity() {
        BaasioBaseEntity entity = new BaasioBaseEntity();
        entity.setUuid(UUID.fromString(this.uuid));
        entity.setProperty(PROPERTY_TIMELINE_UUID, this.timeline_uuid);
        entity.setProperty(PROPERTY_USER_UUID, this.user_uuid);

        return entity;
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
