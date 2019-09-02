package com.thnet.tailairbrakingtest.TestWind;

import com.thnet.tailairbrakingtest.Communication.CTestWindProtocel;
import com.thnet.tailairbrakingtest.DAO.PressureValue;
import com.thnet.tailairbrakingtest.Utility.TipSoundPlayer;

public class Test_KLW extends Test_KLWBase {
    protected String _inputNumStat;
    protected String _inputNumTime;
    public Test_KLW(){
        _testName = CStrTestName_KLW;
        _shortTestName = CStrTestName_KLW;
        _voiceFileNameBegin = TipSoundPlayer.nVoiceFileNameBegin_klw;
        _voiceFileNameCompleted = TipSoundPlayer.nVoiceFileNameCompleted_klw;
        _voiceFileNameNotCompleted = TipSoundPlayer.nVoiceFileNameNotCompleted_klw;
        _TestCommandCode = CTestWindProtocel.TestCommand.TestKLW;
        _analyseMax = 0;
        _analyseMin = 0;
        _standardKeepTime = SysParamsAll.get_lxStandardTime();
        _standardDrop = 0;
        _standardLeak = SysParamsAll.get_lxStandardLeak();
    }

    public Test_KLW(int specifiedPressure, int trainCount, TestState state){
        this();
        SetParms(specifiedPressure, trainCount);
        this._stat = state;
    }

    @Override
    protected int getKLWTestStatus(PressureValue pressureValue) {
        return pressureValue.getTailPressureState1();
    }

    @Override
    protected int getKLWTestPressureValue(PressureValue pressureValue) {
        return pressureValue.getTailPressureValue1();
    }

    @Override
    public @TestViewTypes int get_testViewType() {
        return V_PASSENGER;
    }

    @Override
    public String get_passengerTrainPosition() { return "首部"; }
}
