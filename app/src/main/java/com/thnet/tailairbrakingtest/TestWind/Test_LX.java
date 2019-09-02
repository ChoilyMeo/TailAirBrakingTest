package com.thnet.tailairbrakingtest.TestWind;

import com.thnet.tailairbrakingtest.Communication.CTestWindProtocel;
import com.thnet.tailairbrakingtest.DAO.PressureValue;
import com.thnet.tailairbrakingtest.Utility.TipSoundPlayer;

public class Test_LX extends TestContent {
    public Test_LX() {
        _testName = CStrTestName_LX;
        _shortTestName = CStrShortTestName_LX;
        _voiceFileNameBegin = TipSoundPlayer.nVoiceFileNameBegin_lx;
        _voiceFileNameCompleted = TipSoundPlayer.nVoiceFileNameCompleted_lx;
        _voiceFileNameNotCompleted = TipSoundPlayer.nVoiceFileNameNotCompleted_lx;
        _TestCommandCode = CTestWindProtocel.TestCommand.TestLX;
        _analyseMax = 0;
        _analyseMin = 0;
        _standardKeepTime = SysParamsAll.get_lxStandardTime();
        _standardDrop = 0;
        _standardLeak = SysParamsAll.get_lxStandardLeak();
        _isValid_ZGYL = true;
        _isValid_LXL = true;
        _listViewContent.add(new TestViewContent(VIEW_ROW_NAME_PRESSURE, String.valueOf(get_ZGYL()), "KPa"));
        _listViewContent.add(new TestViewContent(VIEW_ROW_NAME_TIME, String.valueOf(get_BYSJ()), "秒"));
        _listViewContent.add(new TestViewContent(VIEW_ROW_NAME_LEAKVALUE, String.valueOf(get_LXL()), "KPa"));
    }

    public Test_LX(int specifiedPressure, int trainCount, TestState state){
        this();
        SetParms(specifiedPressure, trainCount);
        this._stat = state;
    }

    @Override
    public void DoTestKeepFirst(String stime, PressureValue pd) {
        super.DoTestKeepFirst(stime, pd);
        TestPressureValueMax = currPressureValue;
        TestPressureValueMin = 0;
    }

    @Override
    public void DoTestKeep(String stime, PressureValue pd) {
        if (TestPressureValueMin == 0 || currPressureValue < TestPressureValueMin) {
            TestPressureValueMin = currPressureValue > TestPressureValueMax ? TestPressureValueMax : currPressureValue;
        }
        set_endTime(stime);
        if (_BYSJ <= _standardKeepTime) {
            //_LXL = TestPressureValueMax - TestPressureValueMin;2018-10-02注释，添加下面的过程
            if (TestPressureValueMax - TestPressureValueMin > 10 && TestPressureValueMax - TestPressureValueMin <= 13) {
                _LXL = 10;
            } else {
                _LXL = TestPressureValueMax - TestPressureValueMin;
            }
        }
    }

    @Override
    public void DoTestEnd(String stime, PressureValue pd) {
        if (TestPressureValueMin == 0 || currPressureValue < TestPressureValueMin) {
            TestPressureValueMin = currPressureValue > TestPressureValueMax ? TestPressureValueMax : currPressureValue;
        }
        set_endTime(stime);
        if (_BYSJ <= _standardKeepTime + 5) {
            _LXL = _ZGYL - pd.getTestResult() / 10;
            if (_LXL < 0) {
                _LXL = 0;
            }
        }
        _stat = TestState.tsStoped;
    }
}
