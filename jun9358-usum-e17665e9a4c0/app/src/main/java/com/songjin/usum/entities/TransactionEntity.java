package com.songjin.usum.entities;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.UUID;

public class TransactionEntity implements Parcelable {
    public static final String COLLECTION_NAME = "transactions";
//    public static final String PROPERTY_UUID = BaasioBaseEntity.PROPERTY_UUID;
//    public static final String PROPERTY_MODIFIED = BaasioBaseEntity.PROPERTY_MODIFIED;
    public static final String PROPERTY_STATUS = "status";
    public static final String PROPERTY_DONATOR_UUID = "donator_uuid";
    public static final String PROPERTY_RECEIVER_UUID = "receiver_uuid";
    public static final String PROPERTY_PRODUCT_UUID = "product_uuid";
    public static final String PROPERTY_PRODUCT_NAME = "product_name";

    public String uuid;

    public enum STATUS_TYPE {
        REGISTERED, REQUESTED, SENDED, RECEIVED
    }

    public long modified;
    public STATUS_TYPE status;
    public String donator_uuid;
    public String receiver_uuid;
    public String product_uuid;
    public String product_name;

    public static final Creator<TransactionEntity> CREATOR = new Creator<TransactionEntity>() {
        public TransactionEntity createFromParcel(Parcel in) {
            return new TransactionEntity(in);
        }

        public TransactionEntity[] newArray(int size) {
            return new TransactionEntity[size];
        }
    };

    public TransactionEntity() {
        this.uuid = "";
        this.modified = 0;
        this.status = STATUS_TYPE.REGISTERED;
        this.donator_uuid = "";
        this.receiver_uuid = "";
        this.product_uuid = "";
        this.product_name = "";
    }

    public TransactionEntity(Bundle bundle) {
        set(bundle);
    }

    public TransactionEntity(Parcel in) {
        set(in.readBundle());
    }

    public TransactionEntity(BaasioBaseEntity baasioBaseEntity) {
        set(baasioBaseEntity);
    }

    public void set(Bundle bundle) {
        this.uuid = bundle.getString(PROPERTY_UUID);
        this.modified = bundle.getLong(PROPERTY_MODIFIED);
        this.status = STATUS_TYPE.values()[bundle.getInt(PROPERTY_STATUS)];
        this.donator_uuid = bundle.getString(PROPERTY_DONATOR_UUID);
        this.receiver_uuid = bundle.getString(PROPERTY_RECEIVER_UUID);
        this.product_uuid = bundle.getString(PROPERTY_PRODUCT_UUID);
        this.product_name = bundle.getString(PROPERTY_PRODUCT_NAME);
    }

    public Bundle getBundle() {
        Bundle bundle = new Bundle();
        bundle.putString(PROPERTY_UUID, this.uuid);
        bundle.putLong(PROPERTY_MODIFIED, this.modified);
        bundle.putInt(PROPERTY_STATUS, this.status.ordinal());
        bundle.putString(PROPERTY_DONATOR_UUID, this.donator_uuid);
        bundle.putString(PROPERTY_RECEIVER_UUID, this.receiver_uuid);
        bundle.putString(PROPERTY_PRODUCT_UUID, this.product_uuid);
        bundle.putString(PROPERTY_PRODUCT_NAME, this.product_name);

        return bundle;
    }

    public void set(BaasioBaseEntity baasioBaseEntity) {
        if (baasioBaseEntity.getUuid() != null) {
            this.uuid = baasioBaseEntity.getUuid().toString();
        }
        if (baasioBaseEntity.getModified() != null) {
            this.modified = baasioBaseEntity.getModified();
        }
        if (baasioBaseEntity.getProperty(PROPERTY_STATUS) != null) {
            this.status = STATUS_TYPE.values()[baasioBaseEntity.getProperty(PROPERTY_STATUS).asInt()];
        }
        if (baasioBaseEntity.getProperty(PROPERTY_DONATOR_UUID) != null) {
            this.donator_uuid = baasioBaseEntity.getProperty(PROPERTY_DONATOR_UUID).asText();
        }
        if (baasioBaseEntity.getProperty(PROPERTY_RECEIVER_UUID) != null) {
            this.receiver_uuid = baasioBaseEntity.getProperty(PROPERTY_RECEIVER_UUID).asText();
        }
        if (baasioBaseEntity.getProperty(PROPERTY_PRODUCT_UUID) != null) {
            this.product_uuid = baasioBaseEntity.getProperty(PROPERTY_PRODUCT_UUID).asText();
        }
        if (baasioBaseEntity.getProperty(PROPERTY_PRODUCT_NAME) != null) {
            this.product_name = baasioBaseEntity.getProperty(PROPERTY_PRODUCT_NAME).asText();
        }
    }

    public BaasioBaseEntity getBaasioBaseEntity() {
        BaasioBaseEntity baasioBaseEntity = new BaasioBaseEntity();
        baasioBaseEntity.setUuid(UUID.fromString(this.uuid));
        baasioBaseEntity.setModified(this.modified);
        baasioBaseEntity.setProperty(PROPERTY_STATUS, this.status.ordinal());
        baasioBaseEntity.setProperty(PROPERTY_DONATOR_UUID, this.donator_uuid);
        baasioBaseEntity.setProperty(PROPERTY_RECEIVER_UUID, this.receiver_uuid);
        baasioBaseEntity.setProperty(PROPERTY_PRODUCT_UUID, this.product_uuid);
        baasioBaseEntity.setProperty(PROPERTY_PRODUCT_NAME, this.product_name);

        return baasioBaseEntity;
    }

    public void set(BaasioEntity baasioEntity) {
        this.uuid = baasioEntity.getProperty(PROPERTY_UUID).asText();
        this.modified = baasioEntity.getProperty(PROPERTY_MODIFIED).asLong();
        this.status = STATUS_TYPE.values()[baasioEntity.getProperty(PROPERTY_STATUS).asInt()];
        this.donator_uuid = baasioEntity.getProperty(PROPERTY_DONATOR_UUID).asText();
        this.receiver_uuid = baasioEntity.getProperty(PROPERTY_RECEIVER_UUID).asText();
        this.product_uuid = baasioEntity.getProperty(PROPERTY_PRODUCT_UUID).asText();
        this.product_name = baasioEntity.getProperty(PROPERTY_PRODUCT_NAME).asText();
    }

    public BaasioEntity getBaasioEntity() {
        BaasioEntity baasioEntity = new BaasioEntity();
        if (!this.uuid.isEmpty()) {
            baasioEntity.setUuid(UUID.fromString(this.uuid));
        }
        baasioEntity.setProperty(PROPERTY_MODIFIED, this.modified);
        baasioEntity.setProperty(PROPERTY_STATUS, this.status.ordinal());
        baasioEntity.setProperty(PROPERTY_DONATOR_UUID, this.donator_uuid);
        baasioEntity.setProperty(PROPERTY_RECEIVER_UUID, this.receiver_uuid);
        baasioEntity.setProperty(PROPERTY_PRODUCT_UUID, this.product_uuid);
        baasioEntity.setProperty(PROPERTY_PRODUCT_NAME, this.product_name);

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
