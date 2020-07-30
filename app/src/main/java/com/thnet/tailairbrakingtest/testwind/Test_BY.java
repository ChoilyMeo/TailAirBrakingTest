package com.thnet.tailairbrakingtest.testwind;

import android.graphics.Color;
import android.view.View;

import com.thnet.tailairbrakingtest.utility.StringUtil;
import com.thnet.tailairbrakingtest.utility.TipSoundPlayer;

public class Test_BY extends TestContent {
    protected int testLeakValue2 = -1;//漏泄量第二分钟
    protected int testLeakValue3 = -1;//漏泄量第三分钟
    protected int testLeakValue4 = -1;//漏泄量第四分钟
    protected int testLeakValue5 = -1;//漏泄量第五分钟
    protected int checkLeakValueType = SysParamsAll.CHECK_LEAK_VALUE_TYPE_ONE_MINUTE;//漏泄量判断的类型：0-取第一分钟的漏泄量；1-取每一分钟的漏泄量；2-取标准保压时间内的漏泄量
    public Test_BY() {
        testName = TEST_NAME_BY;
        shortTestName = SHORT_TEST_NAME_BY;
        voiceFileNameBegin = TipSoundPlayer.VOICE_FILE_NAME_BEGIN_BY;
        voiceFileNameCompleted = TipSoundPlayer.VOICE_FILE_NAME_COMPLETED_BY;
        voiceFileNameNotCompleted = TipSoundPlayer.VOICE_FILE_NAME_NOT_COMPLETED_BY;
        checkLeakValueType = SysParamsAll.getCheckLeakValueType();
        analyseMax = SysParamsAll.get_byAnalyseMax();
        analyseMin = SysParamsAll.get_byAnalyseMin();
        standardKeepTime = SysParamsAll.get_byStandardTime();
        standardLeak = 10000;
        drawColor = Color.LTGRAY;
        viewStatMainPressure = View.VISIBLE;
        viewStatKeepTime = View.VISIBLE;
        viewStatDropValue = View.VISIBLE;
        viewStatLeakValue = View.VISIBLE;
    }

    @Override
    public void updateStandardDrop() {
        super.updateStandardDrop();
        standardDrop = SysParamsAll.get_byStandardDrop();
    }

    @Override
    public void reset() {
        super.reset();
        testLeakValue2 = -1;
        testLeakValue3 = -1;
        testLeakValue4 = -1;
        testLeakValue5 = -1;
    }

    @Override
    protected boolean validateLeakValue() {
        return getTestLeakValue() < getStandardLeak() + getWcLouXie() &&
                getTestLeakValue2() < getStandardLeak() + getWcLouXie() &&
                getTestLeakValue3() < getStandardLeak() + getWcLouXie() &&
                getTestLeakValue4() < getStandardLeak() + getWcLouXie() &&
                getTestLeakValue5() < getStandardLeak() + getWcLouXie();
    }

    public int getTestLeakValue2() {
        return testLeakValue2;
    }

    public void setTestLeakValue2(int testLeakValue2) {
        if (testLeakValue2 > getTestLeakValue()){
            this.testLeakValue2 = testLeakValue2 - getTestLeakValue();
        } else {
            this.testLeakValue2 = 0;
        }
    }

    public int getTestLeakValue3() {
        return testLeakValue3;
    }

    public void setTestLeakValue3(int testLeakValue3) {
        if (testLeakValue3 > getTestLeakValue() + getTestLeakValue2()){
            this.testLeakValue3 = testLeakValue3 - getTestLeakValue() - getTestLeakValue2();
        } else {
            this.testLeakValue3 = 0;
        }
    }

    public int getTestLeakValue4() {
        return testLeakValue4;
    }

    public void setTestLeakValue4(int testLeakValue4) {
        if (testLeakValue4 > getTestLeakValue() + getTestLeakValue2() + getTestLeakValue3()){
            this.testLeakValue4 = testLeakValue4 - getTestLeakValue() - getTestLeakValue2() - getTestLeakValue3();
        } else {
            this.testLeakValue4 = 0;
        }
    }

    public int getTestLeakValue5() {
        return testLeakValue5;
    }

    public void setTestLeakValue5(int testLeakValue5) {
        if (testLeakValue5 > getTestLeakValue() + getTestLeakValue2() + getTestLeakValue3() + getTestLeakValue4()){
            this.testLeakValue5 = testLeakValue5 - getTestLeakValue() - getTestLeakValue2() - getTestLeakValue3() - getTestLeakValue4();
        } else {
            this.testLeakValue5 = 0;
        }
    }

    public int getCheckLeakValueType() {
        return checkLeakValueType;
    }

    public void setCheckLeakValueType(int checkLeakValueType) {
        this.checkLeakValueType = checkLeakValueType;
    }

