package com.thnet.tailairbrakingtest.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.thnet.tailairbrakingtest.customapplication.WindTestApplication;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

import java.util.List;

@Entity(
        nameInDb = "TestKind",
        createInDb = false
)
public class TestKind implements Parcelable {
    static final String TEST_CHECKED_VALUE = "1";
    @Id(autoincrement = true)
    @Property(nameInDb = "id")
    Long id;
    @Property(nameInDb = "TestName")
    String testKindName;
    @Property(nameInDb = "Test_AD")
    String testAD;
    @Property(nameInDb = "Test_GD")
    String testGD;
    @Property(nameInDb = "Test_JL")
    String testJL;

    protected TestKind(Parcel in){
        id = in.readLong();
        testKindName = in.readString();
        testAD = in.readString();
        testGD = in.readString();
        testJL = in.readString();
    }

    @Generated(hash = 110329682)
    public TestKind(Long id, String testKindName, String testAD, String testGD,
                    String testJL) {
        this.id = id;
        this.testKindName = testKindName;
        this.testAD = testAD;
        this.testGD = testGD;
        this.testJL = testJL;
    }

    @Generated(hash = 1957612855)
    public TestKind() {
    }

    public static TestKind loadTestKind(String testKindName){
        DaoSession daoSession = WindTestApplication.getWindTestInstance().getDaoSession();
        List<TestKind> testKindList = daoSession.getTestKindDao().queryBuilder().where(TestKindDao.Properties.TestKindName.eq(testKindName)).list();
        if (null != testKindList && testKindList.size() > 0){
            return testKindList.get(0);
        } else {
            return null;
        }
    }

    public boolean isTestADChecked(){
        return  TEST_CHECKED_VALUE.equals(testAD);
    }

    public boolean isTestGDChecked(){
        return  TEST_CHECKED_VALUE.equals(testGD);
    }

    public boolean isTestJLChecked(){
        return  TEST_CHECKED_VALUE.equals(testJL);
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTestKindName() {
        return this.testKindName;
    }

    public void setTestKindName(String testKindName) {
        this.testKindName = testKindName;
    }

    public String getTestAD() {
        return this.testAD;
    }

    public void setTestAD(String testAD) {
        this.testAD = testAD;
    }

    public String getTestGD() {
        return this.testGD;
    }

    public void setTestGD(String testGD) {
        this.testGD = testGD;
    }

    public String getTestJL() {
        return this.testJL;
    }

    public void setTestJL(String testJL) {
        this.testJL = testJL;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(testKindName);
        dest.writeString(testAD);
        dest.writeString(testGD);
        dest.writeString(testJL);
    }

    public static final Creator<TestKind> CREATOR = new Creator<TestKind>() {
        @Override
        public TestKind createFromParcel(Parcel source) {
            return new TestKind(source);
        }

        @Override
        public TestKind[] newArray(int size) {
            return new TestKind[0];
        }
    };
}
