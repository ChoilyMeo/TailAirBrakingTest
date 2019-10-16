package com.thnet.tailairbrakingtest.activitys;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.thnet.tailairbrakingtest.customapplication.WindTestApplication;
import com.thnet.tailairbrakingtest.dao.DaoSession;
import com.thnet.tailairbrakingtest.dao.TestDetail;
import com.thnet.tailairbrakingtest.dao.TestDetailDao;
import com.thnet.tailairbrakingtest.R;
import com.thnet.tailairbrakingtest.adapters.TestAdapter;

import org.greenrobot.greendao.query.Query;

import java.util.ArrayList;
import java.util.List;

public class TestDetailsActivity extends AppCompatActivity {
    private static final String TAG = TestDetailsActivity.class.getSimpleName();
    public static final String PARAM_NAME_TEST_ID = "Test_ID";
    private String testID = "";
    List<TestDetail> testDetailList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_details);
        testID = getIntent().getStringExtra(PARAM_NAME_TEST_ID);
        initView();
    }
    private void initView(){
        findViewById(R.id.back_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        testDetailList = loadData(0,100);
        if (null == testDetailList){
            testDetailList = new ArrayList<>();
        }
        ListView mListView = findViewById(R.id.mListView);
        mListView.setAdapter(new TestAdapter(this, testDetailList));
    }

    private List<TestDetail> loadData(int offset, int limit){
        try{
            DaoSession daoSession = WindTestApplication.getWindTestInstance().getDaoSession();
            Query<TestDetail> testDetailQuery = daoSession.queryBuilder(TestDetail.class).where(TestDetailDao.Properties.TestID.eq(testID)).offset(offset).limit(limit).build();
            return testDetailQuery.list();
        } catch (Exception ex){
            Log.e(TAG, ex.getMessage());
            return null;
        }
    }

    public static void startIntent(Activity context, String testID){
        Intent intent = new Intent(context,TestDetailsActivity.class);
        intent.putExtra(PARAM_NAME_TEST_ID, testID);
        context.startActivity(intent);
    }
}