    @Override
    protected void updateTestLeakValue(int testLeakValue) {
        int lxl = testLeakValue;
        if (lxl < 0) {
            lxl = 0;
        }
        switch (getCheckLeakValueType()){
            case SysParamsAll.CHECK_LEAK_VALUE_TYPE_ONE_MINUTE:
                if (getTestKeepTime() <= 60)
                {
                    setTestLeakValue(lxl);
                }
                break;
            case SysParamsAll.CHECK_LEAK_VALUE_TYPE_PER_MINUTE:
                if (getTestKeepTime() <= 60)
                {
                    setTestLeakValue(lxl);
                } else if (getTestKeepTime() <= 60 * 2)
                {
                    setTestLeakValue2(lxl);
                } else if (getTestKeepTime() <= 60 * 3)
                {
                    setTestLeakValue3(lxl);
                } else if (getTestKeepTime() <= 60 * 4)
                {
                    setTestLeakValue4(lxl);
                } else if (getTestKeepTime() <= 60 * 5)
                {
                    setTestLeakValue5(lxl);
                }
                break;
            case SysParamsAll.CHECK_LEAK_VALUE_TYPE_ALL_KEEP_TIME:
                setTestLeakValue(lxl);
                break;
            default:
                if (getTestKeepTime() <= 60)
                {
                    setTestLeakValue(lxl);
                }
                break;
        }
    }

    @Override
    public String getTestLeakValueDisplay() {
        String rtn;
        switch (getCheckLeakValueType()){
            case SysParamsAll.CHECK_LEAK_VALUE_TYPE_ONE_MINUTE:
                rtn = String.valueOf(getTestLeakValue());
                break;
            case SysParamsAll.CHECK_LEAK_VALUE_TYPE_PER_MINUTE:
                rtn = String.format("%s,%s,%s,%s,%s",
                        getTestLeakValue() >= 0 ? String.valueOf(getTestLeakValue()) : " ",
                        getTestLeakValue2() >= 0 ? String.valueOf(getTestLeakValue2()) : " ",
                        getTestLeakValue3() >= 0 ? String.valueOf(getTestLeakValue3()) : " ",
                        getTestLeakValue4() >= 0 ? String.valueOf(getTestLeakValue4()) : " ",
                        getTestLeakValue5() >= 0 ? String.valueOf(getTestLeakValue5()) : " ");
                break;
            case SysParamsAll.CHECK_LEAK_VALUE_TYPE_ALL_KEEP_TIME:
                rtn = String.valueOf(getTestLeakValue());
                break;
            default:
                rtn = String.valueOf(getTestLeakValue());
                break;
        }
        return rtn;
    }

    //如果取的是一个漏泄量的话，则就是漏泄量的值(例如：5)，如果是取五分钟的每分钟的漏泄量的话，则保存的是每分钟的漏泄量，漏泄量之间以逗号分隔(例如：1,2,1,2,1)
    @Override
    public void setTestLeakValueFromDisplay(String testLeakValueEx) {
        if (StringUtil.isNullOrEmpty(testLeakValueEx)){
            int leakValue = 0;
            String[] arrLeakValue = testLeakValueEx.split(",");
            if (null != arrLeakValue && arrLeakValue.length >= 5){
                try{
                    leakValue = Integer.parseInt(arrLeakValue[0]);
                } catch (Exception ex) {
                    leakValue = 0;
                }
                setTestLeakValue(leakValue);
                try{
                    leakValue = Integer.parseInt(arrLeakValue[1]);
                } catch (Exception ex) {
                    leakValue = -1;
                }
                setTestLeakValue2(leakValue);
                try{
                    leakValue = Integer.parseInt(arrLeakValue[2]);
                } catch (Exception ex) {
                    leakValue = -1;
                }
                setTestLeakValue3(leakValue);
                try{
                    leakValue = Integer.parseInt(arrLeakValue[3]);
                } catch (Exception ex) {
                    leakValue = -1;
                }
                setTestLeakValue4(leakValue);
                try{
                    leakValue = Integer.parseInt(arrLeakValue[4]);
                } catch (Exception ex) {
                    leakValue = -1;
                }
                setTestLeakValue5(leakValue);
            } else {
                try{
                    leakValue = Integer.parseInt(arrLeakValue[0]);
                } catch (Exception ex) {
                    leakValue = 0;
                }
                setTestLeakValue(leakValue);
                setTestLeakValue2(-1);
                setTestLeakValue3(-1);
                setTestLeakValue4(-1);
                setTestLeakValue5(-1);
            }
        } else {
            setTestLeakValue(0);
            setTestLeakValue2(-1);
            setTestLeakValue3(-1);
            setTestLeakValue4(-1);
            setTestLeakValue5(-1);
        }
    }
}
