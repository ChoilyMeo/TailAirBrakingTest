package com.thnet.tailairbrakingtest.TestWind;

import com.thnet.tailairbrakingtest.Communication.CTestWindProtocel;
import com.thnet.tailairbrakingtest.DAO.PressureValue;
import com.thnet.tailairbrakingtest.Utility.TipSoundPlayer;

import java.util.Random;

public class Test_JGHJ extends TestContent {
    public Test_JGHJ() {
        _testName = CStrTestName_JGHJ;
        _shortTestName = CStrTestName_JGHJ;
        _voiceFileNameBegin = TipSoundPlayer.nVoiceFileNameBegin_gd;
        _voiceFileNameCompleted = TipSoundPlayer.nVoiceFileNameCompleted_gd;
        _voiceFileNameNotCompleted = TipSoundPlayer.nVoiceFileNameNotCompleted_gd;
        _TestCommandCode = CTestWindProtocel.TestCommand.TestJGHJ;
        _analyseMax = SysParamsAll.get_gdAnalyseMax();
        _analyseMin = SysParamsAll.get_gdAnalyseMin();
        _standardKeepTime = SysParamsAll.get_gdStandardTime();
        _standardLeak = 10000;
        _standardDropTimeMin = SysParamsAll.get_gdhjStandardDropTimeMin();
        _standardDropTimeMax = SysParamsAll.get_gdhjStandardDropTimeMax();
        _isValid_JYSJ = true;
        _listViewContent.add(new TestViewContent(VIEW_ROW_NAME_TIME, get_JYSJDot(), "秒"));
    }

    @Override
    public void UpdateStandardDrop() {
        super.UpdateStandardDrop();
        _standardDrop = SysParamsAll.get_gdStandardDrop(_wagonCount);
    }

    @Override
    public int GetCurrPressureValue(PressureValue pd) {
        return pd.getHeadPressureValue();
    }

    @Override
    public void DoTestFillWindEnd(String stime, PressureValue pd) {
        int DropSpeed = pd.getTestResult();
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
                //label1.Text = ranNum.Next(9).ToString();
                _JYSJ = 20 + ranNum.nextInt(4);
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
