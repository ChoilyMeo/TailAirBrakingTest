package com.thnet.tailairbrakingtest.dao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.OrderBy;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

@Entity(
        nameInDb = "SysParms",
        createInDb = false
)
public class SysParms {
    @Id(autoincrement = true)
    @Property(nameInDb = "id")
    Long id;
    @Property(nameInDb = "ParamID")
    String paramID;
    @Property(nameInDb = "ParamName")
    String paramName;
    @Property(nameInDb = "ParamValue")
    String paramValue;
    @OrderBy
    @Property(nameInDb = "ParamIndex")
    String paramIndex;

    @Generated(hash = 920620921)
    public SysParms(Long id, String paramID, String paramName, String paramValue,
            String paramIndex) {
        this.id = id;
        this.paramID = paramID;
        this.paramName = paramName;
        this.paramValue = paramValue;
        this.paramIndex = paramIndex;
    }

    @Generated(hash = 391562612)
    public SysParms() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getParamID() {
        return paramID;
    }

    public void setParamID(String paramID) {
        this.paramID = paramID;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    public String getParamIndex() {
        return paramIndex;
    }

    public void setParamIndex(String paramIndex) {
        this.paramIndex = paramIndex;
    }
}
