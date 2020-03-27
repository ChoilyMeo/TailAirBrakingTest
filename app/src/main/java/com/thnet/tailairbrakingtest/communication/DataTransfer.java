package com.thnet.tailairbrakingtest.communication;

import android.util.Log;

import com.elvishew.xlog.XLog;
import com.thnet.tailairbrakingtest.dao.PressureValue;
import com.thnet.tailairbrakingtest.dao.TestKind;
import com.thnet.tailairbrakingtest.serialport.OnOpenSerialPortListener;
import com.thnet.tailairbrakingtest.serialport.OnSerialPortDataListener;
import com.thnet.tailairbrakingtest.serialport.SerialPortManager;
import com.thnet.tailairbrakingtest.testwind.CEstimate;
import com.thnet.tailairbrakingtest.testwind.SysParamsAll;
import com.thnet.tailairbrakingtest.testwind.TestContent;
import com.thnet.tailairbrakingtest.testwind.TestData;
import com.thnet.tailairbrakingtest.utility.HexUtil;
import com.thnet.tailairbrakingtest.utility.TipSoundPlayer;

import java.io.File;
import java.util.ArrayList;

import static com.thnet.tailairbrakingtest.communication.OnDataReceiveSendListener.RECV_DATA;

/**
 * 串口数据收发处理
 * @author mzl
 */
public class DataTransfer implements OnSerialPortDataListener, OnOpenSerialPortListener {
    private static final String TAG = DataTransfer.class.getSimpleName();
    private static final int MIN_ALLOWABLE_PRESSURE_VALUE = 50;
    public SerialPortManager serialPortManager;
    public TestData tData;
    public CEstimate lstEsti = new CEstimate(SysParamsAll.get_EstiDataLen());
    public CEstimate lstTemp = new CEstimate(SysParamsAll.get_TempDateLen());
    public TestContent currTest = null;
    public TestContent failedTest = null;

    private int nLastListCount = 0;
    private int nKeepedTimes = 0;
    ArrayList<Byte> totalReceiveBuffer = new ArrayList<Byte>(0);
    private OnDataReceiveSendListener dataReceiveSendListener;

    public DataTransfer() {
        portInit();
        tData = new TestData();
    }

    public DataTransfer(String line, String trainNo, String trainCount, String specifyPressureValue, String testKind) {
        this();
        tData.setTrackNo(line);
        tData.setTrainNo(trainNo);
        tData.setTrainCount(trainCount);
        tData.setSpecifiedPressureValue(specifyPressureValue);
        tData.setTestKind(testKind);
    }

    public void setDataReceiveSendListener(OnDataReceiveSendListener dataReceiveSendListener) {
        this.dataReceiveSendListener = dataReceiveSendListener;
    }

    public void startWatch(String line, String trainNo, String trainCount, String specifyPressureValue, TestKind testKindName) {
        tData.reset();
        tData.setTrackNo(line);
        tData.setTrainNo(trainNo);
        tData.setTrainCount(trainCount);
        tData.setSpecifiedPressureValue(specifyPressureValue);
        tData.setTestKind(testKindName.getTestKindName());
        CTestWindProtocel.createSendCmd(SysParamsAll.get_modalChanelNo(), SysParamsAll.get_deviceNo(), tData.getTrackNoInt());
        if (testKindName.isTestLXChecked()){
            tData.testLx.setParms(tData.getSpecifyPressureValueInt(), tData.getTrainCountInt());
            tData.testLx.setStat(TestContent.TestState.tsNotBegin);
        }
        if (testKindName.isTestADChecked()){
            tData.testAd.setParms(tData.getSpecifyPressureValueInt(), tData.getTrainCountInt());
            tData.testAd.setStat(TestContent.TestState.tsNotBegin);
        }
        if (testKindName.isTestGDChecked()){
            tData.testGd.setParms(tData.getSpecifyPressureValueInt(), tData.getTrainCountInt());
            tData.testGd.setStat(TestContent.TestState.tsNotBegin);
        }
        if (testKindName.isTestBYChecked()){
            tData.testBy.setParms(tData.getSpecifyPressureValueInt(), tData.getTrainCountInt());
            tData.testBy.setStat(TestContent.TestState.tsNotBegin);
        }
        if (testKindName.isTestJLChecked()){
            tData.testJl.setParms(tData.getSpecifyPressureValueInt(), tData.getTrainCountInt());
            tData.testJl.setStat(TestContent.TestState.tsNotBegin);
        }
        serialPortManager.openSerialPort(new File(SysParamsAll.get_portName()), SysParamsAll.get_baudRate());
    }

    public void startReplay(String testIndex){
        if (!tData.loadData(testIndex)) {
            tData.reset();
        }
    }

