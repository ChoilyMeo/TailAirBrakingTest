package com.thnet.tailairbrakingtest.testwind;

import com.thnet.tailairbrakingtest.customapplication.WindTestApplication;
import com.thnet.tailairbrakingtest.dao.DaoSession;
import com.thnet.tailairbrakingtest.dao.SysParms;

import java.util.List;

public class SysParamsAll {
    public static final int SpecifiedPressure500 = 500;
    public static final int SpecifiedPressure600 = 600;
    public static final int PARAM_MAX_STANDARD_LEAKVALUE_GD = 10000;
    public static final int PARAM_MIN_STANDARD_KEEPTIME_AD = 0;
    public static final int PARAM_MIN_STANDARD_KEEPTIME_JL = 0;
    public static final int PARAM_MIN_STANDARD_KEEPTIME_JL60 = 60;
    public static final int CHECK_LEAK_VALUE_TYPE_ONE_MINUTE = 0;
    public static final int CHECK_LEAK_VALUE_TYPE_PER_MINUTE = 1;
    public static final int CHECK_LEAK_VALUE_TYPE_ALL_KEEP_TIME = 2;
    public static final String PARAM_KEHUOCHE_KECHE = "0";
    public static final String PARAM_KEHUOCHE_HUOCHE = "1";
    //各项参数设置
    private static String _testOperator;
    private static String _testOperatorName;
    private static int _tipRedoTestDelay = 10000;
    private static int _chartMovePoint = 10;
    private static long _diskSpaceMin = 20 * 1024 * 1024;
    private static String _portName = "/dev/ttyMT1";
    private static int _baudRate = 1200;
    private static int _maxTestPoint = 5000;
    private static byte _modalChanelNo = 0x00;
    private static byte _deviceNo = 0x08;
    private static int _sendCommandTimer = 4000;
    private static int _playSound = 0;
    private static int _autoRepeat = 0;
    private static int _autoSave = 0;
    private static int _judgeEndTimes = 15;
    //客车货车区分标志：0客车，1货车
    private static String keHuoChe = "1";
    //持续保压试验漏泄量判定的类型：0-取第一分钟的漏泄量；1-取每一分钟的漏泄量；2-取标准保压时间内的漏泄量
    private static int checkLeakValueType = CHECK_LEAK_VALUE_TYPE_ONE_MINUTE;

    private static int _TempDateLen = 100;
    private static int _EstiDataLen = 10;
    private static int _fluxRange = 3;
    private static int _reliefRange = 5;
    private static int _gdAnalyseMin = 30;
    private static int _gdAnalyseMax = 200;
    private static int _adAnalyseMin = 50;
    private static int _adAnalyseMax = 250;
    private static int _byAnalyseMin = 80;
    private static int _byAnalyseMax = 200;
    private static int _jlAnalyseMin = 50;
    private static int _jlAnalyseMax = 200;
    private static int _pressureValueMin500 = 450;
    private static int _pressureValueMin600 = 550;

    //判断标准
    private static int _lxStandardTime = 60;
    private static int _lxStandardLeak = 20;
    private static int _gdStandardDrop60u = 70;
    private static int _gdStandardDrop60d = 50;
    private static int _gdStandardTime = 180;
    private static int _adStandardTime = 3;;
    private static int _gdStandardLeak = 20;
    private static int _adStandardLeak = 1000;
    private static int _adStandardDrop600 = 170;
    private static int _adStandardDrop500 = 140;
    private static int _byStandardTime = 180;
    private static int _byStandardDrop = 100;
    private static int _byStandardLeak = 20;
    private static int _jlStandardDrop = 100;
    private static int _jlStandardLeak = 20;
    private static int _jlStandardTime = PARAM_MIN_STANDARD_KEEPTIME_JL;

    //试验判断的误差设置
    private static int _wcDingYa = 15;
    private static int _wcJianYa = 15;
    private static int _wcLouXie = 1;
    private static int _wcBaoYa = 1;
    private static String _protocolVer = "1.0";
    private static int _pointWidth = 2;//压力曲线横向显示的颗粒度，每两点之间用几个像素显示

