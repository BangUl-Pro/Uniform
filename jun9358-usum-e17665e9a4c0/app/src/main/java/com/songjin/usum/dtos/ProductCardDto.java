package com.songjin.usum.dtos;

import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.songjin.usum.entities.FileEntity;
import com.songjin.usum.entities.ProductEntity;
import com.songjin.usum.entities.TransactionEntity;

import org.json.JSONObject;

import java.util.ArrayList;

public class ProductCardDto implements Parcelable {
    public static final String PROPERTY_PRODUCT_ENTITY = "product_entity";
    public static final String PROPERTY_TRANSACTION_ENTITY = "transaction_entity";
    public static final String PROPERTY_URIS = "uris";
    public static final String PROPERTY_FILE_ENTITIES = "file_entities";
    private static final String TAG = "ProductCardDto";

    public ProductEntity productEntity;
    public TransactionEntity transactionEntity;
    public ArrayList<Uri> uris;
    public ArrayList<FileEntity> fileEntities;

    public static final Creator<ProductCardDto> CREATOR = new Creator<ProductCardDto>() {
        public ProductCardDto createFromParcel(Parcel in) {
            return new ProductCardDto(in);
        }

        public ProductCardDto[] newArray(int size) {
            return new ProductCardDto[size];
        }
    };

    public ProductCardDto() {
        uris = new ArrayList<>();
        fileEntities = new ArrayList<>();
    }

    public ProductCardDto(Parcel in) {
        set(in.readBundle(ProductCardDto.class.getClassLoader()));
    }

    public ProductCardDto(JSONObject object) {
        set(object);
    }

    private  void set(JSONObject object) {
        productEntity = new ProductEntity(object);
        transactionEntity = new TransactionEntity(object);
        uris = new ArrayList<>();
        fileEntities = new ArrayList<>();
        FileEntity fileEntity = new FileEntity(object);
        if (!fileEntity.id.equals("null") && !fileEntity.parent_uuid.equals("null")) {
            fileEntities.add(fileEntity);
        }
    }

    public boolean isSame(ProductCardDto productCardDto) {
//        if (productEntity.isSame(productCardDto.productEntity) && transactionEntity.isSame(productCardDto.transactionEntity))
//            return true;
        if (productEntity.id.equals(productCardDto.productEntity.id))
            return true;
        return false;
    }


    public ProductCardDto addFile(FileEntity fileEntity) {
        fileEntities.add(fileEntity);
        return this;
    }


    public void set(Bundle bundle) {
        this.productEntity = bundle.getParcelable(PROPERTY_PRODUCT_ENTITY);
        this.transactionEntity = bundle.getParcelable(PROPERTY_TRANSACTION_ENTITY);
        this.uris = bundle.getParcelableArrayList(PROPERTY_URIS);
        this.fileEntities = bundle.getParcelableArrayList(PROPERTY_FILE_ENTITIES);
    }

    public Bundle getBundle() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(PROPERTY_PRODUCT_ENTITY, this.productEntity);
        bundle.putParcelable(PROPERTY_TRANSACTION_ENTITY, this.transactionEntity);
        bundle.putParcelableArrayList(PROPERTY_URIS, this.uris);
        bundle.putParcelableArrayList(PROPERTY_FILE_ENTITIES, this.fileEntities);

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
