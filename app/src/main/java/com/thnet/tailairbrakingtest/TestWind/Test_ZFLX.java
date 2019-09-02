package com.thnet.tailairbrakingtest.TestWind;

import com.thnet.tailairbrakingtest.Communication.CTestWindProtocel;
import com.thnet.tailairbrakingtest.DAO.PressureValue;
import com.thnet.tailairbrakingtest.Utility.TipSoundPlayer;

public class Test_ZFLX extends TestContent {
    public Test_ZFLX() {
        _testName = CStrTestName_ZFLX;
        _shortTestName = CStrTestName_ZFLX;
        _voiceFileNameBegin = TipSoundPlayer.nVoiceFileNameBegin_lx;
        _voiceFileNameCompleted = TipSoundPlayer.nVoiceFileNameCompleted_lx;
        _voiceFileNameNotCompleted = TipSoundPlayer.nVoiceFileNameNotCompleted_lx;
        _TestCommandCode = CTestWindProtocel.TestCommand.TestZFLX;
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

    @Override
    public boolean Valid_ZGYL() {
        return (_ZGYL >= 550 && _ZGYL <= 620);
    }

    @Override
    public int GetCurrPressureValue(PressureValue pd) {
        return pd.getHeadPressureValue();
    }

    @Override
    public void DoTestFillWindEnd(String stime, PressureValue pd) {
        if (_stat == TestState.tsNotBegin) {
            _stat = TestState.tsDoing;
            TipSoundPlayer.PlayVoicePrompts(_voiceFileNameBegin);
        }
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
