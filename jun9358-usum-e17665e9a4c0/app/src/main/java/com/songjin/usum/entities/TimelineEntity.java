package com.songjin.usum.entities;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class TimelineEntity implements Parcelable {
    // FIXME: timeline_items => timelines
    public static final String COLLECTION_NAME = "timeline_items";

//    public static final String PROPERTY_UUID = BaasioEntity.PROPERTY_UUID;
    public static final String PROPERTY_ID = "id";
    public static final String PROPERTY_SCHOOL_ID = "school_id";
    public static final String PROPERTY_USER_UUID = "user_uuid";
//    public static final String PROPERTY_CREATED = BaasioEntity.PROPERTY_CREATED;
    public static final String PROPERTY_CREATED = "created";
    public static final String PROPERTY_CONTENTS = "contents";

//    public String uuid;
    public String id;
    public String user_uuid;
    public int school_id;
    public long created;
    public String contents;

    public static final Creator<TimelineEntity> CREATOR = new Creator<TimelineEntity>() {
        public TimelineEntity createFromParcel(Parcel in) {
            return new TimelineEntity(in);
        }

        public TimelineEntity[] newArray(int size) {
            return new TimelineEntity[size];
        }
    };

    public TimelineEntity() {

    }

    public TimelineEntity(Parcel in) {
        set(in.readBundle());
    }

    public TimelineEntity(JSONObject object) {
        set(object);
    }

    public void set(JSONObject object) {
        try {
            this.id = object.getString(PROPERTY_ID);
            if (object.getString(PROPERTY_USER_UUID) != null) {
                this.user_uuid = object.getString(PROPERTY_USER_UUID);
            }
            if (object.getInt(PROPERTY_SCHOOL_ID) != -1) {
                this.school_id = object.getInt(PROPERTY_SCHOOL_ID);
            }
            this.created = object.getLong(PROPERTY_CREATED);
            if (object.getString(PROPERTY_CONTENTS) != null) {
                this.contents = object.getString(PROPERTY_CONTENTS);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void set(Bundle bundle) {
        this.id = bundle.getString(PROPERTY_ID);
        this.user_uuid = bundle.getString(PROPERTY_USER_UUID);
        this.school_id = bundle.getInt(PROPERTY_SCHOOL_ID);
        this.created = bundle.getLong(PROPERTY_CREATED);
        this.contents = bundle.getString(PROPERTY_CONTENTS);
    }

//    public BaasioBaseEntity getBaasioBaseEntity() {
//        BaasioBaseEntity entity = new BaasioBaseEntity();
//        entity.setType(COLLECTION_NAME);
//        entity.setUuid(UUID.fromString(this.uuid));
//        entity.setProperty(PROPERTY_USER_UUID, this.user_uuid);
//        entity.setProperty(PROPERTY_SCHOOL_ID, this.school_id);
//        entity.setProperty(PROPERTY_CREATED, this.created);
//        entity.setProperty(PROPERTY_CONTENTS, this.contents);
//
//        return entity;
//    }

//    public BaasioEntity getBaasioEntity() {
//        BaasioEntity entity = new BaasioEntity();
//        entity.setType(COLLECTION_NAME);
//        entity.setUuid(UUID.fromString(this.uuid));
//        entity.setProperty(PROPERTY_USER_UUID, this.user_uuid);
//        entity.setProperty(PROPERTY_SCHOOL_ID, this.school_id);
//        entity.setProperty(PROPERTY_CREATED, this.created);
//        entity.setProperty(PROPERTY_CONTENTS, this.contents);
//
//        return entity;
//    }

    public Bundle getBundle() {
        Bundle bundle = new Bundle();
        bundle.putString(PROPERTY_ID, this.id);
        bundle.putString(PROPERTY_USER_UUID, this.user_uuid);
        bundle.putInt(PROPERTY_SCHOOL_ID, this.school_id);
        bundle.putLong(PROPERTY_CREATED, this.created);
        bundle.putString(PROPERTY_CONTENTS, this.contents);

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