    public void stopWatch() {
        dataReceiveSendListener = null;
        if (null != serialPortManager) {
            serialPortManager.closeSerialPort();
            serialPortManager = null;
        }
    }

    private void portInit() {
        totalReceiveBuffer.clear();
        serialPortManager = new SerialPortManager();
        serialPortManager.setOnOpenSerialPortListener(this);
        serialPortManager.setOnSerialPortDataListener(this);
    }

    private void checkTestStatus(String stime, int nPressureValue) {
        if (lstEsti.getAvg() < tData.getSpecifyPressureValueInt() - SysParamsAll.get_allAnalyseMax() - 30) {
            XLog.d("压力评估平均值"+lstEsti.getAvg()+"小于试验最低压力值"+tData.getSpecifyPressureValueInt()+"-"+SysParamsAll.get_allAnalyseMax());
            return;
        }
        if (currTest == null || currTest.getStat() == TestContent.TestState.tsStoped) {
            if (currTest != null && TestContent.TEST_STATE_NOT_COMPLETED.equals(currTest.getTestResult())) {
                failedTest = currTest;
            } else {
                failedTest = null;
            }
            if (tData.testGd.getStat() == TestContent.TestState.tsNotBegin) {
                currTest = tData.testGd;
            } else if (tData.testAd.getStat() == TestContent.TestState.tsNotBegin) {
                currTest = tData.testAd;
            } else if (tData.testBy.getStat() == TestContent.TestState.tsNotBegin) {
                currTest = tData.testBy;
            } else if (tData.testJl.getStat() == TestContent.TestState.tsNotBegin) {
                currTest = tData.testJl;
            } else {
                XLog.i("所有试验未准备开始。");
                return;
            }
        }
        currTest = currTest.checkStatus(stime, nPressureValue, lstEsti, lstTemp, tData.lstPressureValue);
    }

    private void addBytesToList(ArrayList<Byte> src, byte[] dst) {
        for (byte b : dst) {
            src.add(b);
        }
    }

    private void processOnPressureValue(PressureValue pd) {
        if (tData.lstPressureValue.size() <= SysParamsAll.get_maxTestPoint() && !pd.getPressureTime().isEmpty() && pd.getPressureValue() >= MIN_ALLOWABLE_PRESSURE_VALUE) {
            String stime = pd.getPressureTime();
            if (tData.lstPressureValue.size() <= 0) {
                tData.beginTest();
                TipSoundPlayer.PlayVoicePrompts(TipSoundPlayer.VOICE_FILE_NAME_BEGIN_TEST_WIND);
            }
            tData.lstPressureValue.add(pd);
            lstEsti.add(pd.getPressureValue());
            lstTemp.add(pd.getPressureValue());
            checkTestStatus(stime, pd.getPressureValue());
        } else {
            XLog.i("压力值取点数量" + tData.lstPressureValue.size() + "大于设置允许最大值" + SysParamsAll.get_maxTestPoint() + "或者压力值" + pd.getPressureValue() + "小于允许最小值50");
        }
    }

