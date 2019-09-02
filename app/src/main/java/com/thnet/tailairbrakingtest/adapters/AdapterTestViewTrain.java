package com.thnet.tailairbrakingtest.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.thnet.tailairbrakingtest.R;
import com.thnet.tailairbrakingtest.TestWind.TestViewContent;

import java.util.List;

public class AdapterTestViewTrain extends BaseAdapter {
    Context context;
    private List<TestViewContent> testViewContentList;

    public AdapterTestViewTrain(Context context, List<TestViewContent> testViewContents) {
        this.context = context;
        this.testViewContentList = testViewContents;
    }

    @Override
    public int getCount() {
        return testViewContentList.size();
    }

    @Override
    public Object getItem(int position) {
        return testViewContentList.get(position);
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
            convertView = View.inflate(context,R.layout.item_test_view_train,null);
            holder.tvColumn1 = convertView.findViewById(R.id.tv_column1);
            holder.tvColumn2 = convertView.findViewById(R.id.tv_column2);
            holder.tvColumn3 = convertView.findViewById(R.id.tv_column3);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.tvColumn1.setText(((TestViewContent)getItem(position)).getColumn1());
        holder.tvColumn2.setText(((TestViewContent)getItem(position)).getColumn2());
        holder.tvColumn3.setText(((TestViewContent)getItem(position)).getColumn3());
        return convertView;
    }

    private class Holder{
        private TextView tvColumn1;
        private TextView tvColumn2;
        private TextView tvColumn3;
    }
}
