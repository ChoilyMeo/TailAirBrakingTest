package com.thnet.tailairbrakingtest.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.thnet.tailairbrakingtest.dao.TestKind;
import com.thnet.tailairbrakingtest.R;

import java.util.List;

public class TestKindAdapter extends BaseAdapter {
    Context context;
    private List<TestKind> testKindList;

    public TestKindAdapter(Context context) {
        this.context = context;
    }

    public TestKindAdapter(Context context, List<TestKind> testKinds){
        this.context = context;
        this.testKindList = testKinds;
    }

    @Override
    public int getCount() {
        return testKindList.size();
    }

    @Override
    public Object getItem(int position) {
        return testKindList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TestKindAdapter.Holder holder;
        if (convertView == null || convertView.getTag() == null){
            holder = new TestKindAdapter.Holder();
            convertView = View.inflate(context, R.layout.item_test_kind,null);
            holder.tvTestKindName = convertView.findViewById(R.id.item_testKindName);
            convertView.setTag(holder);
        } else {
            holder = (TestKindAdapter.Holder) convertView.getTag();
        }
        holder.tvTestKindName.setText(((TestKind)getItem(position)).getTestKindName());
        return convertView;
    }

    private class Holder{
        private TextView tvTestKindName;
    }
}
