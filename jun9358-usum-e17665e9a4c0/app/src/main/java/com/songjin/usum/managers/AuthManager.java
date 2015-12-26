package com.songjin.usum.managers;

import com.songjin.usum.Global;
import com.songjin.usum.entities.UserEntity;

public class AuthManager {
    public static int getSignedInUserType() {
        if (Global.userEntity == null)
            Global.userEntity = new UserEntity();
        UserEntity signedInUser = Global.userEntity;
        return signedInUser.userType;
    }
}
