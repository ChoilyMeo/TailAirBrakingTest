package com.thnet.tailairbrakingtest.dao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Transient;

@Entity(
        nameInDb = "PressureValue",
        createInDb = false
)
public class PressureValue {
    @Id(autoincrement = true)
    @Property(nameInDb = "id")
    Long id;
    @Property(nameInDb = "TestID")
    String testID;
    @Property(nameInDb = "PressureTime")
    String pressureTime;
    @Property(nameInDb = "PressureValue")
    int pressureValue;

    public PressureValue() {
        this.pressureValue = 0;
        this.pressureTime = "";
    }

    @Generated(hash = 170822317)
    public PressureValue(Long id, String testID, String pressureTime, int pressureValue) {
        this.id = id;
        this.testID = testID;
        this.pressureTime = pressureTime;
        this.pressureValue = pressureValue;
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

    public String getPressureTime() {
        return pressureTime;
    }

    public void setPressureTime(String pressureTime) {
        this.pressureTime = pressureTime;
    }

    public int getPressureValue() {
        return pressureValue;
    }

    public void setPressureValue(int pressureValue) {
        this.pressureValue = pressureValue;
    }
}
