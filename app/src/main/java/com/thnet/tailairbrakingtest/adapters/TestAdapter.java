package com.thnet.tailairbrakingtest.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.thnet.tailairbrakingtest.dao.TestDetail;
import com.thnet.tailairbrakingtest.R;

import java.util.ArrayList;
import java.util.List;

public class TestAdapter extends BaseAdapter {
    Context context;
    List<TestDetail> testDetailList = new ArrayList<>();

    public TestAdapter(Context context, List<TestDetail> testDetails) {
        this.context = context;
        if (null != testDetails){
            testDetailList = testDetails;
        }
    }

    @Override
    public int getCount() {
        return testDetailList.size();
    }

    @Override
    public Object getItem(int position) {
        return testDetailList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null || convertView.getTag() == null){
            holder = new Holder();
            convertView = View.inflate(context,R.layout.item_test,null);
            holder.tvTestName = convertView.findViewById(R.id.item_testName);
            holder.tvTestStatus = convertView.findViewById(R.id.item_status);
            holder.tvPressureValue = convertView.findViewById(R.id.item_pressureValue);
            holder.tvKeepTime = convertView.findViewById(R.id.item_keepTime);
            holder.tvLeakValue = convertView.findViewById(R.id.item_leakValue);
            holder.tvDropValue = convertView.findViewById(R.id.item_dropValue);
            holder.tvStartTime = convertView.findViewById(R.id.item_testStartTime);
            holder.tvEndTime = convertView.findViewById(R.id.item_testEndTime);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.tvTestName.setText(String.valueOf(((TestDetail)getItem(position)).getTestName()));
        holder.tvTestStatus.setText(((TestDetail)getItem(position)).getState());
        holder.tvPressureValue.setText(String.valueOf(((TestDetail)getItem(position)).getTestPressure()));
        holder.tvKeepTime.setText(((TestDetail)getItem(position)).getKeepTime());
        holder.tvLeakValue.setText(String.valueOf(((TestDetail)getItem(position)).getLeakValue()));
        holder.tvDropValue.setText(((TestDetail)getItem(position)).getDropValue());
        holder.tvStartTime.setText(String.valueOf(((TestDetail)getItem(position)).getBeginTime()));
        holder.tvEndTime.setText(((TestDetail)getItem(position)).getEndTime());
        return convertView;
    }

    private class Holder{
        private TextView tvTestName;
        private TextView tvTestStatus;
        private  TextView tvPressureValue;
        private  TextView tvKeepTime;
        private  TextView tvLeakValue;
        private  TextView tvDropValue;
        private  TextView tvStartTime;
        private  TextView tvEndTime;
    }
}
