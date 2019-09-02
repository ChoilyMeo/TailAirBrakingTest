package com.thnet.tailairbrakingtest.Activitys;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.thnet.tailairbrakingtest.Communication.CTestWindProtocel;
import com.thnet.tailairbrakingtest.R;
import com.thnet.tailairbrakingtest.TestWind.SysParamsAll;

public class InputInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = InputInfoActivity.class.getSimpleName();
    private static final String PREFERENCE_NAME_OPERATOR_CLASS = "OperatorClass";
    private static final String PREFERENCE_NAME_OPERATOR_GROUP = "OperatorGroup";
    private static final String PREFERENCE_NAME_VALVE_NO = "valveNo";
    private static final String PREFERENCE_NAME_RATED_PRESSURE = "RatedPressure";
    private CTestWindProtocel.TestCommandExt intentType = CTestWindProtocel.TestCommandExt.tkNormal;
    private SharedPreferences preferences;
    EditText etOperatorClass;
    EditText etOperatorGroup;
    EditText etValveNo;
    EditText etRatedPressure;
    EditText etTrack;
    EditText etTrainCount;
    EditText etTrainNo;
    EditText etTrainHead;
    EditText etTrainTail;
    EditText etTrainBackup;
    LinearLayout llTrainHead;
    LinearLayout llTrainTail;
    LinearLayout llTrainBackup;
    LinearLayout llTrack;
    LinearLayout llTrainCount;
    LinearLayout llTrainNo;
    Button btnManualStart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_info);
        intentType = CTestWindProtocel.TestCommandExt.values()[getIntent().getIntExtra("intent_type",0)];
        preferences = getPreferences(MODE_PRIVATE);
        initView();
    }

    private void initView(){
        findViewById(R.id.back_tv).setOnClickListener(this);
        findViewById(R.id.start_btn).setOnClickListener(this);
        findViewById(R.id.manual_start_btn).setOnClickListener(this);
        etOperatorClass = findViewById(R.id.et_OperatorClass);
        etOperatorClass.setText(preferences.getString(PREFERENCE_NAME_OPERATOR_CLASS, ""));
        etOperatorGroup = findViewById(R.id.et_OperatorGroup);
        etOperatorGroup.setText(preferences.getString(PREFERENCE_NAME_OPERATOR_GROUP, ""));
        etValveNo = findViewById(R.id.et_valveNo);
        etValveNo.setText(preferences.getString(PREFERENCE_NAME_VALVE_NO, ""));
        etRatedPressure = findViewById(R.id.et_ratedPressure);
        etRatedPressure.setText(preferences.getString(PREFERENCE_NAME_RATED_PRESSURE,"600"));
        etTrack = findViewById(R.id.et_track);
        etTrainCount = findViewById(R.id.et_trainCount);
        etTrainNo = findViewById(R.id.et_trainNo);
        etTrainHead = findViewById(R.id.et_trainHead);
        etTrainTail = findViewById(R.id.et_trainTail);
        etTrainBackup = findViewById(R.id.et_trainBackup);
        llTrainHead = findViewById(R.id.ll_TrainHead);
        llTrainTail = findViewById(R.id.ll_TrainTail);
        llTrainBackup = findViewById(R.id.ll_TrainBackup);
        llTrack = findViewById(R.id.ll_Track);
        llTrainCount = findViewById(R.id.ll_TrainCount);
        llTrainNo = findViewById(R.id.ll_TrainNo);
        btnManualStart = findViewById(R.id.manual_start_btn);

        llTrainBackup.setVisibility(View.GONE);
        if (intentType == CTestWindProtocel.TestCommandExt.tkNormal) {//试风
            llTrack.setVisibility(View.VISIBLE);
            llTrainCount.setVisibility(View.VISIBLE);
            llTrainNo.setVisibility(View.VISIBLE);
            llTrainHead.setVisibility(View.VISIBLE);
            llTrainTail.setVisibility(View.VISIBLE);
            btnManualStart.setVisibility(View.VISIBLE);
        } else {//机能和校验不显示 股道、车次、辆数、客列首、客列尾输入框和手动按钮
            llTrack.setVisibility(View.GONE);
            llTrainCount.setVisibility(View.GONE);
            llTrainNo.setVisibility(View.GONE);
            llTrainHead.setVisibility(View.GONE);
            llTrainTail.setVisibility(View.GONE);
            btnManualStart.setVisibility(View.GONE);
        }
        if (SysParamsAll.get_kehuoche() == 1){//货车不显示 客列尾、客列首输入框
            llTrainHead.setVisibility(View.GONE);
            llTrainTail.setVisibility(View.GONE);
            btnManualStart.setText("测试");
        } else {
            btnManualStart.setText("手动");
        }
        if (SysParamsAll.get_modalChanelNo() == 0){//信道号是0的时候，不显示手动按钮
            btnManualStart.setVisibility(View.GONE);
        } else {
            btnManualStart.setVisibility(View.VISIBLE);
        }
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
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(PREFERENCE_NAME_OPERATOR_CLASS, etOperatorClass.getText().toString());
                    editor.putString(PREFERENCE_NAME_OPERATOR_GROUP, etOperatorGroup.getText().toString());
                    editor.putString(PREFERENCE_NAME_VALVE_NO, etValveNo.getText().toString());
                    editor.putString(PREFERENCE_NAME_RATED_PRESSURE, etRatedPressure.getText().toString());
                    editor.commit();
                    finish();
                    TestControlActivity.startIntent(this, intentType,etTrack.getText().toString().trim(),etTrainNo.getText().toString().trim(),etTrainCount.getText().toString().trim(),
                            etRatedPressure.getText().toString().trim(),Byte.toString(SysParamsAll.get_handDeviceNo()),etValveNo.getText().toString().trim(),etOperatorClass.getText().toString().trim(),
                            etOperatorGroup.getText().toString().trim(), etTrainHead.getText().toString().trim(), etTrainTail.getText().toString().trim(), etTrainBackup.getText().toString().trim(), 0);
                }
                else {
                    Toast.makeText(this, errMsg.toString(), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.manual_start_btn:
                boolean result = true;
                if (result && etValveNo.getText().toString().trim().isEmpty()){
                    errMsg.append("请输入阀号。");
                    result = false;
                }
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
                if (result){
                    finish();
                    TestControlActivity.startIntent(this, intentType,"0","0000","00",
                            etRatedPressure.getText().toString().trim(),Byte.toString(SysParamsAll.get_handDeviceNo()),etValveNo.getText().toString().trim(),etOperatorClass.getText().toString().trim(),
                            etOperatorGroup.getText().toString().trim(), "000000", "000000", "000000", 1);
                }
                break;
                default:
        }
    }

    private boolean inputValidate(StringBuilder errMsg){
        boolean result = true;
        errMsg.delete(0, errMsg.length());
        if (etOperatorClass.getText().toString().trim().isEmpty()){
            errMsg.append("请输入班号。");
            result = false;
        }
        if (result && etOperatorGroup.getText().toString().trim().isEmpty()){
            errMsg.append("请输入组号。");
            result = false;
        }
        if (result && etValveNo.getText().toString().trim().isEmpty()){
            errMsg.append("请输入阀号。");
            result = false;
        }
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
        if (result && llTrack.getVisibility() == View.VISIBLE && etTrack.getText().toString().trim().isEmpty()){
            errMsg.append("请输入股道。");
            result = false;
        }
        if (result && llTrainCount.getVisibility() == View.VISIBLE && etTrainCount.getText().toString().trim().isEmpty()){
            errMsg.append("请输入辆数。");
            result = false;
        }
        String trainNo = etTrainNo.getText().toString().trim();
        if (result && llTrainNo.getVisibility() == View.VISIBLE && trainNo.isEmpty()){
            errMsg.append("请输入车次。");
            result = false;
        } else if (result && llTrainNo.getVisibility() == View.VISIBLE && !trainNo.matches("^[a-zA-Z]{0,1}\\d{1,8}$")){
            errMsg.append("车次格式不正确");
            result = false;
        }
        if(result && llTrainHead.getVisibility() == View.VISIBLE && !etTrainHead.getText().toString().trim().isEmpty() && !etTrainHead.getText().toString().trim().matches("^\\d{6}$")){
            errMsg.append("输入客列首格式有误。");
            result = false;
        }
        if(result && llTrainTail.getVisibility() == View.VISIBLE && !etTrainTail.getText().toString().trim().isEmpty() && !etTrainTail.getText().toString().trim().matches("^\\d{6}$")){
            errMsg.append("输入客列尾格式有误。");
            result = false;
        }
        if(result && llTrainBackup.getVisibility() == View.VISIBLE && !etTrainBackup.getText().toString().trim().isEmpty() && !etTrainBackup.getText().toString().trim().matches("^\\d{6}$")){
            errMsg.append("输入客列备格式有误。");
            result = false;
        }
        if (result && !etTrainHead.getText().toString().trim().isEmpty() && !etTrainTail.getText().toString().trim().isEmpty() && !etTrainBackup.getText().toString().trim().isEmpty()
            && (etTrainBackup.getText().toString().trim().equals(etTrainHead.getText().toString().trim()) || etTrainBackup.getText().toString().trim().equals(etTrainTail.getText().toString().trim()))){
            errMsg.append("输入客列尾ID有误。");
            result = false;
        }
        return result;
    }

    public static void startIntent(Activity context,CTestWindProtocel.TestCommandExt type){
        //type:1-试风，2-机能，3-校验
        Intent intent = new Intent(context,InputInfoActivity.class);
        intent.putExtra("intent_type",type.getValue());
        context.startActivity(intent);
    }
}
