package com.thnet.tailairbrakingtest.activitys;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.elvishew.xlog.XLog;
import com.thnet.tailairbrakingtest.R;
import com.thnet.tailairbrakingtest.adapters.InnerItemOnclickListener;
import com.thnet.tailairbrakingtest.adapters.TestKindAdapter;
import com.thnet.tailairbrakingtest.adapters.TestSelectAdapter;
import com.thnet.tailairbrakingtest.customapplication.WindTestApplication;
import com.thnet.tailairbrakingtest.dao.TestKind;

import java.util.ArrayList;
import java.util.List;

public class TestSettingActivity extends AppCompatActivity implements View.OnClickListener, InnerItemOnclickListener {
    private List<TestKind> testKindList;
    private List<TestSelectData> testSelectDataList;
    private ListView lvTestKind;
    private ListView lvTestName;
    private TestSelectAdapter testSelectAdapter;
    private TestKindAdapter testKindAdapter;
    private TestKind selectedTestKind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_setting);
        testKindList = WindTestApplication.getWindTestInstance().getDaoSession().loadAll(TestKind.class);
        testSelectDataList = new ArrayList<>(0);
        initView();
    }

    private void initView(){
        findViewById(R.id.back_tv).setOnClickListener(this);
        lvTestKind = findViewById(R.id.lv_TestKind);
        View testKindFooterView = LayoutInflater.from(this).inflate(R.layout.list_test_kind_footer, null);
        testKindFooterView.findViewById(R.id.tv_TestKindAdd).setOnClickListener(this);
        lvTestKind.addFooterView(testKindFooterView, null, false);
        lvTestKind.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedTestKind = (TestKind) parent.getAdapter().getItem(position);
                if (null != selectedTestKind){
                    updateTestSelected(selectedTestKind);
                    testSelectAdapter.notifyDataSetChanged();
                }
            }
        });
        testKindAdapter = new TestKindAdapter(this, testKindList);
        lvTestKind.setAdapter(testKindAdapter);
        lvTestName = findViewById(R.id.lv_TestName);
        testSelectAdapter = new TestSelectAdapter(this, testSelectDataList);
        testSelectAdapter.setItemButtonOnClickListener(this);
        lvTestName.setAdapter(testSelectAdapter);
    }

    private void updateTestSelected(TestKind testKind){
        if (null != testSelectDataList){
            testSelectDataList.clear();
        } else {
            testSelectDataList = new ArrayList<>(0);
        }
        for (String tName : TestKind.TEST_NAME_ALL){
            TestSelectData tsd = new TestSelectData(false, tName);
            tsd.setChecked(testKind.getTestCheckedByName(tsd.getTestName()));
            testSelectDataList.add(tsd);
        }
    }

    private void listReload(){
        testKindList.clear();
        testKindList.addAll( WindTestApplication.getWindTestInstance().getDaoSession().loadAll(TestKind.class));
        testKindAdapter.notifyDataSetChanged();
        selectedTestKind = null;
        testSelectDataList.clear();
        testSelectAdapter.notifyDataSetChanged();
    }

    private void testKindAdd(String testName){
        TestKind testKind = new TestKind();
        testKind.setTestKindName(testName);
        testKind.setTextLXChecked(false);
        testKind.setTextGDChecked(false);
        testKind.setTextADChecked(false);
        testKind.setTextBYChecked(false);
        testKind.setTextJLChecked(false);
        WindTestApplication.getWindTestInstance().getDaoSession().getTestKindDao().insert(testKind);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_TestKindAdd:
                try{
                    if (null != testKindList && testKindList.size() >= TestKind.MAX_TEST_KIND_COUNT){
                        Toast.makeText(getApplicationContext(), "试验类型最多允许添加" + TestKind.MAX_TEST_KIND_COUNT + "个。", Toast.LENGTH_LONG).show();
                        return;
                    }
                    final EditText editText = new EditText(this);
                    new AlertDialog.Builder(this).setTitle("请输入试验类型名称")
                            .setIcon(android.R.drawable.sym_def_app_icon)
                            .setView(editText)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //按下确定键后的事件
                                    testKindAdd(editText.getText().toString());
                                    listReload();
                                    Toast.makeText(getApplicationContext(), "试验类型" + editText.getText().toString() + "添加成功，请选择需要的试验。", Toast.LENGTH_LONG).show();
                                }
                            }).setNegativeButton("取消",null).show();
                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(), "试验类型添加异常！", Toast.LENGTH_LONG).show();
                    XLog.e("添加试风类型异常" + ex.getMessage());
                }
                break;
            case R.id.back_tv:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void itemButtOnClick(View v) {
    }

    @Override
    public void itemCheckBoxOnCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.cb_TestName:
                try {
                    if (null != selectedTestKind) {
                        int position = (Integer) buttonView.getTag();
                        if (null != testSelectDataList && testSelectDataList.size() > position){
                            selectedTestKind.setTestCheckByName(((TestSelectData)testSelectDataList.get(position)).getTestName(), isChecked);
                            WindTestApplication.getWindTestInstance().getDaoSession().getTestKindDao().update(selectedTestKind);
                        } else {
                            Toast.makeText(getApplicationContext(), "试验类型设置失败！", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "请先选择试风类型！", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(), "试验类型设置异常！", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
    }

    public class TestSelectData{
        private boolean isChecked;
        private String testName;

        public TestSelectData(boolean checked, String tName){
            isChecked = checked;
            testName = tName;
        }

        public boolean isChecked() {
            return isChecked;
        }

        public void setChecked(boolean checked) {
            isChecked = checked;
        }

        public String getTestName() {
            return testName;
        }

        public void setTestName(String testName) {
            this.testName = testName;
        }
    }
}
