package com.thnet.tailairbrakingtest.testwind;

import android.graphics.Color;

import com.thnet.tailairbrakingtest.utility.TipSoundPlayer;

public class Test_GD extends TestContent {
    public Test_GD() {
        testName = TEST_NAME_GD;
        shortTestName = SHORT_TEST_NAME_GD;
        voiceFileNameBegin = TipSoundPlayer.VOICE_FILE_NAME_BEGIN_GD;
        voiceFileNameCompleted = TipSoundPlayer.VOICE_FILE_NAME_COMPLETED_GD;
        voiceFileNameNotCompleted = TipSoundPlayer.VOICE_FILE_NAME_NOT_COMPLETED_GD;
        analyseMax = SysParamsAll.get_gdAnalyseMax();
        analyseMin = SysParamsAll.get_gdAnalyseMin();
        standardKeepTime = SysParamsAll.get_gdStandardTime();
        standardLeak = SysParamsAll.get_gdStandardLeak();
        drawColor = Color.BLUE;
    }

    @Override
    public void updateStandardDrop() {
        super.updateStandardDrop();
        standardDrop = SysParamsAll.get_gdStandardDrop(wagonCount);
    }
}
