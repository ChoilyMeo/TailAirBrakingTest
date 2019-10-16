package com.thnet.tailairbrakingtest.activitys;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.thnet.tailairbrakingtest.customapplication.WindTestApplication;
import com.thnet.tailairbrakingtest.dao.TestKind;
import com.thnet.tailairbrakingtest.R;
import com.thnet.tailairbrakingtest.adapters.TestKindAdapter;

import java.util.List;

public class InputInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = InputInfoActivity.class.getSimpleName();
    EditText etRatedPressure;
    EditText etTrack;
    EditText etTrainCount;
    EditText etTrainNo;
    TextView tvTestKind;
    private PopupWindow popupWindow;
    List<TestKind> testKindList;
    TestKind selectedTestKind;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_info);
        testKindList = WindTestApplication.getWindTestInstance().getDaoSession().loadAll(TestKind.class);
        initView();
    }

    private void initView(){
        findViewById(R.id.back_tv).setOnClickListener(this);
        findViewById(R.id.start_btn).setOnClickListener(this);
        findViewById(R.id.manual_start_btn).setOnClickListener(this);
        etRatedPressure = findViewById(R.id.et_ratedPressure);
        etTrack = findViewById(R.id.et_track);
        etTrainCount = findViewById(R.id.et_trainCount);
        etTrainNo = findViewById(R.id.et_trainNo);
        tvTestKind = findViewById(R.id.tv_testKind);
        tvTestKind.setOnClickListener(this);
        //默认显示试风种类列表中第一个
        if (null != testKindList && testKindList.size() > 0){
            selectedTestKind = testKindList.get(0);
            tvTestKind.setText(selectedTestKind.getTestKindName());
        }
    }

    private void showPop(){
        View view = View.inflate(this,R.layout.pop_layout,null);
        popupWindow = new PopupWindow(view,LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setOutsideTouchable(true);
        //在这儿显示用户名列表，设置用户名
        final ListView listView = view.findViewById(R.id.mListView);
        if (null != testKindList) {
            listView.setAdapter(new TestKindAdapter(this, testKindList));
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedTestKind = (TestKind)listView.getItemAtPosition(position);
                tvTestKind.setText(selectedTestKind.getTestKindName());
                popupWindow.dismiss();
            }
        });
        View line = findViewById(R.id.tv_testKind);
        popupWindow.showAsDropDown(line,0,0);
    }

    @Override
    public void onClick(View v) {
        StringBuilder errMsg = new StringBuilder();
        switch (v.getId()){
            case R.id.back_tv:
                finish();
                break;
            case R.id.start_btn:
                if (inputValidate(errMsg)) {
                    TestActivity.startIntent(this, TestActivity.VIEW_TYPE_TEST, etTrack.getText().toString(), etTrainNo.getText().toString(), etTrainCount.getText().toString(), etRatedPressure.getText().toString(), "", selectedTestKind);
                    finish();
                }
                else {
                    Toast.makeText(this, errMsg.toString(), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.manual_start_btn:
                break;
            case R.id.tv_testKind:
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                } else {
                    showPop();
                }
                break;
            default:
                break;
        }
    }

    private boolean inputValidate(StringBuilder errMsg){
        boolean result = true;
        errMsg.delete(0, errMsg.length());
        if (result && etRatedPressure.getText().toString().trim().isEmpty()){
            errMsg.append("请输入定压500或600。");
            result = false;
        }
        else if(result){
            int dy = 0;
            try {
                dy = Integer.parseInt(etRatedPressure.getText().toString().trim());
                if (dy != 500 && dy != 600){
                    errMsg.append("定压只能是500或600。");
                    result = false;
                }
            }
            catch (Exception ex) {
                Log.i(TAG, "定压" + etRatedPressure.getText().toString().trim() + "转换为数值失败。");
                errMsg.append("定压只能是数字500或600。");
                result = false;
            }
        }
        if (result && etTrack.getText().toString().trim().isEmpty()){
            errMsg.append("请输入股道。");
            result = false;
        }
        if (result && etTrainCount.getText().toString().trim().isEmpty()){
            errMsg.append("请输入辆数。");
            result = false;
        }
        String trainNo = etTrainNo.getText().toString().trim();
        if (result && trainNo.isEmpty()){
            errMsg.append("请输入车次。");
            result = false;
        } else if (result && !trainNo.matches("^[a-zA-Z]{0,1}\\d{1,8}$")){
            errMsg.append("车次格式不正确");
            result = false;
        }
        if (null == selectedTestKind){
            errMsg.append("请选择试风类型");
            result = false;
        }
        return result;
    }

    public static void startIntent(Activity context){
        Intent intent = new Intent(context,InputInfoActivity.class);
        context.startActivity(intent);
    }
}
