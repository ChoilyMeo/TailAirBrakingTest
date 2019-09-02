package com.thnet.tailairbrakingtest.TestWind;

import android.graphics.Canvas;
import android.support.annotation.IntDef;

import com.elvishew.xlog.XLog;
import com.thnet.tailairbrakingtest.Communication.CTestWindProtocel;
import com.thnet.tailairbrakingtest.Communication.OnTestFailedListener;
import com.thnet.tailairbrakingtest.DAO.PressureValue;
import com.thnet.tailairbrakingtest.Utility.TipSoundPlayer;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TestContent {
    public static final String CStrTestName_AD = "安定试验";
    public static final String CStrTestName_BY = "持续保压";
    public static final String CStrTestName_GD = "感度试验";
    public static final String CStrTestName_JL = "简略试验";
    public static final String CStrTestName_LX = "漏泄试验";
    public static final String CStrTestName_ZFLX = "总风漏泄";
    public static final String CStrTestName_JGHJ = "感度缓解";
    public static final String CStrTestName_JNHJ = "机能缓解";
    public static final String CStrTestName_JNLX = "机能漏泄";
    public static final String CStrTestName_JNGD = "感度制动";
    public static final String CStrTestName_JNAD = "机能安定";
    public static final String CStrTestName_KLW = "客列首部";
    public static final String CStrTestName_KLWWB = "客列尾部";
    public static final String CStrTestName_BYKLW = "备用客列";
    public static final String CStrTestName_YLJY = "压力校验";
    public static final String CStrTestName_KSJY = "压力校验";
    public static final String CStrShortTestName_AD = "安定";
    public static final String CStrShortTestName_BY = "持保";
    public static final String CStrShortTestName_GD = "感度";
    public static final String CStrShortTestName_JL = "简略";
    public static final String CStrShortTestName_LX = "漏泄";
    public static final String CStrTestStateCompleted = "合格";
    public static final String CStrTestStateNotCompleted = "不合格";
    public static final String VIEW_ROW_NAME_PRESSURE = "压力";
    public static final String VIEW_ROW_NAME_DROPVALUE = "减压量";
    public static final String VIEW_ROW_NAME_TIME = "时间";
    public static final String VIEW_ROW_NAME_LEAKVALUE = "漏泄量";
    public static final String VIEW_ROW_NAME_SPEED = "速度";
    public static final String VIEW_ROW_NAME_INPUTNUM = "输号";
    public static final String VIEW_ROW_NAME_600KPA = "600KPa";
    public static final String VIEW_ROW_NAME_LOWPRESSURETIP = "欠压提示";
    public static final String VIEW_ROW_NAME_500KPA = "500KPa";
    public static final String VIEW_ROW_NAME_AIREXHUAST = "排风";
    public static final String VIEW_ROW_NAME_EMERGENCYBRAKING = "紧急制动";
    public static final String VIEW_ROW_NAME_CANCELNUM = "销号";
    public static final String VIEW_ROW_NAME_PRESSURE_ALL = "总";
    public static final String VIEW_ROW_NAME_PRESSURE_TRAIN = "列";
    public static final String VIEW_ROW_NAME_PRESSURE_CENTER = "中";
    public static final String VIEW_ROW_NAME_PRESSURE_STANDARD = "标";
    //试验显示的类别：0-正常试验、1-客列尾试验、2-压力校验
    public static final int V_NORMAL = 0;
    public static final int V_PASSENGER = 1;
    public static final int V_CALIBRATION = 2;
    @IntDef({
            V_NORMAL,
            V_PASSENGER,
            V_CALIBRATION
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface TestViewTypes {}
    //试验区别变量，不同的试验在构造函数中进行不同的赋值
    protected String _testName;
    protected String _shortTestName;
    protected int _voiceFileNameBegin;
    protected int _voiceFileNameCompleted;
    protected int _voiceFileNameNotCompleted;
    protected CTestWindProtocel.TestCommand _TestCommandCode = CTestWindProtocel.TestCommand.TestBegin;
    //试验数据变量，根据试验结果进行赋值，最终保存到数据库
    protected String _startTime = "";
    protected String _endTime = "";
    protected String _keepPressureTime = "";
    protected int _ZGYL = 0;
    protected int _JYL = 0;
    protected int _LXL = 0;
    protected int _LXL2 = -1;
    protected int _LXL3 = -1;
    protected int _LXL4 = -1;
    protected int _LXL5 = -1;
    protected int _BYSJ = 0;
    protected int _JYSD = 0;
    protected int _JYSJ = 0;
    protected int _HJSJ = 0;
    protected String _state = "";
    protected TestState _stat = TestState.tsNotBegin;
    //试验中间变量，根据试验结果赋值，中间进行临时计算和图形显示
    protected int TestPressureValueMax = 0;
    protected int TestPressureValueMin = 0;
    protected int _xBeginPos = -1;
    protected int _xEndPos = -1;
    protected int _yPressureValue = 0;
    //试验参数变量，根据不同的试验，构造时已经赋值
    protected int _analyseMin = 0;
    protected int _analyseMax = 0;
    protected int _standardKeepTime = 0;
    protected int _standardDrop = 0;
    protected int _standardLeak = 0;
    protected int _definedPressureValue = 0;
    protected int _standardDropSpeedMin = 0;
    protected int _standardDropSpeedMax = 0;
    protected int _standardDropTimeMin = 0;
    protected int _standardDropTimeMax = 0;
    protected int _wcDingYa = 0;
    protected int _wcBaoYa = 0;
    protected int _wcJianYa = 0;
    protected int _wcLouXie = 0;
    //试验是否成功的判定条件
    protected boolean _isValid_ZGYL = false;
    protected boolean _isValid_BYSJ = false;
    protected boolean _isValid_JYL = false;
    protected boolean _isValid_LXL = false;
    protected boolean _isValid_JYSJ = false;

    //其他变量
    public enum TestState {
        tsNotSelected, tsNotCheck, tsNotBegin, tsDoing, tsStoped
    }
    int _wagonCount = 0;
    int currPressureValue = 0;
    protected OnTestFailedListener onTestFailedListener;
    protected List<TestViewContent> _listViewContent;//界面展示内容

    public TestContent() {
        _testName = "";
        _stat = TestState.tsNotSelected;
        _wcDingYa = SysParamsAll.get_wcDingYa();
        _wcBaoYa = SysParamsAll.get_wcBaoYa();
        _wcJianYa = SysParamsAll.get_wcJianYa();
        _wcLouXie = SysParamsAll.get_wcLouXie();
        _isValid_ZGYL = false;
        _isValid_BYSJ = false;
        _isValid_JYL = false;
        _isValid_LXL = false;
        _isValid_JYSJ = false;
        _listViewContent = new ArrayList<TestViewContent>(0);
        initViewContentList();
    }

    public TestContent(String testName) {
        this();
        _testName = testName;
    }

    public void SetParms(int nDefinedPressureValue, int nWagonCount) {
        _definedPressureValue = nDefinedPressureValue;
        _wagonCount = nWagonCount;
        UpdateStandardDrop();
    }

    public void UpdateStandardDrop() {
    }

    public void Reset() {
        _startTime = "";
        _endTime = "";
        _keepPressureTime = "";
        _ZGYL = 0;
        _JYL = 0;
        _LXL = 0;
        _LXL2 = -1;
        _LXL3 = -1;
        _LXL4 = -1;
        _LXL5 = -1;
        _BYSJ = 0;
        _JYSD = 0;
        _JYSJ = 0;
        _HJSJ = 0;
        TestPressureValueMax = 0;
        TestPressureValueMin = 0;
        _xBeginPos = -1;
        _xEndPos = -1;
        _yPressureValue = 0;
        _state = "";
        _stat = TestState.tsNotBegin;
    }

    public int GetCurrPressureValue(PressureValue pd) {
        return (SysParamsAll.get_noTail() == 0 ? pd.getPressureValue() : pd.getHeadPressureValue());
    }

    public boolean Valid_ZGYL() {
        return (_ZGYL >= _definedPressureValue - _wcDingYa && _ZGYL <= _definedPressureValue + _wcDingYa);
    }

    public boolean Valid_BYSJ() {
        return (_BYSJ >= _standardKeepTime - _wcBaoYa);
    }

    public boolean Valid_JYL() {
        return (_JYL >= _standardDrop - _wcJianYa && _JYL <= _standardDrop + _wcJianYa);
    }

    public boolean Valid_LXL() {
        return (_LXL < _standardLeak + _wcLouXie);
    }

    public boolean Valid_JYSJ() {
        return (_JYSJ >= _standardDropTimeMin && _JYSJ <= _standardDropTimeMax);
    }

    /**
     * 接收到报文的状态是试验开始的处理
     */
    public void DoTestBegin() {
        Reset();
        _stat = TestState.tsDoing;
        TipSoundPlayer.PlayVoicePrompts(_voiceFileNameBegin);
    }

    /**
     * 接收报文的状态是减压开始的处理
     * @param pd 减压开始的压力数据
     */
    public void DoTestDropPressureBegin(PressureValue pd) {
        int DropSpeed = pd.getTestResult();
        if (_stat == TestState.tsNotBegin) {
            _stat = TestState.tsDoing;
            TipSoundPlayer.PlayVoicePrompts(_voiceFileNameBegin);
        }
        if (_ZGYL <= 50) {
            _ZGYL = DropSpeed / 10;//2019-01-19注释
        }
    }

    public void DoTestDropPressureEnd(String stime, int DropSpeed) {
        _JYSD = DropSpeed / 10;//2018-09-29把减压速度除以10
    }

    public void DoTestKeepFirst(String stime, PressureValue pd) {
        int currPressureValue = GetCurrPressureValue(pd);
        int DropSpeed = pd.getTestResult();
        if (_stat == TestState.tsNotBegin) {
            _stat = TestState.tsDoing;
            TipSoundPlayer.PlayVoicePrompts(_voiceFileNameBegin);
        }
        if (_ZGYL <= 50) //漏泄饰演的主管压力在保压阶段取
        {
            _ZGYL = DropSpeed / 10;
        }
        _startTime = stime;
        _keepPressureTime = stime;
        _JYL = _ZGYL - DropSpeed / 10;//_JYL = _ZGYL - currPressureValue;//把保压开始的压力更为取长宣赋值//2019-01-16注释

        _yPressureValue = currPressureValue;
        if (_JYL < 0) {
            _JYL = 0;
        }
    }

    public void DoTestKeep(String stime, PressureValue pd) {
        int currPressureValue = GetCurrPressureValue(pd);
        set_endTime(stime);
        //漏泄量只取指定保压时间的漏泄量
        if (_BYSJ <= _standardKeepTime) {
            int lxl = _yPressureValue - currPressureValue;
            if (lxl < 0) {
                lxl = 0;
            }
            _LXL = lxl;
        }
    }

    public void DoTestEnd(String stime, PressureValue pd) {
        int DropSpeed = pd.getTestResult();
        set_endTime(stime);
        _stat = TestState.tsStoped;
        //漏泄量只取指定保压时间的漏泄量
        if (_BYSJ <= _standardKeepTime) {
            int lxl = _ZGYL - _JYL - DropSpeed / 10;//_yPressureValue - currPressureValue;
            if (lxl < 0) {
                lxl = 0;
            }
            _LXL = lxl;
        }
    }

    public void DoTestEndValidResult() {
        if (IsTestSuccess()) {
            _state = CStrTestStateCompleted;
            //修正保压时间为标准保压时间
            _BYSJ = _BYSJ < _standardKeepTime ? _standardKeepTime : _BYSJ;
            TipSoundPlayer.PlayVoicePrompts(_voiceFileNameCompleted);
        } else {
            _state = CStrTestStateNotCompleted;
            if (null != onTestFailedListener) {
                onTestFailedListener.onTestFailed();
            }
            TipSoundPlayer.PlayVoicePrompts(_voiceFileNameNotCompleted);
        }
    }

    public void DoTestFillWindEnd(String stime, PressureValue pd) {
    }

    public boolean IsTestSuccess() {
        if ((_isValid_ZGYL ? Valid_ZGYL() : true) &&
                (_isValid_BYSJ ? Valid_BYSJ() : true) &&
                (_isValid_JYL ? Valid_JYL() : true) &&
                (_isValid_LXL ? Valid_LXL() : true) &&
                (_isValid_JYSJ ? Valid_JYSJ() : true)) {
            return true;
        } else {
            return false;
        }
    }

    public TestContent CheckStatus(String stime, PressureValue pd, CTestWindProtocel.TestStatus testStatus, int DropSpeed) {
        currPressureValue = GetCurrPressureValue(pd);
        if (testStatus == CTestWindProtocel.TestStatus.TestBegin) {
            XLog.i(get_testName() + "：试验开始。");
            DoTestBegin();
        } else if (testStatus == CTestWindProtocel.TestStatus.TestFillWindEnd) {
            XLog.i(get_testName() + "：充风缓解完毕。");
            DoTestFillWindEnd(stime, pd);
        } else if (testStatus == CTestWindProtocel.TestStatus.TestDropPressureBegin) {
            XLog.i(get_testName() + "：减压开始。");
            DoTestDropPressureBegin(pd);
        } else if (testStatus == CTestWindProtocel.TestStatus.TestDropPressureEnd) {
            XLog.i(get_testName() + "：减压结束。");
            DoTestDropPressureEnd(stime, pd.getTestResult());
        } else if (testStatus == CTestWindProtocel.TestStatus.TestKeep) {
            if (_keepPressureTime.isEmpty()) {
                XLog.i(get_testName() + "：保压开始。");
                DoTestKeepFirst(stime, pd);
            }
            XLog.i(get_testName() + "：保压中。");
            DoTestKeep(stime, pd);
        } else if (testStatus == CTestWindProtocel.TestStatus.TestEnd) {
            if (_stat == TestState.tsDoing) {
                XLog.i(get_testName() + "：试验结束。");
                DoTestEnd(stime, pd);
                DoTestEndValidResult();
            }
        } else {
            XLog.i(get_testName() + "：试验状态未处理：" + String.valueOf(testStatus));
        }
        updateViewContentInfo();
        return this;
    }

    public void DrawSelf(Canvas canvas, int nStartPos) {
//        if (_xBeginPos < 0 || _xBeginPos < nStartPos || _xEndPos < nStartPos || _xEndPos < _xBeginPos
//                || _xEndPos - nStartPos - 4 > frmGraphs.cnChartWidth)
//        {
//            return;
//        }
//        if (_stat == TestState.tsDoing )
//        {
//            e.Graphics.DrawString(_BYSJ.ToString(), f, bText, _xBeginPos - nStartPos + frmGraphs.cnChartLeftStartPos, frmGraphs.PressureValueToChartYPos(_yPressureValue - 20) + 4);
//        }
//        else
//        {
//            e.Graphics.DrawString(_shortTestName, f, bText, _xBeginPos - nStartPos + 24, frmGraphs.PressureValueToChartYPos(_yPressureValue - 20) + 4);
//        }
//        e.Graphics.DrawRectangle(p, _xBeginPos - nStartPos + frmGraphs.cnChartLeftStartPos, frmGraphs.PressureValueToChartYPos(_yPressureValue + 20),
//                _xEndPos - _xBeginPos, frmGraphs.PressureValueToChartYPos(_yPressureValue - 20) - frmGraphs.PressureValueToChartYPos(_yPressureValue + 20));
    }

    public void initViewContentList(){}

    public void updateViewContentInfo(){
        for(TestViewContent testViewContent : _listViewContent){
            if (testViewContent.getColumn1().equals(VIEW_ROW_NAME_PRESSURE)){
                testViewContent.setColumn2(String.valueOf(get_ZGYL()));
            } else if (testViewContent.getColumn1().equals(VIEW_ROW_NAME_DROPVALUE)){
                testViewContent.setColumn2(String.valueOf(get_JYL()));
            } else if (testViewContent.getColumn1().equals(VIEW_ROW_NAME_TIME)){
                testViewContent.setColumn2(String.valueOf(get_BYSJ()));
            } else if (testViewContent.getColumn1().equals(VIEW_ROW_NAME_LEAKVALUE)){
                testViewContent.setColumn2(String.valueOf(get_LXL()));
            } else if (testViewContent.getColumn1().equals(VIEW_ROW_NAME_SPEED)){
                testViewContent.setColumn2(String.valueOf(get_JYSD()));
            }
        }
    }

    public void setOnTestFailedListener(OnTestFailedListener onTestFailedListener) {
        this.onTestFailedListener = onTestFailedListener;
    }

    public TestState get_stat() {
        return _stat;
    }

    public void set_stat(TestState _stat) {
        this._stat = _stat;
    }

    public CTestWindProtocel.TestCommand get_TestCommandCode() {
        return _TestCommandCode;
    }

    public void set_TestCommandCode(CTestWindProtocel.TestCommand _TestCommandCode) {
        this._TestCommandCode = _TestCommandCode;
    }

    public String get_testName() {
        return _testName;
    }

    public void set_testName(String _testName) {
        this._testName = _testName;
    }

    public String get_endTime() {
        return _endTime;
    }

    /**
     * 设置结束时间时，自动计算保压时间
     *
     * @param _endTime
     */
    public void set_endTime(String _endTime) {
        this._endTime = _endTime;
        try {
            if (_keepPressureTime.isEmpty()) {
                _BYSJ = 0;
            } else {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date startTime = dateFormat.parse("2019-06-18 " + _keepPressureTime);
                Date endTime = dateFormat.parse("2019-06-18 " + _endTime);
                if (endTime.getTime() < startTime.getTime()) {
                    endTime = dateFormat.parse("2019-06-19 " + _endTime);
                }
                long timeDiff = endTime.getTime() - startTime.getTime();
                _BYSJ = (int) (timeDiff / 1000);
            }
        } catch (Exception ex) {
            _BYSJ = 0;
        }
    }

    public String get_LXL() {
        String strLXL = String.valueOf(_LXL);
        if (_LXL2 >= 0 || _LXL3 >= 0 || _LXL4 >= 0 || _LXL5 >= 0) {
            strLXL = strLXL + "," + _LXL2 + "," + _LXL3 + "," + _LXL4 + "," + _LXL5;
        }
        return strLXL;
    }

    /**
     * 设置漏泄量的时候做特殊处理
     *
     * @param _LXL
     */
    public void set_LXL(String _LXL) {
        if (_LXL.isEmpty()) {
            this._LXL = 0;
            this._LXL2 = -1;
            this._LXL3 = -1;
            this._LXL4 = -1;
            this._LXL5 = -1;
        } else {
            try {
                this._LXL = Integer.parseInt(_LXL);
                _LXL2 = -1;
                _LXL3 = -1;
                _LXL4 = -1;
                _LXL5 = -1;
            } catch (Exception ex) {
                try {
                    String[] all_lxl = _LXL.split(",");
                    if (all_lxl.length > 0) {
                        this._LXL = Integer.parseInt(all_lxl[0]);
                        _LXL2 = Integer.parseInt(all_lxl[1]);
                        _LXL3 = Integer.parseInt(all_lxl[2]);
                        _LXL4 = Integer.parseInt(all_lxl[3]);
                        _LXL5 = Integer.parseInt(all_lxl[4]);
                    } else {
                        this._LXL = 0;
                        _LXL2 = -1;
                        _LXL3 = -1;
                        _LXL4 = -1;
                        _LXL5 = -1;
                    }
                } catch (Exception e) {
                    this._LXL = 0;
                    _LXL2 = -1;
                    _LXL3 = -1;
                    _LXL4 = -1;
                    _LXL5 = -1;
                }
            }
        }
    }

    public int get_ZGYL() {
        return _ZGYL;
    }

    public void set_ZGYL(int _ZGYL) {
        this._ZGYL = _ZGYL;
    }

    public int get_JYL() {
        return _JYL;
    }

    public void set_JYL(int _JYL) {
        this._JYL = _JYL;
    }

    public int get_JYSD() {
        return _JYSD;
    }

    public void set_JYSD(int _JYSD) {
        this._JYSD = _JYSD;
    }

    public int get_BYSJ() {
        return _BYSJ;
    }

    public void set_BYSJ(int _BYSJ) {
        this._BYSJ = _BYSJ;
    }

    public String get_JYSJDot() {
        return String.valueOf(_JYSJ / 10) + "." + String.valueOf(_JYSJ % 10);
    }

    public int get_JYSJ() {
        return _JYSJ;
    }

    public void set_JYSJ(int _JYSJ) {
        this._JYSJ = _JYSJ;
    }

    public String get_state() {
        return _state;
    }

    public List<TestViewContent> get_listViewContent() {
        if (null == _listViewContent) {
            _listViewContent = new ArrayList<TestViewContent>(0);
        }
        return _listViewContent;
    }

    public @TestViewTypes int get_testViewType() { return V_NORMAL; }

    public String get_passengerTrainPosition() { return ""; }

    public String get_passengerTrainID(){ return ""; }

    public void set_passengerTrainID(String passengerTrainID){  }
}
