package com.thnet.tailairbrakingtest.testwind;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

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
    static final String TEST_NAME_GD = "感度保压";
    public static final String TEST_NAME_JL = "简略试验";
    public static final String TEST_NAME_LX = "漏泄试验";
    static final String SHORT_TEST_NAME_AD = "安定";
    static final String SHORT_TEST_NAME_GD = "感度";
    static final String SHORT_TEST_NAME_JL = "简略";
    static final String SHORT_TEST_NAME_LX = "漏泄";
    public static final String TEST_STATE_COMPLETED = "完成";
    public static final String TEST_STATE_NOT_COMPLETED = "已作业";
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
    protected int testLeakValue = 0;//漏泄量
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

    private int getTestPressureValue(CEstimate lstTemp) {
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

    public TestContent checkStatus(String stime, int nPressureValue, CEstimate lstEsti, CEstimate lstTemp, List<PressureValue> lstPressureValue) {
        if (stat == TestState.tsNotBegin) {
            if (lstEsti.getMax() - lstEsti.getMin() <= SysParamsAll.get_fluxRange() && lstTemp.getMax() - lstEsti.getMin() > SysParamsAll.get_fluxRange()) {
                XLog.d("试验开始");
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
                XLog.d("不符合开始条件："+lstEsti.getMax()+"-"+lstEsti.getMin()+"<="+SysParamsAll.get_fluxRange()+"&&"+lstTemp.getMax()+"-"+lstTemp.getMin()+">"+SysParamsAll.get_fluxRange());
            }
            return this;
        } else if (stat == TestState.tsDoing) {
            setEndTime(stime);
            xEndPos = lstPressureValue.size() - 1;
            if (testKeepTime <= standardKeepTime)
            {
                XLog.d("计算漏泄量以及判断试验是否结束");
                if (testKeepTime <= 60)
                {
                    int lxl = testPressureValueMax - nPressureValue;
                    if (lxl < 0) {
                        lxl = 0;
                    }
                    this.testLeakValue = lxl;
                }
                if (nPressureValue - lstEsti.getAvg() >= SysParamsAll.get_reliefRange())
                {
                    XLog.d("未到保压时间试验结束:zg"+testMainPressureValue+"keeptime"+testKeepTime+"dropvalue"+testDropValue+"leakvalue"+testLeakValue);
                    stat = TestState.tsStoped;
                    if (testMainPressureValue >= definedPressureValue - SysParamsAll.get_wcDingYa()
                            && testMainPressureValue <= definedPressureValue + SysParamsAll.get_wcDingYa()
                            && testKeepTime >= standardKeepTime
                            && testDropValue >= standardDrop - SysParamsAll.get_wcJianYa()
                            && testDropValue <= standardDrop + SysParamsAll.get_wcJianYa()
                            && testLeakValue < standardLeak + SysParamsAll.get_wcLouXie()) {
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
                XLog.d("超过保压时间试验结束:zg"+testMainPressureValue+"keeptime"+testKeepTime+"dropvalue"+testDropValue+"leakvalue"+testLeakValue);
                stat = TestState.tsNotSelected;
                if (testResult != TEST_STATE_COMPLETED && testResult != TEST_STATE_NOT_COMPLETED) {
                    if (testMainPressureValue >= definedPressureValue - SysParamsAll.get_wcDingYa()
                            && testMainPressureValue <= definedPressureValue + SysParamsAll.get_wcDingYa()
                            && testKeepTime >= standardKeepTime
                            && testDropValue >= standardDrop - SysParamsAll.get_wcJianYa()
                            && testDropValue <= standardDrop + SysParamsAll.get_wcJianYa()
                            && testLeakValue < standardLeak + SysParamsAll.get_wcLouXie()) {
                        testResult = TEST_STATE_COMPLETED;
                        //修正保压时间为标准保压 时间
                        testKeepTime = testKeepTime < standardKeepTime ? standardKeepTime : testKeepTime;
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
                XLog.d(testName + "试验失败:" + testResult);
                stat = TestState.tsStoped;
                if (TEST_NAME_GD.equals(getTestName())) {
                }
                if (TEST_STATE_NOT_COMPLETED.equals(testResult)) {
                    if (null != onTestFailedListener) {
                        XLog.d("调用试验失败处理");
                        onTestFailedListener.onTestFailed();
                    }
                }
            }
            return this;
        } else {
            XLog.i(getTestName() + "试验状态" + stat + "未处理。");
            return null;
        }
    }

    public void drawSelf(Canvas canvas, ChartView chartView) {
        if (!chartView.validTestViewInRange(xBeginPos, xEndPos)) {
            return;
        }
        Paint textPaint = chartView.getTextPaint();
        textPaint.setColor(drawColor);
        Paint linePaint = chartView.getChartLinePaint();
        linePaint.setColor(drawColor);
        int lineWidth = (int)linePaint.getStrokeWidth();
        int textHeight = 0 - (textPaint.getFontMetricsInt().top - textPaint.getFontMetricsInt().bottom);
        Rect testRect = new Rect(chartView.convertDataValuetoXpos(xBeginPos), (int)chartView.convertDataValueToYpos(yPressureValue) - textHeight / 2,
                chartView.convertDataValuetoXpos(xEndPos) + lineWidth * 2, (int)chartView.convertDataValueToYpos(yPressureValue) + textHeight / 2 + lineWidth * 2);
        canvas.drawRect(testRect, linePaint);
        String viewTestName = "";
        if (stat == TestState.tsDoing ) {
            viewTestName = String.valueOf(testKeepTime);
        } else {
            viewTestName = shortTestName;
        }
        canvas.drawText(viewTestName, testRect.left + lineWidth, testRect.top - textPaint.getFontMetricsInt().top + lineWidth, textPaint);
    }

    public void update(DaoSession daoSession, String testID){
        TestDetail testDetail = new TestDetail();
        testDetail.setTestID(testID);
        testDetail.setTestName(getTestName());
        testDetail.setBeginTime(getStartTime());
        testDetail.setEndTime(getEndTime());
        testDetail.setTestPressure(String.valueOf(getTestMainPressureValue()));
        testDetail.setKeepTime(String.valueOf(getTestKeepTime()));
        testDetail.setLeakValue(String.valueOf(getTestLeakValue()));
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
                setTestLeakValue(Integer.parseInt(testDetail.getLeakValue()));
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
                timeEnd = dateFormat.parse("2019-06-19 " + timeEnd);
            }
            long timeDiff = timeEnd.getTime() - timeStart.getTime();
            this.testKeepTime = (int) (timeDiff / 1000);
        } catch (Exception ex) {
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
}
