package com.thnet.tailairbrakingtest.testwind;

import android.graphics.Color;
import android.view.View;

import com.thnet.tailairbrakingtest.dao.PressureValue;
import com.thnet.tailairbrakingtest.utility.TipSoundPlayer;

import java.util.List;

public class Test_LX extends TestContent {
    public Test_LX() {
        testName = TEST_NAME_LX;
        shortTestName = SHORT_TEST_NAME_LX;
        voiceFileNameBegin = TipSoundPlayer.VOICE_FILE_NAME_BEGIN_LX;
        voiceFileNameCompleted = TipSoundPlayer.VOICE_FILE_NAME_COMPLETED_LX;
        voiceFileNameNotCompleted = TipSoundPlayer.VOICE_FILE_NAME_NOT_COMPLETED_LX;
        analyseMax = 0;
        analyseMin = 0;
        standardKeepTime = SysParamsAll.get_lxStandardTime();
        standardDrop = 0;
        standardLeak = SysParamsAll.get_lxStandardLeak();
        drawColor = Color.RED;
        viewStatMainPressure = View.VISIBLE;
        viewStatKeepTime = View.VISIBLE;
        viewStatDropValue = View.GONE;
        viewStatLeakValue = View.VISIBLE;
    }

    @Override
    public TestContent checkStatus(String stime, int nPressureValue, CEstimate lstEsti, CEstimate lstTemp, List<PressureValue> lstPressureValue) {
        if (stat == TestState.tsNotBegin) {
            if (lstEsti.getAvg() > SysParamsAll.get_testPressureValueMin(definedPressureValue)) {
                startTime = stime;
                testMainPressureValue = nPressureValue;
                testPressureValueMax = nPressureValue;
                testPressureValueMin = 0;
                stat = TestState.tsDoing;
                xBeginPos = lstPressureValue.size() - 1;
                yPressureValue = nPressureValue;
                TipSoundPlayer.PlayVoicePrompts(voiceFileNameBegin);
            }
            return this;
        } else if (stat == TestState.tsDoing) {
            if (testPressureValueMin == 0 || nPressureValue < testPressureValueMin) {
                testPressureValueMin = nPressureValue > testPressureValueMax ? testPressureValueMax : nPressureValue;
            }
            setEndTime(stime);
            setTestLeakValue(testPressureValueMax - testPressureValueMin);
            xEndPos = lstPressureValue.size() - 1;
            if (testKeepTime >= SysParamsAll.get_lxStandardTime()) {
                //修正保压时间为标准保压时间
                testKeepTime = testKeepTime < standardKeepTime ? standardKeepTime : testKeepTime;
                stat = TestState.tsStoped;
                if (testMainPressureValue < definedPressureValue - SysParamsAll.get_wcDingYa() || getTestLeakValue() >= SysParamsAll.get_lxStandardLeak()
                        || testMainPressureValue > definedPressureValue + SysParamsAll.get_wcDingYa()) {
                    testResult = TEST_STATE_NOT_COMPLETED;
                    TipSoundPlayer.PlayVoicePrompts(voiceFileNameNotCompleted);
                    if (null != onTestFailedListener) {
                        onTestFailedListener.onTestFailed();
                    }
                } else {
                    testResult = TEST_STATE_COMPLETED;
                    TipSoundPlayer.PlayVoicePrompts(voiceFileNameCompleted);
                }
            }
            return this;
        } else {
            return this;
        }
    }
}
