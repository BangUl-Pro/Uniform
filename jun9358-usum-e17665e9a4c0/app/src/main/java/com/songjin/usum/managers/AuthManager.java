package com.songjin.usum.managers;

import com.songjin.usum.Global;
import com.songjin.usum.entities.UserEntity;

public class AuthManager {
    public static UserEntity.UserType getSignedInUserType() {
        UserEntity signedInUser = Global.userEntity;
        return signedInUser.userType;
    }
}
