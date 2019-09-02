package com.thnet.tailairbrakingtest.Activitys;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.thnet.tailairbrakingtest.CustomApplication.WindTestApplication;
import com.thnet.tailairbrakingtest.DAO.DaoSession;
import com.thnet.tailairbrakingtest.DAO.TestWindContent;
import com.thnet.tailairbrakingtest.R;
import com.thnet.tailairbrakingtest.adapters.SearchAdapter;
import com.thnet.tailairbrakingtest.views.DialogDatePicker;

import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.WhereCondition;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = SearchActivity.class.getSimpleName();
    private String selectedTestID;
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    private TextView tvStartDate;
    private TextView tvEndDate;
    private EditText edtTrainNo;
    List<TestWindContent> testWindContentList = new ArrayList<>();
    private SearchAdapter searchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
    }

    private void initView(){
        //testWindContentList = WindTestApplication.getWindTestInstance().getDaoSession().loadAll(TestWindContent.class);
        searchAdapter = new SearchAdapter(this, testWindContentList);
        ListView mListView = findViewById(R.id.mListView);
        mListView.setAdapter(searchAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Intent intent = new Intent(SearchActivity.this,SearchItemDetailActivity.class);
                //startActivity(intent);
                selectedTestID = ((TestWindContent)((ListView)parent).getItemAtPosition(position)).getTestID();
            }
        });

        findViewById(R.id.cancle).setOnClickListener(this);
        findViewById(R.id.tv_pressure).setOnClickListener(this);
        findViewById(R.id.tv_test).setOnClickListener(this);
        findViewById(R.id.starttime_layout).setOnClickListener(this);
        findViewById(R.id.endtime_layout).setOnClickListener(this);
        findViewById(R.id.btn_query).setOnClickListener(this);
        edtTrainNo = findViewById(R.id.edt_trainNo);
        tvStartDate = findViewById(R.id.start_date_tv);
        tvStartDate.setText(format.format(new Date(System.currentTimeMillis())));
        tvEndDate = findViewById(R.id.end_date_tv);
        tvEndDate.setText(format.format(new Date(System.currentTimeMillis())));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cancle:
                finish();
                break;
            case R.id.tv_pressure:
                if (null == selectedTestID || selectedTestID.isEmpty()){
                    Toast.makeText(this, "请选择要查看的记录。", Toast.LENGTH_SHORT).show();
                } else {
                    PressureDetailsActivity.startIntent(this, selectedTestID);
                }
                break;
            case R.id.tv_test:
                if (null == selectedTestID || selectedTestID.isEmpty()){
                    Toast.makeText(this, "请选择要查看的记录。", Toast.LENGTH_SHORT).show();
                } else {
                    TestDetailsActivity.startIntent(this, selectedTestID);
                }
                break;
            case R.id.starttime_layout:
                DialogDatePicker dialog = new DialogDatePicker(this,true);
                dialog.show();
                dialog.setOnYesListener(new DialogDatePicker.OnYesListener() {
                    @Override
                    public void onYes(String date) {
                        tvStartDate.setText(date);
                    }
                });
                break;
            case R.id.endtime_layout:
                DialogDatePicker dialog1 = new DialogDatePicker(this,false);
                dialog1.show();
                dialog1.setOnYesListener(new DialogDatePicker.OnYesListener() {
                    @Override
                    public void onYes(String date) {
                        tvEndDate.setText(date);
                    }
                });
                break;
            case R.id.btn_query://查询
                String whereCase = "TestDate >= '" + tvStartDate.getText().toString() + "' and TestDate <= '" + tvEndDate.getText().toString() + "'";
                if (null != edtTrainNo.getText() && !edtTrainNo.getText().toString().trim().isEmpty()){
                    whereCase += " and TrainNo = '" + edtTrainNo.getText().toString().trim() + "'";
                }
                try {
                    DaoSession daoSession = WindTestApplication.getWindTestInstance().getDaoSession();
                    Query<TestWindContent> testWindContentQuery = daoSession.queryBuilder(TestWindContent.class).where(new WhereCondition.StringCondition(whereCase)).build();
                    if (null == testWindContentList){
                        testWindContentList = testWindContentQuery.list();
                    } else {
                        testWindContentList.clear();
                        testWindContentList.addAll(testWindContentQuery.list());
                    }
                }
                catch (Exception ex){
                    Log.e(TAG, ex.getMessage());
                    testWindContentList = null;
                }
                if(null == testWindContentList){testWindContentList = new ArrayList<>();}
                searchAdapter.notifyDataSetChanged();
                break;
                default:
        }
    }
}
