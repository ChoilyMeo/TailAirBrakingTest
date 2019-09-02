package com.thnet.tailairbrakingtest.SerialPort;

import com.elvishew.xlog.XLog;
import com.thnet.tailairbrakingtest.Utility.HexUtil;

import java.io.IOException;
import java.io.InputStream;

public abstract class SerialPortReadThread extends Thread {

    public abstract void onDataReceived(byte[] bytes);

    private static final String TAG = SerialPortReadThread.class.getSimpleName();
    private InputStream mInputStream;
    private byte[] mReadBuffer;

    public SerialPortReadThread(InputStream inputStream) {
        mInputStream = inputStream;
        mReadBuffer = new byte[4096];
    }

    @Override
    public void run() {
        super.run();

        while (!isInterrupted()) {
            try {
                if (null == mInputStream) {
                    XLog.e("未发现输入缓冲。");
                    return;
                }

                int size = mInputStream.read(mReadBuffer);

                if (-1 == size || 0 >= size) {
                    sleep(200);
                    continue;
                }

                byte[] readBytes = new byte[size];

                System.arraycopy(mReadBuffer, 0, readBytes, 0, size);

                XLog.i("接收" + readBytes.length + "字节数据：" + HexUtil.bytesToHex(readBytes));
                onDataReceived(readBytes);
                //Log.i(TAG, "after onDataReceived");

            } catch (IOException e) {
                XLog.e("接收线程IO异常：" + e.getLocalizedMessage());
                e.printStackTrace();
                return;
            } catch (Exception ex){
                XLog.e("接收线程发生异常：" + ex.getLocalizedMessage());
                ex.printStackTrace();
                return;
            }
        }
        XLog.i("接收线程已终止。");
    }

    @Override
    public synchronized void start() {
        super.start();
    }

    /**
     * 关闭线程 释放资源
     */
    public void release() {
        interrupt();

        if (null != mInputStream) {
            try {
                mInputStream.close();
                mInputStream = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