    public static List<SysParms> load(){
        //从配置文件加载参数
        DaoSession daoSession = WindTestApplication.getWindTestInstance().getDaoSession();
        List<SysParms> parmsList = daoSession.loadAll(SysParms.class);
        for (SysParms parms : parmsList){
            if (parms.getParamID().equals("PortName"))
            {
                _portName = parms.getParamValue();
            }
            if (parms.getParamID().equals("BaudRate"))
            {
                _baudRate = Integer.parseInt(parms.getParamValue());
            }
            if (parms.getParamID().equals("ChanelNo"))
            {
                _modalChanelNo = (byte)Integer.parseInt(parms.getParamValue());
            }
            if (parms.getParamID().equals("DeviceNo"))
            {
                _deviceNo = (byte)Integer.parseInt(parms.getParamValue());
            }
            if (parms.getParamID().equals("SendTimer"))
            {
                _sendCommandTimer = Integer.parseInt(parms.getParamValue());
            }
            if (parms.getParamID().equals("TempLen"))
            {
                _TempDateLen = Integer.parseInt(parms.getParamValue());
            }
            if (parms.getParamID().equals("EstiLen"))
            {
                _EstiDataLen = Integer.parseInt(parms.getParamValue());
            }
            if (parms.getParamID().equals("FluxRange"))
            {
                _fluxRange = Integer.parseInt(parms.getParamValue());
            }
            if (parms.getParamID().equals("ReliefRange"))
            {
                _reliefRange = Integer.parseInt(parms.getParamValue());
            }
            if (parms.getParamID().equals("GDAnalyseMin"))
            {
                _gdAnalyseMin = Integer.parseInt(parms.getParamValue());
            }
            if (parms.getParamID().equals("GDAnalyseMax"))
            {
                _gdAnalyseMax = Integer.parseInt(parms.getParamValue());
            }
            if (parms.getParamID().equals("ADAnalyseMin"))
            {
                _adAnalyseMin = Integer.parseInt(parms.getParamValue());
            }
            if (parms.getParamID().equals("ADAnalyseMax"))
            {
                _adAnalyseMax = Integer.parseInt(parms.getParamValue());
            }
            if (parms.getParamID().equals("BYAnalyseMin"))
            {
                _byAnalyseMin = Integer.parseInt(parms.getParamValue());
            }
            if (parms.getParamID().equals("BYAnalyseMax"))
            {
                _byAnalyseMax = Integer.parseInt(parms.getParamValue());
            }
            if (parms.getParamID().equals("JLAnalyseMin"))
            {
                _jlAnalyseMin = Integer.parseInt(parms.getParamValue());
            }
            if (parms.getParamID().equals("JLAnalyseMax"))
            {
                _jlAnalyseMax = Integer.parseInt(parms.getParamValue());
            }
            if (parms.getParamID().equals("PValueMin500"))
            {
                _pressureValueMin500 = Integer.parseInt(parms.getParamValue());
            }
            if (parms.getParamID().equals("PValueMin600"))
            {
                _pressureValueMin600 = Integer.parseInt(parms.getParamValue());
            }
            if (parms.getParamID().equals("LXTime"))
            {
                _lxStandardTime = Integer.parseInt(parms.getParamValue());
            }
            if (parms.getParamID().equals("LXLeak"))
            {
                _lxStandardLeak = Integer.parseInt(parms.getParamValue());
            }
            if (parms.getParamID().equals("GDDrop60d"))
            {
                _gdStandardDrop60d = Integer.parseInt(parms.getParamValue());
            }
            if (parms.getParamID().equals("GDDrop60u"))
            {
                _gdStandardDrop60u = Integer.parseInt(parms.getParamValue());
            }
            if (parms.getParamID().equals("GDTime"))
            {
                _gdStandardTime = Integer.parseInt(parms.getParamValue());
            }
            if (parms.getParamID().equals("GDLeak"))
            {
                _gdStandardLeak = Integer.parseInt(parms.getParamValue());
            }
            if (parms.getParamID().equals("ADTime"))
            {
                _adStandardTime = Integer.parseInt(parms.getParamValue());
            }
            if (parms.getParamID().equals("ADLeak"))
            {
                _adStandardLeak = Integer.parseInt(parms.getParamValue());
            }
            if (parms.getParamID().equals("ADDrop500"))
            {
                _adStandardDrop500 = Integer.parseInt(parms.getParamValue());
            }
            if (parms.getParamID().equals("ADDrop600"))
            {
                _adStandardDrop600 = Integer.parseInt(parms.getParamValue());
            }
            if (parms.getParamID().equals("BYTime"))
            {
                _byStandardTime = Integer.parseInt(parms.getParamValue());
            }
            if (parms.getParamID().equals("BYDrop"))
            {
                _byStandardDrop = Integer.parseInt(parms.getParamValue());
            }
            if (parms.getParamID().equals("JLDrop"))
            {
                _jlStandardDrop = Integer.parseInt(parms.getParamValue());
            }
            if (parms.getParamID().equals("WCDingYa"))
            {
                _wcDingYa = Integer.parseInt(parms.getParamValue());
            }
            if (parms.getParamID().equals("WCJianYa"))
            {
                _wcJianYa = Integer.parseInt(parms.getParamValue());
            }
            if (parms.getParamID().equals("WCLouXie"))
            {
                _wcLouXie = Integer.parseInt(parms.getParamValue());
            }
            if (parms.getParamID().equals("WCBaoYa"))
            {
                _wcBaoYa = Integer.parseInt(parms.getParamValue());
            }
            if (parms.getParamID().equals("PlaySound"))
            {
                _playSound = Integer.parseInt(parms.getParamValue());
            }
            if (parms.getParamID().equals("AutoRepeat"))
            {
                _autoRepeat = Integer.parseInt(parms.getParamValue());
            }
            if (parms.getParamID().equals("AutoSave"))
            {
                _autoSave = Integer.parseInt(parms.getParamValue());
            }
            if (parms.getParamID().equals("JudgeEndTimes"))
            {
                _judgeEndTimes = Integer.parseInt(parms.getParamValue());
            }
            if (parms.getParamID().equals("KeHuoChe"))
            {
                keHuoChe = parms.getParamValue();
            }
            if (parms.getParamID().equals("ChkLeakType"))
            {
                checkLeakValueType = Integer.parseInt(parms.getParamValue());
            }
            if (parms.getParamID().equals("BYLeak"))
            {
                _byStandardLeak = Integer.parseInt(parms.getParamValue());
            }
            if (parms.getParamID().equals("JLLeak"))
            {
                _jlStandardLeak = Integer.parseInt(parms.getParamValue());
            }
            if (parms.getParamID().equals("ProtocolVer"))
            {
                _protocolVer = parms.getParamValue();
            }
            if (parms.getParamID().equals("PointWidth"))
            {
                _pointWidth = Integer.parseInt(parms.getParamValue());
            }
        }
        return parmsList;
    }

