package com.thnet.tailairbrakingtest.activitys;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.elvishew.xlog.XLog;
import com.thnet.tailairbrakingtest.R;
import com.thnet.tailairbrakingtest.customapplication.WindTestApplication;
import com.thnet.tailairbrakingtest.dao.DaoSession;
import com.thnet.tailairbrakingtest.dao.UserInfo;
import com.thnet.tailairbrakingtest.testwind.TestOperator;
import com.thnet.tailairbrakingtest.utility.StringUtil;

import java.util.List;

public class UserEditActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText etUserId;
    private EditText etUserName;
    List<UserInfo> userInfoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit);
        initView();
        userInfoList = TestOperator.getAllUserInfo();
    }

    private void initView(){
        etUserId = (EditText) findViewById(R.id.et_user_id);
        etUserName = (EditText) findViewById(R.id.et_user_name);
        findViewById(R.id.user_add_btn).setOnClickListener(this);
        findViewById(R.id.back_tv).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.user_add_btn:
                try {
                    if (StringUtil.isNullOrEmpty(etUserId.getText().toString())) {
                        Toast.makeText(this, "请输入登陆ID。", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (StringUtil.isNullOrEmpty(etUserName.getText().toString())) {
                        Toast.makeText(this, "请输入用户名。", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (null != userInfoList) {
                        for (UserInfo ui : userInfoList) {
                            if (etUserId.getText().toString().equals(ui.getUserID())) {
                                Toast.makeText(this, "登陆ID已经存在，不能重复添加。", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if (etUserName.getText().toString().equals(ui.getUserName())) {
                                Toast.makeText(this, "用户名已经存在，不能重复添加。", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    }
                    UserInfo newUser = new UserInfo();
                    newUser.setUserID(etUserId.getText().toString());
                    newUser.setUserName(etUserName.getText().toString());
                    newUser.setUserPwd("0");
                    final DaoSession daoSession = WindTestApplication.getWindTestInstance().getDaoSession();
                    daoSession.getUserInfoDao().insert(newUser);
                    Toast.makeText(this, "添加用户成功。", Toast.LENGTH_SHORT).show();
                    finish();
                } catch (Exception ex) {
                    Toast.makeText(this, "添加用户异常。", Toast.LENGTH_SHORT).show();
                    XLog.e("添加用户异常：" + ex.getMessage());
                    return;
                }
                break;
            case R.id.back_tv:
                finish();
                break;
            default:
                break;
        }
    }
}
