package com.thnet.tailairbrakingtest.testwind;

import android.graphics.Color;
import android.view.View;

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
        viewStatMainPressure = View.VISIBLE;
        viewStatKeepTime = View.VISIBLE;
        viewStatDropValue = View.VISIBLE;
        //设置是客车的话，不显示漏泄量
        if (SysParamsAll.PARAM_KEHUOCHE_KECHE.equals(SysParamsAll.getKeHuoChe())){
            viewStatLeakValue = View.GONE;
        } else {
            viewStatLeakValue = View.VISIBLE;
        }
    }

    @Override
    public void updateStandardDrop() {
        super.updateStandardDrop();
        standardDrop = SysParamsAll.get_gdStandardDrop(wagonCount);
    }
}
