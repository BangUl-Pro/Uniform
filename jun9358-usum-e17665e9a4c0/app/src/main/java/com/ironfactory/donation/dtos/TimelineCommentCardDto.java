package com.ironfactory.donation.dtos;

import com.ironfactory.donation.entities.CommentEntity;
import com.ironfactory.donation.entities.UserEntity;

import java.io.Serializable;

public class TimelineCommentCardDto implements Serializable {
    public CommentEntity commentEntity;
    public UserEntity userEntity;

    public Boolean isAllDataLoaded() {
        if (commentEntity == null) {
            return false;
        }
        return userEntity != null;

    }
}
