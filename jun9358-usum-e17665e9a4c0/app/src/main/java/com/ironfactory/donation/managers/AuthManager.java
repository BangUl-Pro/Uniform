package com.ironfactory.donation.managers;

import com.ironfactory.donation.Global;
import com.ironfactory.donation.entities.UserEntity;

public class AuthManager {
    public static UserEntity.UserType getSignedInUserType() {
        UserEntity signedInUser = Global.userEntity;
        return signedInUser.userType;
    }
}
