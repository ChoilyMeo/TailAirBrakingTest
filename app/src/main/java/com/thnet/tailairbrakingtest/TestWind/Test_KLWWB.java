package com.thnet.tailairbrakingtest.TestWind;

import com.thnet.tailairbrakingtest.Communication.CTestWindProtocel;
import com.thnet.tailairbrakingtest.DAO.PressureValue;
import com.thnet.tailairbrakingtest.Utility.TipSoundPlayer;

public class Test_KLWWB extends Test_KLWBase {
    public Test_KLWWB(){
        _testName = CStrTestName_KLWWB;
        _shortTestName = CStrTestName_KLWWB;
        _voiceFileNameBegin = TipSoundPlayer.nVoiceFileNameBegin_klw;
        _voiceFileNameCompleted = TipSoundPlayer.nVoiceFileNameCompleted_klw;
        _voiceFileNameNotCompleted = TipSoundPlayer.nVoiceFileNameNotCompleted_klw;
        _TestCommandCode = CTestWindProtocel.TestCommand.TestKLWWB;
        _analyseMax = 0;
        _analyseMin = 0;
        _standardKeepTime = SysParamsAll.get_lxStandardTime();
        _standardDrop = 0;
        _standardLeak = SysParamsAll.get_lxStandardLeak();
    }

    public Test_KLWWB(int specifiedPressure, int trainCount, TestState state){
        this();
        SetParms(specifiedPressure, trainCount);
        this._stat = state;
    }

    @Override
    protected int getKLWTestStatus(PressureValue pressureValue) {
        return pressureValue.getTailPressureState2();
    }

    @Override
    protected int getKLWTestPressureValue(PressureValue pressureValue) {
        return pressureValue.getTailPressureValue2();
    }

    @Override
    public @TestViewTypes int get_testViewType() {
        return V_PASSENGER;
    }

    @Override
    public String get_passengerTrainPosition() { return "尾部"; }
}
