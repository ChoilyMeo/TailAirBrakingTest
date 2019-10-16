package com.thnet.tailairbrakingtest.testwind;

import android.database.Cursor;
import android.util.Log;

import com.elvishew.xlog.XLog;
import com.thnet.tailairbrakingtest.customapplication.WindTestApplication;
import com.thnet.tailairbrakingtest.dao.DaoSession;
import com.thnet.tailairbrakingtest.dao.PressureValue;
import com.thnet.tailairbrakingtest.dao.PressureValueDao;
import com.thnet.tailairbrakingtest.dao.TestWindContent;
import com.thnet.tailairbrakingtest.dao.TestWindContentDao;
import com.thnet.tailairbrakingtest.utility.StringUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * 试验数据
 * @author mzl
 */
public class TestData {
    private static final String TAG = TestData.class.getSimpleName();

    public Test_GD testGd;
    public Test_AD testAd;
    public Test_JL testJl;
    public List<PressureValue> lstPressureValue = new ArrayList<PressureValue>();
    public List<TestContent> listTest = new ArrayList<TestContent>();
    private TestWindContent mTestContent = new TestWindContent();

    private SimpleDateFormat formatterDate = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat formatterTime = new SimpleDateFormat("HH:mm:ss");

    public TestData() {
        testGd = new Test_GD();
        testAd = new Test_AD();
        testJl = new Test_JL();
        listTest = new ArrayList<TestContent>(0);
        listTest.add(testGd);
        listTest.add(testAd);
        listTest.add(testJl);
        lstPressureValue = new ArrayList<PressureValue>(0);
        setTestDate(formatterDate.format(new Date()));
    }

    public void reset() {
        lstPressureValue.clear();
        for (TestContent testContent : listTest){
            testContent.reset();
            testContent.setStat(TestContent.TestState.tsNotSelected);
        }
    }

    public void beginTest() {
        Date curDate = new Date();
        setTestDate(formatterDate.format(curDate));
        setStartTime(formatterTime.format(curDate));
    }

    public void endTest() {
        setEndTime(formatterTime.format(new Date()));
    }

    public TestData(String line, String trainNo, String trainCount, String specifyPressureValue, String testKind) {
        this();
        setTrackNo(trainNo);
        setTrackNo(line);
        setTrainCount(trainCount);
        setSpecifiedPressureValue(specifyPressureValue);
        setTestKind(testKind);
    }

    public TestData(String testIndex, String line, String trainNo, String trainCount, String specifyPressureValue, String testKind) {
        this(line, trainNo, trainCount, specifyPressureValue, testKind);
        setTestIndex(testIndex);
    }

    public boolean loadData(String testIndex){
        try {
            //从数据库中加载数据
            DaoSession daoSession = WindTestApplication.getWindTestInstance().getDaoSession();
            List<TestWindContent> windContentList = daoSession.getTestWindContentDao().queryBuilder().where(TestWindContentDao.Properties.TestID.eq(testIndex)).list();
            if (null != windContentList && windContentList.size() > 0){
                mTestContent = windContentList.get(0);
                for(TestContent testContent : listTest){
                    if (!testContent.load(testIndex)){
                        return false;
                    }
                }
                lstPressureValue = daoSession.getPressureValueDao().queryBuilder().where(PressureValueDao.Properties.TestID.eq(testIndex)).list();
                if (null == lstPressureValue || lstPressureValue.size() <= 0){
                    lstPressureValue = new ArrayList<PressureValue>(0);
                    return false;
                } else {
                    int index = 0;
                    for (PressureValue pressureValue : lstPressureValue){
                        for (TestContent testContent : listTest){
                            if (pressureValue.getPressureTime().equals(testContent.getStartTime())){
                                testContent.setxBeginPos(index);
                                testContent.setyPressureValue(pressureValue.getPressureValue());
                            }
                            if (testContent.getxBeginPos() > 0 && pressureValue.getPressureTime().equals(testContent.getEndTime())){
                                testContent.setxEndPos(index);
                            }
                        }
                        index ++;
                    }
                }
                return true;
            } else {
                XLog.i(TAG + "查询试验数据为空");
            }
        } catch (Exception ex){
            XLog.e(TAG + "加载试验数据异常：" + ex);
        }
        return false;
    }

