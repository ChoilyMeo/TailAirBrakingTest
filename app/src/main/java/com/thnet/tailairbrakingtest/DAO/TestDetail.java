package com.thnet.tailairbrakingtest.DAO;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Property;

@Entity(
        nameInDb = "TestDetail",
        createInDb = false
)
public class TestDetail {
    @Id(autoincrement = true)
    @Property(nameInDb = "id")
    Long id;
    @Property(nameInDb = "TestID")
    String testID;//试风编号
    @Property(nameInDb = "TestName")
    String testName;//试验名称
    @Property(nameInDb = "BeginTime")
    String beginTime;//开始时间
    @Property(nameInDb = "EndTime")
    String endTime;//结束时间
    @Property(nameInDb = "TestPressure")
    String testPressure;//主管压力
    @Property(nameInDb = "KeepTime")
    String keepTime;//保压时间
    @Property(nameInDb = "LeakValue")
    String leakValue;//漏写量
    @Property(nameInDb = "DropValue")
    String dropValue;//减压量
    @Property(nameInDb = "DropTime")
    String dropTime;//减压时间（机能的感度、安定）
    @Property(nameInDb = "DropSpeed")
    String dropSpeed;//减压速度（试风的感度、安定）
    @Property(nameInDb = "State")
    String state;//试验状态

    @Generated(hash = 1825960893)
    public TestDetail(Long id, String testID, String testName, String beginTime,
            String endTime, String testPressure, String keepTime, String leakValue,
            String dropValue, String dropTime, String dropSpeed, String state) {
        this.id = id;
        this.testID = testID;
        this.testName = testName;
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.testPressure = testPressure;
        this.keepTime = keepTime;
        this.leakValue = leakValue;
        this.dropValue = dropValue;
        this.dropTime = dropTime;
        this.dropSpeed = dropSpeed;
        this.state = state;
    }

    @Generated(hash = 1862156018)
    public TestDetail() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTestID() {
        return testID;
    }

    public void setTestID(String testID) {
        this.testID = testID;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getTestPressure() {
        return testPressure;
    }

    public void setTestPressure(String testPressure) {
        this.testPressure = testPressure;
    }

    public String getKeepTime() {
        return keepTime;
    }

    public void setKeepTime(String keepTime) {
        this.keepTime = keepTime;
    }

    public String getLeakValue() {
        return leakValue;
    }

    public void setLeakValue(String leakValue) {
        this.leakValue = leakValue;
    }

    public String getDropValue() {
        return dropValue;
    }

    public void setDropValue(String dropValue) {
        this.dropValue = dropValue;
    }

    public String getDropTime() {
        return dropTime;
    }

    public void setDropTime(String dropTime) {
        this.dropTime = dropTime;
    }

    public String getDropSpeed() {
        return dropSpeed;
    }

    public void setDropSpeed(String dropSpeed) {
        this.dropSpeed = dropSpeed;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
