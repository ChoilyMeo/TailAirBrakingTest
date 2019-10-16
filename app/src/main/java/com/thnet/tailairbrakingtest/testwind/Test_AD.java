package com.thnet.tailairbrakingtest.testwind;

import android.graphics.Color;

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
    }

    @Override
    public void updateStandardDrop() {
        super.updateStandardDrop();
        standardDrop = SysParamsAll.get_adStandardDrop(definedPressureValue);
    }
}
