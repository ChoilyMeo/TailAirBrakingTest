package com.thnet.tailairbrakingtest.activitys;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.IntDef;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.elvishew.xlog.XLog;
import com.thnet.tailairbrakingtest.communication.CTestWindProtocel;
import com.thnet.tailairbrakingtest.communication.DataTransfer;
import com.thnet.tailairbrakingtest.communication.OnDataReceiveSendListener;
import com.thnet.tailairbrakingtest.communication.OnTestFailedListener;
import com.thnet.tailairbrakingtest.customapplication.WindTestApplication;
import com.thnet.tailairbrakingtest.customcontrol.ChartView;
import com.thnet.tailairbrakingtest.dao.TestKind;
import com.thnet.tailairbrakingtest.R;
import com.thnet.tailairbrakingtest.testwind.SysParamsAll;
import com.thnet.tailairbrakingtest.testwind.TestContent;
import com.thnet.tailairbrakingtest.testwind.Test_BY;
import com.thnet.tailairbrakingtest.utility.TipSoundPlayer;

import java.io.File;
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
    private static final int MSG_TIP_PRESSURE_TOO_HIGH = 4;//压力过高的提示
    public static final int VIEW_TYPE_TEST = 0;
    public static final int VIEW_TYPE_REPLAY = 1;
    @IntDef({
            VIEW_TYPE_TEST,
            VIEW_TYPE_REPLAY
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface ViewTypes {}
    private TextView tvTestName, tvTrackNo, tvTrainNo, tvTrainCount, tvTestLeak, tvSave, tvLeakUnit;
    private EditText etMainPressureValue, etDropValue, etKeepTime, etLeakValue;
    private LinearLayout llKeepTime, llLeakValue, llDropValue;
    private ChartView chartView;
    private String trackNo, trainNo, trainCount, specifyPressure, testId;
    private TestKind testKind;
    private DataTransfer dt;
    private ScheduledExecutorService serviceSendCmd = new ScheduledThreadPoolExecutor(1);
    private int vibrateTimes = 0;//压力超过定压的震动次数
    private int vibrateTimesLimit = 10;//设定的最大震动次数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置屏幕常亮
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_test);
        int intentType = getIntent().getIntExtra(PARAM_INTENT_TYPE,0);
        trackNo = getIntent().getStringExtra(PARAM_TRACK);
        trainNo = getIntent().getStringExtra(PARAM_TRAIN_NO);
        trainCount = getIntent().getStringExtra(PARAM_TRAIN_COUNT);
        specifyPressure = getIntent().getStringExtra(PARAM_SPECIFIED_PRESSURE);
        testId = getIntent().getStringExtra(PARAM_TEST_ID);
        testKind = getIntent().getParcelableExtra(PARAM_TEST_KIND);
        dt = new DataTransfer(testKind.getTestKindName());
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
        XLog.i("试验窗口关闭，停止试验！");
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
        tvLeakUnit = findViewById(R.id.tv_leakUnit);
        etMainPressureValue = findViewById(R.id.et_mainPressureValue);
        etDropValue = findViewById(R.id.et_dropValue);
        etKeepTime = findViewById(R.id.et_keepTime);
        etLeakValue = findViewById(R.id.et_leakValue);
        llKeepTime = findViewById(R.id.ll_keepTime);
        llLeakValue = findViewById(R.id.ll_leakValue);
        llDropValue = findViewById(R.id.ll_dropValue);
        tvTestLeak = findViewById(R.id.tv_testLeak);
        tvTestLeak.setOnClickListener(this);
        //根据漏泄试验的是否选择状态，来决定是否展示漏泄试验按钮
        if (testKind.isTestLXChecked()) {
            tvTestLeak.setVisibility(View.VISIBLE);
            tvTestLeak.setEnabled(false);
        } else {
            tvTestLeak.setVisibility(View.GONE);
            tvTestLeak.setEnabled(false);
        }
        tvSave = findViewById(R.id.tv_save);//保存按钮
        TextView tvExit = findViewById(R.id.tv_exit);//退出按钮
        tvSave.setOnClickListener(this);
        tvExit.setOnClickListener(this);
        chartView = findViewById(R.id.image_chart);
        chartView.setViewTestData(dt.tData);
        chartView.setCanScroll(false);
    }

    private void tipPressureTooHigh(){
        TipSoundPlayer.PlayVoicePrompts(TipSoundPlayer.VOICE_FILE_NAME_PRESSURE_TOO_HIGH);
        try {
            Vibrator vibrator = (Vibrator) this.getSystemService(this.VIBRATOR_SERVICE);
            vibrator.vibrate(500);
        } catch (Exception ex){
            XLog.e("震动提示异常：" + ex.getMessage());
        }
    }

    private void displayTestStatus(){
        try {
            if (tvTestLeak.isEnabled() == false && dt.tData.lstPressureValue.size() > 0 && (dt.currTest == null || dt.currTest.getTestName() != TestContent.TEST_NAME_LX)) {
                tvTestLeak.setEnabled(true);
            }
            XLog.i(String.format("testName=%s,testStat=%d",dt.currTest == null ? "null":dt.currTest.getTestName(),dt.currTest == null ? -1 : dt.currTest.getStat().ordinal()));
            //根据实验结果设置实验名称的颜色，由于实验结束之后，TestState=0，会导致无法更新显示颜色，因此把此处理和显示实验内容的处理分开
            if (null != dt.currTest && !tvTestName.getText().toString().isEmpty()
                    && tvTestName.getText().toString().equals(dt.currTest.getTestName())){
                //根据实验结果把实验名称的背景显示不同颜色
                if (TestContent.TEST_STATE_COMPLETED.equals(dt.currTest.getTestResult())){
                    //XLog.d("实验结果完成，设置为绿色");
                    tvTestName.setBackgroundColor(Color.GREEN);
                } else if (TestContent.TEST_STATE_NOT_COMPLETED.equals(dt.currTest.getTestResult())){
                    //XLog.d("实验结果未完成，设置为红色");
                    tvTestName.setBackgroundColor(Color.RED);
                } else {
                    //XLog.d("实验结果其他，设置为灰色");
                    tvTestName.setBackgroundColor(getResources().getColor(R.color.color_gray_c));
                }
            }
            if (null != dt.currTest && (dt.currTest.getStat() == TestContent.TestState.tsDoing || dt.currTest.getStat() == TestContent.TestState.tsStoped)) {
                //持续保压实验，如果是取每分钟漏泄量，由于漏泄量显示不下，所以隐藏单位，其他正常显示
                if (dt.currTest instanceof Test_BY && SysParamsAll.CHECK_LEAK_VALUE_TYPE_PER_MINUTE == ((Test_BY) dt.currTest).getCheckLeakValueType()){
                    if (tvLeakUnit.getVisibility() == View.VISIBLE){
                        tvLeakUnit.setVisibility(View.GONE);
                    }
                } else {
                    if (tvLeakUnit.getVisibility() == View.GONE){
                        tvLeakUnit.setVisibility(View.VISIBLE);
                    }
                }
                llDropValue.setVisibility(dt.currTest.getViewStatDropValue());
                llKeepTime.setVisibility(dt.currTest.getViewStatKeepTime());
                llLeakValue.setVisibility(dt.currTest.getViewStatLeakValue());
                String pressureValue, keepTime, leakValue, dropValue;
                pressureValue = String.valueOf(dt.currTest.getTestMainPressureValue());
                keepTime = String.valueOf(dt.currTest.getTestKeepTime());
                leakValue = dt.currTest.getTestLeakValueDisplay();
                dropValue = String.valueOf(dt.currTest.getTestDropValue());
                tvTestName.setText(dt.currTest.getTestName());
                etMainPressureValue.setText(pressureValue);
                etDropValue.setText(dropValue);
                etKeepTime.setText(keepTime);
                etLeakValue.setText(leakValue);
                XLog.i(String.format("testname=%s,dropValueEnabled=%d,leakValueEnabled=%d,keepTimeEnabled=%d",
                        dt.currTest.getTestName(),
                        dt.currTest.getViewStatDropValue(),
                        dt.currTest.getViewStatLeakValue(),
                        dt.currTest.getViewStatKeepTime()));
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
                case MSG_TIP_PRESSURE_TOO_HIGH:
                    tipPressureTooHigh();
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
                    dt.sendCommandToDevice(CTestWindProtocel.getSendCmd());
                    //通知刷新显示试验状态信息
                    handler.sendEmptyMessage(MSG_DISPLAY_TEST_STATUS);
                    if (dt.isTestEnded()){
                        dt.tData.endTest();
                        endTest();
                        TipSoundPlayer.PlayVoicePrompts(TipSoundPlayer.VOICE_FILE_NAME_END_TEST_WIND);
                        XLog.e("状态异常：" + SysParamsAll.get_autoSave()+","+dt.tData.lstPressureValue.size());
                        if (0 != SysParamsAll.get_autoSave() && dt.tData.lstPressureValue.size() > 0){
                            //dt.tData.update();
                            if (saveData()) {
                                Toast.makeText(TestActivity.this, "试验结束，数据自动保存成功。", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(TestActivity.this, "试验结束，数据保存失败！", Toast.LENGTH_SHORT).show();
                            }
                            handler.sendEmptyMessage(MSG_CLOSE_WINDOW);
                        }
                    }
                    //handler.sendEmptyMessage(MSG_TIP_PRESSURE_TOO_HIGH);
                    //压力超过定压则震动加语音提示，连续震动超过vibrateTimesLimit次后不再震动
                    if (null != dt.currTest){
                        if (dt.currTest.isPressureOverflowFlag()){
                            if (vibrateTimes < vibrateTimesLimit){
                                handler.sendEmptyMessage(MSG_TIP_PRESSURE_TOO_HIGH);
                                vibrateTimes ++;
                            }
                        } else {
                            vibrateTimes = 0;
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
                endTest();
                if (ifSave){
                    dt.tData.endTest();
//                    dt.tData.update();
                    if (saveData()) {
                        Toast.makeText(TestActivity.this, "数据保存完毕，正在退出。", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(TestActivity.this, "数据保存失败！", Toast.LENGTH_SHORT).show();
                    }
                }
                //endTest();
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * 通过发送广播通知系统刷新文件
     * @param filePath 文件路径
     */
    public static void notifySystemToScan(String filePath) {
        MediaScannerConnection.scanFile(WindTestApplication.getWindTestInstance(),
                new String[]{filePath}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        if (uri != null) {
                            // 扫描完成，文件已被系统媒体库识别
                            XLog.i("扫描完成，文件已被系统媒体库识别");
                        } else {
                            // 扫描失败
                            XLog.i("文件同步媒体库扫描失败");
                        }
                    }
                });
//        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//        File file = new File(filePath);
//        Uri uri = Uri.fromFile(file);
//        intent.setData(uri);
//        WindTestApplication.getWindTestInstance().sendBroadcast(intent);
    }

    //怀疑db文件没有及时刷新，导致db文件复制到电脑端后和手持机中不一致，因此在保存数据后刷新文件夹
    private Boolean saveData(){
        if (dt.tData.update()) {
            notifySystemToScan(getExternalFilesDir("databases").getAbsolutePath());
            return true;
        } else {
            XLog.e("数据保存失败！");
            return false;
        }
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
                    dt.lstTemp.clear();
                    dt.lstEsti.clear();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private void doTestLeak(){
        if (dt.tData.lstPressureValue.size() <= 0){
            Toast.makeText(this, "压力数据未就绪。", Toast.LENGTH_LONG).show();
        } else if (null != dt.currTest && !TestContent.TEST_NAME_LX.equals(dt.currTest.getTestName()) && dt.currTest.getStat() == TestContent.TestState.tsDoing){
            Toast.makeText(this, "正在进行" + dt.currTest.getTestName() + "作业。", Toast.LENGTH_LONG).show();
        } else if (dt.tData.lstPressureValue.get(dt.tData.lstPressureValue.size() - 1).getPressureValue() <= SysParamsAll.get_testPressureValueMin(Integer.parseInt(dt.tData.getSpecifiedPressureValue()))){
            Toast.makeText(this, "压力低于充风下限。", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "漏泄试验准备就绪。", Toast.LENGTH_LONG).show();
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
        if(trainCount == null || trainCount.isEmpty()){
            trainCount = "0";
        }
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
