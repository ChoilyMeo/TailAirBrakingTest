package com.thnet.tailairbrakingtest.testwind;

import android.graphics.Color;
import android.view.View;

import com.thnet.tailairbrakingtest.utility.TipSoundPlayer;

public class Test_AD extends TestContent {
    public Test_AD() {
        testName = TEST_NAME_AD;
        shortTestName = SHORT_TEST_NAME_AD;
        voiceFileNameBegin = TipSoundPlayer.VOICE_FILE_NAME_BEGIN_AD;
        voiceFileNameCompleted = TipSoundPlayer.VOICE_FILE_NAME_COMPLETED_AD;
        voiceFileNameNotCompleted = TipSoundPlayer.VOICE_FILE_NAME_NOT_COMPLETED_AD;
        analyseMax = SysParamsAll.get_adAnalyseMax();
        analyseMin = SysParamsAll.get_adAnalyseMin();
        standardKeepTime = SysParamsAll.get_adStandardTime();
        standardLeak = SysParamsAll.get_adStandardLeak();
        drawColor = Color.GREEN;
        //设置为客车时，安定试验全部显示，货车只显示主管压力和减压量
        if (SysParamsAll.PARAM_KEHUOCHE_KECHE.equals(SysParamsAll.getKeHuoChe())) {
            viewStatMainPressure = View.VISIBLE;
            viewStatKeepTime = View.VISIBLE;
            viewStatDropValue = View.VISIBLE;
            viewStatLeakValue = View.VISIBLE;
        } else {
            viewStatMainPressure = View.VISIBLE;
            viewStatKeepTime = View.GONE;
            viewStatDropValue = View.VISIBLE;
            viewStatLeakValue = View.GONE;
        }
    }

    @Override
    public void updateStandardDrop() {
        super.updateStandardDrop();
        standardDrop = SysParamsAll.get_adStandardDrop(definedPressureValue);
    }
}
