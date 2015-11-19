package com.songjin.usum.managers;

import com.kth.baasio.Baas;
import com.songjin.usum.entities.UserEntity;

public class AuthManager {
    public static UserEntity.UserType getSignedInUserType() {
        UserEntity signedInUser = new UserEntity(Baas.io().getSignedInUser());
        return signedInUser.userType;
    }
}
