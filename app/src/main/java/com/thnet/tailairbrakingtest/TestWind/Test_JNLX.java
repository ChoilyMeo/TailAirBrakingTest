package com.thnet.tailairbrakingtest.TestWind;

import com.thnet.tailairbrakingtest.Communication.CTestWindProtocel;
import com.thnet.tailairbrakingtest.DAO.PressureValue;
import com.thnet.tailairbrakingtest.Utility.TipSoundPlayer;

public class Test_JNLX extends TestContent {
    public Test_JNLX() {
        _testName = CStrTestName_JNLX;
        _shortTestName = CStrShortTestName_LX;
        _voiceFileNameBegin = TipSoundPlayer.nVoiceFileNameBegin_lx;
        _voiceFileNameCompleted = TipSoundPlayer.nVoiceFileNameCompleted_lx;
        _voiceFileNameNotCompleted = TipSoundPlayer.nVoiceFileNameNotCompleted_lx;
        _TestCommandCode = CTestWindProtocel.TestCommand.TestJNLX;
        _analyseMax = 0;
        _analyseMin = 0;
        _standardKeepTime = SysParamsAll.get_lxStandardTime();
        _standardDrop = 0;
        _standardLeak = SysParamsAll.get_jnlxStandardLeak();
        _isValid_LXL = true;
        _listViewContent.add(new TestViewContent(VIEW_ROW_NAME_TIME, String.valueOf(get_BYSJ()), "秒"));
        _listViewContent.add(new TestViewContent(VIEW_ROW_NAME_LEAKVALUE, String.valueOf(get_LXL()), "KPa"));
    }

    @Override
    public int GetCurrPressureValue(PressureValue pd) {
        return pd.getHeadPressureValue();
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
            _LXL = TestPressureValueMax - TestPressureValueMin;
        }
    }

    @Override
    public void DoTestEnd(String stime, PressureValue pd) {
        if (TestPressureValueMin == 0 || currPressureValue < TestPressureValueMin) {
            TestPressureValueMin = currPressureValue > TestPressureValueMax ? TestPressureValueMax : currPressureValue;
        }
        set_endTime(stime);
        if (_BYSJ <= _standardKeepTime) {
            _LXL = TestPressureValueMax - TestPressureValueMin;
        }
        _stat = TestState.tsStoped;
    }
}
