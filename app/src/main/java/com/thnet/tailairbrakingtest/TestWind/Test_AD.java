package com.thnet.tailairbrakingtest.TestWind;

import com.elvishew.xlog.XLog;
import com.thnet.tailairbrakingtest.Communication.CTestWindProtocel;
import com.thnet.tailairbrakingtest.DAO.PressureValue;
import com.thnet.tailairbrakingtest.Utility.TipSoundPlayer;

public class Test_AD extends TestContent {
    public Test_AD() {
        _testName = CStrTestName_AD;
        _shortTestName = CStrShortTestName_AD;
        _voiceFileNameBegin = TipSoundPlayer.nVoiceFileNameBegin_ad;
        _voiceFileNameCompleted = TipSoundPlayer.nVoiceFileNameCompleted_ad;
        _voiceFileNameNotCompleted = TipSoundPlayer.nVoiceFileNameNotCompleted_ad;
        _TestCommandCode = CTestWindProtocel.TestCommand.TestAD;
        _analyseMax = SysParamsAll.get_adAnalyseMax();
        _analyseMin = SysParamsAll.get_adAnalyseMin();
        _standardKeepTime = SysParamsAll.get_adStandardTime();
        _standardLeak = SysParamsAll.get_adStandardLeak();
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
        _listViewContent.add(new TestViewContent(VIEW_ROW_NAME_LEAKVALUE, String.valueOf(get_LXL()), "KPa"));
    }

    @Override
    public void UpdateStandardDrop() {
        super.UpdateStandardDrop();
        _standardDrop = SysParamsAll.get_adStandardDrop(_definedPressureValue);
    }

    @Override
    public boolean Valid_LXL() {
        if(SysParamsAll.get_kehuoche() == 0){
            return (_LXL <= _standardLeak + _wcLouXie && _LXL2 <= _standardLeak + _wcLouXie && _LXL3 <= _standardLeak + _wcLouXie && _LXL4 <= _standardLeak + _wcLouXie
                    && _LXL5 <= _standardLeak + _wcLouXie && _LXL+_LXL2+_LXL3+_LXL4+_LXL5 <= _standardLeak + _wcLouXie);
        } else {
            return true;
        }
    }

    @Override
    public void DoTestKeep(String stime, PressureValue pd) {
        int currPressureValue = GetCurrPressureValue(pd);
        int DropSpeed = pd.getTestResult();
        set_endTime(stime);
        //漏泄量只取指定保压时间的漏泄量
        if (SysParamsAll.get_kehuoche() == 0) {
            if (_BYSJ <= _standardKeepTime + 5) {
                int lxl = 0;
                if (pd.getPressureKeepMinutes() == 1) {
                    lxl = _ZGYL - _JYL - DropSpeed / 10;
                    if (lxl < 0) {
                        lxl = 0;
                    }
                    _LXL = lxl;
                }
                if (pd.getPressureKeepMinutes() == 2) {
                    lxl = _ZGYL - _JYL - _LXL - DropSpeed / 10;
                    if (lxl < 0) {
                        lxl = 0;
                    }
                    _LXL2 = lxl;
                }
                if (pd.getPressureKeepMinutes() == 3) {
                    lxl = _ZGYL - _JYL - _LXL - _LXL2 - DropSpeed / 10;
                    if (lxl < 0) {
                        lxl = 0;
                    }
                    _LXL3 = lxl;
                }
                if (pd.getPressureKeepMinutes() == 4) {
                    lxl = _ZGYL - _JYL - _LXL - _LXL2 - _LXL3 - DropSpeed / 10;
                    if (lxl < 0) {
                        lxl = 0;
                    }
                    _LXL4 = lxl;
                }
                if (pd.getPressureKeepMinutes() == 5) {
                    lxl = _ZGYL - _JYL - _LXL - _LXL2 - _LXL3 - _LXL4 - DropSpeed / 10;
                    if (lxl < 0) {
                        lxl = 0;
                    }
                    _LXL5 = lxl;
                }
            }
        } else {
            XLog.i("货车安定未处理漏泄量。");
        }
    }

    @Override
    public void DoTestEnd(String stime, PressureValue pd) {
        int DropSpeed = pd.getTestResult();
        if (_stat == TestState.tsDoing) {
            set_endTime(stime);
            _stat = TestState.tsStoped;
            //漏泄量只取指定保压时间的漏泄量
            if (SysParamsAll.get_kehuoche() == 0) {//客车
                if (_BYSJ <= _standardKeepTime + 5) {
                    int lxl = 0;
                    if (pd.getPressureKeepMinutes() == 1) {
                        lxl = _ZGYL - _JYL - DropSpeed / 10;
                        if (lxl < 0) {
                            lxl = 0;
                        }
                        _LXL = lxl;
                    }
                    if (pd.getPressureKeepMinutes() == 2) {
                        lxl = _ZGYL - _JYL - _LXL - DropSpeed / 10;
                        if (lxl < 0) {
                            lxl = 0;
                        }
                        _LXL2 = lxl;
                    }
                    if (pd.getPressureKeepMinutes() == 3) {
                        lxl = _ZGYL - _JYL - _LXL - _LXL2 - DropSpeed / 10;
                        if (lxl < 0) {
                            lxl = 0;
                        }
                        _LXL3 = lxl;
                    }
                    if (pd.getPressureKeepMinutes() == 4) {
                        lxl = _ZGYL - _JYL - _LXL - _LXL2 - _LXL3 - DropSpeed / 10;
                        if (lxl < 0) {
                            lxl = 0;
                        }
                        _LXL4 = lxl;
                    }
                    if (pd.getPressureKeepMinutes() == 5) {
                        lxl = _ZGYL - _JYL - _LXL - _LXL2 - _LXL3 - _LXL4 - DropSpeed / 10;
                        if (lxl < 0) {
                            lxl = 0;
                        }
                        _LXL5 = lxl;
                    }
                }
            } else {
                XLog.i("货车安定未处理漏泄量。");
            }
        }
    }
}
