package com.thnet.tailairbrakingtest.DAO;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Property;

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
    @Property(nameInDb = "HeadPressureValue")
    int headPressureValue;
    @Property(nameInDb = "SourcePressureValue")
    int sourcePressureValue;
    @Property(nameInDb = "CenterPressureValue")
    int centerPressureValue;
    @Property(nameInDb = "BoxTemperature")
    int boxTemperature;
    @Property(nameInDb = "BoxHumidity")
    int boxHumidity;
    @Property(nameInDb = "PressureKeepMinutes")
    int pressureKeepMinutes;
    @Property(nameInDb = "TailPressureState1")
    int tailPressureState1;
    @Property(nameInDb = "TailPressureState2")
    int tailPressureState2;
    @Property(nameInDb = "TailPressureValue1")
    int tailPressureValue1;
    @Property(nameInDb = "TailPressureValue2")
    int tailPressureValue2;
    @Property(nameInDb = "PressureSourceOrTotal")
    int pressureSourceOrTotal;
    @Property(nameInDb = "TestResult")
    int testResult = 0;

    public PressureValue() {
        this.pressureValue = 0;
        this.pressureTime = "";
        this.headPressureValue = 0;
        this.sourcePressureValue = 0;
        this.centerPressureValue = 0;
        this.boxTemperature = 0;
        this.boxHumidity = 0;
        this.pressureKeepMinutes = 0;
        this.tailPressureState1 = 0;
        this.tailPressureState2 = 0;
        this.tailPressureValue1 = 0;
        this.tailPressureValue2 = 0;
        this.pressureSourceOrTotal = 0;
        this.testResult = 0;
    }

    public PressureValue(String pressureTime, int pressureValue, int headPressureValue, int sourcePressureValue, int centerPressureValue, int boxTemperature, int boxHumidity, int minutes, int tailPressureState1, int tailPressureState2, int tailPressureValue1, int tailPressureValue2, int pressureSourceOrTotal, int testResult) {
        this.pressureValue = pressureValue;
        this.pressureTime = pressureTime;
        this.headPressureValue = headPressureValue;
        this.sourcePressureValue = sourcePressureValue;
        this.centerPressureValue = centerPressureValue;
        this.boxTemperature = boxTemperature;
        this.boxHumidity = boxHumidity;
        this.pressureKeepMinutes = minutes;
        this.tailPressureState1 = tailPressureState1;
        this.tailPressureState2 = tailPressureState2;
        this.tailPressureValue1 = tailPressureValue1;
        this.tailPressureValue2 = tailPressureValue2;
        this.pressureSourceOrTotal = pressureSourceOrTotal;
        this.testResult = testResult;
    }

    @Generated(hash = 529333309)
    public PressureValue(Long id, String testID, String pressureTime, int pressureValue, int headPressureValue, int sourcePressureValue, int centerPressureValue, int boxTemperature, int boxHumidity, int pressureKeepMinutes, int tailPressureState1, int tailPressureState2, int tailPressureValue1, int tailPressureValue2,
            int pressureSourceOrTotal, int testResult) {
        this.id = id;
        this.testID = testID;
        this.pressureTime = pressureTime;
        this.pressureValue = pressureValue;
        this.headPressureValue = headPressureValue;
        this.sourcePressureValue = sourcePressureValue;
        this.centerPressureValue = centerPressureValue;
        this.boxTemperature = boxTemperature;
        this.boxHumidity = boxHumidity;
        this.pressureKeepMinutes = pressureKeepMinutes;
        this.tailPressureState1 = tailPressureState1;
        this.tailPressureState2 = tailPressureState2;
        this.tailPressureValue1 = tailPressureValue1;
        this.tailPressureValue2 = tailPressureValue2;
        this.pressureSourceOrTotal = pressureSourceOrTotal;
        this.testResult = testResult;
    }

    public String createInsertSql(String strBH) {
        String sql;
        sql = "insert into PressureValue values('" + strBH + "','" + pressureTime + "','" + pressureValue + "','" +
                headPressureValue + "','" + sourcePressureValue + "','" +
                centerPressureValue + "','" + boxTemperature + "','" + boxHumidity + "')";
        return sql;
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

    public int getHeadPressureValue() {
        return headPressureValue;
    }

    public void setHeadPressureValue(int headPressureValue) {
        this.headPressureValue = headPressureValue;
    }

    public int getSourcePressureValue() {
        return sourcePressureValue;
    }

    public void setSourcePressureValue(int sourcePressureValue) {
        this.sourcePressureValue = sourcePressureValue;
    }

    public int getCenterPressureValue() {
        return centerPressureValue;
    }

    public void setCenterPressureValue(int centerPressureValue) {
        this.centerPressureValue = centerPressureValue;
    }

    public int getBoxTemperature() {
        return boxTemperature;
    }

    public void setBoxTemperature(int boxTemperature) {
        this.boxTemperature = boxTemperature;
    }

    public int getBoxHumidity() {
        return boxHumidity;
    }

    public void setBoxHumidity(int boxHumidity) {
        this.boxHumidity = boxHumidity;
    }

    public int getPressureKeepMinutes() {
        return pressureKeepMinutes;
    }

    public void setPressureKeepMinutes(int pressureKeepMinutes) {
        this.pressureKeepMinutes = pressureKeepMinutes;
    }

    public int getTailPressureState1() {
        return tailPressureState1;
    }

    public void setTailPressureState1(int tailPressureState1) {
        this.tailPressureState1 = tailPressureState1;
    }

    public int getTailPressureState2() {
        return tailPressureState2;
    }

    public void setTailPressureState2(int tailPressureState2) {
        this.tailPressureState2 = tailPressureState2;
    }

    public int getTailPressureValue1() {
        return tailPressureValue1;
    }

    public void setTailPressureValue1(int tailPressureValue1) {
        this.tailPressureValue1 = tailPressureValue1;
    }

    public int getTailPressureValue2() {
        return tailPressureValue2;
    }

    public void setTailPressureValue2(int tailPressureValue2) {
        this.tailPressureValue2 = tailPressureValue2;
    }

    public int getTestResult() {
        return testResult;
    }

    public void setTestResult(int testResult) {
        this.testResult = testResult;
    }

    public int getPressureSourceOrTotal() {
        return this.pressureSourceOrTotal;
    }

    public void setPressureSourceOrTotal(int pressureSourceOrTotal) {
        this.pressureSourceOrTotal = pressureSourceOrTotal;
    }
}
