package com.thnet.tailairbrakingtest.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.thnet.tailairbrakingtest.customapplication.WindTestApplication;
import com.thnet.tailairbrakingtest.testwind.TestContent;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.OrderBy;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

import java.util.List;

@Entity(
        nameInDb = "TestKind",
        createInDb = false
)
public class TestKind implements Parcelable {
    public final static int MAX_TEST_KIND_COUNT = 10;
    private static final String TEST_CHECKED_VALUE = "1";
    private static final String TEST_NOT_CHECKED_VALUE = "0";
    public static final String[] TEST_NAME_ALL = {TestContent.TEST_NAME_LX, TestContent.TEST_NAME_GD, TestContent.TEST_NAME_AD, TestContent.TEST_NAME_BY, TestContent.TEST_NAME_JL};

    @Id(autoincrement = true)
    @Property(nameInDb = "id")
    Long id;
    @Property(nameInDb = "TestName")
    String testKindName;
    @Property(nameInDb = "Test_LX")
    String testLX;
    @Property(nameInDb = "Test_AD")
    String testAD;
    @Property(nameInDb = "Test_GD")
    String testGD;
    @Property(nameInDb = "Test_BY")
    String testBY;
    @Property(nameInDb = "Test_JL")
    String testJL;

    protected TestKind(Parcel in){
        id = in.readLong();
        testKindName = in.readString();
        testLX = in.readString();
        testAD = in.readString();
        testGD = in.readString();
        testBY = in.readString();
        testJL = in.readString();
    }

    @Generated(hash = 1717774386)
    public TestKind(Long id, String testKindName, String testLX, String testAD, String testGD, String testBY, String testJL) {
        this.id = id;
        this.testKindName = testKindName;
        this.testLX = testLX;
        this.testAD = testAD;
        this.testGD = testGD;
        this.testBY = testBY;
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

    public boolean getTestCheckedByName(String testName){
        switch (testName){
            case TestContent.TEST_NAME_LX:
                return TEST_CHECKED_VALUE.equals(testLX);
            case TestContent.TEST_NAME_GD:
                return TEST_CHECKED_VALUE.equals(testGD);
            case TestContent.TEST_NAME_AD:
                return TEST_CHECKED_VALUE.equals(testAD);
            case TestContent.TEST_NAME_BY:
                return TEST_CHECKED_VALUE.equals(testBY);
            case TestContent.TEST_NAME_JL:
                return TEST_CHECKED_VALUE.equals(testJL);
            default:
                return false;
        }
    }

    public void setTestCheckByName(String testName, boolean isCheck){
        switch (testName){
            case TestContent.TEST_NAME_LX:
                testLX = isCheck ? TEST_CHECKED_VALUE : TEST_NOT_CHECKED_VALUE;
                break;
            case TestContent.TEST_NAME_GD:
                testGD = isCheck ? TEST_CHECKED_VALUE : TEST_NOT_CHECKED_VALUE;
                break;
            case TestContent.TEST_NAME_AD:
                testAD = isCheck ? TEST_CHECKED_VALUE : TEST_NOT_CHECKED_VALUE;
                break;
            case TestContent.TEST_NAME_BY:
                testBY = isCheck ? TEST_CHECKED_VALUE : TEST_NOT_CHECKED_VALUE;
                break;
            case TestContent.TEST_NAME_JL:
                testJL = isCheck ? TEST_CHECKED_VALUE : TEST_NOT_CHECKED_VALUE;
                break;
            default:
                break;
        }
    }

    public boolean isTestLXChecked(){
        return  TEST_CHECKED_VALUE.equals(testLX);
    }

    public void setTextLXChecked(boolean isCheck){
        testLX = isCheck ? TEST_CHECKED_VALUE : TEST_NOT_CHECKED_VALUE;
    }

    public boolean isTestADChecked(){
        return  TEST_CHECKED_VALUE.equals(testAD);
    }

    public void setTextADChecked(boolean isCheck){
        testAD = isCheck ? TEST_CHECKED_VALUE : TEST_NOT_CHECKED_VALUE;
    }

    public boolean isTestGDChecked(){
        return  TEST_CHECKED_VALUE.equals(testGD);
    }

    public void setTextGDChecked(boolean isCheck){
        testGD = isCheck ? TEST_CHECKED_VALUE : TEST_NOT_CHECKED_VALUE;
    }

    public boolean isTestBYChecked(){
        return  TEST_CHECKED_VALUE.equals(testBY);
    }

    public void setTextBYChecked(boolean isCheck){
        testBY = isCheck ? TEST_CHECKED_VALUE : TEST_NOT_CHECKED_VALUE;
    }

    public boolean isTestJLChecked(){
        return  TEST_CHECKED_VALUE.equals(testJL);
    }

    public void setTextJLChecked(boolean isCheck){
        testJL = isCheck ? TEST_CHECKED_VALUE : TEST_NOT_CHECKED_VALUE;
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


    public String getTestLX() {
        return this.testLX;
    }

    public void setTestLX(String testLX) {
        this.testLX = testLX;
    }

    public String getTestBY() {
        return this.testBY;
    }

    public void setTestBY(String testBY) {
        this.testBY = testBY;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(testKindName);
        dest.writeString(testLX);
        dest.writeString(testAD);
        dest.writeString(testGD);
        dest.writeString(testBY);
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