    public static String get_testOperator() {
        return _testOperator;
    }

    public static void set_testOperator(String _testOperator) {
        SysParamsAll._testOperator = _testOperator;
    }

    public static String get_testOperatorName() {
        return _testOperatorName;
    }

    public static void set_testOperatorName(String _testOperatorName) {
        SysParamsAll._testOperatorName = _testOperatorName;
    }

    public static int get_tipRedoTestDelay() {
        return _tipRedoTestDelay;
    }

    public static void set_tipRedoTestDelay(int _tipRedoTestDelay) {
        SysParamsAll._tipRedoTestDelay = _tipRedoTestDelay;
    }

    public static int get_chartMovePoint() {
        return _chartMovePoint;
    }

    public static void set_chartMovePoint(int _chartMovePoint) {
        SysParamsAll._chartMovePoint = _chartMovePoint;
    }

    public static long get_diskSpaceMin() {
        return _diskSpaceMin;
    }

    public static void set_diskSpaceMin(long _diskSpaceMin) {
        SysParamsAll._diskSpaceMin = _diskSpaceMin;
    }

    public static int get_testPressureValueMin(int nDefinedPressureValue){
        if (nDefinedPressureValue == 500)
        {
            return _pressureValueMin500;
        }
        else
        {
            return _pressureValueMin600;
        }
    }

