package com.thnet.tailairbrakingtest.testwind;

//重载运用和整备的安定试验
public class Test_AD_ZZ extends Test_AD{
    public Test_AD_ZZ(){
        standardLeak = 999;//重载运用和整备的安定试验，只判断减压170kpa
        standardDrop = SysParamsAll.get_zzadStandardDrop();
    }
}
