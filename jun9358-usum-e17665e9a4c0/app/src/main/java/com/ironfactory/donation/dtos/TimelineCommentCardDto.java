package com.ironfactory.donation.dtos;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.ironfactory.donation.entities.CommentEntity;
import com.ironfactory.donation.entities.UserEntity;

import org.json.JSONObject;

public class TimelineCommentCardDto implements Parcelable {

    private static final String PROPERTY_USER = "user";
    private static final String PROPERTY_COMMENT = "comment";

    public CommentEntity commentEntity;
    public UserEntity userEntity;

    public Boolean isAllDataLoaded() {
        if (commentEntity == null) {
            return false;
        }
        return userEntity != null;
    }


    public TimelineCommentCardDto(JSONObject object) {
        commentEntity = new CommentEntity(object);
        userEntity = new UserEntity(object);
    }


    public TimelineCommentCardDto (Parcel in) {
        set(in.readBundle(TimelineCardDto.class.getClassLoader()));
    }


    public void set(Bundle bundle) {
        this.commentEntity = bundle.getParcelable(PROPERTY_COMMENT);
        this.userEntity = bundle.getParcelable(PROPERTY_USER);
    }


    public static final Creator<TimelineCommentCardDto> CREATOR = new Creator<TimelineCommentCardDto>() {
        public TimelineCommentCardDto createFromParcel(Parcel in) {
            return new TimelineCommentCardDto(in);
        }

        public TimelineCommentCardDto[] newArray(int size) {
            return new TimelineCommentCardDto[size];
        }
    };


    public Bundle getBundle() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(PROPERTY_COMMENT, commentEntity);
        bundle.putParcelable(PROPERTY_USER, userEntity);
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
