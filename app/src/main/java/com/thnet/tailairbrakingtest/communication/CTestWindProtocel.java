package com.thnet.tailairbrakingtest.communication;

import com.elvishew.xlog.XLog;
import com.thnet.tailairbrakingtest.dao.PressureValue;
import com.thnet.tailairbrakingtest.utility.DateTimeUtil;
import com.thnet.tailairbrakingtest.utility.HexUtil;

import java.util.Date;

/**
 * 试风串口数据传输协议相关处理
 * @author mzl
 */
public class CTestWindProtocel {
    private static final String TAG = CTestWindProtocel.class.getSimpleName();
    /**
     * 第一种风压数据的协议数据长度
     */
    public final static int RECV_PROTOCOL_DATA_LEN1 = 8;
    /**
     * 第二种风压数据协议的数据长度
     */
    public final static int RECV_PROTOCOL_DATA_LEN2 = 10;
    /**
     * 获取尾部压力的指令，程序通过定时发送指令来获取压力数据
     */
    public static byte[] sendCmd = { (byte) 0xA6, (byte) 0xA6, 0x02, 0x08, 0x54, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x19, 0x06 };
    /**
     * 第一种风压数据的协议头标志
     */
    public final static int FLAG_RECV_PROTOCOL_HEAD_1 = 0xC0;
    /**
     * 第二种风压数据的协议头标志
     */
    public final static int FLAG_RECV_PROTOCOL_HEAD_2 = 0xB0;

    public static void createSendCmd(int modalChanelNo, int deviceNo, int line){
        sendCmd[2] = (byte)modalChanelNo;
        sendCmd[3] = (byte) deviceNo;
        sendCmd[4] = (byte)(line + 0x40);
        int sum = (int) getCheckSum(sendCmd, sendCmd.length, 2, sendCmd.length - 4);
        sendCmd[10] = (byte)(sum % 64);
        sendCmd[11] = (byte)(sum / 64);
    }

    /**
     * 计算校验和
     * @param buffer 计算的缓冲区
     * @param bufLen 计算的缓冲区的总长度
     * @param start 计算校验和的起始位置
     * @param checkLen 计算校验和的长度
     * @return 计算的校验和
     */
    private static long getCheckSum(byte[] buffer, int bufLen, int start, int checkLen) {
        long sum = 0x0000;
        if (bufLen >= start + checkLen && checkLen > 0) {
            for (int i = start; i < bufLen - 1 && i <= start + checkLen - 1; i++) {
                sum = sum + HexUtil.byteToUnsigned(buffer[i]);
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
    private static boolean checkSum(byte[] buffer, int buflen, int start, int checkLen){
        long sum = getCheckSum(buffer, buflen, start, checkLen);
        return (sum == HexUtil.byteToUnsigned(buffer[buflen-1]) * 64 + HexUtil.byteToUnsigned(buffer[buflen-2]));
    }

    /**
     * 从接收的报文中解析风压数据
     * @param buffer 接收的报文缓冲
     * @param bufLen 报文缓冲的长度
     * @param trackNo 要接收的股道，需要判断报文中的股道是否和要接收的股道一致，不一致的话属于无效数据
     * @param pd 解析之后的风压数据
     * @return 返回0成功，返回其他失败
     */
    public static int splitPressureFromBuffer(byte[] buffer, int bufLen, int trackNo, PressureValue pd){
        int rtn = 0, getTrackNo;
        int protocolType = HexUtil.byteToUnsigned(buffer[0]);
        if (FLAG_RECV_PROTOCOL_HEAD_1 == protocolType){
            getTrackNo = HexUtil.byteToUnsigned(buffer[1]) - 0xC0;
            if (getTrackNo == trackNo){
                if (checkSum(buffer, bufLen, 0, bufLen - 2)){
                    if (null == pd){
                        pd = new PressureValue();
                    }
                    pd.setPressureValue(((HexUtil.byteToUnsigned(buffer[3]) & 0x3F) * 64 + (HexUtil.byteToUnsigned(buffer[2]) & 0x3F)) / 4);
                    pd.setPressureTime(DateTimeUtil.formatDateTimetoString(new Date(), DateTimeUtil.FMT_HHmmss));
                } else {
                    XLog.i("接收到的数据的校验和不正确");
                    rtn = 2;
                }
            } else {
                XLog.i("接收到的股道号" + getTrackNo + "不等于输入的" + trackNo);
                rtn = 3;
            }
        } else if (FLAG_RECV_PROTOCOL_HEAD_2 == protocolType){
            getTrackNo = HexUtil.byteToUnsigned(buffer[3]);
            if (getTrackNo == trackNo){
                if (checkSum(buffer, bufLen, 2, bufLen -4)){
                    if (null == pd){
                        pd = new PressureValue();
                    }
                    pd.setPressureValue(((HexUtil.byteToUnsigned(buffer[5]) & 0x3F) * 64 + (HexUtil.byteToUnsigned(buffer[4]) & 0x3F)) / 4);
                    pd.setPressureTime(DateTimeUtil.formatDateTimetoString(new Date(), DateTimeUtil.FMT_HHmmss));
                } else {
                    XLog.i("接收到的数据的校验和不正确");
                    rtn = 2;
                }
            } else {
                XLog.i("接收到的股道号" + getTrackNo + "不等于输入的" + trackNo);
                rtn = 3;
            }
        } else {
            XLog.i("接收到的数据的报文头不正确" + protocolType);
            return 1;
        }
        return rtn;
    }
}