    public static int get_allAnalyseMax(){
        int max = 0;
        if (_gdAnalyseMax > max)
        {
            max = _gdAnalyseMax;
        }
        if (_adAnalyseMax > max)
        {
            max = _adAnalyseMax;
        }
        if (_byAnalyseMax > max)
        {
            max = _byAnalyseMax;
        }
        if (_jlAnalyseMax > max)
        {
            max = _jlAnalyseMax;
        }
        return max;
    }
    public static int get_allAnalyseMin()
    {
        int min = 0;
        if (_gdAnalyseMin > min)
        {
            min = _gdAnalyseMin;
        }
        if (_adAnalyseMin > min)
        {
            min = _adAnalyseMin;
        }
        if (_byAnalyseMin > min)
        {
            min = _byAnalyseMin;
        }
        if (_jlAnalyseMin > min)
        {
            min = _jlAnalyseMin;
        }
        return min;
    }

    public static String get_portName() {
        return _portName;
    }

    public static void set_portName(String _portName) {
        SysParamsAll._portName = _portName;
    }

    public static int get_baudRate() {
        return _baudRate;
    }

    public static void set_baudRate(int _baudRate) {
        SysParamsAll._baudRate = _baudRate;
    }

    public static int get_maxTestPoint() {
        return _maxTestPoint;
    }

    public static void set_maxTestPoint(int _maxTestPoint) {
        SysParamsAll._maxTestPoint = _maxTestPoint;
    }

    public static byte get_modalChanelNo() {
        return _modalChanelNo;
    }

    public static void set_modalChanelNo(byte _modalChanelNo) {
        SysParamsAll._modalChanelNo = _modalChanelNo;
    }

    public static byte get_deviceNo() {
        return _deviceNo;
    }

    public static void set_deviceNo(byte _deviceNo) {
        SysParamsAll._deviceNo = _deviceNo;
    }

    public static int get_sendCommandTimer() {
        return _sendCommandTimer;
    }

    public static void set_sendCommandTimer(int _sendCommandTimer) {
        SysParamsAll._sendCommandTimer = _sendCommandTimer;
    }

    public static int get_playSound() {
        return _playSound;
    }

    public static void set_playSound(int _playSound) {
        SysParamsAll._playSound = _playSound;
    }

    public static int get_autoRepeat() {
        return _autoRepeat;
    }

    public static void set_autoRepeat(int _autoRepeat) {
        SysParamsAll._autoRepeat = _autoRepeat;
    }

    public static int get_autoSave() {
        return _autoSave;
    }

    public static void set_autoSave(int _autoSave) {
        SysParamsAll._autoSave = _autoSave;
    }

    public static int get_judgeEndTimes() {
        return _judgeEndTimes;
    }

    public static void set_judgeEndTimes(int _judgeEndTimes) {
        SysParamsAll._judgeEndTimes = _judgeEndTimes;
    }

    public static int get_TempDateLen() {
        return _TempDateLen;
    }

    public static void set_TempDateLen(int _TempDateLen) {
        SysParamsAll._TempDateLen = _TempDateLen;
    }

    public static int get_EstiDataLen() {
        return _EstiDataLen;
    }

    public static void set_EstiDataLen(int _EstiDataLen) {
        SysParamsAll._EstiDataLen = _EstiDataLen;
    }

    public static int get_fluxRange() {
        return _fluxRange;
    }

    public static void set_fluxRange(int _fluxRange) {
        SysParamsAll._fluxRange = _fluxRange;
    }

    public static int get_reliefRange() {
        return _reliefRange;
    }

