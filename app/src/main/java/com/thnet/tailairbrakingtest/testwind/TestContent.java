package com.thnet.tailairbrakingtest.testwind;

import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;

import com.elvishew.xlog.XLog;
import com.thnet.tailairbrakingtest.communication.OnTestFailedListener;
import com.thnet.tailairbrakingtest.customapplication.WindTestApplication;
import com.thnet.tailairbrakingtest.customcontrol.ChartView;
import com.thnet.tailairbrakingtest.dao.DaoSession;
import com.thnet.tailairbrakingtest.dao.PressureValue;
import com.thnet.tailairbrakingtest.dao.TestDetail;
import com.thnet.tailairbrakingtest.dao.TestDetailDao;
import com.thnet.tailairbrakingtest.utility.TipSoundPlayer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 试风试验基类
 * @author mzl
 */
public class TestContent {
    public static final String TEST_NAME_AD = "安定试验";
    public static final String TEST_NAME_GD = "感度试验";
    public static final String TEST_NAME_GD_HC = "感度保压";
    public static final String TEST_NAME_JL = "简略试验";
    public static final String TEST_NAME_LX = "漏泄试验";
    public static final String TEST_NAME_BY = "持续保压";
    static final String SHORT_TEST_NAME_AD = "安定";
    static final String SHORT_TEST_NAME_GD = "感度";
    static final String SHORT_TEST_NAME_JL = "简略";
    static final String SHORT_TEST_NAME_LX = "漏泄";
    static final String SHORT_TEST_NAME_BY = "持保";
    public static final String TEST_STATE_COMPLETED = "完成";
    public static final String TEST_STATE_NOT_COMPLETED = "已作业";
    //界面展示控制变量，控制是否在界面显示主管压力、漏泄量、减压量、保压时间等值
    protected int viewStatMainPressure = View.VISIBLE;
    protected int viewStatKeepTime = View.VISIBLE;
    protected int viewStatDropValue = View.VISIBLE;
    protected int viewStatLeakValue = View.VISIBLE;
    //试验区别变量，不同的试验在构造函数中进行不同的赋值
    protected String testName;
    protected String shortTestName;
    protected int voiceFileNameBegin;
    protected int voiceFileNameCompleted;
    protected int voiceFileNameNotCompleted;
    //试验数据变量，根据试验结果进行赋值，最终保存到数据库
    protected String startTime = "";//开始时间
    protected String endTime = "";//结束时间
    protected int testMainPressureValue = 0;//主管压力
    protected int testDropValue = 0;//减压量
    protected int testLeakValue = 0;//漏泄量，如果取的是五分钟漏泄量的话，则是第一分钟漏泄量
    protected int testKeepTime = 0;//保压时间
    protected String testResult = "";//试验结果
    protected TestState stat = TestState.tsNotBegin;
    //试验中间变量，根据试验结果赋值，中间进行临时计算和图形显示
    protected int testPressureValueMax = 0;
    protected int testPressureValueMin = 0;
    protected int xBeginPos = -1;
    protected int xEndPos = -1;
    protected int yPressureValue = 0;
    //试验参数变量，根据不同的试验，构造时已经赋值
    protected int drawColor = Color.BLACK;
    protected int analyseMin = 0;
    protected int analyseMax = 0;
    protected int standardKeepTime = 0;
    protected int standardDrop = 0;
    protected int standardLeak = 0;
    protected int definedPressureValue = 0;
    protected int wcDingYa = 0;
    protected int wcBaoYa = 0;
    protected int wcJianYa = 0;
    protected int wcLouXie = 0;

    //其他变量
    public enum TestState {
        tsNotSelected, tsNotBegin, tsDoing, tsStoped
    }

    //压力大于定压提醒标志（压力连续大于定压指定值一定次数时，设置此标志，程序进行震动提醒）
    protected boolean pressureOverflowFlag = false;//压力超过定压标志，如果为true则程序需要震动提醒
    protected int pressureOverflowCountLimit = 5;//压力大于定压限制次数，超过这个次数就设置超过标志为true
    protected int pressureOverflowNumLimit = 115;//压力超过定压这个数值才会被认为是超过定压标准
    protected int overflowTimes = 0;//中间变量，计算的连续超过定压的次数，中间有任意一次压力小于定压，则次数从0计算

