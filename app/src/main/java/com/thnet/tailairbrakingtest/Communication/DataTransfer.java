package com.thnet.tailairbrakingtest.Communication;

import android.util.Log;

import com.elvishew.xlog.XLog;
import com.thnet.tailairbrakingtest.DAO.PressureValue;
import com.thnet.tailairbrakingtest.SerialPort.OnOpenSerialPortListener;
import com.thnet.tailairbrakingtest.SerialPort.OnSerialPortDataListener;
import com.thnet.tailairbrakingtest.SerialPort.SerialPortManager;
import com.thnet.tailairbrakingtest.TestWind.SysParamsAll;
import com.thnet.tailairbrakingtest.TestWind.TestContent;
import com.thnet.tailairbrakingtest.TestWind.TestData;
import com.thnet.tailairbrakingtest.Utility.HexUtil;
import com.thnet.tailairbrakingtest.Utility.TipSoundPlayer;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.thnet.tailairbrakingtest.Communication.OnDataReceiveSendListener.RECV_DATA;

/**
 *
 */
public class DataTransfer implements OnSerialPortDataListener, OnOpenSerialPortListener {
    private static final String TAG = DataTransfer.class.getSimpleName();
    public SerialPortManager m_sp;
    public TestData tData;

    public TestContent currTest = null;
    public TestContent failedTest = null;
    public TestContent prevTest = null;
    public CTestWindProtocel.TestCommand currTestCommand = CTestWindProtocel.TestCommand.TestBegin;
    public CTestWindProtocel.TestStatus currTestStatus = CTestWindProtocel.TestStatus.TestNone;

    private int nLastListCount = 0;
    private int nKeepedTimes = 0;
    ArrayList<Byte> m_buffer = new ArrayList(0);
    private OnDataReceiveSendListener dataReceiveSendListener;

    public DataTransfer() {
        PortInit();
        tData = new TestData();
    }

    public DataTransfer(String line, String trainNo, String trainCount, String specifyPressureValue, String testKind, String handDeviceNo, String deviceNo,String operatorClass, String operatorGroup, String klw1, String klw2, String klwby) {
        PortInit();
        tData = new TestData(line, trainNo, trainCount, specifyPressureValue, testKind, handDeviceNo, deviceNo, operatorClass, operatorGroup, klw1, klw2, klwby);
    }

    public OnDataReceiveSendListener getDataReceiveSendListener() {
        return dataReceiveSendListener;
    }

    public void setDataReceiveSendListener(OnDataReceiveSendListener dataReceiveSendListener) {
        this.dataReceiveSendListener = dataReceiveSendListener;
    }

    public void StartWatch(String line, String trainNo, String trainCount, String specifyPressureValue, String testKind, String handDeviceNo, String deviceNo, String operatorClass, String operatorGroup, String klw1, String klw2, String klwby) {
        tData = new TestData(line, trainNo, trainCount, specifyPressureValue, testKind, handDeviceNo, deviceNo, operatorClass, operatorGroup, klw1, klw2, klwby);
        tData._GDSY.SetParms(tData.get_DingYaInt(), tData.get_LiangshuInt());
        tData._ADSY.SetParms(tData.get_DingYaInt(), tData.get_LiangshuInt());
        tData._BYSY.SetParms(tData.get_DingYaInt(), tData.get_LiangshuInt());
        tData._JLSY.SetParms(tData.get_DingYaInt(), tData.get_LiangshuInt());
        tData._ZFLX.SetParms(tData.get_DingYaInt(), tData.get_LiangshuInt());
        tData._JNLX.SetParms(tData.get_DingYaInt(), tData.get_LiangshuInt());
        tData._JNGD.SetParms(tData.get_DingYaInt(), tData.get_LiangshuInt());
        tData._JNAD.SetParms(tData.get_DingYaInt(), tData.get_LiangshuInt());
        tData._JGHJ.SetParms(tData.get_DingYaInt(), tData.get_LiangshuInt());
        m_sp.openSerialPort(new File(SysParamsAll.get_portName()), SysParamsAll.get_baudRate());
    }

    public void StopWatch(){
        dataReceiveSendListener = null;
        m_sp.closeSerialPort();
        m_sp = null;
    }

