package com.thnet.tailairbrakingtest.testwind;

///重载运用的感度试验
public class Test_GD_ZZYY extends Test_GD{
    public Test_GD_ZZYY(){
        standardKeepTime = SysParamsAll.get_zzyygdStandardTime();//保压时间标准
        standardDrop = SysParamsAll.get_zzyygdStandardDrop();
        standardLeak = SysParamsAll.get_zzyygdStandardLeak();
    }

    //重载运用的感度试验，漏泄量取整个保压时间内的漏泄量，而不是第一分钟
    @Override
    protected void updateTestLeakValue(int testLeakValue) {
        int lxl = testLeakValue;
        if (lxl < 0) {
            lxl = 0;
        }
        setTestLeakValue(lxl);
    }
}
