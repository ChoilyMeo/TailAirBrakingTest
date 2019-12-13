package com.thnet.tailairbrakingtest.serialport;

import com.thnet.tailairbrakingtest.BuildConfig;
import com.elvishew.xlog.XLog;

import  java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class RF433PowerControl {
    private static final String TAG = RF433PowerControl.class.getSimpleName();
    private static final String IO_SCAN_SWITCH = "/proc/jbcommon/gpio_control/scan_switch";//无线通讯模块硬件IO路径
    //串口片选控制IO文件
    private static final String IO_OE = "/proc/jbcommon/gpio_control/scan_ir_gps_rs232_switch_oe"; // 默认值：1，其他值无效
    private static final String IO_CS1 = "/proc/jbcommon/gpio_control/scan_ir_gps_rs232_cs1";// 默认值：1，其他值无效
    private static final String IO_CS0 = "/proc/jbcommon/gpio_control/scan_ir_gps_rs232_cs0";// 默认值：1，其他值无效
    public static void powerOn(){
        try{
            if(writeFile(IO_SCAN_SWITCH, 1)){
                writeFile(IO_OE, 1);
                writeFile(IO_CS0, 1);
                writeFile(IO_CS1, 0);
                XLog.i("无线通讯模块启动。");
            }
        }
        catch (Exception ex) {
            XLog.e("无线通讯模块上电异常。");
        }
    }
    public static void powerOff(){
        if (BuildConfig.CONTROL_RF433_POWER_OFF) {
            try {
                if (writeFile(IO_SCAN_SWITCH, 0)) {
                    XLog.i("无线通讯模块关闭。");
                }
            } catch (Exception ex) {
                XLog.e("无线通讯模块断电异常。");
            }
        } else {
            XLog.i("无线模块设置为不关闭。");
        }
    }
    private static synchronized boolean writeFile(String fileName, int status) {
        try {
            File file = new File(fileName);
            if (file.exists()) {
                OutputStream out = new FileOutputStream(file);
                out.write((status + "").getBytes());
                out.flush();
                out.close();
                return true;
            }
            else{
                XLog.e("无线通讯模块IO设备文件不存在。");
            }
        } catch (Exception e) {
            // TODO: handle exception
            XLog.e("无线通讯模块IO硬件设备文件操作异常。");
            return false;
        }
        return false;
    }
}
