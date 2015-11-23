package com.songjin.usum.dtos;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.songjin.usum.entities.FileEntity;
import com.songjin.usum.entities.LikeEntity;
import com.songjin.usum.entities.TimelineEntity;
import com.songjin.usum.entities.UserEntity;

import java.util.ArrayList;

public class TimelineCardDto implements Parcelable {
    public static final String COLLECTION_NAME = "timeline_card_dto";

    public static final String PROPERTY_TIMELINE_ENTITY = "timeline_entity";
    public static final String PROPERTY_USER_ENTITY = "user_entity";
    public static final String PROPERTY_LIKE_ENTITY = "like_entity";
    public static final String PROPERTY_FILE_ENTITIES = "file_entities";

    public TimelineEntity timelineEntity;
    public UserEntity userEntity;
    public LikeEntity likeEntity;
    public ArrayList<FileEntity> fileEntities;

    public static final Creator<TimelineCardDto> CREATOR = new Creator<TimelineCardDto>() {
        public TimelineCardDto createFromParcel(Parcel in) {
            return new TimelineCardDto(in);
        }

        public TimelineCardDto[] newArray(int size) {
            return new TimelineCardDto[size];
        }
    };

    public TimelineCardDto() {

    }

    public TimelineCardDto(Parcel in) {
        set(in.readBundle(TimelineCardDto.class.getClassLoader()));
    }

    public void set(Bundle bundle) {
        this.timelineEntity = bundle.getParcelable(PROPERTY_TIMELINE_ENTITY);
        this.userEntity = bundle.getParcelable(PROPERTY_USER_ENTITY);
        this.likeEntity = bundle.getParcelable(PROPERTY_LIKE_ENTITY);
        this.fileEntities = bundle.getParcelableArrayList(PROPERTY_FILE_ENTITIES);
    }

    public Bundle getBundle() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(PROPERTY_TIMELINE_ENTITY, this.timelineEntity);
        bundle.putParcelable(PROPERTY_USER_ENTITY, this.userEntity);
        bundle.putParcelable(PROPERTY_LIKE_ENTITY, this.likeEntity);
        bundle.putParcelableArrayList(PROPERTY_FILE_ENTITIES, this.fileEntities);

        return bundle;
    }

    public Boolean isAllDataLoaded() {
        if (timelineEntity == null) {
            return false;
        }
        if (userEntity == null) {
            return false;
        }
        if (likeEntity == null) {
            return false;
        }
        return fileEntities != null;

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