    int wagonCount = 0;
    protected OnTestFailedListener onTestFailedListener;

    public TestContent() {
        testName = "";
        stat = TestState.tsNotSelected;
        wcDingYa = SysParamsAll.get_wcDingYa();
        wcBaoYa = SysParamsAll.get_wcBaoYa();
        wcJianYa = SysParamsAll.get_wcJianYa();
        wcLouXie = SysParamsAll.get_wcLouXie();
    }

    public TestContent(String testName) {
        this();
        this.testName = testName;
    }

    public void setParms(int nDefinedPressureValue, int nWagonCount) {
        definedPressureValue = nDefinedPressureValue;
        wagonCount = nWagonCount;
        updateStandardDrop();
    }

    public void updateStandardDrop() {
    }

    public void reset() {
        startTime = "";
        endTime = "";
        testMainPressureValue = 0;
        testDropValue = 0;
        testLeakValue = 0;
        testKeepTime = 0;
        testPressureValueMax = 0;
        testPressureValueMin = 0;
        xBeginPos = -1;
        xEndPos = -1;
        yPressureValue = 0;
        testResult = "";
        stat = TestState.tsNotBegin;
    }

    protected int getTestPressureValue(CEstimate lstTemp) {
        int pressureValue = 0;
        CEstimate e = new CEstimate(SysParamsAll.get_EstiDataLen());
        for (int i = lstTemp.getListLen() - 1; i >= 0; i--) {
            e.add(lstTemp.getAtPosition(i));
            if (lstTemp.getMax() - e.getAvg() < (int) (SysParamsAll.get_fluxRange() * 1.00 / 2 + 0.5)) {
                pressureValue = e.getMax();
                break;
            }
        }
        if (pressureValue == 0) {
            pressureValue = lstTemp.getMax();
        }
        return pressureValue;
    }

    protected boolean validatePressureOverflow(int nPressureValue){
        if (nPressureValue >= getDefinedPressureValue() + getPressureOverflowNumLimit() && overflowTimes < Integer.MAX_VALUE){
            overflowTimes ++;
        } else {
            overflowTimes = 0;
        }
        return pressureOverflowFlag = overflowTimes > getPressureOverflowCountLimit();
    }

    protected boolean validateTestSuccess(){
        return validateMainPressureValue() &&
                validateKeepTime() &&
                validateDropValue() &&
                validateLeakValue();
    }

    protected boolean validateMainPressureValue(){
        return getTestMainPressureValue() >= getDefinedPressureValue() - getWcDingYa()
                && getTestMainPressureValue() <= getDefinedPressureValue() + getWcDingYa();
    }

    protected boolean validateKeepTime(){
        return getTestKeepTime() >= getStandardKeepTime();
    }

    protected boolean validateDropValue(){
        return getTestDropValue() >= getStandardDrop() - getWcJianYa()
                && getTestDropValue() <= getStandardDrop() + getWcJianYa();
    }

    protected boolean validateLeakValue(){
        return getTestLeakValue() < getStandardLeak() + getWcLouXie();
    }

    protected void updateTestLeakValue(int testLeakValue){
        if (getTestKeepTime() <= 60)
        {
            int lxl = testLeakValue;
            if (lxl < 0) {
                lxl = 0;
            }
            setTestLeakValue(lxl);
        }
    }

