package com.songjin.usum.entities;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.kth.baasio.entity.BaasioBaseEntity;
import com.kth.baasio.entity.entity.BaasioEntity;

import java.util.UUID;

public class CommentEntity implements Parcelable {
    // FIXME: comment_items => comments
    public static final String COLLECTION_NAME = "timeline_comments";
    public static final String PROPERTY_UUID = BaasioEntity.PROPERTY_UUID;
    // FIXME: timeline_item_uuid => timeline_uuid
    public static final String PROPERTY_TIMELINE_ITEM_UUID = "timeline_item_uuid";
    public static final String PROPERTY_USER_UUID = "user_uuid";
    public static final String PROPERTY_CREATED = BaasioEntity.PROPERTY_CREATED;
    public static final String PROPERTY_CONTENTS = "contents";
    public static final Creator<CommentEntity> CREATOR = new Creator<CommentEntity>() {
        public CommentEntity createFromParcel(Parcel in) {
            return new CommentEntity(in);
        }

        public CommentEntity[] newArray(int size) {
            return new CommentEntity[size];
        }
    };
    public String uuid;
    public String timeline_item_uuid;
    public String user_uuid;
    public long created;
    public String contents;

    public CommentEntity() {

    }

    public CommentEntity(Parcel in) {
        set(in.readBundle());
    }

    public CommentEntity(BaasioBaseEntity entity) {
        set(entity);
    }

    public void set(BaasioBaseEntity entity) {
        if (entity.getUuid() != null) {
            this.uuid = entity.getUuid().toString();
        }
        if (entity.getProperty(PROPERTY_TIMELINE_ITEM_UUID) != null) {
            this.user_uuid = entity.getProperty(PROPERTY_TIMELINE_ITEM_UUID).asText();
        }
        if (entity.getProperty(PROPERTY_USER_UUID) != null) {
            this.user_uuid = entity.getProperty(PROPERTY_USER_UUID).asText();
        }
        this.created = entity.getCreated();
        if (entity.getProperty(PROPERTY_CONTENTS) != null) {
            this.contents = entity.getProperty(PROPERTY_CONTENTS).asText();
        }
    }

    public void set(Bundle bundle) {
        this.uuid = bundle.getString(PROPERTY_UUID);
        this.timeline_item_uuid = bundle.getString(PROPERTY_TIMELINE_ITEM_UUID);
        this.user_uuid = bundle.getString(PROPERTY_USER_UUID);
        this.created = bundle.getLong(PROPERTY_CREATED);
        this.contents = bundle.getString(PROPERTY_CONTENTS);
    }

    public BaasioBaseEntity getBaasioBaseEntity() {
        BaasioBaseEntity entity = new BaasioBaseEntity();
        entity.setType(COLLECTION_NAME);
        entity.setUuid(UUID.fromString(this.uuid));
        entity.setProperty(PROPERTY_TIMELINE_ITEM_UUID, this.timeline_item_uuid);
        entity.setProperty(PROPERTY_USER_UUID, this.user_uuid);
        entity.setProperty(PROPERTY_CREATED, this.created);
        entity.setProperty(PROPERTY_CONTENTS, this.contents);

        return entity;
    }

    public BaasioEntity getBaasioEntity() {
        BaasioEntity entity = new BaasioEntity();
        entity.setType(COLLECTION_NAME);
        entity.setUuid(UUID.fromString(this.uuid));
        entity.setProperty(PROPERTY_TIMELINE_ITEM_UUID, this.timeline_item_uuid);
        entity.setProperty(PROPERTY_USER_UUID, this.user_uuid);
        entity.setProperty(PROPERTY_CREATED, this.created);
        entity.setProperty(PROPERTY_CONTENTS, this.contents);

        return entity;
    }

    public Bundle getBundle() {
        Bundle bundle = new Bundle();
        bundle.putString(PROPERTY_UUID, this.uuid);
        bundle.putString(PROPERTY_TIMELINE_ITEM_UUID, this.timeline_item_uuid);
        bundle.putString(PROPERTY_USER_UUID, this.user_uuid);
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
