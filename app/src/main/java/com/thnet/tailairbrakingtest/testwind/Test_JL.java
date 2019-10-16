package com.thnet.tailairbrakingtest.testwind;

import android.graphics.Color;

import com.thnet.tailairbrakingtest.utility.TipSoundPlayer;

public class Test_JL extends TestContent {
    public Test_JL(){
        testName = TEST_NAME_JL;
        shortTestName = SHORT_TEST_NAME_JL;
        voiceFileNameBegin = TipSoundPlayer.VOICE_FILE_NAME_BEGIN_JL;
        voiceFileNameCompleted = TipSoundPlayer.VOICE_FILE_NAME_COMPLETED_JL;
        voiceFileNameNotCompleted = TipSoundPlayer.VOICE_FILE_NAME_NOT_COMPLETED_JL;
        analyseMax = SysParamsAll.get_jlAnalyseMax();
        analyseMin = SysParamsAll.get_jlAnalyseMin();
        standardKeepTime = 0;
        standardDrop = SysParamsAll.get_jlStandardDrop();
        standardLeak = 10000;
        drawColor = Color.YELLOW;
    }
}
