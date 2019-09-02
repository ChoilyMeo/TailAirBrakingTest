package com.thnet.tailairbrakingtest.DAO;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

@Entity(
        nameInDb = "UserInfo",
        createInDb = false
)
public class UserInfo {
    @Id(autoincrement = true)
    @Property(nameInDb = "id")
    Long id;
    @Unique
    @Property(nameInDb = "UserID")
    String userID;
    @Property(nameInDb = "UserName")
    String userName;
    @Property(nameInDb = "UserPwd")
    String userPwd;

    @Generated(hash = 452866237)
    public UserInfo(Long id, String userID, String userName, String userPwd) {
        this.id = id;
        this.userID = userID;
        this.userName = userName;
        this.userPwd = userPwd;
    }

    @Generated(hash = 1279772520)
    public UserInfo() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPwd() {
        return userPwd;
    }

    public void setUserPwd(String userPwd) {
        this.userPwd = userPwd;
    }
}
