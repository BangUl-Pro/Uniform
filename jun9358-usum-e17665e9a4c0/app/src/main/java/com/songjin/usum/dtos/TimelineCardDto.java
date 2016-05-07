package com.songjin.usum.dtos;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.songjin.usum.entities.FileEntity;
import com.songjin.usum.entities.LikeEntity;
import com.songjin.usum.entities.TimelineEntity;
import com.songjin.usum.entities.UserEntity;

import org.json.JSONObject;

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

    public TimelineCardDto(JSONObject object) {
        set(object);
    }


    public void set(JSONObject object) {
        try {
            timelineEntity = new TimelineEntity(object);
            userEntity = new UserEntity(object);
            fileEntities = new ArrayList<>();
            fileEntities.add(new FileEntity(object));
            likeEntity = new LikeEntity(object);
//            if (!object.get(Global.TIMELINE).equals(null)) {
//                JSONObject timelineObject = object.getJSONObject(Global.TIMELINE);
//                timelineEntity = new TimelineEntity(timelineObject);
//            }
//            if (!object.get(Global.USER).equals(null)) {
//                JSONObject userObject = object.getJSONObject(Global.USER);
//                userEntity = new UserEntity(userObject);
//            }
//            if (!object.get(Global.LIKE).equals(null)) {
//                JSONObject likeObject = object.getJSONObject(Global.USER);
//                likeEntity = new LikeEntity(likeObject);
//            }
//            if (!object.get(Global.FILE).equals(null)) {
//                JSONArray fileArray = object.getJSONArray(Global.FILE);
//                for (int i = 0; i < fileArray.length(); i++) {
//                    JSONObject fileObject = fileArray.getJSONObject(i);
//                    FileEntity fileEntity = new FileEntity(fileObject);
//                    fileEntities.add(fileEntity);
//                }
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setTimeline(JSONObject object) {
        timelineEntity = new TimelineEntity(object);
    }


    public void setUser(JSONObject object) {
        userEntity = new UserEntity(object);
    }


    public void setLike(JSONObject object) {
        likeEntity = new LikeEntity(object);
    }


    public void setFile(JSONObject object) {
        fileEntities = new ArrayList<>();
        FileEntity fileEntity = new FileEntity(object);
        if (!fileEntity.id.equals("null") && !fileEntity.parent_uuid.equals("null"))
            fileEntities.add(fileEntity);
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


    public void addFile(JSONObject object) {
        FileEntity fileEntity = new FileEntity(object);
        fileEntities.add(fileEntity);
    }


    public boolean isSame(TimelineCardDto timelineCardDto) {
        if (timelineEntity.isSame(timelineCardDto.timelineEntity) && userEntity.isSame(timelineCardDto.userEntity))
            return true;
        return false;
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