    public static void set_reliefRange(int _reliefRange) {
        SysParamsAll._reliefRange = _reliefRange;
    }

    public static int get_gdAnalyseMin() {
        return _gdAnalyseMin;
    }

    public static void set_gdAnalyseMin(int _gdAnalyseMin) {
        SysParamsAll._gdAnalyseMin = _gdAnalyseMin;
    }

    public static int get_gdAnalyseMax() {
        return _gdAnalyseMax;
    }

    public static void set_gdAnalyseMax(int _gdAnalyseMax) {
        SysParamsAll._gdAnalyseMax = _gdAnalyseMax;
    }

    public static int get_adAnalyseMin() {
        return _adAnalyseMin;
    }

    public static void set_adAnalyseMin(int _adAnalyseMin) {
        SysParamsAll._adAnalyseMin = _adAnalyseMin;
    }

    public static int get_adAnalyseMax() {
        return _adAnalyseMax;
    }

    public static void set_adAnalyseMax(int _adAnalyseMax) {
        SysParamsAll._adAnalyseMax = _adAnalyseMax;
    }

    public static int get_byAnalyseMin() {
        return _byAnalyseMin;
    }

    public static void set_byAnalyseMin(int _byAnalyseMin) {
        SysParamsAll._byAnalyseMin = _byAnalyseMin;
    }

    public static int get_byAnalyseMax() {
        return _byAnalyseMax;
    }

    public static void set_byAnalyseMax(int _byAnalyseMax) {
        SysParamsAll._byAnalyseMax = _byAnalyseMax;
    }

    public static int get_jlAnalyseMin() {
        return _jlAnalyseMin;
    }

    public static void set_jlAnalyseMin(int _jlAnalyseMin) {
        SysParamsAll._jlAnalyseMin = _jlAnalyseMin;
    }

    public static int get_jlAnalyseMax() {
        return _jlAnalyseMax;
    }

    public static void set_jlAnalyseMax(int _jlAnalyseMax) {
        SysParamsAll._jlAnalyseMax = _jlAnalyseMax;
    }

    public static int get_pressureValueMin500() {
        return _pressureValueMin500;
    }

    public static void set_pressureValueMin500(int _pressureValueMin500) {
        SysParamsAll._pressureValueMin500 = _pressureValueMin500;
    }

    public static int get_pressureValueMin600() {
        return _pressureValueMin600;
    }

    public static void set_pressureValueMin600(int _pressureValueMin600) {
        SysParamsAll._pressureValueMin600 = _pressureValueMin600;
    }

    public static int get_wcDingYa() {
        return _wcDingYa;
    }

    public static void set_wcDingYa(int _wcDingYa) {
        SysParamsAll._wcDingYa = _wcDingYa;
    }

    public static int get_wcJianYa() {
        return _wcJianYa;
    }

    public static void set_wcJianYa(int _wcJianYa) {
        SysParamsAll._wcJianYa = _wcJianYa;
    }

    public static int get_wcLouXie() {
        return _wcLouXie;
    }

    public static void set_wcLouXie(int _wcLouXie) {
        SysParamsAll._wcLouXie = _wcLouXie;
    }

    public static int get_wcBaoYa() {
        return _wcBaoYa;
    }

    public static void set_wcBaoYa(int _wcBaoYa) {
        SysParamsAll._wcBaoYa = _wcBaoYa;
    }

    public static int get_lxStandardTime() {
        return _lxStandardTime;
    }

    public static void set_lxStandardTime(int _lxStandardTime) {
        SysParamsAll._lxStandardTime = _lxStandardTime;
    }

    public static int get_lxStandardLeak() {
        return _lxStandardLeak;
    }

    public static void set_lxStandardLeak(int _lxStandardLeak) {
        SysParamsAll._lxStandardLeak = _lxStandardLeak;
    }

    public static int get_gdStandardTime() {
        return _gdStandardTime;
    }

    public static void set_gdStandardTime(int _gdStandardTime) {
        SysParamsAll._gdStandardTime = _gdStandardTime;
    }

