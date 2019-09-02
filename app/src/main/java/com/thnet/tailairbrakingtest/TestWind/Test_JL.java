package com.thnet.tailairbrakingtest.TestWind;

import com.thnet.tailairbrakingtest.Communication.CTestWindProtocel;
import com.thnet.tailairbrakingtest.Utility.TipSoundPlayer;

public class Test_JL extends TestContent {
    public Test_JL(){
        _testName = CStrTestName_JL;
        _shortTestName = CStrShortTestName_JL;
        _voiceFileNameBegin = TipSoundPlayer.nVoiceFileNameBegin_jl;
        _voiceFileNameCompleted = TipSoundPlayer.nVoiceFileNameCompleted_jl;
        _voiceFileNameNotCompleted = TipSoundPlayer.nVoiceFileNameNotCompleted_jl;
        _TestCommandCode = CTestWindProtocel.TestCommand.TestJL;
        _analyseMax = SysParamsAll.get_jlAnalyseMax();
        _analyseMin = SysParamsAll.get_jlAnalyseMin();
        _standardKeepTime = 0;
        _standardDrop = SysParamsAll.get_jlStandardDrop();
        _standardLeak = 10000;
        _isValid_ZGYL = true;
        _isValid_BYSJ = true;
        _isValid_JYL = true;
        _isValid_LXL = true;
    }
}
