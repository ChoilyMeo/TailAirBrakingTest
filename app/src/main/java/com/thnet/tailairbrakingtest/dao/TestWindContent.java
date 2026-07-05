package com.thnet.tailairbrakingtest.dao;

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
    @Property(nameInDb = "TestOperator")
    String testOperator;

    @Generated(hash = 978971230)
    public TestWindContent(Long id, String testID, String testDate, String trainNo,
                           String trainCount, String specifyPressure, String startTime,
                           String endTime, String testKind, String line, String testOperator) {
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
        this.testOperator = testOperator;
    }

    @Generated(hash = 649603714)
    public TestWindContent() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTestID() {
        return this.testID;
    }

    public void setTestID(String testID) {
        this.testID = testID;
    }

    public String getTestDate() {
        return this.testDate;
    }

    public void setTestDate(String testDate) {
        this.testDate = testDate;
    }

    public String getTrainNo() {
        return this.trainNo;
    }

    public void setTrainNo(String trainNo) {
        this.trainNo = trainNo;
    }

    public String getTrainCount() {
        return this.trainCount;
    }

    public void setTrainCount(String trainCount) {
        this.trainCount = trainCount;
    }

    public String getSpecifyPressure() {
        return this.specifyPressure;
    }

    public void setSpecifyPressure(String specifyPressure) {
        this.specifyPressure = specifyPressure;
    }

    public String getStartTime() {
        return this.startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return this.endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getTestKind() {
        return this.testKind;
    }

    public void setTestKind(String testKind) {
        this.testKind = testKind;
    }

    public String getLine() {
        return this.line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getTestOperator() {
        return this.testOperator;
    }

    public void setTestOperator(String testOperator) {
        this.testOperator = testOperator;
    }
}
