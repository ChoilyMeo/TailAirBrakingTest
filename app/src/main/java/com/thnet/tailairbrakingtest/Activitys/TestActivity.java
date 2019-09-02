package com.thnet.tailairbrakingtest.Activitys;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thnet.tailairbrakingtest.R;
import com.thnet.tailairbrakingtest.SerialPort.RF433PowerControl;

public class TestActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvShowControl,tvHideControl;
    private LinearLayout llAllBtns,llShow;
    private int intentType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        //433模块上电
        RF433PowerControl.powerOn();
        intentType = getIntent().getIntExtra("intent_type",0);
        initView();
    }

    @Override
    protected void onDestroy() {
        //433模块断电
        RF433PowerControl.powerOff();
        super.onDestroy();
    }

    private void initView(){
        //所有按钮的最外层
        llAllBtns = findViewById(R.id.ll_allbtns);
        GridLayout glTestWind = findViewById(R.id.gl_testWind);//试风按钮
        GridLayout glEnginery = findViewById(R.id.gl_enginery);//机能按钮
        LinearLayout llCheck = findViewById(R.id.ll_check);//校验按钮

        //点击隐藏后要显示的部分
        llShow = findViewById(R.id.ll_show);
        //隐藏控制按钮
        tvHideControl = findViewById(R.id.tv_hideControl);
        tvHideControl.setOnClickListener(this);
        //显示控制按钮
        tvShowControl = findViewById(R.id.tv_showControl);
        tvShowControl.setOnClickListener(this);
        TextView tvSave = findViewById(R.id.tv_save);//保存按钮
        TextView tvExit = findViewById(R.id.tv_exit);//退出按钮
        tvExit.setOnClickListener(this);

        //type:1-试风，2-机能，3-校验
        if (intentType == 1){
            llAllBtns.setVisibility(View.VISIBLE);
            glTestWind.setVisibility(View.VISIBLE);
            glEnginery.setVisibility(View.GONE);
            llCheck.setVisibility(View.GONE);
        } else if (intentType == 2){
            llAllBtns.setVisibility(View.VISIBLE);
            glTestWind.setVisibility(View.GONE);
            glEnginery.setVisibility(View.VISIBLE);
            llCheck.setVisibility(View.GONE);
        } else if (intentType == 3){
            llAllBtns.setVisibility(View.VISIBLE);
            glTestWind.setVisibility(View.GONE);
            glEnginery.setVisibility(View.GONE);
            llCheck.setVisibility(View.VISIBLE);
        } else {
            glTestWind.setVisibility(View.GONE);
            glEnginery.setVisibility(View.GONE);
            llCheck.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_hideControl:
                llAllBtns.setVisibility(View.GONE);
                llShow.setVisibility(View.VISIBLE);
                tvHideControl.setVisibility(View.GONE);
                break;
            case R.id.tv_showControl:
                llAllBtns.setVisibility(View.VISIBLE);
                llShow.setVisibility(View.GONE);
                tvHideControl.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_exit:
                finish();
                break;
                default:
        }
    }

    public static void startIntent(Activity context, int type){
        //type:1-试风，2-机能，3-试验
        Intent intent = new Intent(context,TestActivity.class);
        intent.putExtra("intent_type",type);
        context.startActivity(intent);
    }

}