    public static int get_gdStandardDrop(int ls) {
            if (ls >= 60)
            {
                return _gdStandardDrop60u;
            }
            else
            {
                return _gdStandardDrop60d;
            }
    }

    public static int get_gdStandardLeak() {
        //设置是客车的话，感度试验不进行漏泄量的判定，因此在获取漏泄量参数的时候，设置为最大值
        if (PARAM_KEHUOCHE_KECHE.equals(getKeHuoChe())){
            return PARAM_MAX_STANDARD_LEAKVALUE_GD;
        } else {
            return _gdStandardLeak;
        }
    }

    public static void set_gdStandardLeak(int _gdStandardLeak) {
        SysParamsAll._gdStandardLeak = _gdStandardLeak;
    }

    public static int get_adStandardTime() {
        //设置时货车的话，安定试验不判断保压时间，因此保压时间标准值取最小值
        if (PARAM_KEHUOCHE_HUOCHE.equals(getKeHuoChe())){
            return PARAM_MIN_STANDARD_KEEPTIME_AD;
        } else {
            return _adStandardTime;
        }
    }

    public static void set_adStandardTime(int _adStandardTime) {
        SysParamsAll._adStandardTime = _adStandardTime;
    }

    public static int get_adStandardLeak() {
        return _adStandardLeak;
    }

    public static void set_adStandardLeak(int _adStandardLeak) {
        SysParamsAll._adStandardLeak = _adStandardLeak;
    }

    public static int get_adStandardDrop(int dy) {
        if (dy == 500)
        {
            return _adStandardDrop500;
        }
        else
        {
            return _adStandardDrop600;
        }
    }

    public static int get_byStandardTime() {
        return _byStandardTime;
    }

    public static void set_byStandardTime(int _byStandardTime) {
        SysParamsAll._byStandardTime = _byStandardTime;
    }

    public static int get_byStandardDrop() {
        return _byStandardDrop;
    }

    public static void set_byStandardDrop(int _byStandardDrop) {
        SysParamsAll._byStandardDrop = _byStandardDrop;
    }

    public static int get_jlStandardDrop() {
        return _jlStandardDrop;
    }

    public static void set_jlStandardDrop(int _jlStandardDrop) {
        SysParamsAll._jlStandardDrop = _jlStandardDrop;
    }

    public static int get_jlStandardTime() {
        //设置是客车的话，简略判断时间1分钟，货车不判断
        if (PARAM_KEHUOCHE_KECHE.equals(getKeHuoChe())){
            return PARAM_MIN_STANDARD_KEEPTIME_JL60;
        } else {
            return _jlStandardTime;
        }
    }

    public static void set_jlStandardTime(int _jlStandardTime) {
        SysParamsAll._jlStandardTime = _jlStandardTime;
    }

    public static String getKeHuoChe() {
        return keHuoChe;
    }

    public static void setKeHuoChe(String keHuoChe) {
        SysParamsAll.keHuoChe = keHuoChe;
    }

    public static int getCheckLeakValueType() {
        return checkLeakValueType;
    }

    public static void setCheckLeakValueType(int checkLeakValueType) {
        SysParamsAll.checkLeakValueType = checkLeakValueType;
    }

    public static int get_byStandardLeak() {
        return _byStandardLeak;
    }

    public static void set_byStandardLeak(int _byStandardLeak) {
        SysParamsAll._byStandardLeak = _byStandardLeak;
    }

    public static int get_jlStandardLeak() {
        return _jlStandardLeak;
    }

    public static void set_jlStandardLeak(int _jlStandardLeak) {
        SysParamsAll._jlStandardLeak = _jlStandardLeak;
    }

    public static String get_protocolVer() {
        return _protocolVer;
    }

    public static void set_protocolVer(String _protocolVer) {
        SysParamsAll._protocolVer = _protocolVer;
    }

    public static int get_pointWidth() {
        return _pointWidth;
    }

    public static void set_pointWidth(int _pointWidth) {
        SysParamsAll._pointWidth = _pointWidth;
    }
}
