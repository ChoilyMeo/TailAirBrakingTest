package com.thnet.tailairbrakingtest.TestWind;

import com.thnet.tailairbrakingtest.Communication.CTestWindProtocel;
import com.thnet.tailairbrakingtest.Utility.TipSoundPlayer;

public class Test_GD extends TestContent {
    public Test_GD() {
        _testName = CStrTestName_GD;
        _shortTestName = CStrShortTestName_GD;
        _voiceFileNameBegin = TipSoundPlayer.nVoiceFileNameBegin_gd;
        _voiceFileNameCompleted = TipSoundPlayer.nVoiceFileNameCompleted_gd;
        _voiceFileNameNotCompleted = TipSoundPlayer.nVoiceFileNameNotCompleted_gd;
        _TestCommandCode = CTestWindProtocel.TestCommand.TestGD;
        _analyseMax = SysParamsAll.get_gdAnalyseMax();
        _analyseMin = SysParamsAll.get_gdAnalyseMin();
        _standardKeepTime = SysParamsAll.get_gdStandardTime();
        _standardLeak = 10000;
        _isValid_ZGYL = true;
        _isValid_BYSJ = true;
        _isValid_JYL = true;
        _isValid_LXL = true;
        _listViewContent.add(new TestViewContent(VIEW_ROW_NAME_PRESSURE, String.valueOf(get_ZGYL()), "KPa"));
        _listViewContent.add(new TestViewContent(VIEW_ROW_NAME_DROPVALUE, String.valueOf(get_JYL()), "KPa"));
        if (0 == SysParamsAll.get_kehuoche()) {//客车
            _listViewContent.add(new TestViewContent(VIEW_ROW_NAME_SPEED, String.valueOf(get_JYSD()), "KPa/s"));
        }
        _listViewContent.add(new TestViewContent(VIEW_ROW_NAME_TIME, String.valueOf(get_BYSJ()), "秒"));
    }

    @Override
    public void UpdateStandardDrop() {
        super.UpdateStandardDrop();
        _standardDrop = SysParamsAll.get_gdStandardDrop(_wagonCount);
    }
}
