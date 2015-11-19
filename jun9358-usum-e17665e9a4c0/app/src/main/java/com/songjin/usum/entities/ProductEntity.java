package com.songjin.usum.entities;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.kth.baasio.entity.BaasioBaseEntity;
import com.kth.baasio.entity.entity.BaasioEntity;

import java.util.UUID;

public class ProductEntity implements Parcelable {
    public static final String COLLECTION_NAME = "products";
    public static final String PROPERTY_CREATED = BaasioBaseEntity.PROPERTY_CREATED;
    public static final String PROPERTY_USER_UUID = "user_uuid";
    public static final String PROPERTY_UUID = BaasioBaseEntity.PROPERTY_UUID;
    public static final String PROPERTY_PRODUCT_NAME = "product_name";
    public static final String PROPERTY_SCHOOL_ID = "school_id";
    public static final String PROPERTY_CATEGORY = "category";
    public static final String PROPERTY_SIZE = "size";
    public static final String PROPERTY_CONDITION = "condition";
    public static final String PROPERTY_SEX = "sex";
    public static final String PROPERTY_CONTENTS = "contents";
    public static final Creator<ProductEntity> CREATOR = new Creator<ProductEntity>() {
        public ProductEntity createFromParcel(Parcel in) {
            return new ProductEntity(in);
        }

        public ProductEntity[] newArray(int size) {
            return new ProductEntity[size];
        }
    };
    public long created;
    public String uuid;
    public String product_name;
    public String user_uuid;
    public int school_id;
    public int category;
    public int size;
    public int condition;
    public int sex;
    public String contents;

    public ProductEntity() {
        this.product_name = UUID.randomUUID().toString();
    }

    public ProductEntity(BaasioBaseEntity baasioBaseEntity) {
        set(baasioBaseEntity);
    }

    public ProductEntity(Bundle bundle) {
        set(bundle);
    }

    public ProductEntity(Parcel in) {
        set(in.readBundle());
    }

    public void set(Bundle bundle) {
        this.created = bundle.getLong(PROPERTY_CREATED);
        this.uuid = bundle.getString(PROPERTY_UUID);
        this.product_name = bundle.getString(PROPERTY_PRODUCT_NAME);
        this.user_uuid = bundle.getString(PROPERTY_USER_UUID);
        this.school_id = bundle.getInt(PROPERTY_SCHOOL_ID);
        this.category = bundle.getInt(PROPERTY_CATEGORY);
        this.size = bundle.getInt(PROPERTY_SIZE);
        this.condition = bundle.getInt(PROPERTY_CONDITION);
        this.sex = bundle.getInt(PROPERTY_SEX);
        this.contents = bundle.getString(PROPERTY_CONTENTS);
    }

    public void set(BaasioBaseEntity entity) {
        if (entity.getCreated() != null) {
            this.created = entity.getCreated();
        }
        if (entity.getUuid() != null) {
            this.uuid = entity.getUuid().toString();
        }
        if (entity.getProperty(PROPERTY_PRODUCT_NAME) != null) {
            this.product_name = entity.getProperty(PROPERTY_PRODUCT_NAME).asText();
        }
        if (entity.getProperty(PROPERTY_USER_UUID) != null) {
            this.user_uuid = entity.getProperty(PROPERTY_USER_UUID).asText();
        }
        if (entity.getProperty(PROPERTY_SCHOOL_ID) != null) {
            this.school_id = entity.getProperty(PROPERTY_SCHOOL_ID).asInt();
        }
        if (entity.getProperty(PROPERTY_CATEGORY) != null) {
            this.category = entity.getProperty(PROPERTY_CATEGORY).asInt();
        }
        if (entity.getProperty(PROPERTY_SIZE) != null) {
            this.size = entity.getProperty(PROPERTY_SIZE).asInt();
        }
        if (entity.getProperty(PROPERTY_CONDITION) != null) {
            this.condition = entity.getProperty(PROPERTY_CONDITION).asInt();
        }
        if (entity.getProperty(PROPERTY_SEX) != null) {
            this.sex = entity.getProperty(PROPERTY_SEX).asInt();
        }
        if (entity.getProperty(PROPERTY_CONTENTS) != null) {
            this.contents = entity.getProperty(PROPERTY_CONTENTS).asText();
        }
    }

    public Bundle getBundle() {
        Bundle bundle = new Bundle();
        bundle.putLong(PROPERTY_CREATED, this.created);
        bundle.putString(PROPERTY_UUID, this.uuid);
        bundle.putString(PROPERTY_PRODUCT_NAME, this.product_name);
        bundle.putString(PROPERTY_USER_UUID, this.user_uuid);
        bundle.putInt(PROPERTY_SCHOOL_ID, this.school_id);
        bundle.putInt(PROPERTY_CATEGORY, this.category);
        bundle.putInt(PROPERTY_SIZE, this.size);
        bundle.putInt(PROPERTY_CONDITION, this.condition);
        bundle.putInt(PROPERTY_SEX, this.sex);
        bundle.putString(PROPERTY_CONTENTS, this.contents);

        return bundle;
    }

    public BaasioEntity getBaasioEntity() {
        BaasioEntity baasioEntity = new BaasioEntity();
        baasioEntity.setType(COLLECTION_NAME);
        if (this.uuid != null) {
            baasioEntity.setUuid(UUID.fromString(this.uuid));
        }
        baasioEntity.setCreated(this.created);
        baasioEntity.setProperty(PROPERTY_PRODUCT_NAME, this.product_name);
        baasioEntity.setProperty(PROPERTY_SCHOOL_ID, this.school_id);
        baasioEntity.setProperty(PROPERTY_USER_UUID, this.user_uuid);
        baasioEntity.setProperty(PROPERTY_CATEGORY, this.category);
        baasioEntity.setProperty(PROPERTY_SIZE, this.size);
        baasioEntity.setProperty(PROPERTY_CONDITION, this.condition);
        baasioEntity.setProperty(PROPERTY_SEX, this.sex);
        baasioEntity.setProperty(PROPERTY_CONTENTS, this.contents);

        return baasioEntity;
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
