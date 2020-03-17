package com.thnet.tailairbrakingtest.activitys;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.elvishew.xlog.XLog;
import com.thnet.tailairbrakingtest.R;
import com.thnet.tailairbrakingtest.customapplication.WindTestApplication;
import com.thnet.tailairbrakingtest.dao.DaoSession;
import com.thnet.tailairbrakingtest.dao.UserInfo;
import com.thnet.tailairbrakingtest.dao.UserInfoDao;
import com.thnet.tailairbrakingtest.utility.StringUtil;

import java.util.List;

public class ChangePasswordActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String PARAM_USER_ID = "userID";
    private String sUserId;
    private EditText etUserName;
    private EditText etOldPassword;
    private EditText etNewPassword;
    private EditText etConfirmPassword;
    private UserInfo currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        sUserId = getIntent().getStringExtra(PARAM_USER_ID);
        final DaoSession daoSession = WindTestApplication.getWindTestInstance().getDaoSession();
        List<UserInfo> userInfoList = daoSession.getUserInfoDao().queryBuilder().where(UserInfoDao.Properties.UserID.eq(sUserId)).list();
        if (null != userInfoList && userInfoList.size() > 0) {
            currentUser = userInfoList.get(0);
        } else {
            currentUser = null;
        }
        initView();
    }

    private void initView(){
        etUserName = (EditText) findViewById(R.id.et_user_name);
        etOldPassword = (EditText) findViewById(R.id.et_old_password);
        etNewPassword = (EditText) findViewById(R.id.et_new_password);
        etConfirmPassword = (EditText) findViewById(R.id.et_confirm_password);
        findViewById(R.id.back_tv).setOnClickListener(this);
        findViewById(R.id.btn_modify).setOnClickListener(this);
        if (null != currentUser){
            etUserName.setText(currentUser.getUserName());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_modify:
                try {
                    if (StringUtil.isNullOrEmpty(etNewPassword.getText().toString())) {
                        Toast.makeText(this, "请输入新密码。", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (StringUtil.isNullOrEmpty(etConfirmPassword.getText().toString())) {
                        Toast.makeText(this, "请输入确认密码。", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!etNewPassword.getText().toString().equals(etConfirmPassword.getText().toString())){
                        Toast.makeText(this, "输入的新密码和确认密码不一致。", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (null == currentUser){
                        Toast.makeText(this, "获取用户信息错误。", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (null != currentUser && !StringUtil.isNullOrEmpty(currentUser.getUserPwd())) {
                        if (!currentUser.getUserPwd().equals(etOldPassword.getText().toString())){
                            Toast.makeText(this, "原密码输入不正确。", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    currentUser.setUserPwd(etNewPassword.getText().toString());
                    final DaoSession daoSession = WindTestApplication.getWindTestInstance().getDaoSession();
                    daoSession.getUserInfoDao().update(currentUser);
                    Toast.makeText(this, "密码修改成功。", Toast.LENGTH_SHORT).show();
                    finish();
                } catch (Exception ex) {
                    Toast.makeText(this, "密码修改异常。", Toast.LENGTH_SHORT).show();
                    XLog.e("密码修改异常：" + ex.getMessage());
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

    public static void startIntent(Activity context, String userId){
        Intent intent = new Intent(context,ChangePasswordActivity.class);
        intent.putExtra(PARAM_USER_ID,userId);
        context.startActivity(intent);
    }
}
