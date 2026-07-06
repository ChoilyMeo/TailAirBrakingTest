package com.thnet.tailairbrakingtest.testwind;

///重载整备感度试验
public class Test_GD_ZZZB extends Test_GD{
    public Test_GD_ZZZB(){
        standardKeepTime = SysParamsAll.get_zzzbgdStandardTime();
        standardDrop = SysParamsAll.get_zzzbgdStandardDrop();
        standardLeak = SysParamsAll.get_zzzbgdStandardLeak();
    }
}
