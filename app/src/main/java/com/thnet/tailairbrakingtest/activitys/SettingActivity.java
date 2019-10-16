package com.thnet.tailairbrakingtest.activitys;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.thnet.tailairbrakingtest.customapplication.WindTestApplication;
import com.thnet.tailairbrakingtest.dao.SysParms;
import com.thnet.tailairbrakingtest.R;
import com.thnet.tailairbrakingtest.testwind.SysParamsAll;
import com.thnet.tailairbrakingtest.adapters.SettingAdapter;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvParamsName;
    EditText etParamsValue;
    ListView mListView;
    SysParms selectedParm = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
    }

    private void initView(){
        selectedParm = null;
        tvParamsName = (TextView) findViewById(R.id.tv_paramsName);
        etParamsValue = (EditText) findViewById(R.id.et_paramsValue);
        findViewById(R.id.cancle).setOnClickListener(this);
        findViewById(R.id.setParamValue).setOnClickListener(this);
        mListView = findViewById(R.id.mListView);
        mListView.setAdapter(new SettingAdapter(this, R.layout.item_setting, SysParamsAll.load()));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedParm = (SysParms) mListView.getItemAtPosition(position);
                tvParamsName.setText(selectedParm.getParamName());
                etParamsValue.setText(selectedParm.getParamValue());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cancle:
                finish();
                break;
            case R.id.setParamValue:
                if (null != mListView && null != selectedParm){
                    try{
                        setParamValue();
                        ((SettingAdapter)mListView.getAdapter()).notifyDataSetChanged();
                        Toast.makeText(this,"修改成功。", Toast.LENGTH_SHORT).show();
                    }
                    catch (Exception ex){
                        Toast.makeText(this,"参数修改异常：" + ex.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                else if (etParamsValue.getText().toString().isEmpty()){
                    Toast.makeText(this,"参数值不能为空。", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(this,"请选择需要修改的项目。", Toast.LENGTH_SHORT).show();
                }
                break;
                default:
        }
    }

    private void setParamValue(){
        selectedParm.setParamValue(etParamsValue.getText().toString());
        WindTestApplication.getWindTestInstance().getDaoSession().getSysParmsDao().update(selectedParm);
        WindTestApplication.getWindTestInstance().getDaoSession().clear();
        SysParamsAll.load();
    }
}
