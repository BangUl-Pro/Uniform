package com.songjin.usum.dtos;

import com.kth.baasio.entity.BaasioBaseEntity;
import com.songjin.usum.entities.CommentEntity;
import com.songjin.usum.entities.UserEntity;

public class TimelineCommentCardDto extends BaasioBaseEntity {
    public CommentEntity commentEntity;
    public UserEntity userEntity;

    public Boolean isAllDataLoaded() {
        if (commentEntity == null) {
            return false;
        }
        return userEntity != null;

    }
}