    private void PortInit() {
        m_buffer.clear();
        m_sp = new SerialPortManager();
        m_sp.setOnOpenSerialPortListener(this);
        m_sp.setOnSerialPortDataListener(this);
    }

    public void CheckTestStatus(String stime, PressureValue pd, int cmd, int testStatus, int keepStatus) {
        TestContent tst = currTest;
        currTest = tData.GetTestByCommand(cmd);
        if (currTest != null) {
            if (tst != currTest) {
                prevTest = tst;
            }
            currTest = currTest.CheckStatus(stime, pd, CTestWindProtocel.TestStatus.values()[testStatus], pd.getTestResult());
        } else {
            if (prevTest != tst) {
                prevTest = tst;
            }
        }

        return;
    }

    private ArrayList<Byte> addBytesToList(ArrayList<Byte> src, byte[] dst){
        for (byte b : dst){
            src.add(b);
        }
        return src;
    }

    /**
     * 串口接收数据处理
     *
     * @param bytes 接收到的数据
     */
    @Override
    public void onDataReceived(byte[] bytes) {
        try {
            //Log.i(TAG,"data Received." + bytes.length);
            if (m_sp == null || !m_sp.IsOpen()) {
                XLog.e(TAG + "接收数据时串口未打开");
                return;
            }

            CTestWindProtocel.ProtocolKind protocolType;

            addBytesToList(m_buffer, bytes);
            while (m_buffer.size() >= CTestWindProtocel.recvProtocolCmdLen || m_buffer.size() >= CTestWindProtocel.recvProtocolDataLen) {//防止一次收到多条数据，增加循环处理
                //Log.i(TAG, "all buffer size " + m_buffer.size());
                //整理列表，从协议报文头开始
                while (m_buffer.size() > 0) {
                    if (HexUtil.byteToUnsigned((byte) m_buffer.get(0)) != CTestWindProtocel.flagRecvProtocolHead) {
                        m_buffer.remove(0);
                    } else {
                        break;
                    }
                }
                //Log.i(TAG, "after process,all buffer size " + m_buffer.size());
                if (m_buffer.size() >= CTestWindProtocel.recvProtocolCmdLen || m_buffer.size() >= CTestWindProtocel.recvProtocolDataLen) {
                    protocolType = CTestWindProtocel.GetProtocolKind((byte) m_buffer.get(CTestWindProtocel.posCommand));
                    //Log.i(TAG, "protocol type is " + protocolType);
                    if (protocolType == CTestWindProtocel.ProtocolKind.tckCommand) {//报文协议类型是命令
                        if (m_buffer.size() >= CTestWindProtocel.recvProtocolCmdLen) {
                            byte[] processBuffer = new byte[CTestWindProtocel.recvProtocolCmdLen];
                            for (int i = 0; i < processBuffer.length && m_buffer.size() > 0; i++) {
                                processBuffer[i] = (byte) m_buffer.get(0);
                                m_buffer.remove(0);
                            }
                            CommandData commandData = new CommandData();
                            if (0 == CTestWindProtocel.SplitCommandFromBuffer(processBuffer, processBuffer.length, tData.get_ZXQBHInt(), commandData)) {
                                currTestCommand = CTestWindProtocel.TestCommand.values()[commandData.command];
                                currTestStatus = CTestWindProtocel.TestStatus.values()[commandData.testStatus];
                                if (commandData.command == CTestWindProtocel.TestCommand.TestBegin.getValue() && commandData.testStatus == CTestWindProtocel.TestStatus.TestBegin.getValue()) {
                                    tData.set_cmdParam(commandData.cmdParam);
                                }
                            }
                            if (null != dataReceiveSendListener){
                                dataReceiveSendListener.OnDataReceiveSend(OnDataReceiveSendListener.RECV_COMMAND);
                            }
                            //Log.i(TAG, "process command commpleted.");
                        } else {//接收数据长度不到协议指定长度，继续接收数据下次处理
                            Log.i(TAG, "buffer length" + m_buffer.size() + "less than command length" + CTestWindProtocel.recvProtocolCmdLen);
                            break;
                        }
                    } else if (protocolType == CTestWindProtocel.ProtocolKind.tckData) {//报文协议类型是数据
                        if (m_buffer.size() >= CTestWindProtocel.recvProtocolDataLen) {
                            if (null != dataReceiveSendListener){
                                dataReceiveSendListener.OnDataReceiveSend(RECV_DATA);
                            }
                            byte[] processBuffer = new byte[CTestWindProtocel.recvProtocolDataLen];
                            for (int i = 0; i < processBuffer.length && m_buffer.size() > 0; i++) {
                                processBuffer[i] = (byte) m_buffer.get(0);
                                m_buffer.remove(0);
                            }
                            CommandData commandData = new CommandData();
                            PressureValue pd = new PressureValue();
                            if (0 == CTestWindProtocel.SplitDataFromBuffer(processBuffer, processBuffer.length, tData.get_ZXQBHInt(), commandData, pd)) {
                                currTestCommand = CTestWindProtocel.TestCommand.values()[commandData.command];
                                currTestStatus = CTestWindProtocel.TestStatus.values()[commandData.testStatus];
                                if (tData.lstPressureValue.size() <= SysParamsAll.get_maxTestPoint() && !pd.getPressureTime().isEmpty()) {
                                    String stime = new SimpleDateFormat("HH:mm:ss").format(new Date(System.currentTimeMillis()));
                                    if (tData.lstPressureValue.size() <= 0) {
                                        tData.BeginTest();
                                        TipSoundPlayer.PlayVoicePrompts(TipSoundPlayer.nVoiceFileNameBeginTestWind);
                                    }
                                    tData.lstPressureValue.add(pd);
                                    CheckTestStatus(stime, pd, commandData.command, commandData.testStatus, pd.getTestResult());
                                }
                            }
                            //Log.i(TAG, "process data commpleted.");
                        } else {//接收数据长度不到协议指定长度，继续接收数据下次处理
                            Log.i(TAG, "buffer length" + m_buffer.size() + "less than data length" + CTestWindProtocel.recvProtocolDataLen);
                            break;
                        }
                    } else {//报文协议类型不对，重新开始下一条数据的处理
                        Log.i(TAG, "protect type not correct.");
                    }
                } else {
                    Log.i(TAG, "buffer size not correct." + m_buffer.size());
                    break;
                }
            }
            //Log.i(TAG, "process buffer completed.");
        } catch (Exception ex) {
            XLog.e("处理接收数据异常：" + ex.getMessage());
        }
    }