    private boolean insertToDatabase(){
        try {
            final DaoSession daoSession = WindTestApplication.getWindTestInstance().getDaoSession();
            daoSession.getTestWindContentDao().insert(mTestContent);
            for(TestContent testContent : listTest){
                testContent.update(daoSession, getTestIndex());
            }
            for(PressureValue pressureValue : lstPressureValue){
                pressureValue.setTestID(getTestIndex());
                daoSession.getPressureValueDao().insert(pressureValue);
            }
            daoSession.clear();
            return true;
        } catch (Exception ex) {
            XLog.e("数据插入数据库异常：" + ex.getMessage());
        }
        return false;
    }

    public boolean update(){
        if (null == getTestIndex() || getTestIndex().isEmpty())
        {
            //数据的插入操作
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            String dateStr = dateFormat.format(new Date());
            String strBH = dateStr + "0001";
            String sql = "select max(" + TestWindContentDao.Properties.TestID.columnName + ") as MAXBH from " + TestWindContentDao.TABLENAME + " where " + TestWindContentDao.Properties.TestID.columnName + " like '" + dateStr + "%'";
            try{
                DaoSession daoSession = WindTestApplication.getWindTestInstance().getDaoSession();
                Cursor cursor = daoSession.getDatabase().rawQuery(sql, null);
                if (null != cursor && cursor.getCount() > 0){
                    cursor.moveToFirst();
                    if (!cursor.isNull(0)) {
                        String maxBH = cursor.getString(0);
                        int nBH = Integer.parseInt(maxBH.substring(maxBH.length() - 4, maxBH.length())) + 1;
                        strBH = dateStr + String.format("%04d", nBH);
                    }
                    cursor.close();
                }
                setTestIndex(strBH);
                if (!daoSession.callInTx(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return insertToDatabase();
                    }
                })){
                    setTestIndex("");
                } else {
                    return true;
                }
            } catch (Exception ex){
                setTestIndex("");
                XLog.e(TAG + "数据更新异常：" + ex.getMessage());
            }
        }
        else
        {
            XLog.i(TAG + "数据已经插入，试风编号：" + getTestIndex());
        }
        return false;
    }

    public String getTestIndex() {
        return mTestContent.getTestID();
    }

    public void setTestIndex(String testIndex) {
        mTestContent.setTestID(testIndex);
    }

    public String getTestDate() {
        return mTestContent.getTestDate();
    }

    public void setTestDate(String testDate) {
        mTestContent.setTestDate(testDate);
    }

    public String getStartTime() {
        return mTestContent.getStartTime();
    }

    public void setStartTime(String startTime) {
        mTestContent.setStartTime(startTime);
    }

    public String getEndTime() {
        return mTestContent.getEndTime();
    }

    public void setEndTime(String endTime) {
        mTestContent.setEndTime(endTime);
    }

    public String getTrainNo() {
        return mTestContent.getTrainNo();
    }

    public void setTrainNo(String trainNo) {
        mTestContent.setTrainNo(trainNo);
    }

    public String getTrackNo() {
        return mTestContent.getLine();
    }

    public void setTrackNo(String trackNo) {
        mTestContent.setLine(trackNo);
    }

    public int getTrackNoInt() {
        int n = 0;
        try {
            n = Integer.parseInt(getTrackNo());
        } catch (Exception ex) {
            XLog.i(TAG + "股道转换失败。");
            n = 0;
        }
        return n;
    }

    public String getTrainCount() {
        return mTestContent.getTrainCount();
    }

    public void setTrainCount(String trainCount) {
        mTestContent.setTrainCount(trainCount);
    }

    public int getTrainCountInt() {
        int n = 0;
        try {
            n = Integer.parseInt(getTrainCount());
        } catch (Exception ex) {
            XLog.i(TAG + "辆数转换失败");
            n = 0;
        }
        return n;
    }

    public String getSpecifiedPressureValue() {
        return mTestContent.getSpecifyPressure();
    }

    public void setSpecifiedPressureValue(String specifiedPressureValue) {
        mTestContent.setSpecifyPressure(specifiedPressureValue);
    }

    public int getSpecifyPressureValueInt() {
        int n = 0;
        try {
            n = Integer.parseInt(getSpecifiedPressureValue());
        } catch (Exception ex) {
            XLog.i(TAG + "定压转换失败。");
            n = 0;
        }
        return n;
    }

    public String getTestKind() {
        return mTestContent.getTestKind();
    }

    public void setTestKind(String testKind) {
        mTestContent.setTestKind(testKind);
    }
}
