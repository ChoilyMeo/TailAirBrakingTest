package com.thnet.tailairbrakingtest.TestWind;

import com.elvishew.xlog.XLog;
import com.thnet.tailairbrakingtest.Communication.CTestWindProtocel;
import com.thnet.tailairbrakingtest.DAO.PressureValue;
import com.thnet.tailairbrakingtest.Utility.TipSoundPlayer;

import java.util.ArrayList;
import java.util.List;

public class Test_YLJYBase extends TestContent {
    private static final int PARAM_MAX_LIST_COUNT = 10;
    private boolean bDidTest = false;
    protected List<PressureValue> lstTestPressure;
    public Test_YLJYBase(){
        _testName = CStrTestName_KSJY;
        _shortTestName = CStrTestName_KSJY;
        _voiceFileNameBegin = TipSoundPlayer.nVoiceFileNameBegin_yljy;
        _voiceFileNameCompleted = TipSoundPlayer.nVoiceFileNameCompleted_yljy;
        _voiceFileNameNotCompleted = TipSoundPlayer.nVoiceFileNameNotCompleted_yljy;
        _TestCommandCode = CTestWindProtocel.TestCommand.PressureCheck;
        _analyseMax = SysParamsAll.get_jlAnalyseMax();
        _analyseMin = SysParamsAll.get_jlAnalyseMin();
        _standardKeepTime = 0;
        _standardDrop = SysParamsAll.get_jlStandardDrop();
        _standardLeak = 10000;
        bDidTest = false;
        lstTestPressure = new ArrayList<PressureValue>(0);
    }

    public Test_YLJYBase(int specifiedPressure, int trainCount, TestState state){
        this();
        SetParms(specifiedPressure, trainCount);
        this._stat = state;
    }

    @Override
    public void Reset() {
        super.Reset();
        lstTestPressure.clear();
        bDidTest = false;
    }

    @Override
    public TestContent CheckStatus(String stime, PressureValue pd, CTestWindProtocel.TestStatus testStatus, int DropSpeed) {
        if (testStatus == CTestWindProtocel.TestStatus.TestBegin) {
            XLog.i(get_testName() + "：试验开始。");
            DoTestBegin();
        } else if (testStatus == CTestWindProtocel.TestStatus.TestDoing) {
            XLog.i(get_testName() + "：充风缓解完毕。");
            if (_stat == TestState.tsNotBegin){
                DoTestBegin();
            }
            bDidTest = false;
        } else if (testStatus == CTestWindProtocel.TestStatus.TestDropPressureEnd) {
            XLog.i(get_testName() + "：减压结束。");
            if (!bDidTest && lstTestPressure.size() < PARAM_MAX_LIST_COUNT){
                lstTestPressure.add(pd);
                bDidTest = true;
            }
        } else if (testStatus == CTestWindProtocel.TestStatus.TestEnd) {
            if (_stat == TestState.tsDoing) {
                XLog.i(get_testName() + "：试验结束。");
                set_endTime(stime);
                _stat = TestState.tsStoped;
                _state = CStrTestStateCompleted;
                TipSoundPlayer.PlayVoicePrompts(_voiceFileNameCompleted);
            }
        } else {
            XLog.i(get_testName() + "：试验状态未处理：" + String.valueOf(testStatus));
        }
        updateViewContentInfo();
        return this;
    }

    @Override
    public void initViewContentList() {
        _listViewContent.add(new TestViewContent("", "", "", "", ""));
        _listViewContent.add(new TestViewContent(VIEW_ROW_NAME_PRESSURE_ALL, "", "", "", ""));
        _listViewContent.add(new TestViewContent(VIEW_ROW_NAME_PRESSURE_TRAIN, "", "", "", ""));
        _listViewContent.add(new TestViewContent(VIEW_ROW_NAME_PRESSURE_CENTER, "", "", "", ""));
        _listViewContent.add(new TestViewContent(VIEW_ROW_NAME_PRESSURE_STANDARD, "", "", "", ""));
    }

    /**
     * 从列表中取列对应列的数据索引
     * 第一列：0、4、8、12、16
     * 第二列：1、5、9、13、17
     * 第三列：2、6、10、14、18
     * 第四列：3、7、11、15、19
     * @param col 列数：只能是1、2、3、4四个值
     * @return 如果列表的长度小于列最小的值，则返回空白字符串
     */
    protected int getColoumnIndex(int col, int maxPos){
        if (maxPos < col - 1 || maxPos < 0){
            return -1;
        } else {
            return (maxPos + 5 - col) / 4 * 4 - 5 + col;
        }
    }