    public int SendCommandToDevice(byte cmd) {
        int rtn = 1;
        try {
            CTestWindProtocel.CreateSendCmd(SysParamsAll.get_handDeviceNo(), (byte) tData.get_ZXQBHInt(), cmd, (byte) tData.get_GuDaoInt(), tData.get_CheCi(), CTestWindProtocel.MegerTestCommandParam(SysParamsAll.get_noTail(), SysParamsAll.get_hasComputer(), SysParamsAll.get_kehuoche()), (byte) tData.get_LiangshuInt(), (byte) Integer.parseInt(tData.get_Ban()), (byte) Integer.parseInt(tData.get_Zu()), tData.get_KeLieWeiID1(), tData.get_KeLieWeiID2(), tData.get_BeiYongKeLieWeiID());
            if (m_sp != null && m_sp.IsOpen()) {
                if(m_sp.sendBytes(CTestWindProtocel.getSendCmd())){
                    XLog.i("开始发送数据：" + HexUtil.bytesToHex(CTestWindProtocel.getSendCmd()));
                    rtn = 0;
                }
                else {
                    XLog.i("发送数据失败：" + HexUtil.bytesToHex(CTestWindProtocel.getSendCmd()));
                    rtn = 1;
                }
            } else {
                XLog.i("发送数据时端口未就绪。");
                rtn = 2;
            }
            return rtn;
        } catch (Exception ex) {
            XLog.e("发送数据失败：" + HexUtil.bytesToHex(CTestWindProtocel.getSendCmd()) + "发生异常" + ex.getMessage());
            return -1;
        }
    }

    public boolean IsTestEnded() {
        if (currTestCommand == CTestWindProtocel.TestCommand.TestEnd && currTestStatus == CTestWindProtocel.TestStatus.TestEnd) {
            return true;
        }
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
        if (null != dataReceiveSendListener){
            dataReceiveSendListener.OnDataReceiveSend(OnDataReceiveSendListener.SEND_COMMAND);
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
