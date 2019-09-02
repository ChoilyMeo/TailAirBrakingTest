package com.thnet.tailairbrakingtest.DAO;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

@Entity(
        nameInDb = "TestWindContent",
        createInDb = false
)
public class TestWindContent {
    @Id(autoincrement = true)
    @Property(nameInDb = "id")
    Long id;
    @Property(nameInDb = "TestID")
    String testID;
    @Property(nameInDb = "TestDate")
    String testDate;
    @Property(nameInDb = "TrainNo")
    String trainNo;
    @Property(nameInDb = "TrainCount")
    String trainCount;
    @Property(nameInDb = "SpecifyPressure")
    String specifyPressure;
    @Property(nameInDb = "StartTime")
    String startTime;
    @Property(nameInDb = "EndTime")
    String endTime;
    @Property(nameInDb = "TestKind")
    String testKind;
    @Property(nameInDb = "Line")
    String line;
    @Property(nameInDb = "UserGroup")
    String userGroup;
    @Property(nameInDb = "TestOperator")
    String testOperator;
    @Property(nameInDb = "ValveNo")
    String valveNo;

    @Generated(hash = 1281953774)
    public TestWindContent(Long id, String testID, String testDate, String trainNo,
            String trainCount, String specifyPressure, String startTime,
            String endTime, String testKind, String line, String userGroup,
            String testOperator, String valveNo) {
        this.id = id;
        this.testID = testID;
        this.testDate = testDate;
        this.trainNo = trainNo;
        this.trainCount = trainCount;
        this.specifyPressure = specifyPressure;
        this.startTime = startTime;
        this.endTime = endTime;
        this.testKind = testKind;
        this.line = line;
        this.userGroup = userGroup;
        this.testOperator = testOperator;
        this.valveNo = valveNo;
    }

    @Generated(hash = 649603714)
    public TestWindContent() {
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

    public String getTestDate() {
        return testDate;
    }

    public void setTestDate(String testDate) {
        this.testDate = testDate;
    }

    public String getTrainNo() {
        return trainNo;
    }

    public void setTrainNo(String trainNo) {
        this.trainNo = trainNo;
    }

    public String getTrainCount() {
        return trainCount;
    }

    public void setTrainCount(String trainCount) {
        this.trainCount = trainCount;
    }

    public String getSpecifyPressure() {
        return specifyPressure;
    }

    public void setSpecifyPressure(String specifyPressure) {
        this.specifyPressure = specifyPressure;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getTestKind() {
        return testKind;
    }

    public void setTestKind(String testKind) {
        this.testKind = testKind;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(String userGroup) {
        this.userGroup = userGroup;
    }

    public String getTestOperator() {
        return testOperator;
    }

    public void setTestOperator(String testOperator) {
        this.testOperator = testOperator;
    }

    public String getValveNo() {
        return valveNo;
    }

    public void setValveNo(String valveNo) {
        this.valveNo = valveNo;
    }
}
