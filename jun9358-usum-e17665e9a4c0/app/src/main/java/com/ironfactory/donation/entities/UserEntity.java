package com.ironfactory.donation.entities;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.ironfactory.donation.dtos.KakaoProfile;

import org.json.JSONException;
import org.json.JSONObject;

public class UserEntity implements Parcelable {
    public static final String COLLECTION_NAME = "users";

//    public static final String PROPERTY_UUID = BaasioEntity.PROPERTY_UUID;
    public static final String PROPERTY_ID = "id";
    public static final String PROPERTY_NAME = "name";
    public static final String PROPERTY_HAS_EXTRA_PROFILE = "has_extra_profile";
    public static final String PROPERTY_KAKAOTALK = "kakaotalk";
    public static final String PROPERTY_PHONE = "phone";
    public static final String PROPERTY_PICTURE = "picture";
    public static final String PROPERTY_REAL_NAME = "real_name";
    public static final String PROPERTY_SCHOOL_ID = "school_id";
    public static final String PROPERTY_SEX = "sex";
    public static final String PROPERTY_USER_TYPE = "user_type";

//    public String uuid;
    public String id;
    public String name;
    public boolean hasExtraProfile;
    public KakaoProfile kakaotalk;
    public String phone;
    public String picture;
    public String realName;
    public int schoolId;
    public int sex;
    public int userType;

    public static final Creator<UserEntity> CREATOR = new Creator<UserEntity>() {
        public UserEntity createFromParcel(Parcel in) {
            return new UserEntity(in);
        }

        public UserEntity[] newArray(int size) {
            return new UserEntity[size];
        }
    };

    public UserEntity() {

    }

    public UserEntity(Parcel in) {
        set(in.readBundle(UserEntity.class.getClassLoader()));
    }

    public UserEntity(JSONObject entity) {
        set(entity);
    }

    public void set(Bundle bundle) {
//        this.uuid = bundle.getString(PROPERTY_UUID);
        this.id = bundle.getString(PROPERTY_ID);
        this.name = bundle.getString(PROPERTY_NAME);
        this.hasExtraProfile = bundle.getBoolean(PROPERTY_HAS_EXTRA_PROFILE);
        this.kakaotalk = bundle.getParcelable(PROPERTY_KAKAOTALK);
        this.phone = bundle.getString(PROPERTY_PHONE);
        this.picture = bundle.getString(PROPERTY_PICTURE);
        this.realName = bundle.getString(PROPERTY_REAL_NAME);
        this.schoolId = bundle.getInt(PROPERTY_SCHOOL_ID);
        this.sex = bundle.getInt(PROPERTY_SEX);
        this.userType = bundle.getInt(PROPERTY_USER_TYPE);
    }

    public void set(JSONObject entity) {
        try {
            if (entity == null) {
                return;
            }
//        if (entity.getUuid() != null) {
//            this.uuid = entity.getUuid().toString();
//        }
            if (entity.getString(PROPERTY_ID) != null) {
                this.id = entity.getString(PROPERTY_ID);
            }
            if (entity.getString(PROPERTY_NAME) != null) {
                this.name = entity.getString(PROPERTY_NAME);
            }
            if (entity.getString(PROPERTY_HAS_EXTRA_PROFILE) != null) {
                String hasProfile = entity.getString(PROPERTY_HAS_EXTRA_PROFILE);
                if (hasProfile.equals("1")) {
                    hasExtraProfile = true;
                } else {
                    hasExtraProfile = false;
                }
            }
            if (entity.getString(PROPERTY_PHONE) != null) {
                this.phone = entity.getString(PROPERTY_PHONE);
            }
            if (entity.getString(PROPERTY_PICTURE) != null) {
                this.picture = entity.getString(PROPERTY_PICTURE);
            }
            if (entity.getString(PROPERTY_REAL_NAME) != null) {
                this.realName = entity.getString(PROPERTY_REAL_NAME);
            }
            if (entity.getInt(PROPERTY_SCHOOL_ID) != -1) {
                this.schoolId = entity.getInt(PROPERTY_SCHOOL_ID);
            }
            if (entity.getInt(PROPERTY_SEX) != -1) {
                this.sex = entity.getInt(PROPERTY_SEX);
            }
            if (entity.getInt(PROPERTY_USER_TYPE) != -1) {
                this.userType = entity.getInt(PROPERTY_USER_TYPE);
            }
            if (entity.get(PROPERTY_KAKAOTALK).equals(null)) {
                this.kakaotalk = new KakaoProfile(entity.getJSONObject(PROPERTY_KAKAOTALK));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Bundle getBundle() {
        Bundle bundle = new Bundle();
//        bundle.putString(PROPERTY_UUID, this.uuid);
        bundle.putString(PROPERTY_ID, this.id);
        bundle.putString(PROPERTY_NAME, this.name);
        bundle.putBoolean(PROPERTY_HAS_EXTRA_PROFILE, this.hasExtraProfile);
        bundle.putParcelable(PROPERTY_KAKAOTALK, this.kakaotalk);
        bundle.putString(PROPERTY_PHONE, this.phone);
        bundle.putString(PROPERTY_PICTURE, this.picture);
        bundle.putString(PROPERTY_REAL_NAME, this.realName);
        bundle.putInt(PROPERTY_SCHOOL_ID, this.schoolId);
        bundle.putInt(PROPERTY_SEX, this.sex);
        bundle.putInt(PROPERTY_USER_TYPE, this.userType);

        return bundle;
    }

//    public JSONObject getBaasioBaseEntity() {
//        BaasioBaseEntity entity = new BaasioBaseEntity();
//        entity.setUuid(UUID.fromString(this.uuid));
//        entity.setProperty(PROPERTY_NAME, this.name);
//        entity.setProperty(PROPERTY_HAS_EXTRA_PROFILE, this.hasExtraProfile);
//        // TODO: JsonNode 컨버
//        //entity.setProperty(PROPERTY_KAKAOTALK, this.kakaotalk);
//        entity.setProperty(PROPERTY_PHONE, this.phone);
//        entity.setProperty(PROPERTY_PICTURE, this.picture);
//        entity.setProperty(PROPERTY_REAL_NAME, this.realName);
//        entity.setProperty(PROPERTY_SCHOOL_ID, this.schoolId);
//        entity.setProperty(PROPERTY_SEX, this.sex.ordinal());
//        entity.setProperty(PROPERTY_USER_TYPE, this.userType.ordinal());
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
