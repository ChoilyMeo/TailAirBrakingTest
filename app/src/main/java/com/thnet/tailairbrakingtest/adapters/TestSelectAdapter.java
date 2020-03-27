package com.thnet.tailairbrakingtest.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.thnet.tailairbrakingtest.R;
import com.thnet.tailairbrakingtest.activitys.TestSettingActivity;

import java.util.List;

public class TestSelectAdapter extends BaseAdapter implements CompoundButton.OnCheckedChangeListener {
    private Context context;
    private InnerItemOnclickListener itemButtonListener;
    private List<TestSettingActivity.TestSelectData> testSelectDataList;

    public TestSelectAdapter(Context context){
        this.context = context;
    }

    public TestSelectAdapter(Context context, List<TestSettingActivity.TestSelectData> testSelectDatas){
        this.context = context;
        testSelectDataList = testSelectDatas;
    }
    @Override
    public int getCount() {
        return testSelectDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return testSelectDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TestSelectAdapter.Holder holder;
        if (convertView == null || convertView.getTag() == null){
            holder = new TestSelectAdapter.Holder();
            convertView = View.inflate(context, R.layout.item_test_kind_content,null);
            holder.cbTestName = convertView.findViewById(R.id.cb_TestName);
            convertView.setTag(holder);
        } else {
            holder = (TestSelectAdapter.Holder) convertView.getTag();
        }
        holder.cbTestName.setText(((TestSettingActivity.TestSelectData)getItem(position)).getTestName());
        holder.cbTestName.setChecked(((TestSettingActivity.TestSelectData)getItem(position)).isChecked());
        holder.cbTestName.setTag(position);
        holder.cbTestName.setOnCheckedChangeListener(this);
        return convertView;
    }

    public void setItemButtonOnClickListener(InnerItemOnclickListener listener){
        this.itemButtonListener = listener;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.cb_TestName:
                if (null != itemButtonListener){
                    itemButtonListener.itemCheckBoxOnCheckedChanged(buttonView, isChecked);
                }
                break;
            default:
                break;
        }
    }

    private class Holder{
        private CheckBox cbTestName;
    }
}
