package com.thnet.tailairbrakingtest.Activitys;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.thnet.tailairbrakingtest.Communication.CTestWindProtocel;
import com.thnet.tailairbrakingtest.DAO.UserInfo;
import com.thnet.tailairbrakingtest.R;
import com.thnet.tailairbrakingtest.TestWind.SysParamsAll;
import com.thnet.tailairbrakingtest.TestWind.TestOperator;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    CardView cvEnginery;
    CardView cvTest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadSysParams();
        initView();
    }

    private void initView(){
        cvEnginery = findViewById(R.id.tv_enginery);
        cvTest = findViewById(R.id.tv_test);
        findViewById(R.id.tv_testWind).setOnClickListener(this);
        findViewById(R.id.tv_enginery).setOnClickListener(this);
        findViewById(R.id.tv_test).setOnClickListener(this);
        findViewById(R.id.tv_search).setOnClickListener(this);
        findViewById(R.id.tv_setting).setOnClickListener(this);
        findViewById(R.id.tv_test_serial_port).setOnClickListener(this);
        if (SysParamsAll.get_kehuoche() == 1){
            cvEnginery.setVisibility(View.GONE);
        }
        if (SysParamsAll.get_modalChanelNo() == 0){
            cvTest.setVisibility(View.GONE);
        }
    }

    private void loadSysParams(){
        //加载系统参数
        SysParamsAll.load();
        //获取操作人员信息
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_testWind:
                //type:1-试风，2-机能，3-校验
                InputInfoActivity.startIntent(this, CTestWindProtocel.TestCommandExt.tkNormal);
                break;
            case R.id.tv_enginery:
                InputInfoActivity.startIntent(this,CTestWindProtocel.TestCommandExt.tkMachineAbility);
                break;
            case R.id.tv_test:
                InputInfoActivity.startIntent(this,CTestWindProtocel.TestCommandExt.tkPressureCalibration);
                break;
            case R.id.tv_search:
                Intent intentS = new Intent(this,SearchActivity.class);
                startActivity(intentS);
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
        }
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
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

}
