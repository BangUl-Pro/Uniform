package com.ironfactory.donation.entities;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.ironfactory.donation.Global;

import org.json.JSONException;
import org.json.JSONObject;

public class TransactionEntity implements Parcelable {
    public static final String COLLECTION_NAME = "transactions";
//    public static final String PROPERTY_UUID = BaasioBaseEntity.PROPERTY_UUID;
    public static final String PROPERTY_MODIFIED = "transaction_modified";
    public static final String PROPERTY_STATUS = "transaction_status";
    public static final String PROPERTY_DONATOR_UUID = "transaction_donator_id";
    public static final String PROPERTY_RECEIVER_UUID = "transaction_receiver_id";
    public static final String PROPERTY_PRODUCT_ID = "transaction_product_id";
    public static final String PROPERTY_PRODUCT_NAME = "transaction_product_name";


    public long modified;
    public int status;
    public String donator_id;
    public String receiver_id;
    public String product_id;
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
//        this.uuid = "";
        this.modified = 1;
        this.status = Global.REGISTERED;
        this.donator_id = "";
        this.receiver_id = "";
        this.product_id = "";
        this.product_name = "";
    }

    public TransactionEntity(Bundle bundle) {
        set(bundle);
    }

    public TransactionEntity(Parcel in) {
        set(in.readBundle());
    }

    public TransactionEntity(JSONObject object) {
        set(object);
    }

    public void set(Bundle bundle) {
//        this.uuid = bundle.getString(PROPERTY_UUID);
        this.modified = bundle.getLong(PROPERTY_MODIFIED);
        this.status = bundle.getInt(PROPERTY_STATUS);
        this.donator_id = bundle.getString(PROPERTY_DONATOR_UUID);
        this.receiver_id = bundle.getString(PROPERTY_RECEIVER_UUID);
        this.product_id = bundle.getString(PROPERTY_PRODUCT_ID);
        this.product_name = bundle.getString(PROPERTY_PRODUCT_NAME);
    }

    public Bundle getBundle() {
        Bundle bundle = new Bundle();
//        bundle.putString(PROPERTY_UUID, this.uuid);
        bundle.putLong(PROPERTY_MODIFIED, this.modified);
        bundle.putInt(PROPERTY_STATUS, this.status);
        bundle.putString(PROPERTY_DONATOR_UUID, this.donator_id);
        bundle.putString(PROPERTY_RECEIVER_UUID, this.receiver_id);
        bundle.putString(PROPERTY_PRODUCT_ID, this.product_id);
        bundle.putString(PROPERTY_PRODUCT_NAME, this.product_name);

        return bundle;
    }

    public void set(JSONObject object) {
//        if (object.getUuid() != null) {
//            this.uuid = object.getUuid().toString();
//        }
//        if (object.getModified() != null) {
//            this.modified = object.getModified();
//        }
        try {
            if (!object.get(PROPERTY_STATUS).equals(null)) {
                this.status = object.getInt(PROPERTY_STATUS);
            }
            if (object.getString(PROPERTY_DONATOR_UUID) != null) {
                this.donator_id = object.getString(PROPERTY_DONATOR_UUID);
            }
            if (object.getString(PROPERTY_RECEIVER_UUID) != null) {
                this.receiver_id = object.getString(PROPERTY_RECEIVER_UUID);
            }
            if (object.getString(PROPERTY_PRODUCT_ID) != null) {
                this.product_id = object.getString(PROPERTY_PRODUCT_ID);
            }
            if (object.getString(PROPERTY_PRODUCT_NAME) != null) {
                this.product_name = object.getString(PROPERTY_PRODUCT_NAME);
            }
            if (!object.get(PROPERTY_MODIFIED).equals(null)) {
                this.modified = object.getLong(PROPERTY_MODIFIED);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

//    public BaasioBaseEntity getBaasioBaseEntity() {
//        BaasioBaseEntity baasioBaseEntity = new BaasioBaseEntity();
//        baasioBaseEntity.setUuid(UUID.fromString(this.uuid));
//        baasioBaseEntity.setModified(this.modified);
//        baasioBaseEntity.setProperty(PROPERTY_STATUS, this.status.ordinal());
//        baasioBaseEntity.setProperty(PROPERTY_donator_id, this.donator_id);
//        baasioBaseEntity.setProperty(PROPERTY_receiver_id, this.receiver_id);
//        baasioBaseEntity.setProperty(PROPERTY_product_id, this.product_id);
//        baasioBaseEntity.setProperty(PROPERTY_PRODUCT_NAME, this.product_name);
//
//        return baasioBaseEntity;
//    }

//    public void set(BaasioEntity baasioEntity) {
//        this.uuid = baasioEntity.getProperty(PROPERTY_UUID).asText();
//        this.modified = baasioEntity.getProperty(PROPERTY_MODIFIED).asLong();
//        this.status = STATUS_TYPE.values()[baasioEntity.getProperty(PROPERTY_STATUS).asInt()];
//        this.donator_id = baasioEntity.getProperty(PROPERTY_donator_id).asText();
//        this.receiver_id = baasioEntity.getProperty(PROPERTY_receiver_id).asText();
//        this.product_id = baasioEntity.getProperty(PROPERTY_product_id).asText();
//        this.product_name = baasioEntity.getProperty(PROPERTY_PRODUCT_NAME).asText();
//    }

//    public BaasioEntity getBaasioEntity() {
//        BaasioEntity baasioEntity = new BaasioEntity();
//        if (!this.uuid.isEmpty()) {
//            baasioEntity.setUuid(UUID.fromString(this.uuid));
//        }
//        baasioEntity.setProperty(PROPERTY_MODIFIED, this.modified);
//        baasioEntity.setProperty(PROPERTY_STATUS, this.status.ordinal());
//        baasioEntity.setProperty(PROPERTY_donator_id, this.donator_id);
//        baasioEntity.setProperty(PROPERTY_receiver_id, this.receiver_id);
//        baasioEntity.setProperty(PROPERTY_product_id, this.product_id);
//        baasioEntity.setProperty(PROPERTY_PRODUCT_NAME, this.product_name);
//
//        return baasioEntity;
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