    public TestContent checkStatus(String stime, int nPressureValue, CEstimate lstEsti, CEstimate lstTemp, List<PressureValue> lstPressureValue) {
        validatePressureOverflow(nPressureValue);
        if (stat == TestState.tsNotBegin) {
            if (lstEsti.getMax() - lstEsti.getMin() <= SysParamsAll.get_fluxRange() && lstTemp.getMax() - lstEsti.getMax() > SysParamsAll.get_fluxRange()) {
                XLog.d("试验(" + getTestName() + ")开始:EstiMax=" + lstEsti.getMax() + ",EstiMin=" + lstEsti.getMin() + ",EstiLen=" + lstEsti.getListLen() + ",TempMax=" + lstTemp.getMax() + ",TempLen=" + lstTemp.getListLen());
                int dropPressureValue = lstTemp.getMax() - lstEsti.getAvg();
                if (dropPressureValue >= analyseMin && dropPressureValue <= analyseMax) {
                    testMainPressureValue = getTestPressureValue(lstTemp);
                    int count = 0;
                    for (int i : lstEsti.lstValue) {
                        if (i <= lstEsti.getAvg()) {
                            count++;
                            break;
                        }
                    }
                    startTime = lstPressureValue.get(lstPressureValue.size() - lstEsti.lstValue.size() + count - 1).getPressureTime();
                    testDropValue = (testMainPressureValue - lstPressureValue.get(lstPressureValue.size() - lstEsti.lstValue.size() + count - 1).getPressureValue());
                    testPressureValueMax = lstPressureValue.get(lstPressureValue.size() - lstEsti.lstValue.size() + count - 1).getPressureValue();
                    stat = TestState.tsDoing;
                    xBeginPos = lstPressureValue.size() - lstEsti.lstValue.size() + count - 1;
                    yPressureValue = nPressureValue;
                    TipSoundPlayer.PlayVoicePrompts(voiceFileNameBegin);
                }
            } else {
                XLog.d("试验(" + getTestName() + ")不符合开始条件："+lstEsti.getMax()+"-"+lstEsti.getMin()+"<="+SysParamsAll.get_fluxRange()+"&&"+lstTemp.getMax()+"-"+lstEsti.getMax()+">"+SysParamsAll.get_fluxRange());
            }
            return this;
        } else if (stat == TestState.tsDoing) {
            setEndTime(stime);
            xEndPos = lstPressureValue.size() - 1;
            if (testKeepTime <= standardKeepTime)
            {
                XLog.d("试验(" + getTestName() + ")计算漏泄量以及判断试验是否结束");
                updateTestLeakValue(testPressureValueMax - nPressureValue);
                if (nPressureValue - lstEsti.getAvg() >= SysParamsAll.get_reliefRange())
                {
                    XLog.d("试验(" + getTestName() + ")未到保压时间试验结束:zg"+testMainPressureValue+"keeptime"+testKeepTime+"dropvalue"+testDropValue+"leakvalue"+ getTestLeakValueDisplay());
                    stat = TestState.tsStoped;
                    if (validateTestSuccess()) {
                        testResult = TEST_STATE_COMPLETED;
                        TipSoundPlayer.PlayVoicePrompts(voiceFileNameCompleted);
                    } else {
                        testResult = TEST_STATE_NOT_COMPLETED;
                        if (null != onTestFailedListener) {
                            onTestFailedListener.onTestFailed();
                        }
                        TipSoundPlayer.PlayVoicePrompts(voiceFileNameNotCompleted);
                    }
                }
            } else {
                XLog.d("试验(" + getTestName() + ")超过保压时间试验结束:zg="+testMainPressureValue+",keeptime="+testKeepTime+",standardKeepTime="+standardKeepTime+",dropvalue="+testDropValue+",leakvalue="+ getTestLeakValueDisplay());
                stat = TestState.tsNotSelected;
                if (testResult != TEST_STATE_COMPLETED && testResult != TEST_STATE_NOT_COMPLETED) {
                    if (validateTestSuccess()) {
                        testResult = TEST_STATE_COMPLETED;
                        //修正保压时间为标准保压 时间
                        if (standardKeepTime > 0) {
                            testKeepTime = standardKeepTime;
                        }
                        TipSoundPlayer.PlayVoicePrompts(voiceFileNameCompleted);
                    } else {
                        testResult = TEST_STATE_NOT_COMPLETED;
                        TipSoundPlayer.PlayVoicePrompts(voiceFileNameNotCompleted);
                    }
                }
            }
            return this;
        } else if (stat == TestState.tsNotSelected) {
            if (nPressureValue - lstEsti.getAvg() >= SysParamsAll.get_reliefRange())
            {
                XLog.d("试验(" + getTestName() + ")失败:" + testResult);
                stat = TestState.tsStoped;
                if (TEST_NAME_GD.equals(getTestName())) {
                }
                if (TEST_STATE_NOT_COMPLETED.equals(testResult)) {
                    if (null != onTestFailedListener) {
                        XLog.d("试验(" + getTestName() + ")调用试验失败处理");
                        onTestFailedListener.onTestFailed();
                    }
                }
            }
            return this;
        } else {
            XLog.i("试验(" + getTestName() + ")状态" + stat + "未处理。");
            return null;
        }
    }

