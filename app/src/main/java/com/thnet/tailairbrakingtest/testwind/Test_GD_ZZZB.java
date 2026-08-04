package com.thnet.tailairbrakingtest.testwind;

///重载整备感度试验
public class Test_GD_ZZZB extends Test_GD{
    public Test_GD_ZZZB(){
        standardKeepTime = SysParamsAll.get_zzzbgdStandardTime();
        standardDrop = SysParamsAll.get_zzzbgdStandardDrop();
        standardLeak = SysParamsAll.get_zzzbgdStandardLeak();
    }

    //重载整备的感度试验，漏泄量取整个保压时间内的漏泄量，而不是第一分钟
    @Override
    protected void updateTestLeakValue(int testLeakValue) {
        int lxl = testLeakValue;
        if (lxl < 0) {
            lxl = 0;
        }
        setTestLeakValue(lxl);
    }
}
