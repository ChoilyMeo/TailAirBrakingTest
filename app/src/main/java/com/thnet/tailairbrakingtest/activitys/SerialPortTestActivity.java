package com.thnet.tailairbrakingtest.activitys;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.thnet.tailairbrakingtest.R;
import com.thnet.tailairbrakingtest.serialport.OnOpenSerialPortListener;
import com.thnet.tailairbrakingtest.serialport.OnSerialPortDataListener;
import com.thnet.tailairbrakingtest.serialport.RF433PowerControl;
import com.thnet.tailairbrakingtest.serialport.SerialPortManager;
import com.thnet.tailairbrakingtest.utility.HexUtil;

import java.io.File;
import java.util.Arrays;

public class SerialPortTestActivity extends Activity implements OnOpenSerialPortListener,View.OnClickListener {

    private static final String TAG = SerialPortTestActivity.class.getSimpleName();
    public static final String DEVICE_FILE = "/dev/ttyMT1";
    TextView tv_receiveText;
    CheckBox cb_hexView;
    CheckBox cb_hexSend;
    CheckBox cb_autoSend;
    CheckBox cb_sendTestData;
    Spinner sp_baudRate;
    Spinner sp_sendData;
    Button btn_openClose;
    Button btn_sendData;
    private SerialPortManager mSerialPortManager;
    int nSerialPortStatus = 0;//0关闭,1打开
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serial_port_test);
        tv_receiveText = (TextView)findViewById(R.id.edt_receiveText);
        //tv_receiveText.setKeyListener(null);
        cb_hexView = (CheckBox)findViewById(R.id.cb_hexView);
        cb_hexSend = (CheckBox)findViewById(R.id.cb_hexSend);
        cb_autoSend = (CheckBox)findViewById(R.id.cb_autoSend);
        cb_sendTestData = (CheckBox)findViewById(R.id.cb_sendTestData);
        sp_baudRate = (Spinner)findViewById(R.id.sp_baudRate);
        sp_sendData = (Spinner)findViewById(R.id.sp_sendData);
        btn_openClose = (Button)findViewById(R.id.btn_openClose);
        btn_sendData = (Button)findViewById(R.id.btn_sendData);
        btn_sendData.setEnabled(false);
        mSerialPortManager = new SerialPortManager();
    }

    @Override
    protected void onDestroy() {
        //关闭串口
        if (null != mSerialPortManager) {
            mSerialPortManager.closeSerialPort();
            mSerialPortManager = null;
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_clear:
                tv_receiveText.setText("");
                break;
            case R.id.btn_openClose:
                if (nSerialPortStatus == 0){
                    // 打开串口
                    boolean openSerialPort = mSerialPortManager.setOnOpenSerialPortListener(this)
                            .setOnSerialPortDataListener(new OnSerialPortDataListener() {
                                @Override
                                public void onDataReceived(final byte[] bytes) {
                                    Log.i(TAG, "onDataReceived [ byte[] ]: " + Arrays.toString(bytes));
                                    Log.i(TAG, "onDataReceived [ String ]: " + new String(bytes));
                                    final byte[] finalBytes = bytes;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            //showToast(String.format("接收\n%s", HexUtil.bytesToHex(bytes)));
                                            tv_receiveText.setText(tv_receiveText.getText().toString()+"接收："+HexUtil.bytesToHex(bytes)+"\r\n");
                                        }
                                    });
                                }

                                @Override
                                public void onDataSent(byte[] bytes) {
                                    Log.i(TAG, "onDataSent [ byte[] ]: " + Arrays.toString(bytes));
                                    Log.i(TAG, "onDataSent [ String ]: " + new String(bytes));
                                    final byte[] finalBytes = bytes;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            tv_receiveText.setText(tv_receiveText.getText().toString()+"发送："+HexUtil.bytesToHex(finalBytes)+"\r\n");
                                            showToast(String.format("发送\n%s", HexUtil.bytesToHex(finalBytes)));
                                        }
                                    });
                                }
                            })
                            .openSerialPort(new File(DEVICE_FILE), Integer.parseInt(sp_baudRate.getSelectedItem().toString()));
                    if(openSerialPort){
                        btn_openClose.setText("关闭串口");
                        nSerialPortStatus = 1;
                        btn_sendData.setEnabled(true);
                    }
                }
                else if(nSerialPortStatus == 1){
                    mSerialPortManager.closeSerialPort();
                    btn_openClose.setText("打开串口");
                    nSerialPortStatus = 0;
                    btn_sendData.setEnabled(false);
                }
                break;
            case R.id.btn_sendData:
                byte[] bSendData;
                if(cb_hexSend.isChecked()){
                    bSendData = HexUtil.hexToBytes(sp_sendData.getSelectedItem().toString());
                }
                else {
                    bSendData = sp_sendData.getSelectedItem().toString().getBytes();
                }
                mSerialPortManager.sendBytes(bSendData);
                break;
                default:
        }
    }

    @Override
    public void onSuccess(File device) {
        Toast.makeText(getApplicationContext(), String.format("串口 [%s] 打开成功", device.getPath()), Toast.LENGTH_SHORT).show();
        Log.i(TAG,"打开串口成功。");
    }

    @Override
    public void onFail(File device, Status status) {
        switch (status) {
            case NO_READ_WRITE_PERMISSION:
                showToast("串口打开失败");
                break;
            case OPEN_FAIL:
            default:
                showToast("串口打开失败");
                break;
        }
        Log.i(TAG,"打开串口失败。");
    }

    private Toast mToast;

    /**
     * Toast
     *
     * @param content content
     */
    private void showToast(String content) {
        if (null == mToast) {
            mToast = Toast.makeText(getApplicationContext(), null, Toast.LENGTH_SHORT);
        }
        mToast.setText(content);int[][] arr=new int[][]{{1},{2,3,4},{5,6}};
        mToast.show();
    }
}
