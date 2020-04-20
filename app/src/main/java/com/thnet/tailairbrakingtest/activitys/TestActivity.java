package com.thnet.tailairbrakingtest.activitys;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IntDef;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.elvishew.xlog.XLog;
import com.thnet.tailairbrakingtest.communication.CTestWindProtocel;
import com.thnet.tailairbrakingtest.communication.DataTransfer;
import com.thnet.tailairbrakingtest.communication.OnDataReceiveSendListener;
import com.thnet.tailairbrakingtest.communication.OnTestFailedListener;
import com.thnet.tailairbrakingtest.customcontrol.ChartView;
import com.thnet.tailairbrakingtest.dao.TestKind;
import com.thnet.tailairbrakingtest.R;
import com.thnet.tailairbrakingtest.serialport.RF433PowerControl;
import com.thnet.tailairbrakingtest.testwind.SysParamsAll;
import com.thnet.tailairbrakingtest.testwind.TestContent;
import com.thnet.tailairbrakingtest.utility.TipSoundPlayer;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 试风试验控制及图形展示界面
 * @author mzl
 */
public class TestActivity extends AppCompatActivity implements View.OnClickListener, OnTestFailedListener, OnDataReceiveSendListener {
    private static final String PARAM_INTENT_TYPE = "intent_type";
    private static final String PARAM_TRACK = "track";
    private static final String PARAM_TRAIN_NO = "trainNo";
    private static final String PARAM_TRAIN_COUNT = "trainCount";
    private static final String PARAM_SPECIFIED_PRESSURE = "specifiedPressure";
    private static final String PARAM_TEST_KIND = "testKind";
    private static final String PARAM_TEST_ID = "testID";
    private static final int MSG_TEST_END = 0;
    private static final int MSG_DISPLAY_TEST_STATUS = 1;
    private static final int MSG_TEST_FAILED = 2;
    private static final int MSG_CLOSE_WINDOW = 3;
    public static final int VIEW_TYPE_TEST = 0;
    public static final int VIEW_TYPE_REPLAY = 1;
    @IntDef({
            VIEW_TYPE_TEST,
            VIEW_TYPE_REPLAY
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface ViewTypes {}
    private TextView tvTestName, tvTrackNo, tvTrainNo, tvTrainCount, tvTestLeak, tvSave;
    private EditText etMainPressureValue, etDropValue, etKeepTime, etLeakValue;
    private LinearLayout llKeepTime, llLeakValue, llDropValue;
    private ChartView chartView;
    private String trackNo, trainNo, trainCount, specifyPressure, testId;
    private TestKind testKind;
    private DataTransfer dt;
    private ScheduledExecutorService serviceSendCmd = new ScheduledThreadPoolExecutor(1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置屏幕常亮
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_test);
        dt = new DataTransfer();
        int intentType = getIntent().getIntExtra(PARAM_INTENT_TYPE,0);
        trackNo = getIntent().getStringExtra(PARAM_TRACK);
        trainNo = getIntent().getStringExtra(PARAM_TRAIN_NO);
        trainCount = getIntent().getStringExtra(PARAM_TRAIN_COUNT);
        specifyPressure = getIntent().getStringExtra(PARAM_SPECIFIED_PRESSURE);
        testId = getIntent().getStringExtra(PARAM_TEST_ID);
        testKind = getIntent().getParcelableExtra(PARAM_TEST_KIND);
        initView();
        //根据设置的定时器时间，定时刷新界面
        switch (intentType){
            case VIEW_TYPE_TEST:
                startWatch();
                break;
            case VIEW_TYPE_REPLAY:
                startReplay();
                break;
            default:
                XLog.i("未知的试验窗口启动类型：" + intentType);
                break;
        }
    }

    private void endTest(){
        //停止定时器
        serviceSendCmd.shutdown();
        dt.stopWatch();
    }

    @Override
    protected void onDestroy() {
        //停止定时器
        serviceSendCmd.shutdown();
        dt.stopWatch();
        super.onDestroy();
    }

    private void initView(){
        tvTrackNo = findViewById(R.id.tv_trackNo);
        tvTrainNo = findViewById(R.id.tv_trainNo);
        tvTrainCount = findViewById(R.id.tv_trainCount);
        tvTestName = findViewById(R.id.tv_testName);
        etMainPressureValue = findViewById(R.id.et_mainPressureValue);
        etDropValue = findViewById(R.id.et_dropValue);
        etKeepTime = findViewById(R.id.et_keepTime);
        etLeakValue = findViewById(R.id.et_leakValue);
        llKeepTime = findViewById(R.id.ll_keepTime);
        llLeakValue = findViewById(R.id.ll_leakValue);
        llDropValue = findViewById(R.id.ll_dropValue);
        tvTestLeak = findViewById(R.id.tv_testLeak);
        tvSave = findViewById(R.id.tv_save);//保存按钮
        TextView tvExit = findViewById(R.id.tv_exit);//退出按钮
        tvSave.setOnClickListener(this);
        tvExit.setOnClickListener(this);
        chartView = findViewById(R.id.image_chart);
        chartView.setViewTestData(dt.tData);
        chartView.setCanScroll(false);
    }

    private void displayTestStatus(){
        try {
            if (tvTestLeak.isEnabled() == false && dt.tData.lstPressureValue.size() > 0 && (dt.currTest == null || dt.currTest.getTestName() != TestContent.TEST_NAME_LX)) {
                tvTestLeak.setEnabled(true);
            }
            if (null != dt.currTest && (dt.currTest.getStat() == TestContent.TestState.tsDoing || dt.currTest.getStat() == TestContent.TestState.tsStoped)) {
                String pressureValue, keepTime, leakValue, dropValue;
                pressureValue = String.valueOf(dt.currTest.getTestMainPressureValue());
                keepTime = String.valueOf(dt.currTest.getTestKeepTime());
                leakValue = String.valueOf(dt.currTest.getTestLeakValue());
                dropValue = String.valueOf(dt.currTest.getTestDropValue());
                tvTestName.setText(dt.currTest.getTestName());
                etMainPressureValue.setText(pressureValue);
                etDropValue.setText(dropValue);
                etKeepTime.setText(keepTime);
                etLeakValue.setText(leakValue);
                llDropValue.setVisibility(dt.currTest.getViewStatDropValue());
                llKeepTime.setVisibility(dt.currTest.getViewStatKeepTime());
                llLeakValue.setVisibility(dt.currTest.getViewStatLeakValue());
            }
        } catch (Exception ex) {
            XLog.e("显示试验状态异常：" + ex);
        }
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_TEST_END:
                    break;
                case MSG_DISPLAY_TEST_STATUS:
                    displayTestStatus();
                    chartView.viewToEnd();
                    break;
                case MSG_TEST_FAILED:
                    tipRedoTest();
                    break;
                case MSG_CLOSE_WINDOW:
                    finish();
                    break;
                default:
                    break;
            }
        }
    };

    private void startWatch(){
        tvTrackNo.setText(trackNo);
        tvTrainNo.setText(trainNo);
        tvTrainCount.setText(trainCount);
        dt.startWatch(trackNo, trainNo, trainCount, specifyPressure, testKind);
        dt.setDataReceiveSendListener(this);
        for (TestContent tst : dt.tData.listTest) {
            tst.setOnTestFailedListener(this);
        }
        //开启自动发送获取数据报文定时器
        serviceSendCmd.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try{
                    //发送获取风压数据指令
                    dt.sendCommandToDevice(CTestWindProtocel.sendCmd);
                    //通知刷新显示试验状态信息
                    handler.sendEmptyMessage(MSG_DISPLAY_TEST_STATUS);
                    if (dt.isTestEnded()){
                        dt.tData.endTest();
                        endTest();
                        TipSoundPlayer.PlayVoicePrompts(TipSoundPlayer.VOICE_FILE_NAME_END_TEST_WIND);
                        if (0 != SysParamsAll.get_autoSave() && dt.tData.lstPressureValue.size() > 0){
                            dt.tData.update();
                            handler.sendEmptyMessage(MSG_CLOSE_WINDOW);
                        }
                    }
                } catch (Exception ex){
                    XLog.e("发送获取风压命令异常：" + ex.getMessage());
                }
            }
        }, SysParamsAll.get_sendCommandTimer(), SysParamsAll.get_sendCommandTimer(), TimeUnit.MILLISECONDS);
    }

    private void startReplay(){
        tvTrackNo.setText(trackNo);
        tvTrainNo.setText(trainNo);
        tvTrainCount.setText(trainCount);
        tvTestLeak.setVisibility(View.GONE);
        tvSave.setVisibility(View.GONE);
        dt.startReplay(testId);
        chartView.setCanScroll(true);
    }

    private void queryClose(String tipText, final boolean ifSave){
        AlertDialog.Builder builder = new AlertDialog.Builder(TestActivity.this);
        builder.setTitle("提示");
        builder.setMessage(tipText);
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (ifSave){
                    dt.tData.endTest();
                    dt.tData.update();
                }
                endTest();
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void tipRedoTest(){
        if (1 == SysParamsAll.get_autoRepeat() && null != dt.failedTest) {
            AlertDialog.Builder builder = new AlertDialog.Builder(TestActivity.this);
            builder.setTitle("提示");
            builder.setMessage(dt.failedTest.getTestName() + "已作业，是否重做？");
            builder.setNegativeButton("取消", null);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dt.failedTest.reset();
                    dt.currTest = dt.failedTest;
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private void doTestLeak(){
        if (dt.tData.lstPressureValue.size() <= 0){
            return;
        } else if (null != dt.currTest && !TestContent.TEST_NAME_LX.equals(dt.currTest.getTestName()) && dt.currTest.getStat() == TestContent.TestState.tsDoing){
            new AlertDialog.Builder(this).setTitle("提示").setMessage("正在进行" + dt.currTest.getTestName() + "作业。").setPositiveButton("确定", null).show();
        } else if (dt.tData.lstPressureValue.get(dt.tData.lstPressureValue.size() - 1).getPressureValue() <= SysParamsAll.get_testPressureValueMin(Integer.parseInt(dt.tData.getSpecifiedPressureValue()))){
            new AlertDialog.Builder(this).setTitle("提示").setMessage("压力低于充风下限。").setPositiveButton("确定", null).show();
        } else {
            tvTestLeak.setEnabled(false);
            dt.tData.testLx.reset();
            dt.currTest = dt.tData.testLx;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_testLeak:
                doTestLeak();
                break;
            case R.id.tv_exit:
                queryClose("是否退出？", false);
                break;
            case R.id.tv_save:
                queryClose("是否保存数据退出？", true);
                break;
            default:
                break;
        }
    }

    @Override
    public void onTestFailed() {
        dt.failedTest = dt.currTest;
        handler.sendEmptyMessage(MSG_TEST_FAILED);
    }

    @Override
    public void onDataReceiveSend(final int flag) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (flag) {
                    case RECV_DATA:
                        displayTestStatus();
                        break;
                    case RECV_COMMAND:
                        break;
                    case SEND_COMMAND:
                        break;
                    default:
                }
            }
        });
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            queryClose("是否退出？", false);
            return true;
        }
        return super.dispatchKeyEvent(event);
    }


    public static void startIntent(Activity context, @ViewTypes int type, String track, String trainNo, String trainCount, String specifiedPressure, String testId, TestKind testKind){
        //type:0-试风，1-试验重放展示
        Intent intent = new Intent(context,TestActivity.class);
        intent.putExtra(PARAM_INTENT_TYPE,type);
        intent.putExtra(PARAM_TRACK, track);
        intent.putExtra(PARAM_TRAIN_NO, trainNo);
        intent.putExtra(PARAM_TRAIN_COUNT, trainCount);
        intent.putExtra(PARAM_SPECIFIED_PRESSURE, specifiedPressure);
        intent.putExtra(PARAM_TEST_ID, testId);
        intent.putExtra(PARAM_TEST_KIND, testKind);
        context.startActivity(intent);
    }
}
