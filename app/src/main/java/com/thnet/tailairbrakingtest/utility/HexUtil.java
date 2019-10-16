package com.thnet.tailairbrakingtest.utility;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class HexUtil {

    public static final int BIT_ONE = 0;
    public static final int BIT_TWO = 1;
    public static final int BIT_THREE = 2;
    public static final int BIT_FOUR = 3;
    public static final int BIT_FIVE = 4;
    public static final int BIT_SIX = 5;
    public static final int BIT_SEVEN = 6;
    public static final int BIT_EIGHT = 7;
    @IntDef({
            BIT_ONE,
            BIT_TWO,
            BIT_THREE,
            BIT_FOUR,
            BIT_FIVE,
            BIT_SIX,
            BIT_SEVEN,
            BIT_EIGHT
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface Bits {}
    public static final int BIT_RANGE_ONE = 1;
    public static final int BIT_RANGE_TWO = 2;
    public static final int BIT_RANGE_THREE = 3;
    public static final int BIT_RANGE_FOUR = 4;
    public static final int BIT_RANGE_FIVE = 5;
    public static final int BIT_RANGE_SIX = 6;
    public static final int BIT_RANGE_SEVEN = 7;
    public static final int BIT_RANGE_EIGHT = 8;
    @IntDef({
            BIT_RANGE_ONE,
            BIT_RANGE_TWO,
            BIT_RANGE_THREE,
            BIT_RANGE_FOUR,
            BIT_RANGE_FIVE,
            BIT_RANGE_SIX,
            BIT_RANGE_SEVEN,
            BIT_RANGE_EIGHT
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface Ranges {}

    /**
     * 字符串转成字节流
     */
    public static byte[] hexToBytes(String src) {
    int m = 0, n = 0;
    src=src.replace(" ","");
    int byteLen = src.length() / 2; // 每两个字符描述一个字节
    byte[] ret = new byte[byteLen];
    for (int i = 0; i < byteLen; i++) {
        m = i * 2 + 1;
        n = m + 1;
        int intVal = Integer.decode("0x" + src.substring(i * 2, m) + src.substring(m, n));
        ret[i] = Byte.valueOf((byte)intVal);
    }
    return ret;
}
    /**
     * 字节流转成十六进制表示
     */
    public static String bytesToHex(byte[] src) {
        String strHex = "";
        StringBuilder sb = new StringBuilder("");
        for (int n = 0; n < src.length; n++) {
            strHex = Integer.toHexString(src[n] & 0xFF);
            sb.append(((strHex.length() == 1) ? "0" + strHex : strHex) + " "); // 每个字节由两个字符表示，位数不够，高位补0
        }
        return sb.toString().trim().toUpperCase();
    }

    /**
     * 获取一个字节指定位数的值，位数从高位位0开始到低位位7结束
     * @param b
     * @param i
     * @return
     */
    public static int GetBit(byte b, @Bits int i){
        if (i <0 || i > 7) {
            return 0;
        }
        int pos = 7 - i;
        int bit = (int)((b>>pos) & 0x1);
        return bit;
    }

    /**
     * 获取一个字节指定长度位数的值，位数从高位0开始到低位7结束，长度从1到8
     * @param b
     * @param start
     * @param length
     * @return
     */
    public static int GetBits(byte b, @Bits int start, @Ranges int length){
        if (start < 0 || start > 7 || length < 1 || length > 8 || start + length > 8) {
            return 0;
        }
        int pos = 8 - start - length;
        int bit = (int)((b>>pos)&(0xFF>>(8-length)));
        return bit;
    }

    /**
     * 转换有符号的byte为无符号的 例如 byte=-1(11111111)转换为int=255
     * @param b 输入的有符号的byte数
     * @return 返回int，值是转换后的无符号的byte的值
     */
    public static int byteToUnsigned(byte b){
        return b&0xff;
    }

    /**
     * 转换有符号的int为无符号的 例如 i=-1(1111111111111111)转换为long=65535
     * @param i 输入有符号的int数
     * @return 返回Long，值是转换后的无符号的int值
     */
    public static long intToUnsigned(int i) { return i&0xffffffff; }
    /**
     * 计算高低位字节数据组成的无符号数值
     * @param bHigh
     * @param bLow
     * @return
     */
    public static int bytesToShort(byte bHigh, byte bLow){
        return byteToUnsigned(bHigh) * 256 + byteToUnsigned(bLow);
    }
}
