package com.thnet.tailairbrakingtest.TestWind;

import com.thnet.tailairbrakingtest.Communication.CTestWindProtocel;
import com.thnet.tailairbrakingtest.Utility.TipSoundPlayer;

public class Test_KSJY extends Test_YLJYBase {
    public Test_KSJY(){
        _testName = CStrTestName_KSJY;
        _shortTestName = CStrTestName_KSJY;
        _voiceFileNameBegin = TipSoundPlayer.nVoiceFileNameBegin_yljy;
        _voiceFileNameCompleted = TipSoundPlayer.nVoiceFileNameCompleted_yljy;
        _voiceFileNameNotCompleted = TipSoundPlayer.nVoiceFileNameNotCompleted_yljy;
        _TestCommandCode = CTestWindProtocel.TestCommand.PressureCheck;
        _analyseMax = SysParamsAll.get_jlAnalyseMax();
        _analyseMin = SysParamsAll.get_jlAnalyseMin();
        _standardKeepTime = 0;
        _standardDrop = SysParamsAll.get_jlStandardDrop();
        _standardLeak = 10000;
    }

    public Test_KSJY(int specifiedPressure, int trainCount, TestState state){
        this();
        SetParms(specifiedPressure, trainCount);
        this._stat = state;
    }
}
