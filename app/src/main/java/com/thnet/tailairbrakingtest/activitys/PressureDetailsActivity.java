package com.thnet.tailairbrakingtest.activitys;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.thnet.tailairbrakingtest.customapplication.WindTestApplication;
import com.thnet.tailairbrakingtest.dao.DaoSession;
import com.thnet.tailairbrakingtest.dao.PressureValue;
import com.thnet.tailairbrakingtest.dao.PressureValueDao;
import com.thnet.tailairbrakingtest.R;
import com.thnet.tailairbrakingtest.adapters.PressureAdapter;

import org.greenrobot.greendao.query.Query;

import java.util.ArrayList;
import java.util.List;

public class PressureDetailsActivity extends AppCompatActivity implements AbsListView.OnScrollListener {

    private static final String TAG = PressureDetailsActivity.class.getSimpleName();
    public static final String PARAM_NAME_TEST_ID = "Test_ID";
    private static final int pageSize = 20;
    private static final int MESSAGE_LIST_DATA_LOAD_COMPLETE = 0x8001;

    private int startIndex = 0;
    private boolean isListDataLoading = false;
    private boolean isListLastRow = false;
    private String testID = "";
    private List<PressureValue> pressureValueList = new ArrayList<>();

    private View listFooter;
    private ListView listViewPressure;
    private PressureAdapter pressureAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pressure_details);
        testID = getIntent().getStringExtra(PARAM_NAME_TEST_ID);
        initView();
    }

    private void initView(){
        listFooter = getLayoutInflater().inflate(R.layout.list_footer, null);
        pressureValueList = loadData(startIndex, pageSize);
        if (null == pressureValueList){
            pressureValueList = new ArrayList<>();
        }
        findViewById(R.id.back_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        listViewPressure = findViewById(R.id.mListView);
        listViewPressure.addFooterView(listFooter);
        listViewPressure.setOnScrollListener(this);
        pressureAdapter = new PressureAdapter(this, pressureValueList);
        listViewPressure.setAdapter(pressureAdapter);
        listViewPressure.removeFooterView(listFooter);
    }

    private List<PressureValue> loadData(int offset, int limit){
        try{
            DaoSession daoSession = WindTestApplication.getWindTestInstance().getDaoSession();
            Query<PressureValue> pressureValueQuery = daoSession.queryBuilder(PressureValue.class).where(PressureValueDao.Properties.TestID.eq(testID)).offset(offset).limit(limit).build();
            return pressureValueQuery.list();
        } catch (Exception ex){
            Log.e(TAG, ex.getMessage());
            return null;
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case SCROLL_STATE_IDLE:
                if (isListLastRow && !isListDataLoading){
                    isListDataLoading = true;
                    listViewPressure.addFooterView(listFooter);
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            try{
                                Message message = handler.obtainMessage(MESSAGE_LIST_DATA_LOAD_COMPLETE, loadData(startIndex, pageSize));
                                message.sendToTarget();
                            } catch (Exception ex){
                                Log.e(TAG, ex.getMessage());
                            }
                        }
                    }.start();
                }
                break;
            case SCROLL_STATE_TOUCH_SCROLL:
                break;
            case SCROLL_STATE_FLING:
                break;
                default:
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, final int totalItemCount) {
        if(firstVisibleItem + visibleItemCount >= totalItemCount && totalItemCount > 0){
            startIndex = totalItemCount;
            isListLastRow = true;
        } else {
            isListLastRow = false;
        }
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MESSAGE_LIST_DATA_LOAD_COMPLETE:
                    if (null != msg.obj){
                        pressureValueList.addAll((List<PressureValue>)msg.obj);
                        pressureAdapter.notifyDataSetChanged();
                        listViewPressure.removeFooterView(listFooter);
                        isListDataLoading = false;
                    }
                    break;
                    default:
            }
        }
    };

    public static void startIntent(Activity context, String testID){
        Intent intent = new Intent(context,PressureDetailsActivity.class);
        intent.putExtra(PARAM_NAME_TEST_ID, testID);
        context.startActivity(intent);
    }
}
