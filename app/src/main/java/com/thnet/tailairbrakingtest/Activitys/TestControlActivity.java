package com.thnet.tailairbrakingtest.Activitys;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.elvishew.xlog.XLog;
import com.thnet.tailairbrakingtest.Communication.CTestWindProtocel;
import com.thnet.tailairbrakingtest.Communication.DataTransfer;
import com.thnet.tailairbrakingtest.Communication.OnDataReceiveSendListener;
import com.thnet.tailairbrakingtest.Communication.OnTestFailedListener;
import com.thnet.tailairbrakingtest.DAO.PressureValue;
import com.thnet.tailairbrakingtest.R;
import com.thnet.tailairbrakingtest.SerialPort.RF433PowerControl;
import com.thnet.tailairbrakingtest.TestWind.SysParamsAll;
import com.thnet.tailairbrakingtest.TestWind.TestContent;
import com.thnet.tailairbrakingtest.TestWind.TestViewContent;
import com.thnet.tailairbrakingtest.TestWind.Test_KLW;
import com.thnet.tailairbrakingtest.TestWind.Test_KLWWB;
import com.thnet.tailairbrakingtest.TestWind.Test_KSJY;
import com.thnet.tailairbrakingtest.TestWind.Test_LX;
import com.thnet.tailairbrakingtest.TestWind.Test_YLJY;
import com.thnet.tailairbrakingtest.adapters.AdapterTestViewCalibration;
import com.thnet.tailairbrakingtest.adapters.AdapterTestViewTrain;
import com.thnet.tailairbrakingtest.views.ListViewNoScroll;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TestControlActivity extends AppCompatActivity implements View.OnClickListener, OnTestFailedListener, OnDataReceiveSendListener {

    private static final String TAG = TestControlActivity.class.getSimpleName();
    private static final String PARAM_INTENT_TYPE = "intent_type";
    private static final String PARAM_TRACK = "track";
    private static final String PARAM_TRAIN_NO = "trainNo";
    private static final String PARAM_TRAIN_COUNT = "trainCount";
    private static final String PARAM_SPECIFIED_PRESSURE = "specifiedPressure";
    private static final String PARAM_HAND_DEVICE_NO = "handDeviceNo";
    private static final String PARAM_DEVICE_NO = "deviceNo";
    private static final String PARAM_OPERATOR_CLASS = "operatorClass";
    private static final String PARAM_OPERATOR_GROUP = "operatorGroup";
    private static final String PARAM_TRAIN_HEAD_NO = "trainHeadNo";
    private static final String PARAM_TRAIN_TAIL_NO = "trainTailNo";
    private static final String PARAM_TRAIN_BACKUP = "trainBackupNo";
    private static final String PARAM_IS_MANUAL_TEST = "isManualTest";

    private TextView tvHideControl, tv_CurrTime;
    private LinearLayout llAllBtns, llShow, ll_passenger;
    private ListViewNoScroll lv_train, lv_calibration, lv_passenger;
    AdapterTestViewTrain adapterTestViewTrain, adapterTestViewPassenger;
    AdapterTestViewCalibration adapterTestViewCalibration;
    private CTestWindProtocel.TestCommandExt intentType = CTestWindProtocel.TestCommandExt.tkNormal;
    private int isManualTest = 0;//是否手动实验，根据上一界面的选择赋值，1-手动试验、0-正常试验
    private DataTransfer dt;
    private Timer timer;
    private List<TestViewContent> lstTestTrainInfo, lstTestPassengerInfo, lstTestCalibrationInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置屏幕常亮
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_test_control);

        //433模块上电
        RF433PowerControl.powerOn();
        //传入参数
        intentType = CTestWindProtocel.TestCommandExt.values()[getIntent().getIntExtra(PARAM_INTENT_TYPE, 0)];
        isManualTest = getIntent().getIntExtra(PARAM_IS_MANUAL_TEST, 0);
        String track = getIntent().getStringExtra(PARAM_TRACK);
        String trainNo = getIntent().getStringExtra(PARAM_TRAIN_NO);
        String trainCount = getIntent().getStringExtra(PARAM_TRAIN_COUNT);
        String specifiedPressure = getIntent().getStringExtra(PARAM_SPECIFIED_PRESSURE);
        String handDeviceNo = getIntent().getStringExtra(PARAM_HAND_DEVICE_NO);
        String deviceNo = getIntent().getStringExtra(PARAM_DEVICE_NO);
        String operatorClass = getIntent().getStringExtra(PARAM_OPERATOR_CLASS);
        String operatorGroup = getIntent().getStringExtra(PARAM_OPERATOR_GROUP);
        String trainHeadNo = getIntent().getStringExtra(PARAM_TRAIN_HEAD_NO);
        String trainTailNo = getIntent().getStringExtra(PARAM_TRAIN_TAIL_NO);
        String trainBackupNo = getIntent().getStringExtra(PARAM_TRAIN_BACKUP);
        //初始化试验数据
        dt = new DataTransfer();
        dt.setDataReceiveSendListener(this);
        dt.StartWatch(track, trainNo, trainCount, specifiedPressure, "", handDeviceNo, deviceNo, operatorClass, operatorGroup, trainHeadNo, trainTailNo, trainBackupNo);
        for (TestContent tst : dt.tData.listTest) {
            tst.setOnTestFailedListener(this);
        }
        //初始化显示界面
        initView();
        //根据设置的定时器时间，定时刷新界面
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //displayTestStatus();
                    }
                });
            }
        }, SysParamsAll.get_sendCommandTimer(), SysParamsAll.get_sendCommandTimer());
    }

    @Override
    protected void onDestroy() {
        if (null != timer) {
            timer.cancel();
            timer = null;
        }
        //关闭串口
        dt.StopWatch();
        //433模块断电
        RF433PowerControl.powerOff();
        super.onDestroy();
    }

    private void initView() {
        //所有按钮的最外层
        llAllBtns = findViewById(R.id.ll_allbtns);
        GridLayout glTestWind = findViewById(R.id.gl_testWind);//试风按钮列表
        GridLayout glEnginery = findViewById(R.id.gl_enginery);//机能按钮列表
        LinearLayout llCheck = findViewById(R.id.ll_check);//校验按钮列表

        //点击隐藏后要显示的部分
        llShow = findViewById(R.id.ll_show);
        ll_passenger = findViewById(R.id.ll_passenger);
        //隐藏控制按钮
        tvHideControl = findViewById(R.id.tv_hideControl);
        tvHideControl.setOnClickListener(this);
        //显示控制按钮
        TextView tvShowControl = findViewById(R.id.tv_showControl);
        tvShowControl.setOnClickListener(this);
        TextView tvExit = findViewById(R.id.tv_exit);//退出按钮
        tvExit.setOnClickListener(this);
        findViewById(R.id.tv_beginTest).setOnClickListener(this);//开始作业
        findViewById(R.id.tv_cancelTest).setOnClickListener(this);//取消作业
        findViewById(R.id.tv_endTest).setOnClickListener(this);//结束作业
        findViewById(R.id.tv_ventilationRelief).setOnClickListener(this);//充风缓解
        findViewById(R.id.tv_leakTest).setOnClickListener(this);//漏泄试验
        findViewById(R.id.tv_sentisitivityTest).setOnClickListener(this);//感度试验
        findViewById(R.id.tv_stabilityTest).setOnClickListener(this);//安定试验
        findViewById(R.id.tv_pressureHoldingTest).setOnClickListener(this);//持续保压
        TextView tvAllWindLeak = findViewById(R.id.tv_allWindLeak);//总风漏泄
        tvAllWindLeak.setOnClickListener(this);//总风漏泄
        TextView tvHeadTest = findViewById(R.id.tv_headTest);//客首
        tvHeadTest.setOnClickListener(this);//客首
        TextView tvTailTest = findViewById(R.id.tv_tailTest);//客尾
        tvTailTest.setOnClickListener(this);//客尾
        findViewById(R.id.tv_functionRelief).setOnClickListener(this);//机能缓解
        findViewById(R.id.tv_functionLeak).setOnClickListener(this);//机能漏泄
        findViewById(R.id.tv_sentisitivityBraking).setOnClickListener(this);//感度制动
        findViewById(R.id.tv_sentisitivityRelief).setOnClickListener(this);//感度缓解
        findViewById(R.id.tv_functionStability).setOnClickListener(this);//机能安定
        findViewById(R.id.tv_pressureCalibration).setOnClickListener(this);//压力校准
        findViewById(R.id.tv_fastCalibration).setOnClickListener(this);//快速校准
        findViewById(R.id.tv_save).setOnClickListener(this);//保存
        findViewById(R.id.tv_klw_position).setOnClickListener(this);//客列尾 首部/尾部
        //顶部字样
        TextView tvTrack = findViewById(R.id.tv_track);
        tvTrack.setText("股道:" + getIntent().getStringExtra(PARAM_TRACK));
        TextView tvTrainNo = findViewById(R.id.tv_trainNo);
        tvTrainNo.setText("车次:" + getIntent().getStringExtra(PARAM_TRAIN_NO));
        TextView tvTrainCount = findViewById(R.id.tv_trainCount);
        tvTrainCount.setText("辆数:" + getIntent().getStringExtra(PARAM_TRAIN_COUNT));
        TextView tvValveNo = findViewById(R.id.tv_volveNo);
        tvValveNo.setText("阀号:" + getIntent().getStringExtra(PARAM_DEVICE_NO));
        //显示内容
        //试风显示内容
        lstTestTrainInfo = new ArrayList<TestViewContent>();
        lstTestTrainInfo.add(new TestViewContent("主管", "0", "KPa/s"));
        lstTestTrainInfo.add(new TestViewContent("减压", "0", "KPa/s"));
        lstTestTrainInfo.add(new TestViewContent("保压", "0", "KPa/s"));
        lstTestTrainInfo.add(new TestViewContent("速度", "0", "KPa/s"));
        lstTestTrainInfo.add(new TestViewContent("漏泄", "0", "KPa/s"));
        lv_train = findViewById(R.id.lv_train);
        adapterTestViewTrain = new AdapterTestViewTrain(this, lstTestTrainInfo);
        lv_train.setAdapter(adapterTestViewTrain);
        lv_train.setVisibility(View.GONE);
        //客列尾显示内容
        lstTestPassengerInfo = new ArrayList<TestViewContent>();
        lstTestPassengerInfo.add(new TestViewContent("输号", "", ""));
        lstTestPassengerInfo.add(new TestViewContent("600KPa", "", ""));
        lstTestPassengerInfo.add(new TestViewContent("欠压提示", "", ""));
        lstTestPassengerInfo.add(new TestViewContent("500KPa", "", ""));
        lstTestPassengerInfo.add(new TestViewContent("排风", "", ""));
        lstTestPassengerInfo.add(new TestViewContent("紧急制动", "", ""));
        lstTestPassengerInfo.add(new TestViewContent("销号", "", ""));
        lv_passenger = findViewById(R.id.lv_passenger);
        adapterTestViewPassenger = new AdapterTestViewTrain(this, lstTestPassengerInfo);
        lv_passenger.setAdapter(adapterTestViewPassenger);
        lv_passenger.setVisibility(View.GONE);
        //压力校准显示内容
        lstTestCalibrationInfo = new ArrayList<TestViewContent>();
        lstTestCalibrationInfo.add(new TestViewContent("", "", "", "", ""));
        lstTestCalibrationInfo.add(new TestViewContent("总", "", "", "", ""));
        lstTestCalibrationInfo.add(new TestViewContent("列", "", "", "", ""));
        lstTestCalibrationInfo.add(new TestViewContent("中", "", "", "", ""));
        lstTestCalibrationInfo.add(new TestViewContent("标", "", "", "", ""));
        lv_calibration = findViewById(R.id.lv_calibration);
        adapterTestViewCalibration = new AdapterTestViewCalibration(this, lstTestCalibrationInfo);
        lv_calibration.setAdapter(adapterTestViewCalibration);
        lv_calibration.setVisibility(View.GONE);
        ll_passenger.setVisibility(View.GONE);
        tv_CurrTime = findViewById(R.id.tv_currTime);

        //type:0-试风，2-机能，1-校验
        if (intentType == CTestWindProtocel.TestCommandExt.tkNormal) {
            llAllBtns.setVisibility(View.VISIBLE);
            glTestWind.setVisibility(View.VISIBLE);
            glEnginery.setVisibility(View.GONE);
            llCheck.setVisibility(View.GONE);
        } else if (intentType == CTestWindProtocel.TestCommandExt.tkMachineAbility) {
            llAllBtns.setVisibility(View.VISIBLE);
            glTestWind.setVisibility(View.GONE);
            glEnginery.setVisibility(View.VISIBLE);
            llCheck.setVisibility(View.GONE);
        } else if (intentType == CTestWindProtocel.TestCommandExt.tkPressureCalibration) {
            llAllBtns.setVisibility(View.VISIBLE);
            glTestWind.setVisibility(View.GONE);
            glEnginery.setVisibility(View.GONE);
            llCheck.setVisibility(View.VISIBLE);
        } else {
            glTestWind.setVisibility(View.GONE);
            glEnginery.setVisibility(View.GONE);
            llCheck.setVisibility(View.GONE);
        }
        //未输入列尾编号，则隐藏列尾实验按钮
        String trainHeadNo = getIntent().getStringExtra(PARAM_TRAIN_HEAD_NO);
        String trainTailNo = getIntent().getStringExtra(PARAM_TRAIN_TAIL_NO);
        if (trainHeadNo.isEmpty() || trainHeadNo.equals("000000")) {
            tvHeadTest.setVisibility(View.GONE);
        }
        if (trainTailNo.isEmpty() || trainTailNo.equals("000000")) {
            tvTailTest.setVisibility(View.GONE);
        }
        //货车需要隐藏总风漏泄按钮
        if (1 == SysParamsAll.get_kehuoche()) {
            tvAllWindLeak.setVisibility(View.GONE);
        }
        //手动实验隐藏总风漏泄
        if (1 == isManualTest) {
            tvAllWindLeak.setVisibility(View.GONE);
        }
        displayTestStatus();
    }

    private void displayTestStatus() {
        try{
            TextView tv_testName = findViewById(R.id.tv_testName);//试验名称
            TextView tv_testResult = findViewById(R.id.tv_testResult);//试验结果
            TextView tv_temperature = findViewById(R.id.tv_temperature);//温度
            TextView tv_humidity = findViewById(R.id.tv_humidity);//湿度
            TextView tv_pressureTail = findViewById(R.id.tv_pressureTail);//列尾压力
            TextView tv_pressureHead = findViewById(R.id.tv_pressureHead);//列首压力
            TextView tv_pressureSource = findViewById(R.id.tv_pressureSource);//列源压力
            TextView tv_pressureCenter = findViewById(R.id.tv_pressureCenter);//中控压力
            TextView tv_testState = findViewById(R.id.tv_testState);//试验状态
            TextView tv_klw_position = findViewById(R.id.tv_klw_position);//客列尾试验位置
            TextView tv_trainID = findViewById(R.id.tv_trainID);//客列尾ID
            TextView tv_passengerTestState = findViewById(R.id.tv_passengerTestState);//客列尾试验状态
            //显示接收的报文中的压力数据
            tv_testResult.setText("");
            tv_temperature.setText("");
            tv_humidity.setText("");
            tv_CurrTime.setText("");
            tv_pressureTail.setText("尾");
            tv_pressureHead.setText("列");
            tv_pressureCenter.setText("中");
            //tv_pressureSource.setText("源");
            if (dt.tData.lstPressureValue.size() > 0) {
                PressureValue pressureValue = dt.tData.lstPressureValue.get(dt.tData.lstPressureValue.size() - 1);
                tv_pressureTail.setText("尾" + pressureValue.getPressureValue());
                tv_pressureHead.setText("列" + pressureValue.getHeadPressureValue());
                tv_pressureCenter.setText("中" + pressureValue.getCenterPressureValue());
                if ((pressureValue.getSourcePressureValue() * 10) >> 15 == 0) {
                    tv_pressureSource.setText("源" + pressureValue.getSourcePressureValue());
                }
                tv_temperature.setText("温度" + pressureValue.getBoxTemperature());
                tv_humidity.setText("湿度" + pressureValue.getBoxHumidity());
            }
            //显示接收报文中的试验进行步骤
            switch (dt.currTestStatus) {
                case TestBegin:
                    tv_testState.setText("作业开始");
                    break;
                case TestFillWindEnd:
                    tv_testState.setText("充风完成");
                    break;
                case TestDoing:
                    tv_testState.setText("作业中");
                    break;
                case TestDropPressureBegin:
                    tv_testState.setText("减压开始");
                    break;
                case TestDropPressureEnd:
                    tv_testState.setText("减压结束");
                    break;
                case TestKeep:
                    tv_testState.setText("保压开始");
                    break;
                case TestEnd:
                    tv_testState.setText("作业结束");
                    break;
                case TestNone:
                    tv_testState.setText("");
                    break;
                    default:
            }
            if (null != dt.currTest) {
                TestContent testContent = dt.currTest;
                tv_testName.setText(testContent.get_testName());
                tv_testResult.setText(testContent.get_state());
                ll_passenger.setVisibility(View.GONE);
                lv_train.setVisibility(View.GONE);
                lv_passenger.setVisibility(View.GONE);
                lv_calibration.setVisibility(View.GONE);
                switch (testContent.get_testViewType()){
                    case TestContent.V_PASSENGER:
                        ll_passenger.setVisibility(View.VISIBLE);
                        tv_klw_position.setText(testContent.get_passengerTrainPosition());
                        tv_trainID.setText(testContent.get_passengerTrainID());
                        tv_passengerTestState.setText(testContent.get_state());
                        lv_passenger.setVisibility(View.VISIBLE);
                        lstTestPassengerInfo.clear();
                        lstTestPassengerInfo.addAll(testContent.get_listViewContent());
                        adapterTestViewPassenger.notifyDataSetChanged();
                        break;
                    case TestContent.V_NORMAL:
                        lv_train.setVisibility(View.VISIBLE);
                        lstTestTrainInfo.clear();
                        lstTestTrainInfo.addAll(testContent.get_listViewContent());
                        adapterTestViewTrain.notifyDataSetChanged();
                        break;
                    case TestContent.V_CALIBRATION:
                        lv_calibration.setVisibility(View.VISIBLE);
                        tv_pressureTail.setText("标" + dt.tData.lstPressureValue.get(dt.tData.lstPressureValue.size() - 1).getTestResult());
                        lstTestCalibrationInfo.clear();
                        lstTestCalibrationInfo.addAll(testContent.get_listViewContent());
                        adapterTestViewCalibration.notifyDataSetChanged();
                        break;
                        default:
                }
            } else {
                ll_passenger.setVisibility(View.GONE);
                lv_train.setVisibility(View.GONE);
                lv_calibration.setVisibility(View.GONE);
                if (dt.currTestCommand == CTestWindProtocel.TestCommand.PressureRelief || dt.currTestCommand == CTestWindProtocel.TestCommand.TestJNHJ) {
                    tv_testName.setText("充风缓解");
                } else if (dt.currTestCommand == CTestWindProtocel.TestCommand.StopTest || dt.currTestCommand == CTestWindProtocel.TestCommand.TestCancel) {
                    tv_testName.setText("取消作业");
                } else if (dt.currTestCommand == CTestWindProtocel.TestCommand.TestEnd) {
                    tv_testName.setText("结束作业");
                } else if (dt.currTestCommand == CTestWindProtocel.TestCommand.PressureCheck) {
                    tv_testName.setText("压力校验");
                } else if (dt.currTestCommand == CTestWindProtocel.TestCommand.TestBegin || dt.currTestCommand == CTestWindProtocel.TestCommand.TestJNBegin ||
                        dt.currTestCommand == CTestWindProtocel.TestCommand.PressureCheckReady) {
                    tv_testName.setText("准备作业");
                } else {
                    tv_testName.setText(String.valueOf(dt.currTestCommand.getValue()));
                }
            }
        } catch (Exception ex) {
            XLog.e("试风信息显示异常：" + ex.getMessage());
        }
    }

    private void hideControl() {
        llAllBtns.setVisibility(View.GONE);
        llShow.setVisibility(View.VISIBLE);
        tvHideControl.setVisibility(View.GONE);
    }

    private void showControl() {
        llAllBtns.setVisibility(View.VISIBLE);
        llShow.setVisibility(View.VISIBLE);
        tvHideControl.setVisibility(View.VISIBLE);
    }

    private void processSendCommand(CTestWindProtocel.TestCommand cmd) {
        int rtn = dt.SendCommandToDevice(CTestWindProtocel.MergerTestCommand(cmd.getValue(), dt.tData.get_DingYaInt()));
        if (0 != rtn) {
            Toast.makeText(this, "控制指令发送失败" + rtn, Toast.LENGTH_SHORT).show();
        }
        hideControl();
    }

    private void onCloseQuery(){
        AlertDialog.Builder builder = new AlertDialog.Builder(TestControlActivity.this);
        builder.setTitle("提示");
        builder.setMessage("是否退出？");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //toast("取消");
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                processSendCommand(CTestWindProtocel.TestCommand.StopTest);
                if (dt.currTest != null && dt.currTest.get_stat() == TestContent.TestState.tsDoing) {
                    dt.currTest.Reset();
                    dt.currTest = null;
                }
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onTestFailed() {
        if (1 == SysParamsAll.get_autoRepeat() && null != dt.failedTest) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AlertDialog.Builder builder = new AlertDialog.Builder(TestControlActivity.this);
                    builder.setTitle("提示");
                    builder.setMessage(dt.failedTest.get_testName() + "已作业，是否重做？");
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //toast("取消");
                        }
                    });
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dt.failedTest.Reset();
                            dt.currTest = dt.failedTest;
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
        }
    }

    /**
     * 接收到数据后，根据标志，设置显示的时间的颜色
     *
     * @param flag ：0-接收到数据、1-接收到指令、2-发送指令
     */
    @Override
    public void OnDataReceiveSend(final int flag) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (flag) {
                    case RECV_DATA:
                        tv_CurrTime.setTextColor(Color.GREEN);
                        displayTestStatus();
                        break;
                    case RECV_COMMAND:
                        tv_CurrTime.setTextColor(Color.YELLOW);
                        break;
                    case SEND_COMMAND:
                        tv_CurrTime.setTextColor(Color.BLUE);
                        break;
                        default:
                }
                tv_CurrTime.setText(new SimpleDateFormat("HH:mm:ss").format(new Date(System.currentTimeMillis())));
            }
        });
    }

    @Override
    public void onClick(View v) {
        int rtn = 0;
        switch (v.getId()) {
            case R.id.tv_hideControl:
                hideControl();
                break;
            case R.id.tv_showControl:
                showControl();
                break;
            case R.id.tv_beginTest://开始作业
                CTestWindProtocel.TestCommand currCmd = CTestWindProtocel.TestCommand.TestBegin;
                switch (intentType) {
                    case tkNormal:
                        currCmd = CTestWindProtocel.TestCommand.TestBegin;
                        break;
                    case tkMachineAbility:
                        currCmd = CTestWindProtocel.TestCommand.TestJNBegin;
                        dt.tData.set_CheCi("");
                        dt.tData.set_GuDao("0");
                        dt.tData.set_Liangshu("88");
                        break;
                    case tkPressureCalibration:
                        currCmd = CTestWindProtocel.TestCommand.PressureCheckReady;
                        dt.tData.set_CheCi("");
                        dt.tData.set_GuDao("0");
                        dt.tData.set_Liangshu("88");
                        break;
                        default:
                }
                processSendCommand(currCmd);
                break;
            case R.id.tv_cancelTest://取消作业
                processSendCommand(CTestWindProtocel.TestCommand.StopTest);
                if (dt.currTest != null && dt.currTest.get_stat() == TestContent.TestState.tsDoing) {
                    dt.currTest.Reset();
                    dt.currTest = null;
                }
                break;
            case R.id.tv_endTest://结束作业
                processSendCommand(CTestWindProtocel.TestCommand.TestEnd);
                break;
            case R.id.tv_ventilationRelief://充风缓解
                processSendCommand(CTestWindProtocel.TestCommand.PressureRelief);
                break;
            case R.id.tv_leakTest://漏泄试验
                if (dt.currTest != null && dt.currTest.get_testName() == TestContent.CStrTestName_LX && dt.currTest.get_stat() == TestContent.TestState.tsDoing) {
                    return;
                }
                Test_LX todoTestLX = (Test_LX) dt.tData.GetTestByName(TestContent.CStrTestName_LX);
                if (todoTestLX != null) {
                    todoTestLX.Reset();
                } else {
                    todoTestLX = new Test_LX(dt.tData.get_DingYaInt(), dt.tData.get_LiangshuInt(), TestContent.TestState.tsNotBegin);
                    todoTestLX.setOnTestFailedListener(this);
                    dt.tData.listTest.add(todoTestLX);
                }
                processSendCommand(CTestWindProtocel.TestCommand.TestLX);
                break;
            case R.id.tv_sentisitivityTest://感度试验
                if (dt.currTest == dt.tData._GDSY && dt.currTest.get_stat() == TestContent.TestState.tsDoing) {
                    return;
                }
                dt.tData._GDSY.Reset();
                processSendCommand(CTestWindProtocel.TestCommand.TestGD);
                break;
            case R.id.tv_stabilityTest://安定试验
                if (dt.currTest == dt.tData._ADSY && dt.currTest.get_stat() == TestContent.TestState.tsDoing) {
                    return;
                }
                dt.tData._ADSY.Reset();
                processSendCommand(CTestWindProtocel.TestCommand.TestAD);
                break;
            case R.id.tv_pressureHoldingTest://持续保压
                if (dt.currTest == dt.tData._BYSY && dt.currTest.get_stat() == TestContent.TestState.tsDoing) {
                    return;
                }
                dt.tData._BYSY.Reset();
                processSendCommand(CTestWindProtocel.TestCommand.TestBY);
                break;
            case R.id.tv_allWindLeak://总风漏泄
                if (dt.currTest == dt.tData._ZFLX && dt.currTest.get_stat() == TestContent.TestState.tsDoing) {
                    return;
                }
                dt.tData._ZFLX.Reset();
                processSendCommand(CTestWindProtocel.TestCommand.TestZFLX);
                break;
            case R.id.tv_headTest://客首
                if (dt.currTest != null && dt.currTest.get_testName() == TestContent.CStrTestName_KLW && dt.currTest.get_stat() == TestContent.TestState.tsDoing) {
                    return;
                }
                Test_KLW todoTestKLW = (Test_KLW) dt.tData.GetTestByName(TestContent.CStrTestName_KLW);
                if (todoTestKLW != null) {
                    todoTestKLW.Reset();
                } else {
                    todoTestKLW = new Test_KLW(dt.tData.get_DingYaInt(), dt.tData.get_LiangshuInt(), TestContent.TestState.tsNotBegin);
                    todoTestKLW.set_passengerTrainID(dt.tData.get_KeLieWeiID1());
                    todoTestKLW.setOnTestFailedListener(this);
                    dt.tData.listTest.add(todoTestKLW);
                }
                processSendCommand(CTestWindProtocel.TestCommand.TestKLW);
                break;
            case R.id.tv_tailTest://客尾
                if (dt.currTest != null && dt.currTest.get_testName() == TestContent.CStrTestName_KLWWB && dt.currTest.get_stat() == TestContent.TestState.tsDoing) {
                    return;
                }
                Test_KLWWB todoTestKLWWB = (Test_KLWWB) dt.tData.GetTestByName(TestContent.CStrTestName_KLWWB);
                if (todoTestKLWWB != null) {
                    todoTestKLWWB.Reset();
                } else {
                    todoTestKLWWB = new Test_KLWWB(dt.tData.get_DingYaInt(), dt.tData.get_LiangshuInt(), TestContent.TestState.tsNotBegin);
                    todoTestKLWWB.set_passengerTrainID(dt.tData.get_KeLieWeiID2());
                    todoTestKLWWB.setOnTestFailedListener(this);
                    dt.tData.listTest.add(todoTestKLWWB);
                }
                processSendCommand(CTestWindProtocel.TestCommand.TestKLWWB);
                break;
            case R.id.tv_functionRelief://机能缓解
                processSendCommand(CTestWindProtocel.TestCommand.TestJNHJ);
                break;
            case R.id.tv_functionLeak://机能漏泄
                if (dt.currTest == dt.tData._JNLX && dt.currTest.get_stat() == TestContent.TestState.tsDoing) {
                    return;
                }
                dt.tData._JNLX.Reset();
                processSendCommand(CTestWindProtocel.TestCommand.TestJNLX);
                break;
            case R.id.tv_sentisitivityBraking://感度制动
                if (dt.currTest == dt.tData._JNGD && dt.currTest.get_stat() == TestContent.TestState.tsDoing) {
                    return;
                }
                dt.tData._JNGD.Reset();
                processSendCommand(CTestWindProtocel.TestCommand.TestJNGD);
                break;
            case R.id.tv_sentisitivityRelief://感度缓解
                if (dt.currTest == dt.tData._JGHJ && dt.currTest.get_stat() == TestContent.TestState.tsDoing) {
                    return;
                }
                dt.tData._JGHJ.Reset();
                processSendCommand(CTestWindProtocel.TestCommand.TestJGHJ);
                break;
            case R.id.tv_functionStability://机能安定
                if (dt.currTest == dt.tData._JNAD && dt.currTest.get_stat() == TestContent.TestState.tsDoing) {
                    return;
                }
                dt.tData._JNAD.Reset();
                processSendCommand(CTestWindProtocel.TestCommand.TestJNAD);
                break;
            case R.id.tv_pressureCalibration://压力校准
                if (dt.currTest != null && dt.currTest.get_testName() == TestContent.CStrTestName_YLJY && dt.currTest.get_stat() == TestContent.TestState.tsDoing) {
                    return;
                }
                Test_YLJY todoTestYLJY = (Test_YLJY) dt.tData.GetTestByName(TestContent.CStrTestName_YLJY);
                if (todoTestYLJY != null) {
                    todoTestYLJY.Reset();
                } else {
                    todoTestYLJY = new Test_YLJY(dt.tData.get_DingYaInt(), dt.tData.get_LiangshuInt(), TestContent.TestState.tsNotBegin);
                    todoTestYLJY.setOnTestFailedListener(this);
                    dt.tData.listTest.add(todoTestYLJY);
                }
                processSendCommand(CTestWindProtocel.TestCommand.PressureCheck);
                break;
            case R.id.tv_fastCalibration://快速校准
                if (dt.currTest != null && dt.currTest.get_testName() == TestContent.CStrTestName_KSJY && dt.currTest.get_stat() == TestContent.TestState.tsDoing) {
                    return;
                }
                Test_KSJY todoTestKSJY = (Test_KSJY) dt.tData.GetTestByName(TestContent.CStrTestName_KSJY);
                if (todoTestKSJY != null) {
                    todoTestKSJY.Reset();
                } else {
                    todoTestKSJY = new Test_KSJY(dt.tData.get_DingYaInt(), dt.tData.get_LiangshuInt(), TestContent.TestState.tsNotBegin);
                    todoTestKSJY.setOnTestFailedListener(this);
                    dt.tData.listTest.add(todoTestKSJY);
                }
                processSendCommand(CTestWindProtocel.TestCommand.PressureKSCheck);
                break;
            case R.id.tv_save://保存
                hideControl();
                break;
            case R.id.tv_klw_position://客列尾 首部/尾部
                break;
            case R.id.tv_exit:
                onCloseQuery();
                break;
                default:
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            onCloseQuery();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    public static void startIntent(Activity context, CTestWindProtocel.TestCommandExt type, String track, String trainNo, String trainCount, String specifiedPressure, String handDeviceNo, String deviceNo,
                                   String operatorClass, String operatorGroup, String trainHeadNo, String trainTailNo, String trainBackupNo, int isManualTest) {
        //type:1-试风，2-机能，3-试验
        Intent intent = new Intent(context, TestControlActivity.class);
        intent.putExtra(PARAM_INTENT_TYPE, type.getValue());
        intent.putExtra(PARAM_TRACK, track);
        intent.putExtra(PARAM_TRAIN_NO, trainNo);
        intent.putExtra(PARAM_TRAIN_COUNT, trainCount);
        intent.putExtra(PARAM_SPECIFIED_PRESSURE, specifiedPressure);
        intent.putExtra(PARAM_HAND_DEVICE_NO, handDeviceNo);
        intent.putExtra(PARAM_DEVICE_NO, deviceNo);
        intent.putExtra(PARAM_OPERATOR_CLASS, operatorClass);
        intent.putExtra(PARAM_OPERATOR_GROUP, operatorGroup);
        intent.putExtra(PARAM_TRAIN_HEAD_NO, trainHeadNo);
        intent.putExtra(PARAM_TRAIN_TAIL_NO, trainTailNo);
        intent.putExtra(PARAM_TRAIN_BACKUP, trainBackupNo);
        intent.putExtra(PARAM_IS_MANUAL_TEST, isManualTest);
        context.startActivity(intent);
    }
}
