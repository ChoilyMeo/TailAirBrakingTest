package com.thnet.tailairbrakingtest.TestWind;

import com.elvishew.xlog.XLog;
import com.thnet.tailairbrakingtest.Communication.CTestWindProtocel;
import com.thnet.tailairbrakingtest.DAO.PressureValue;
import com.thnet.tailairbrakingtest.Utility.TipSoundPlayer;

import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class Test_KLWBase extends TestContent {
    protected static final int TEST_STAT_INPUT_NUM = 0xB6;//输号状态码
    protected static final int TEST_STAT_UNDERPRESSURE = 0x30;//欠压状态码
    protected static final int TEST_STAT_CONSTANT_PRESSURE = 0x92;//定压状态码
    protected static final int TEST_STAT_AIR_EXHAUST = 0x25;//排风状态码
    protected static final int TEST_STAT_CANCEL_NUM = 0x84;//销号状态码
    protected String _passengerTrainID;
    protected String _inputNumStat;//输号状态
    protected String _inputNumPressureValue;
    protected String _inputNumTime;
    protected String _underpressureStat;//欠压状态
    protected String _underpressureTime;
    protected String _constantPressureValue;//定压压力
    protected String _constantPressureTime;
    protected String _airExhaustStat;//排风状态
    protected String _airExhaustTime;
    protected String _brakeStat;//制动状态
    protected String _brakeTime;
    protected String _cancelNumStat;//销号状态
    protected String _cancelNumTime;
    protected boolean bStatus1Ready = false;
    public Test_KLWBase() {
        _testName = "";
        _shortTestName = "";
        _voiceFileNameBegin = TipSoundPlayer.nVoiceFileNameBegin_klw;
        _voiceFileNameCompleted = TipSoundPlayer.nVoiceFileNameCompleted_klw;
        _voiceFileNameNotCompleted = TipSoundPlayer.nVoiceFileNameNotCompleted_klw;
        _TestCommandCode = CTestWindProtocel.TestCommand.TestKLW;
        _analyseMax = 0;
        _analyseMin = 0;
        _standardKeepTime = SysParamsAll.get_lxStandardTime();
        _standardDrop = 0;
        _standardLeak = SysParamsAll.get_lxStandardLeak();
        _wcDingYa = SysParamsAll.get_wcKLWPressure();
        _passengerTrainID="";
        _inputNumStat = "";
        _inputNumPressureValue = "";
        _inputNumTime = "";
        _underpressureStat = "";
        _underpressureTime = "";
        _constantPressureValue = "";
        _constantPressureTime = "";
        _airExhaustStat = "";
        _airExhaustTime = "";
        _brakeStat = "";
        _brakeTime = "";
        _cancelNumStat = "";
        _cancelNumTime = "";
        bStatus1Ready = false;
        _listViewContent.add(new TestViewContent(VIEW_ROW_NAME_INPUTNUM, "", ""));
        _listViewContent.add(new TestViewContent(VIEW_ROW_NAME_600KPA, "", ""));
        _listViewContent.add(new TestViewContent(VIEW_ROW_NAME_LOWPRESSURETIP, "", ""));
        _listViewContent.add(new TestViewContent(VIEW_ROW_NAME_500KPA, "", ""));
        _listViewContent.add(new TestViewContent(VIEW_ROW_NAME_AIREXHUAST, "", ""));
        _listViewContent.add(new TestViewContent(VIEW_ROW_NAME_EMERGENCYBRAKING, "", ""));
        _listViewContent.add(new TestViewContent(VIEW_ROW_NAME_CANCELNUM, "", ""));
    }

    protected abstract int getKLWTestStatus(PressureValue pressureValue);

    protected abstract int getKLWTestPressureValue(PressureValue pressureValue);

    @Override
    public void Reset() {
        super.Reset();
        _inputNumStat = "";
        _inputNumPressureValue = "";
        _inputNumTime = "";
        _underpressureStat = "";
        _underpressureTime = "";
        _constantPressureValue = "";
        _constantPressureTime = "";
        _airExhaustStat = "";
        _airExhaustTime = "";
        _brakeStat = "";
        _brakeTime = "";
        _cancelNumStat = "";
        _cancelNumTime = "";
        bStatus1Ready = false;
    }

    @Override
    public boolean IsTestSuccess() {
        int inputNumPressureValue = 0, constantPressureValue = 0;
        try{
            inputNumPressureValue = Integer.valueOf(_inputNumPressureValue);
        } catch (Exception ex) {
            inputNumPressureValue = 0;
        }
        try{
            constantPressureValue = Integer.valueOf(_constantPressureValue);
        } catch (Exception ex) {
            constantPressureValue = 0;
        }
        if (_inputNumStat.equals("成功") &&
                _underpressureStat.equals("正常") &&
                _airExhaustStat.equals("正常") &&
                _brakeStat.equals("正常") &&
                inputNumPressureValue >= 600 - _wcDingYa && inputNumPressureValue <= 600 + _wcDingYa &&
                constantPressureValue >= 500 - _wcDingYa && constantPressureValue <= 500 + _wcDingYa){
            return true;
        } else {
            return false;
        }
    }

    protected void doKLWStatusCheck(String stime, PressureValue pd){
        if (_startTime.isEmpty()){
            _startTime = stime;
        }
        set_endTime(stime);
        //输号
        if (_inputNumTime.isEmpty() && TEST_STAT_INPUT_NUM == getKLWTestStatus(pd)){
            _inputNumTime = stime;
            _inputNumStat = "成功";
            _inputNumPressureValue = String.valueOf(getKLWTestPressureValue(pd));
        }
        if (_underpressureTime.isEmpty() && TEST_STAT_UNDERPRESSURE == getKLWTestStatus(pd)) {
            _underpressureTime = stime;
            _underpressureStat = "正常";
        }
        if (_constantPressureTime.isEmpty() && TEST_STAT_CONSTANT_PRESSURE == getKLWTestStatus(pd)) {//查询500压力 查到500压力说明欠压提示正常
            if (_underpressureTime.isEmpty()) {
                _underpressureTime = new SimpleDateFormat("HH:mm:ss").format(new Date(System.currentTimeMillis() - 4 * 1000));
                _underpressureStat = "正常";
            }
            bStatus1Ready = true;
            _constantPressureTime = stime;
            _constantPressureValue = String.valueOf(getKLWTestPressureValue(pd));
        }
        if (!_constantPressureTime.isEmpty() && TEST_STAT_CONSTANT_PRESSURE == getKLWTestStatus(pd) && getKLWTestPressureValue(pd) < 50) {//说明紧急制动过了 把排风和制动都赋值
            if (_airExhaustTime.isEmpty()) {
                _airExhaustTime = new SimpleDateFormat("HH:mm:ss").format(new Date(System.currentTimeMillis() - 35 * 1000));
                _airExhaustStat = "正常";
            }
            if (_brakeTime.isEmpty()) {
                _brakeTime = new SimpleDateFormat("HH:mm:ss").format(new Date(System.currentTimeMillis() - 5 * 1000));
                _brakeStat = "正常";
            }
            DoTestEndValidResult();
        }
        if (_airExhaustTime.isEmpty() && TEST_STAT_AIR_EXHAUST == getKLWTestStatus(pd)){
            _airExhaustTime = stime;
            _airExhaustStat = "正常";
        }
        if (bStatus1Ready && _brakeTime.isEmpty() && TEST_STAT_UNDERPRESSURE == getKLWTestStatus(pd)){
            if (_airExhaustTime.isEmpty()){
                _airExhaustTime = new SimpleDateFormat("HH:mm:ss").format(new Date(System.currentTimeMillis() - 30 * 1000));
                _airExhaustStat = "正常";
            }
            _brakeTime = stime;
            _brakeStat = "正常";
            DoTestEndValidResult();
        }
        if (_cancelNumTime.isEmpty() && TEST_STAT_CANCEL_NUM == getKLWTestStatus(pd)){
            _cancelNumTime = stime;
            _cancelNumStat = "成功";
        }
    }

    @Override
    public TestContent CheckStatus(String stime, PressureValue pd, CTestWindProtocel.TestStatus testStatus, int DropSpeed) {
        if (testStatus == CTestWindProtocel.TestStatus.TestBegin) {
            XLog.i(get_testName() + "：试验开始。");
            DoTestBegin();
        } else if (testStatus == CTestWindProtocel.TestStatus.TestFillWindEnd ||
                testStatus == CTestWindProtocel.TestStatus.TestDropPressureBegin ||
                testStatus == CTestWindProtocel.TestStatus.TestKeep ||
                testStatus == CTestWindProtocel.TestStatus.TestEnd) {
            XLog.i(get_testName() + "：处理试验状态" + testStatus);
            if (_stat == TestState.tsNotBegin) {
                _stat = TestState.tsDoing;
                TipSoundPlayer.PlayVoicePrompts(_voiceFileNameBegin);
            }
            if (TestState.tsDoing == _stat){
                doKLWStatusCheck(stime, pd);
            }
        } else {
            XLog.i(get_testName() + "：试验状态未处理：" + testStatus);
        }
        updateViewContentInfo();
        return this;
    }

    @Override
    public void updateViewContentInfo() {
        for(TestViewContent testViewContent : _listViewContent){
            if (testViewContent.getColumn1().equals(VIEW_ROW_NAME_INPUTNUM)){
                testViewContent.setColumn2(get_inputNumStat());
                testViewContent.setColumn3(get_inputNumTime());
            } else if (testViewContent.getColumn1().equals(VIEW_ROW_NAME_600KPA)){
                testViewContent.setColumn2(get_inputNumPressureValue());
            } else if (testViewContent.getColumn1().equals(VIEW_ROW_NAME_LOWPRESSURETIP)){
                testViewContent.setColumn2(get_underpressureStat());
                testViewContent.setColumn3(get_underpressureTime());
            } else if (testViewContent.getColumn1().equals(VIEW_ROW_NAME_500KPA)){
                testViewContent.setColumn2(get_constantPressureValue());
                testViewContent.setColumn3(get_constantPressureValue());
            } else if (testViewContent.getColumn1().equals(VIEW_ROW_NAME_AIREXHUAST)){
                testViewContent.setColumn2(get_airExhaustStat());
                testViewContent.setColumn3(get_airExhaustTime());
            } else if (testViewContent.getColumn1().equals(VIEW_ROW_NAME_EMERGENCYBRAKING)){
                testViewContent.setColumn2(get_brakeStat());
                testViewContent.setColumn3(get_brakeTime());
            } else if (testViewContent.getColumn1().equals(VIEW_ROW_NAME_CANCELNUM)){
                testViewContent.setColumn2(get_cancelNumStat());
                testViewContent.setColumn3(get_cancelNumTime());
            }
        }
    }

    @Override
    public String get_passengerTrainID(){ return _passengerTrainID; }

    @Override
    public void set_passengerTrainID(String passengerTrainID){ _passengerTrainID = passengerTrainID; }

    public String get_inputNumStat() {
        return _inputNumStat;
    }

    public void set_inputNumStat(String _inputNumStat) {
        this._inputNumStat = _inputNumStat;
    }

    public String get_inputNumPressureValue() {
        return _inputNumPressureValue;
    }

    public void set_inputNumPressureValue(String _inputNumPressureValue) {
        this._inputNumPressureValue = _inputNumPressureValue;
    }

    public String get_inputNumTime() {
        return _inputNumTime;
    }

    public void set_inputNumTime(String _inputNumTime) {
        this._inputNumTime = _inputNumTime;
    }

    public String get_underpressureStat() {
        return _underpressureStat;
    }

    public void set_underpressureStat(String _underpressureStat) {
        this._underpressureStat = _underpressureStat;
    }

    public String get_underpressureTime() {
        return _underpressureTime;
    }

    public void set_underpressureTime(String _underpressureTime) {
        this._underpressureTime = _underpressureTime;
    }

    public String get_constantPressureValue() {
        return _constantPressureValue;
    }

    public void set_constantPressureValue(String _constantPressureValue) {
        this._constantPressureValue = _constantPressureValue;
    }

    public String get_constantPressureTime() {
        return _constantPressureTime;
    }

    public void set_constantPressureTime(String _constantPressureTime) {
        this._constantPressureTime = _constantPressureTime;
    }

    public String get_airExhaustStat() {
        return _airExhaustStat;
    }

    public void set_airExhaustStat(String _airExhaustStat) {
        this._airExhaustStat = _airExhaustStat;
    }

    public String get_airExhaustTime() {
        return _airExhaustTime;
    }

    public void set_airExhaustTime(String _airExhaustTime) {
        this._airExhaustTime = _airExhaustTime;
    }

    public String get_brakeStat() {
        return _brakeStat;
    }

    public void set_brakeStat(String _brakeStat) {
        this._brakeStat = _brakeStat;
    }

    public String get_brakeTime() {
        return _brakeTime;
    }

    public void set_brakeTime(String _brakeTime) {
        this._brakeTime = _brakeTime;
    }

    public String get_cancelNumStat() {
        return _cancelNumStat;
    }

    public void set_cancelNumStat(String _cancelNumStat) {
        this._cancelNumStat = _cancelNumStat;
    }

    public String get_cancelNumTime() {
        return _cancelNumTime;
    }

    public void set_cancelNumTime(String _cancelNumTime) {
        this._cancelNumTime = _cancelNumTime;
    }
}
