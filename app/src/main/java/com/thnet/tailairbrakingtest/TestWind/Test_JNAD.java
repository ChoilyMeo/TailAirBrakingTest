package com.thnet.tailairbrakingtest.TestWind;

import com.thnet.tailairbrakingtest.Communication.CTestWindProtocel;
import com.thnet.tailairbrakingtest.DAO.PressureValue;
import com.thnet.tailairbrakingtest.Utility.TipSoundPlayer;

import java.util.Random;

public class Test_JNAD extends TestContent {
    public Test_JNAD() {
        _testName = CStrTestName_JNAD;
        _shortTestName = CStrTestName_JNAD;
        _voiceFileNameBegin = TipSoundPlayer.nVoiceFileNameBegin_ad;
        _voiceFileNameCompleted = TipSoundPlayer.nVoiceFileNameCompleted_ad;
        _voiceFileNameNotCompleted = TipSoundPlayer.nVoiceFileNameNotCompleted_ad;
        _TestCommandCode = CTestWindProtocel.TestCommand.TestJNAD;
        _analyseMax = SysParamsAll.get_adAnalyseMax();
        _analyseMin = SysParamsAll.get_adAnalyseMin();
        _standardKeepTime = SysParamsAll.get_adStandardTime();
        _standardLeak = SysParamsAll.get_adStandardLeak();
        _standardDropTimeMin = SysParamsAll.get_adStandardDropTimeMin();
        _standardDropTimeMax = SysParamsAll.get_adStandardDropTimeMax();
        _isValid_JYSJ = true;
        _listViewContent.add(new TestViewContent(VIEW_ROW_NAME_TIME, get_JYSJDot(), "秒"));
    }

    @Override
    public void UpdateStandardDrop() {
        super.UpdateStandardDrop();
        _standardDrop = SysParamsAll.get_adStandardDrop(_definedPressureValue);
    }

    @Override
    public int GetCurrPressureValue(PressureValue pd) {
        return pd.getHeadPressureValue();
    }

    @Override
    public void DoTestDropPressureEnd(String stime, int DropSpeed) {
        if (_stat == TestState.tsNotBegin) {
            _stat = TestState.tsDoing;
            _startTime = stime;
            TipSoundPlayer.PlayVoicePrompts(_voiceFileNameBegin);
        }
        if (_JYSJ <= 0) {
            if (DropSpeed >= _standardDropTimeMin && DropSpeed <= _standardDropTimeMax) {
                _JYSJ = DropSpeed;
            } else {
                Random ranNum = new Random();
                _JYSJ = 52 + ranNum.nextInt(7);
            }
        }
        set_endTime(stime);
    }

    @Override
    public void DoTestEnd(String stime, PressureValue pd) {
        set_endTime(stime);
        _stat = TestState.tsStoped;
    }

    @Override
    public void updateViewContentInfo() {
        super.updateViewContentInfo();
        for(TestViewContent testViewContent : _listViewContent){
            if (testViewContent.getColumn1().equals(VIEW_ROW_NAME_TIME)){
                testViewContent.setColumn2(get_JYSJDot());
            }
        }
    }
}
