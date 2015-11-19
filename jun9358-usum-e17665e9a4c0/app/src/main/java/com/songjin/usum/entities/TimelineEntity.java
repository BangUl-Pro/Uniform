package com.songjin.usum.entities;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.kth.baasio.entity.BaasioBaseEntity;
import com.kth.baasio.entity.entity.BaasioEntity;

import java.util.UUID;

public class TimelineEntity implements Parcelable {
    // FIXME: timeline_items => timelines
    public static final String COLLECTION_NAME = "timeline_items";

    public static final String PROPERTY_UUID = BaasioEntity.PROPERTY_UUID;
    public static final String PROPERTY_SCHOOL_ID = "school_id";
    public static final String PROPERTY_USER_UUID = "user_uuid";
    public static final String PROPERTY_CREATED = BaasioEntity.PROPERTY_CREATED;
    public static final String PROPERTY_CONTENTS = "contents";

    public String uuid;
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

    public TimelineEntity(BaasioBaseEntity entity) {
        set(entity);
    }

    public void set(BaasioBaseEntity entity) {
        this.uuid = entity.getUuid().toString();
        if (entity.getProperty(PROPERTY_USER_UUID) != null) {
            this.user_uuid = entity.getProperty(PROPERTY_USER_UUID).asText();
        }
        if (entity.getProperty(PROPERTY_SCHOOL_ID) != null) {
            this.school_id = entity.getProperty(PROPERTY_SCHOOL_ID).asInt();
        }
        this.created = entity.getCreated();
        if (entity.getProperty(PROPERTY_CONTENTS) != null) {
            this.contents = entity.getProperty(PROPERTY_CONTENTS).asText();
        }
    }

    public void set(Bundle bundle) {
        this.uuid = bundle.getString(PROPERTY_UUID);
        this.user_uuid = bundle.getString(PROPERTY_USER_UUID);
        this.school_id = bundle.getInt(PROPERTY_SCHOOL_ID);
        this.created = bundle.getLong(PROPERTY_CREATED);
        this.contents = bundle.getString(PROPERTY_CONTENTS);
    }

    public BaasioBaseEntity getBaasioBaseEntity() {
        BaasioBaseEntity entity = new BaasioBaseEntity();
        entity.setType(COLLECTION_NAME);
        entity.setUuid(UUID.fromString(this.uuid));
        entity.setProperty(PROPERTY_USER_UUID, this.user_uuid);
        entity.setProperty(PROPERTY_SCHOOL_ID, this.school_id);
        entity.setProperty(PROPERTY_CREATED, this.created);
        entity.setProperty(PROPERTY_CONTENTS, this.contents);

        return entity;
    }

    public BaasioEntity getBaasioEntity() {
        BaasioEntity entity = new BaasioEntity();
        entity.setType(COLLECTION_NAME);
        entity.setUuid(UUID.fromString(this.uuid));
        entity.setProperty(PROPERTY_USER_UUID, this.user_uuid);
        entity.setProperty(PROPERTY_SCHOOL_ID, this.school_id);
        entity.setProperty(PROPERTY_CREATED, this.created);
        entity.setProperty(PROPERTY_CONTENTS, this.contents);

        return entity;
    }

    public Bundle getBundle() {
        Bundle bundle = new Bundle();
        bundle.putString(PROPERTY_UUID, this.uuid);
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
