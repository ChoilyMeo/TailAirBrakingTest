package com.thnet.tailairbrakingtest.Activitys;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.thnet.tailairbrakingtest.DAO.UserInfo;
import com.thnet.tailairbrakingtest.R;
import com.thnet.tailairbrakingtest.TestWind.SysParamsAll;
import com.thnet.tailairbrakingtest.TestWind.TestOperator;
import com.thnet.tailairbrakingtest.adapters.UserAdapter;

import java.util.List;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = LoginActivity.class.getSimpleName();
    EditText et_password;
    private PopupWindow popupWindow;
    private TextView tvUserName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.i(TAG, "login activity");
        initView();
    }

    private void initView(){
        et_password = (EditText) findViewById(R.id.et_OperatorGroup);
        findViewById(R.id.login_btn).setOnClickListener(this);
        findViewById(R.id.tv_changePassword).setOnClickListener(this);
        findViewById(R.id.tv_test_serial_port).setOnClickListener(this);
        tvUserName = findViewById(R.id.tv_userName);
        tvUserName.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_btn:
                if (null == TestOperator.getCurrentUser()){
                    Toast.makeText(this, "请选择登录用户。", Toast.LENGTH_SHORT).show();
                }
                if (checkLogin()) {
                    SysParamsAll.load();
                    SysParamsAll.set_testOperator(TestOperator.getCurrentUser().getUserID());
                    SysParamsAll.set_testOperatorName(TestOperator.getCurrentUser().getUserName());
                    finish();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(this,"密码错误，请重新输入。", Toast.LENGTH_SHORT).show();
                    et_password.setText("");
                }
                break;
            case R.id.tv_changePassword:
                if (null == TestOperator.getCurrentUser()){
                    Toast.makeText(this, "请选择用户。", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intentC = new Intent(LoginActivity.this, ChangePasswordActivity.class);
                    startActivity(intentC);
                }
                break;
            case R.id.tv_userName:
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                } else {
                    showPop();
                }
                break;
            case R.id.tv_test_serial_port:
                Intent intentTestSerialPort = new Intent(this, SerialPortTestActivity.class);
                startActivity(intentTestSerialPort);
                break;
                default:
        }
    }

    private void showPop(){
        View view = View.inflate(this,R.layout.pop_layout,null);
        popupWindow = new PopupWindow(view,LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setOutsideTouchable(true);
        //在这儿显示用户名列表，设置用户名
        final ListView listView = view.findViewById(R.id.mListView);
        List<UserInfo> userInfoList = TestOperator.getAllUserInfo();
        if (null != userInfoList) {
            listView.setAdapter(new UserAdapter(this, userInfoList));
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserInfo userInfo = (UserInfo) listView.getItemAtPosition(position);
                TestOperator.setCurrentUser(userInfo);
                tvUserName.setText(userInfo.getUserName());
                popupWindow.dismiss();
            }
        });
        View line = findViewById(R.id.line);
        popupWindow.showAsDropDown(line,0,0);
    }

    private boolean checkLogin(){
        return TestOperator.checkUserPassword(TestOperator.getCurrentUser(), et_password.getText().toString());
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
