package com.ironfactory.donation.entities;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class CommentEntity implements Parcelable {
    // FIXME: comment_items => comments
    public static final String COLLECTION_NAME = "timeline_comments";
//    public static final String PROPERTY_UUID = BaasioEntity.PROPERTY_UUID;
    public static final String PROPERTY_ID = "comment_id";
    // FIXME: timeline_item_id => timeline_uuid
    public static final String PROPERTY_timeline_item_id = "comment_timeline_item_id";
    public static final String PROPERTY_user_id = "comment_user_id";
    public static final String PROPERTY_CREATED = "comment_created";
    public static final String PROPERTY_CONTENTS = "comment_content";
    public static final Creator<CommentEntity> CREATOR = new Creator<CommentEntity>() {
        public CommentEntity createFromParcel(Parcel in) {
            return new CommentEntity(in);
        }

        public CommentEntity[] newArray(int size) {
            return new CommentEntity[size];
        }
    };
//    public String uuid;
    public String id;
    public String timeline_item_id;
    public String user_id;
    public long created;
    public String contents;

    public CommentEntity() {

    }

    public CommentEntity(Parcel in) {
        set(in.readBundle());
    }

    public CommentEntity(JSONObject object) {
        set(object);
    }

    public void set(JSONObject object) {
        try {
            if (object.getString(PROPERTY_ID) != null) {
                this.id = object.getString(PROPERTY_ID).toString();
            }
            if (object.getString(PROPERTY_timeline_item_id) != null) {
                this.timeline_item_id = object.getString(PROPERTY_timeline_item_id);
            }
            if (object.getString(PROPERTY_user_id) != null) {
                this.user_id = object.getString(PROPERTY_user_id);
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
        this.timeline_item_id = bundle.getString(PROPERTY_timeline_item_id);
        this.user_id = bundle.getString(PROPERTY_user_id);
        this.created = bundle.getLong(PROPERTY_CREATED);
        this.contents = bundle.getString(PROPERTY_CONTENTS);
    }

//    public BaasioBaseEntity getBaasioBaseEntity() {
//        BaasioBaseEntity entity = new BaasioBaseEntity();
//        entity.setType(COLLECTION_NAME);
//        entity.setUuid(UUID.fromString(this.uuid));
//        entity.setProperty(PROPERTY_timeline_item_id, this.timeline_item_id);
//        entity.setProperty(PROPERTY_user_id, this.user_id);
//        entity.setProperty(PROPERTY_CREATED, this.created);
//        entity.setProperty(PROPERTY_CONTENTS, this.contents);
//
//        return entity;
//    }

    public JSONObject getBaasioEntity() {
        JSONObject object = new JSONObject();
//        object.setType(COLLECTION_NAME);
        try {
            object.put(PROPERTY_ID, id);
            object.put(PROPERTY_timeline_item_id, this.timeline_item_id);
            object.put(PROPERTY_user_id, this.user_id);
            object.put(PROPERTY_CREATED, this.created);
            object.put(PROPERTY_CONTENTS, this.contents);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    public Bundle getBundle() {
        Bundle bundle = new Bundle();
        bundle.putString(PROPERTY_ID, this.id);
        bundle.putString(PROPERTY_timeline_item_id, this.timeline_item_id);
        bundle.putString(PROPERTY_user_id, this.user_id);
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
