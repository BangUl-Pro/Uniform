package com.songjin.usum.dtos;

import com.songjin.usum.entities.CommentEntity;
import com.songjin.usum.entities.UserEntity;

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
