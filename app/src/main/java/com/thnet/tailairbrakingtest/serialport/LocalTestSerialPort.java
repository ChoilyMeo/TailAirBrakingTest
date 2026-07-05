package com.thnet.tailairbrakingtest.serialport;

import android.content.Context;

import com.choily.serialportlib.serialport.OnOpenSerialPortListener;
import com.choily.serialportlib.serialport.OnSerialPortDataListener;
import com.choily.serialportlib.serialport.SerialPortManager;
import com.elvishew.xlog.XLog;
import com.thnet.tailairbrakingtest.BuildConfig;
import com.thnet.tailairbrakingtest.customapplication.WindTestApplication;
import com.thnet.tailairbrakingtest.utility.HexUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class LocalTestSerialPort extends SerialPortManager {
    private final static String TEST_DATA_FILE_NAME = "test.txt";
    private int mReadedLine = 0;
    private Context mContext = WindTestApplication.getWindTestInstance();
    private OnSerialPortDataListener mOnSerialPortDataListener;
    private ScheduledExecutorService serviceDisplayTestStatus = new ScheduledThreadPoolExecutor(1);

    @Override
    public boolean openSerialPort(File device, int baudRate) {
        if (BuildConfig.USE_LOCAL_TEST_DATA) {
            XLog.i("开始使用本地的测试文件数据开始测试。");
            mReadedLine = 0;
            serviceDisplayTestStatus.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    try {
                        byte[] testBytes = getTestData();
                        if (null != testBytes) {
                            if (null != mOnSerialPortDataListener) {
                                mOnSerialPortDataListener.onDataReceived(testBytes);
                            }
                        }
                    } catch (Exception ex) {
                        XLog.e("使用本地测试文件测试定时处理异常：" + ex.getMessage());
                    }
                }
            }, 1000, 4000, TimeUnit.MILLISECONDS);
            return true;
        } else {
            return super.openSerialPort(device, baudRate);
        }
    }

    @Override
    public void closeSerialPort() {
        if (BuildConfig.USE_LOCAL_TEST_DATA) {
            if (null != serviceDisplayTestStatus) {
                //停止定时器
                serviceDisplayTestStatus.shutdown();
            }
        } else {
            super.closeSerialPort();
        }
    }

    @Override
    public SerialPortManager setOnOpenSerialPortListener(OnOpenSerialPortListener listener) {
        return super.setOnOpenSerialPortListener(listener);
    }

    @Override
    public SerialPortManager setOnSerialPortDataListener(OnSerialPortDataListener listener) {
        if (BuildConfig.USE_LOCAL_TEST_DATA) {
            mOnSerialPortDataListener = listener;
            return this;
        } else {
            return super.setOnSerialPortDataListener(listener);
        }
    }

    @Override
    public boolean sendBytes(byte[] sendBytes) {
        if (BuildConfig.USE_LOCAL_TEST_DATA) {
            return true;
        } else {
            return super.sendBytes(sendBytes);
        }
    }

    @Override
    public boolean IsOpen() {
        if (BuildConfig.USE_LOCAL_TEST_DATA) {
            return true;
        } else {
            return super.IsOpen();
        }
    }

    public byte[] getTestData() {
        byte[] readedBytes = null;
        if (null != mContext) {
            try {
                String fileName = mContext.getExternalFilesDir("").getAbsolutePath() + File.separator + TEST_DATA_FILE_NAME;
                File file = new File(fileName);
                if (null != file && file.isFile() && file.exists()) {
                    FileInputStream fileInputStream = new FileInputStream(file);
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
                    String line = bufferedReader.readLine();
                    int i = 0, pos = 0;
                    while (null != line) {
                        if (i >= mReadedLine && (pos = line.indexOf("字节数据：")) >= 0) {
                            XLog.d("取到本地测试文件" + TEST_DATA_FILE_NAME + "第" + i + "行数据。" + line);
                            readedBytes = HexUtil.hexToBytes(line.substring(pos + 5));
                            mReadedLine = i + 1;
                            break;
                        }
                        i = i + 1;
                        line = bufferedReader.readLine();
                    }
                    fileInputStream.close();
                    bufferedReader.close();
                    return readedBytes;
                } else {
                    XLog.d("文件" + TEST_DATA_FILE_NAME + "不存在。");
                }
            } catch (Exception ex) {
                XLog.d("从文件" + TEST_DATA_FILE_NAME + "获取测试数据异常：" + ex);
            }
        } else {
            XLog.d("本地数据未初始化。");
        }
        return null;
    }
}
