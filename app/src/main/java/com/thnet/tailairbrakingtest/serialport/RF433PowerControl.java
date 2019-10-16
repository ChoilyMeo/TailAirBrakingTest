package com.thnet.tailairbrakingtest.serialport;

import com.elvishew.xlog.XLog;

import  java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class RF433PowerControl {
    private static final String TAG = RF433PowerControl.class.getSimpleName();
    private static final String IO_SCAN_SWITCH = "/proc/jbcommon/gpio_control/scan_switch";//无线通讯模块硬件IO路径
    public static void powerOn(){
        try{
            if(writeFile(IO_SCAN_SWITCH, 1)){
                XLog.i("无线通讯模块启动。");
            }
        }
        catch (Exception ex) {
            XLog.e("无线通讯模块上电异常。");
        }
    }
    public static void powerOff(){
        try{
            if(writeFile(IO_SCAN_SWITCH, 0)){
                XLog.i("无线通讯模块关闭。");
            }
        }
        catch (Exception ex) {
            XLog.e("无线通讯模块断电异常。");
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