    @Override
    public void updateViewContentInfo() {
        int lstCount = lstTestPressure.size();
        if (lstCount <= 0) {
            return;
        }
        int indexCol1 = getColoumnIndex(1, lstCount - 1);
        int indexCol2 = getColoumnIndex(2, lstCount - 1);
        int indexCol3 = getColoumnIndex(3, lstCount - 1);
        int indexCol4 = getColoumnIndex(4, lstCount - 1);
        for(TestViewContent testViewContent : _listViewContent){
            if (testViewContent.getColumn1().equals("")){
                testViewContent.setColumn2(indexCol1 < 0 ? "" : String.valueOf(indexCol1));
                testViewContent.setColumn3(indexCol2 < 0 ? "" : String.valueOf(indexCol2));
                testViewContent.setColumn4(indexCol3 < 0 ? "" : String.valueOf(indexCol3));
                testViewContent.setColumn5(indexCol4 < 0 ? "" : String.valueOf(indexCol4));
            } else if (testViewContent.getColumn1().equals(VIEW_ROW_NAME_PRESSURE_ALL)){
                testViewContent.setColumn2(indexCol1 < 0 ? "" : String.valueOf(lstTestPressure.get(indexCol1).getSourcePressureValue()));
                testViewContent.setColumn3(indexCol2 < 0 ? "" : String.valueOf(lstTestPressure.get(indexCol2).getSourcePressureValue()));
                testViewContent.setColumn4(indexCol3 < 0 ? "" : String.valueOf(lstTestPressure.get(indexCol3).getSourcePressureValue()));
                testViewContent.setColumn5(indexCol4 < 0 ? "" : String.valueOf(lstTestPressure.get(indexCol4).getSourcePressureValue()));
            } else if (testViewContent.getColumn1().equals(VIEW_ROW_NAME_PRESSURE_TRAIN)){
                testViewContent.setColumn2(indexCol1 < 0 ? "" : String.valueOf(lstTestPressure.get(indexCol1).getHeadPressureValue()));
                testViewContent.setColumn3(indexCol2 < 0 ? "" : String.valueOf(lstTestPressure.get(indexCol2).getHeadPressureValue()));
                testViewContent.setColumn4(indexCol3 < 0 ? "" : String.valueOf(lstTestPressure.get(indexCol3).getHeadPressureValue()));
                testViewContent.setColumn5(indexCol4 < 0 ? "" : String.valueOf(lstTestPressure.get(indexCol4).getHeadPressureValue()));
            } else if (testViewContent.getColumn1().equals(VIEW_ROW_NAME_PRESSURE_CENTER)){
                testViewContent.setColumn2(indexCol1 < 0 ? "" : String.valueOf(lstTestPressure.get(indexCol1).getCenterPressureValue()));
                testViewContent.setColumn3(indexCol2 < 0 ? "" : String.valueOf(lstTestPressure.get(indexCol2).getCenterPressureValue()));
                testViewContent.setColumn4(indexCol3 < 0 ? "" : String.valueOf(lstTestPressure.get(indexCol3).getCenterPressureValue()));
                testViewContent.setColumn5(indexCol4 < 0 ? "" : String.valueOf(lstTestPressure.get(indexCol4).getCenterPressureValue()));
            } else if (testViewContent.getColumn1().equals(VIEW_ROW_NAME_PRESSURE_STANDARD)){
                testViewContent.setColumn2(indexCol1 < 0 ? "" : String.valueOf(lstTestPressure.get(indexCol1).getTestResult()));
                testViewContent.setColumn3(indexCol2 < 0 ? "" : String.valueOf(lstTestPressure.get(indexCol2).getTestResult()));
                testViewContent.setColumn4(indexCol3 < 0 ? "" : String.valueOf(lstTestPressure.get(indexCol3).getTestResult()));
                testViewContent.setColumn5(indexCol4 < 0 ? "" : String.valueOf(lstTestPressure.get(indexCol4).getTestResult()));
            }
        }
    }

    @Override
    public @TestViewTypes int get_testViewType() {
        return V_CALIBRATION;
    }
}
