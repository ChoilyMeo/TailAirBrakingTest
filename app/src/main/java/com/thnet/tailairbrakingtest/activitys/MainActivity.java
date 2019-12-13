package com.thnet.tailairbrakingtest.activitys;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.thnet.tailairbrakingtest.customapplication.WindTestApplication;
import com.thnet.tailairbrakingtest.dao.UserInfo;
import com.thnet.tailairbrakingtest.R;
import com.thnet.tailairbrakingtest.serialport.RF433PowerControl;
import com.thnet.tailairbrakingtest.testwind.SysParamsAll;
import com.thnet.tailairbrakingtest.testwind.TestOperator;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadSysParams();
        initView();
    }

    private void initView(){
        findViewById(R.id.tv_testWind).setOnClickListener(this);
        findViewById(R.id.tv_search).setOnClickListener(this);
        findViewById(R.id.tv_exit).setOnClickListener(this);
        findViewById(R.id.tv_setting).setOnClickListener(this);
        findViewById(R.id.tv_test_serial_port).setOnClickListener(this);
    }

    private void loadSysParams(){
        //加载系统参数
        SysParamsAll.load();
        //获取操作人员信息，没有登录的话，重新获取操作人员
        if (null == TestOperator.getCurrentUser()){
            List<UserInfo> userInfoList = TestOperator.getAllUserInfo();
            if (userInfoList.size() > 0) {
                TestOperator.setCurrentUser(userInfoList.get(0));
                SysParamsAll.set_testOperator(TestOperator.getCurrentUser().getUserID());
                SysParamsAll.set_testOperatorName(TestOperator.getCurrentUser().getUserName());
            } else {
                SysParamsAll.set_testOperator("0");
                SysParamsAll.set_testOperatorName("管理员");
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_testWind:
                InputInfoActivity.startIntent(this);
                break;
            case R.id.tv_search:
                Intent intentS = new Intent(this,SearchActivity.class);
                startActivity(intentS);
                break;
            case R.id.tv_exit:
                RF433PowerControl.powerOff();
                finish();
                System.exit(0);
                break;
            case R.id.tv_setting:
                Intent intent = new Intent(this,SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_test_serial_port:
                Intent intentTestSerialPort = new Intent(this, SerialPortTestActivity.class);
                startActivity(intentTestSerialPort);
                break;
            default:
                break;
        }
    }
    /**
     * 通过发送广播通知系统刷新文件
     * @param filePath 文件路径
     */
    public static void notifySystemToScan(String filePath) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(filePath);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        WindTestApplication.getWindTestInstance().sendBroadcast(intent);
    }

    private long exitTime = 0;
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                notifySystemToScan(getExternalFilesDir("log").getAbsolutePath());
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

}
