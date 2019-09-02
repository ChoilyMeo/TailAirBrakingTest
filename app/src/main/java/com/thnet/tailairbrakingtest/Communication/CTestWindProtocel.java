package com.thnet.tailairbrakingtest.Communication;

import com.elvishew.xlog.XLog;
import com.thnet.tailairbrakingtest.DAO.PressureValue;
import com.thnet.tailairbrakingtest.TestWind.SysParamsAll;
import com.thnet.tailairbrakingtest.Utility.HexUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CTestWindProtocel {
    private static final String TAG = CTestWindProtocel.class.getSimpleName();
    //协议长度定义
    public final static int recvProtocolHeadLen = 2;//文件头长度
    public final static int recvProtocolEndLen = 2;//文件尾长度
    public final static int recvProtocolSumLen = 2;//核对校验和长度
    public final static int recvProtocolHeadTotalLen = 5;
    public final static int recvProtocolCmdLen = 18;
    public final static int recvProtocolDataLen = 28;
    public final static int sendProtocolLen = 26;
    //协议位数定义
    public final static int posCommand = 4;
    //协议头、尾标志定义
    public final static int flagSendProtocolHead = 0x91;
    public final static int flagRecvProtocolHead = 0x93;

    public static byte[] getSendCmd() {
        return sendCmd;
    }

    private static byte[] sendCmd = new byte[sendProtocolLen];

    public enum TestCommand {
        TestBegin(0x00), PressureRelief(0x01), TestLX(0x02), TestGD(0x03), TestAD(0x15), TestBY(0x05),
        TestJL(0x06), TestKLW(0x06), TestZFLX(0x16), TestJNBegin(0x08), TestJNHJ(0x09), TestJNLX(0x0A), TestJNGD(0x0B), TestJGHJ(0x0C), TestJNAD(0x0D),
        PressureCheckReady(0x0E), PressureCheck(0x0F), TestBYKLW(0x10), PressureKSCheck(0x11), TestKLWWB(0x12), StopTest(0x1E), TestCancel(0x1E), TestEnd(0x1F),
        TestCMD13(0x13),TestCMD14(0x14), TestCMD17(0x17), TestCMD18(0x18), TestCMD19(0x19), TestCMD1A(0x1A), TestCMD1B(0x1B),
        TestCMD1C(0x1C), TestCMD1D(0x1D);//0x13--0x1D属于备用指令
        private int value;

        private TestCommand(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public enum TestCommandExt {
        tkNormal(0x00), tkMachineAbility(0x02), tkPressureCalibration(0x01);
        private int value;

        private TestCommandExt(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public enum TestStatus {
        TestNone(0x00), TestBegin(0x01), TestDoing(0x02), TestFillWindEnd(0x03), TestDropPressureBegin(0x04), TestDropPressureEnd(0x05), TestKeep(0x06), TestEnd(0x07);
        private int value;

        private TestStatus(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public enum ProtocolKind {
        tckCommand(0x00), tckData(0x01);
        private int value;

        private ProtocolKind(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    /**
     * 根据命令字节的最高位（第1位）判断接收的是指令内容还是数据内容：0-指令，1-数据
     * @param cmd 协议缓冲中的指令字节
     * @return 返回协议类型（指令或者数据）
     */
    public static ProtocolKind GetProtocolKind(byte cmd){
        return ProtocolKind.values()[HexUtil.GetBit(cmd, HexUtil.BIT_ONE)];
    }

    /**
     * 生成发送的指令报文，各个字节的定义参照协议文档
     * @param HandDeviceNo 手持机编号，在手持机参数设置中进行设置
     * @param DeviceNo 设备号，输入的阀号
     * @param Cmd 发送的指令，参照协议中指令字节的定义
     * @param line 股道，输入的股道
     * @param sTrainNo 车次，输入的车次
     * @param cmdParm  参数，客货车、有无计算机、有无列尾的参数
     * @param liangshu 辆数，输入的辆数
     * @param ban 班，输入的班号
     * @param zu 组，输入的组号
     * @param klw1 客列尾1，输入的客列尾首部
     * @param klw2 客列尾2，输入的客列尾尾部
     * @param byklw 备用客列尾，输入的备用客列尾
     * @return 返回安装协议生成的发送内容
     */
    public static byte[] CreateSendCmd(byte HandDeviceNo, byte DeviceNo, byte Cmd, byte line, String sTrainNo, byte cmdParm, byte liangshu, byte ban, byte zu, String klw1, String klw2, String byklw) {
        int nCheCi = 0;
        for (byte b: sendCmd ) { b = 0; }
        sendCmd[0] = (byte) flagSendProtocolHead;
        sendCmd[1] = (byte) flagSendProtocolHead;
        sendCmd[2] = DeviceNo;
        sendCmd[3] = HandDeviceNo;
        sendCmd[4] = Cmd;
        sendCmd[5] = cmdParm;
        sendCmd[6] = 0;
        sendCmd[7] = 0;
        sendCmd[8] = 0;
        sendCmd[9] = 0;
        sendCmd[10] = 0;
        sendCmd[11] = 0;
        if (sTrainNo.isEmpty()) {
            sendCmd[7] = 0;
            sendCmd[8] = 0;
            sendCmd[9] = 0;
            sendCmd[10] = 0;
            sendCmd[11] = 0;
        } else {
            try {
                nCheCi = Integer.parseInt(sTrainNo);
                String strTemp = "00000000" + sTrainNo;
                System.arraycopy(HexUtil.hexToBytes(strTemp.substring(strTemp.length()-8)),0, sendCmd, 8, 4);
            } catch (NumberFormatException e) {
                char chCheCi = 'a';
                chCheCi = sTrainNo.charAt(0);
                if (chCheCi >= 'a' && chCheCi <= 'z') {
                    sendCmd[7] = (byte) (chCheCi - 'a');
                } else if (chCheCi >= 'A' && chCheCi <= 'Z') {
                    sendCmd[7] = (byte) (chCheCi - 'A');
                } else {
                    sendCmd[7] = 0;
                }
                try {
                    nCheCi = Integer.parseInt(sTrainNo.substring(1));
                    String strTemp = "00000000" + sTrainNo.substring(1);
                    System.arraycopy(HexUtil.hexToBytes(strTemp.substring(strTemp.length()-8)),0, sendCmd, 8, 4);
                } catch (Exception ex) {
                    nCheCi = 0;
                    sendCmd[8] = 0;
                    sendCmd[9] = 0;
                    sendCmd[10] = 0;
                    sendCmd[11] = 0;
                }
            } catch (Exception e) {
                sendCmd[7] = 0;
                sendCmd[8] = 0;
                sendCmd[9] = 0;
                sendCmd[10] = 0;
                sendCmd[11] = 0;
            }
        }
        sendCmd[12] = line;
        sendCmd[13] = liangshu;
        sendCmd[14] = (byte) (HexUtil.byteToUnsigned(ban) * 16 + HexUtil.byteToUnsigned(zu));
        if (!klw1.isEmpty()){
            System.arraycopy(HexUtil.hexToBytes(klw1), 0, sendCmd, 15, 3);
        } else {
            sendCmd[15] = 0;
            sendCmd[16] = 0;
            sendCmd[17] = 0;
        }
        if (!klw2.isEmpty()) {
            System.arraycopy(HexUtil.hexToBytes(klw2), 0, sendCmd, 18, 3);
        } else {
            sendCmd[18] = 0;
            sendCmd[19] = 0;
            sendCmd[20] = 0;
        }
        if (!byklw.isEmpty()){
            System.arraycopy(HexUtil.hexToBytes(byklw), 0, sendCmd, 21, 3);
        } else {
            sendCmd[21] = 0;
            sendCmd[22] = 0;
            sendCmd[23] = 0;
        }
        long sum = GetCheckSum(sendCmd, sendProtocolLen, 2, sendProtocolLen - 4);
        sendCmd[24] = (byte) (sum % 256);
        sendCmd[25] = (byte) (sum / 256);
        return sendCmd;
    }

    /**
     * 计算校验和
     * @param buffer 计算的缓冲区
     * @param bufLen 计算的缓冲区的总长度
     * @param start 计算校验和的起始位置
     * @param checkLen 计算校验和的长度
     * @return 计算的校验和
     */
    private static long GetCheckSum(byte[] buffer, int bufLen, int start, int checkLen) {
        long sum = 0xFFFF;
        long genpoly = 0xA001;
        if (bufLen >= start + checkLen && checkLen > 0) {
            for (int i = start; i < bufLen - 1 && i <= start + checkLen - 1; i++) {
                sum = sum ^ HexUtil.byteToUnsigned(buffer[i]);
                for (int j = 0; j < 8; j++) {
                    if ((sum & 0x01) != 0) {
                        sum = (sum >> 1) ^ genpoly;
                    } else {
                        sum = sum >> 1;
                    }
                }
            }
        }
        return sum % 0x010000;
    }

    /***
     * 核对校验和是否正确，报文开始两位是报文头，结束两位是校验和，报文头和尾之间是报文体
     * 校验计算是报文体中除了报文头和校验和的部分累加，然后与核对校验和进行对比是否一致
     * @param buffer 报文内容
     * @param buflen 报文长度
     * @return 校验和比对是否一致
     */
    private static boolean CheckSum(byte[] buffer, int buflen){
        if (buflen < recvProtocolHeadLen + recvProtocolEndLen + recvProtocolSumLen) {
            return false;
        }
        long sum = GetCheckSum(buffer, buflen, 2, buflen - 4);
        return (sum == HexUtil.byteToUnsigned(buffer[buflen-1]) * 256 + HexUtil.byteToUnsigned(buffer[buflen-2]));
    }

    public static String GetSendCmdHexString() {
        String strtmp = "";
        for (byte tmp : sendCmd) {
            strtmp = strtmp + Integer.toHexString(HexUtil.byteToUnsigned(tmp)).toUpperCase() + " ";
        }
        return strtmp;
    }

    public static byte MegerTestCommandParam(int noTail, int hasComputer, int passengerOrFreightTrains){
        int cmdParam = 0;
        return (byte)(cmdParam + noTail == 0 ? 0x00 : 0x01 + hasComputer == 0 ? 0x00 : 0x02 + passengerOrFreightTrains == 0 ? 0x00 : 0x80);
    }

    public static byte MergerTestCommand(int cmd, int specifiedPressure) {
        byte commandByte;
        if (specifiedPressure == SysParamsAll.SpecifiedPressure500) {
            commandByte = (byte) (0x20 + cmd);
        } else {
            commandByte = (byte) (0x60 + cmd);
        }
        return commandByte;
    }

    /**
     * 从报文中解析试验的命令数据
     * @param buffer 报文内容
     * @param bufLen 报文内容长度
     * @param DeviceNo 试验使用的阀号，输入的阀号
     * @param commandData
     * @return
     */
    public static int SplitCommandFromBuffer(byte[] buffer, int bufLen, int DeviceNo, CommandData commandData) {
        int rtn = 0;
        if (HexUtil.byteToUnsigned(buffer[2]) == DeviceNo) {
            if (HexUtil.byteToUnsigned(buffer[3]) == SysParamsAll.get_handDeviceNo()) {
                if (CheckSum(buffer, bufLen)) {
                    if (null == commandData) {
                        commandData = new CommandData();
                    }
                    commandData.GetCommandFromByte(buffer[4]);
                    commandData.cmdParam = buffer[6];
                } else {
                    rtn = 2;
                    XLog.i("接收到的数据的校验和不正确");
                }
            } else {
                rtn = 3;
                XLog.i("接收到的手持机编号" + buffer[3] + "不等于设置的手持机编号" + SysParamsAll.get_handDeviceNo());
            }
        } else {
            rtn = 1;
            XLog.i("接收到的执行器编号" + buffer[2] + "不等于输入的阀号" + DeviceNo);
        }
        return rtn;
    }

    /**
     * 从报文中解析压力数据
     * @param buffer 报文数据
     * @param bufLen 报文长度
     * @param DeviceNo 试验使用的阀号，输入的阀号
     * @param commandData 解析报文后返回的命令数据部分内容
     * @param pd 解析报文后返回的压力数据部分内容
     * @return 报文解析的结果：0-成功，其他-失败
     */
    public static int SplitDataFromBuffer(byte[] buffer, int bufLen, int DeviceNo, CommandData commandData, PressureValue pd) {
        int rtn = 0;
        if (HexUtil.byteToUnsigned(buffer[2]) == DeviceNo) {
            if (HexUtil.byteToUnsigned(buffer[3]) == SysParamsAll.get_handDeviceNo()) {
                if (CheckSum(buffer, bufLen)) {
                    if (null == commandData) {
                        commandData = new CommandData();
                    }
                    commandData.GetCommandFromByte(buffer[4]);
                    commandData.testStatus = buffer[5];
                    commandData.cmdParam = buffer[6];
                    if (null == pd) {
                        pd = new PressureValue();
                    }
                    pd.setPressureTime(new SimpleDateFormat("HH:mm:ss").format(new Date(System.currentTimeMillis())));
                    pd.setPressureKeepMinutes(HexUtil.byteToUnsigned(buffer[7]));
                    pd.setHeadPressureValue(HexUtil.bytesToShort(buffer[15],buffer[14]) / 10);
                    pd.setSourcePressureValue(HexUtil.bytesToShort(buffer[19] , buffer[18]) / 10);
                    pd.setCenterPressureValue(HexUtil.bytesToShort(buffer[17] , buffer[16]) / 10);
                    pd.setPressureValue(HexUtil.bytesToShort(buffer[23] , buffer[22]));
                    pd.setBoxTemperature(HexUtil.byteToUnsigned(buffer[20]));
                    pd.setBoxHumidity(HexUtil.byteToUnsigned(buffer[21]));
                    pd.setTestResult(HexUtil.bytesToShort(buffer[25],buffer[24]));
                } else {
                    rtn = 2;
                    XLog.i("接收到的数据的校验和不正确");
                }
            } else {
                rtn = 3;
                XLog.i("接收到的手持机编号" + buffer[3] + "不等于" + SysParamsAll.get_handDeviceNo());
            }
        } else {
            rtn = 1;
            XLog.i("接收到的执行器编号" + buffer[2] + "不等于" + DeviceNo);
        }
        return rtn;
    }

}
