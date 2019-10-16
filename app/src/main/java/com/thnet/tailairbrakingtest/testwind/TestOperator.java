package com.thnet.tailairbrakingtest.testwind;

import android.util.Log;

import com.thnet.tailairbrakingtest.customapplication.WindTestApplication;
import com.thnet.tailairbrakingtest.dao.DaoSession;
import com.thnet.tailairbrakingtest.dao.UserInfo;

import java.util.List;

public class TestOperator {
    private static final String TAG = TestOperator.class.getSimpleName();
    private static UserInfo currentUser;

    public static List<UserInfo> getAllUserInfo(){
        List<UserInfo> userInfos;
        try {
            DaoSession daoSession = WindTestApplication.getWindTestInstance().getDaoSession();
            userInfos = daoSession.loadAll(UserInfo.class);
        }
        catch (Exception ex){
            Log.e(TAG, ex.getMessage());
            userInfos = null;
        }
        return userInfos;
    }

    public static boolean checkUserPassword(UserInfo userInfo, String pwd){
        if (null != userInfo && null != userInfo.getUserPwd() && userInfo.getUserPwd().endsWith(pwd)){
            return true;
        }
        return false;
    }

    public static UserInfo getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(UserInfo currentUser) {
        TestOperator.currentUser = currentUser;
    }
}
