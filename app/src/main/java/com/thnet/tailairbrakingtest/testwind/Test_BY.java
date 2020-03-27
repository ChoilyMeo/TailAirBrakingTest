package com.thnet.tailairbrakingtest.testwind;

import android.graphics.Color;
import android.view.View;

import com.thnet.tailairbrakingtest.utility.TipSoundPlayer;

public class Test_BY extends TestContent {
    public Test_BY() {
        testName = TEST_NAME_BY;
        shortTestName = SHORT_TEST_NAME_BY;
        voiceFileNameBegin = TipSoundPlayer.VOICE_FILE_NAME_BEGIN_BY;
        voiceFileNameCompleted = TipSoundPlayer.VOICE_FILE_NAME_COMPLETED_BY;
        voiceFileNameNotCompleted = TipSoundPlayer.VOICE_FILE_NAME_NOT_COMPLETED_BY;
        analyseMax = SysParamsAll.get_byAnalyseMax();
        analyseMin = SysParamsAll.get_byAnalyseMin();
        standardKeepTime = SysParamsAll.get_byStandardTime();
        standardLeak = 10000;
        drawColor = Color.LTGRAY;
        viewStatMainPressure = View.VISIBLE;
        viewStatKeepTime = View.VISIBLE;
        viewStatDropValue = View.VISIBLE;
        viewStatLeakValue = View.GONE;
    }

    @Override
    public void updateStandardDrop() {
        super.updateStandardDrop();
        standardDrop = SysParamsAll.get_byStandardDrop();
    }
}