    public void drawSelf(Canvas canvas, ChartView chartView) {
        if (!chartView.validTestViewInRange(xBeginPos, xEndPos)) {
            //XLog.i("试验(" + getTestName() + ")chart显示范围不正确：begin=" + xBeginPos + "end=" + xEndPos);
            return;
        }
        String viewTestName = "";
        if (stat == TestState.tsDoing ) {
            viewTestName = String.valueOf(testKeepTime);
        } else {
            viewTestName = shortTestName;
        }
        chartView.drawTestRectangle(canvas, xBeginPos, xEndPos, yPressureValue, drawColor, viewTestName);
    }

    public void update(DaoSession daoSession, String testID){
        TestDetail testDetail = new TestDetail();
        testDetail.setTestID(testID);
        testDetail.setTestName(getTestName());
        testDetail.setBeginTime(getStartTime());
        testDetail.setEndTime(getEndTime());
        testDetail.setTestPressure(String.valueOf(getTestMainPressureValue()));
        testDetail.setKeepTime(String.valueOf(getTestKeepTime()));
        testDetail.setLeakValue(getTestLeakValueDisplay());
        testDetail.setDropValue(String.valueOf(getTestDropValue()));
        testDetail.setState(getTestResult());
        daoSession.getTestDetailDao().insert(testDetail);
    }

    public boolean load(String testIndex){
        try{
            DaoSession daoSession = WindTestApplication.getWindTestInstance().getDaoSession();
            List<TestDetail> detailList = daoSession.getTestDetailDao().queryBuilder().where(TestDetailDao.Properties.TestID.eq(testIndex),TestDetailDao.Properties.TestName.eq(getTestName())).list();
            if (null != detailList && detailList.size() > 0){
                TestDetail testDetail = detailList.get(0);
                setStartTime(testDetail.getBeginTime());
                setEndTime(testDetail.getEndTime());
                setTestMainPressureValue(Integer.parseInt(testDetail.getTestPressure()));
                setTestDropValue(Integer.parseInt(testDetail.getDropValue()));
                setTestLeakValueFromDisplay(testDetail.getLeakValue());
                setTestKeepTime(Integer.parseInt(testDetail.getKeepTime()));
                setTestResult(testDetail.getState());
                xBeginPos = -1;
                xEndPos = -1;
                yPressureValue = 0;
                return true;
            } else {
                XLog.e("试验明细查询无数据。");
            }
        } catch (Exception ex){
            XLog.e("试验明细加载异常：" + ex);
        }
        return false;
    }

    public void setOnTestFailedListener(OnTestFailedListener onTestFailedListener) {
        this.onTestFailedListener = onTestFailedListener;
    }

    public TestState getStat() {
        return stat;
    }

