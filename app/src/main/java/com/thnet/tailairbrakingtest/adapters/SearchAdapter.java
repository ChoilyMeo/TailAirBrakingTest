package com.thnet.tailairbrakingtest.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.thnet.tailairbrakingtest.dao.TestWindContent;
import com.thnet.tailairbrakingtest.R;

import java.util.List;

public class SearchAdapter extends BaseAdapter {
    Context context;
    List<TestWindContent> testWindContentList;

    public SearchAdapter(Context context, List<TestWindContent> testWindContents) {
        this.context = context;
        this.testWindContentList = testWindContents;
    }

    @Override
    public int getCount() {
        return testWindContentList.size();
    }

    @Override
    public Object getItem(int position) {
        return testWindContentList.get(position);
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
            convertView = View.inflate(context,R.layout.item_search,null);
            holder.tvTestDate = convertView.findViewById(R.id.tv_testDate);
            holder.tvStartTime = convertView.findViewById(R.id.tv_testStartTime);
            holder.tvEndTime = convertView.findViewById(R.id.tv_testEndTime);
            holder.tvTrainNo = convertView.findViewById(R.id.tv_trainNo);
            holder.tvTrack = convertView.findViewById(R.id.tv_track);
            holder.tvTrainCount = convertView.findViewById(R.id.tv_trainCount);
            holder.tvSpecifiedPressure = convertView.findViewById(R.id.tv_specifiedPressure);
            holder.tvTestKind = convertView.findViewById(R.id.tv_testKind);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.tvTestDate.setText(((TestWindContent)getItem(position)).getTestDate());
        holder.tvStartTime.setText(((TestWindContent)getItem(position)).getStartTime());
        holder.tvEndTime.setText(((TestWindContent)getItem(position)).getEndTime());
        holder.tvTrainNo.setText(((TestWindContent)getItem(position)).getTrainNo());
        holder.tvTrack.setText(((TestWindContent)getItem(position)).getLine());
        holder.tvTrainCount.setText(((TestWindContent)getItem(position)).getTrainCount());
        holder.tvSpecifiedPressure.setText(((TestWindContent)getItem(position)).getSpecifyPressure());
        holder.tvTestKind.setText(((TestWindContent)getItem(position)).getTestKind());
        return convertView;
    }

    private class Holder{
        private TextView tvTestDate;
        private TextView tvStartTime;
        private TextView tvEndTime;
        private TextView tvTrainNo;
        private TextView tvTrack;
        private TextView tvTrainCount;
        private TextView tvSpecifiedPressure;
        private TextView tvTestKind;
    }
}