    /**
     * 串口接收数据处理
     *
     * @param bytes 接收到的数据
     */
    @Override
    public void onDataReceived(byte[] bytes) {
        try {
            if (serialPortManager == null || !serialPortManager.IsOpen()) {
                XLog.e(TAG + "接收数据时串口未打开");
                return;
            }
            int protocolType;
            addBytesToList(totalReceiveBuffer, bytes);
            //防止一次收到多条数据，增加循环处理
            while (totalReceiveBuffer.size() >= CTestWindProtocel.RECV_PROTOCOL_DATA_LEN1 || totalReceiveBuffer.size() >= CTestWindProtocel.RECV_PROTOCOL_DATA_LEN2) {
                //整理列表，从协议报文头开始
                while (totalReceiveBuffer.size() > 0) {
                    int readByte = HexUtil.byteToUnsigned((byte) totalReceiveBuffer.get(0));
                    if (readByte != CTestWindProtocel.FLAG_RECV_PROTOCOL_HEAD_1 && readByte != CTestWindProtocel.FLAG_RECV_PROTOCOL_HEAD_2) {
                        totalReceiveBuffer.remove(0);
                    } else {
                        break;
                    }
                }
                //整理之后的报文长度为零，则继续接收后续字节内容
                if (totalReceiveBuffer.size() <= 0){
                    XLog.e(TAG + "接收的数据中未找到报文头，继续接收数据。");
                    break;
                }
                //根据报文头来区分不同的协议
                protocolType = HexUtil.byteToUnsigned((byte) totalReceiveBuffer.get(0));
                if (protocolType == CTestWindProtocel.FLAG_RECV_PROTOCOL_HEAD_1) {
                    if (totalReceiveBuffer.size() >= CTestWindProtocel.RECV_PROTOCOL_DATA_LEN1) {
                        if (null != dataReceiveSendListener) {
                            dataReceiveSendListener.onDataReceiveSend(RECV_DATA);
                        }
                        byte[] processBuffer = new byte[CTestWindProtocel.RECV_PROTOCOL_DATA_LEN1];
                        for (int i = 0; i < processBuffer.length && totalReceiveBuffer.size() > 0; i++) {
                            processBuffer[i] = (byte) totalReceiveBuffer.get(0);
                            totalReceiveBuffer.remove(0);
                        }
                        PressureValue pd = new PressureValue();
                        if (0 == CTestWindProtocel.splitPressureFromBuffer(processBuffer, processBuffer.length, tData.getTrackNoInt(), pd)) {
                            processOnPressureValue(pd);
                        }
                    } else {//接收数据长度不到协议指定长度，继续接收数据下次处理
                        XLog.i(TAG + " buffer length" + totalReceiveBuffer.size() + "less than C0 data length" + CTestWindProtocel.RECV_PROTOCOL_DATA_LEN1);
                        break;
                    }
                } else if (protocolType == CTestWindProtocel.FLAG_RECV_PROTOCOL_HEAD_2) {
                    if (totalReceiveBuffer.size() >= CTestWindProtocel.RECV_PROTOCOL_DATA_LEN2) {
                        if (null != dataReceiveSendListener) {
                            dataReceiveSendListener.onDataReceiveSend(RECV_DATA);
                        }
                        byte[] processBuffer = new byte[CTestWindProtocel.RECV_PROTOCOL_DATA_LEN2];
                        for (int i = 0; i < processBuffer.length && totalReceiveBuffer.size() > 0; i++) {
                            processBuffer[i] = (byte) totalReceiveBuffer.get(0);
                            totalReceiveBuffer.remove(0);
                        }
                        PressureValue pd = new PressureValue();
                        if (0 == CTestWindProtocel.splitPressureFromBuffer(processBuffer, processBuffer.length, tData.getTrackNoInt(), pd)) {
                            processOnPressureValue(pd);
                        }
                    } else {//接收数据长度不到协议指定长度，继续接收数据下次处理
                        XLog.i(TAG + " buffer length" + totalReceiveBuffer.size() + "less than B0 data length" + CTestWindProtocel.RECV_PROTOCOL_DATA_LEN2);
                        break;
                    }
                } else {//报文协议类型不对，重新开始下一条数据的处理
                    XLog.i(TAG + " protect type not correct.");
                    totalReceiveBuffer.remove(0);
                    break;
                }
            }
        } catch (Exception ex) {
            XLog.e("处理接收数据异常：" + ex.getMessage());
        }
    }

    public int sendCommandToDevice(byte[] cmdBuffer) {
        int rtn = 0;
        try {
            if (serialPortManager != null && serialPortManager.IsOpen()) {
                if (serialPortManager.sendBytes(cmdBuffer)) {
                } else {
                    XLog.i("发送数据失败：" + HexUtil.bytesToHex(cmdBuffer));
                    rtn = 1;
                }
            } else {
                XLog.i("发送数据时端口未就绪。");
                rtn = 2;
            }
            return rtn;
        } catch (Exception ex) {
            XLog.e("发送数据失败：" + HexUtil.bytesToHex(cmdBuffer) + "发生异常" + ex.getMessage());
            return -1;
        }
    }

    public boolean isTestEnded() {
        if (nLastListCount == tData.lstPressureValue.size() && nLastListCount > 0) {
            nKeepedTimes = nKeepedTimes + 1;
            if (nKeepedTimes >= SysParamsAll.get_judgeEndTimes()) {
                return true;
            }
        } else {
            nLastListCount = tData.lstPressureValue.size();
            nKeepedTimes = 0;
        }
        return false;
    }

    /**
     * 串口发送数据处理
     *
     * @param bytes 发送的数据
     */
    @Override
    public void onDataSent(byte[] bytes) {
        XLog.i("串口发送数据成功：" + HexUtil.bytesToHex(bytes));
        if (null != dataReceiveSendListener) {
            dataReceiveSendListener.onDataReceiveSend(OnDataReceiveSendListener.SEND_COMMAND);
        }
    }

    /**
     * 串口打开成功
     *
     * @param device 串口设备文件
     */
    @Override
    public void onSuccess(File device) {
        XLog.i("打开串口" + device.getName() + "成功。");
    }

    /**
     * 串口打开失败
     *
     * @param device 串口设备文件
     * @param status 状态
     */
    @Override
    public void onFail(File device, Status status) {
        XLog.i("打开串口" + device.getName() + "失败。");
    }
}