    public void setStat(TestState stat) {
        this.stat = stat;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getEndTime() {
        return endTime;
    }

    /**
     * 设置结束时间时，自动计算保压时间
     *
     * @param endTime
     */
    public void setEndTime(String endTime) {
        this.endTime = endTime;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date timeStart = dateFormat.parse("2019-06-18 " + this.startTime);
            Date timeEnd = dateFormat.parse("2019-06-18 " + endTime);
            if (timeEnd.getTime() < timeStart.getTime()) {
                timeEnd = dateFormat.parse("2019-06-19 " + endTime);
            }
            long timeDiff = timeEnd.getTime() - timeStart.getTime();
            this.testKeepTime = (int) (timeDiff / 1000);
        } catch (Exception ex) {
            XLog.w("Set end time exception,set testKeepTime=0,ex=" + ex.getMessage());
            this.testKeepTime = 0;
        }
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public int getTestMainPressureValue() {
        return testMainPressureValue;
    }

    public void setTestMainPressureValue(int testMainPressureValue) {
        this.testMainPressureValue = testMainPressureValue;
    }

    public int getTestDropValue() {
        return testDropValue;
    }

    public void setTestDropValue(int testDropValue) {
        this.testDropValue = testDropValue;
    }


    public int getTestKeepTime() {
        return testKeepTime;
    }

    public void setTestKeepTime(int testKeepTime) {
        this.testKeepTime = testKeepTime;
    }

    public String getTestResult() {
        return testResult;
    }

    public void setTestResult(String testResult) {
        this.testResult = testResult;
    }

    public int getTestLeakValue() {
        return testLeakValue;
    }

    public void setTestLeakValue(int testLeakValue) {
        this.testLeakValue = testLeakValue;
    }

    public int getxBeginPos() {
        return xBeginPos;
    }

    public void setxBeginPos(int xBeginPos) {
        this.xBeginPos = xBeginPos;
    }

    public void setxEndPos(int xEndPos) {
        this.xEndPos = xEndPos;
    }

    public void setyPressureValue(int yPressureValue) {
        this.yPressureValue = yPressureValue;
    }

    public int getViewStatMainPressure() {
        return viewStatMainPressure;
    }

    public void setViewStatMainPressure(int viewStatMainPressure) {
        this.viewStatMainPressure = viewStatMainPressure;
    }

    public int getViewStatKeepTime() {
        return viewStatKeepTime;
    }

    public void setViewStatKeepTime(int viewStatKeepTime) {
        this.viewStatKeepTime = viewStatKeepTime;
    }

    public int getViewStatDropValue() {
        return viewStatDropValue;
    }

    public void setViewStatDropValue(int viewStatDropValue) {
        this.viewStatDropValue = viewStatDropValue;
    }

    public int getViewStatLeakValue() {
        return viewStatLeakValue;
    }

    public void setViewStatLeakValue(int viewStatLeakValue) {
        this.viewStatLeakValue = viewStatLeakValue;
    }

    public int getWcDingYa() {
        return wcDingYa;
    }

    public void setWcDingYa(int wcDingYa) {
        this.wcDingYa = wcDingYa;
    }

    public int getDefinedPressureValue() {
        return definedPressureValue;
    }

    public void setDefinedPressureValue(int definedPressureValue) {
        this.definedPressureValue = definedPressureValue;
    }

    public int getAnalyseMax() {
        return analyseMax;
    }

    public void setAnalyseMax(int analyseMax) {
        this.analyseMax = analyseMax;
    }

    public int getStandardKeepTime() {
        return standardKeepTime;
    }

    public void setStandardKeepTime(int standardKeepTime) {
        this.standardKeepTime = standardKeepTime;
    }

    public int getStandardDrop() {
        return standardDrop;
    }

    public void setStandardDrop(int standardDrop) {
        this.standardDrop = standardDrop;
    }

    public int getStandardLeak() {
        return standardLeak;
    }

    public void setStandardLeak(int standardLeak) {
        this.standardLeak = standardLeak;
    }

    public int getWcBaoYa() {
        return wcBaoYa;
    }

    public void setWcBaoYa(int wcBaoYa) {
        this.wcBaoYa = wcBaoYa;
    }

    public int getWcJianYa() {
        return wcJianYa;
    }

    public void setWcJianYa(int wcJianYa) {
        this.wcJianYa = wcJianYa;
    }

    public int getWcLouXie() {
        return wcLouXie;
    }

    public void setWcLouXie(int wcLouXie) {
        this.wcLouXie = wcLouXie;
    }

    public String getTestLeakValueDisplay() {
        return String.valueOf(testLeakValue);
    }

    public void setTestLeakValueFromDisplay(String testLeakValueEx) {
        int inConvertValue = 0;
        try{
            inConvertValue = Integer.parseInt(testLeakValueEx);
        } catch (Exception ex) {
            inConvertValue = 0;
            XLog.w("漏泄量("+ testLeakValueEx +")转换为整数失败。");
        }
        this.testLeakValue = inConvertValue;
    }

    public boolean isPressureOverflowFlag() {
        return pressureOverflowFlag;
    }

    public int getPressureOverflowCountLimit() {
        return pressureOverflowCountLimit;
    }

    public int getPressureOverflowNumLimit() {
        return pressureOverflowNumLimit;
    }
}
