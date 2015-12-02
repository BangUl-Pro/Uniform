package com.ironfactory.donation.managers;

import com.ironfactory.donation.Global;
import com.ironfactory.donation.entities.UserEntity;

public class AuthManager {
    public static int getSignedInUserType() {
        if (Global.userEntity == null)
            Global.userEntity = new UserEntity();
        UserEntity signedInUser = Global.userEntity;
        return signedInUser.userType;
    }
}
