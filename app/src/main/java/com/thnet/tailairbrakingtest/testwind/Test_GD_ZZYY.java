package com.thnet.tailairbrakingtest.testwind;

///重载运用的感度试验
public class Test_GD_ZZYY extends Test_GD{
    public Test_GD_ZZYY(){
        standardLeak = SysParamsAll.get_zzyygdStandardLeak();//漏泄量标准
        standardDrop = SysParamsAll.get_zzyygdStandardDrop();
        standardLeak = SysParamsAll.get_zzyygdStandardLeak();
    }
}
