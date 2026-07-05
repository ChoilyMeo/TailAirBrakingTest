package com.thnet.tailairbrakingtest.adapters;

import android.content.Context;
import android.media.Image;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.thnet.tailairbrakingtest.R;
import com.thnet.tailairbrakingtest.dao.TestKind;

import java.util.List;

public class TestKindExAdapter extends BaseAdapter implements View.OnClickListener {
    Context context;
    private InnerItemOnclickListener itemButtonListener;
    private List<TestKind> testKindList;

    public TestKindExAdapter(Context context) {
        this.context = context;
    }

    public TestKindExAdapter(Context context, List<TestKind> testKinds){
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
        TestKindExAdapter.Holder holder;
        if (convertView == null || convertView.getTag() == null){
            holder = new TestKindExAdapter.Holder();
            convertView = View.inflate(context, R.layout.item_test_kind_extend,null);
            holder.tvTestKindName = convertView.findViewById(R.id.item_testKindName);
            holder.ivDelete = convertView.findViewById(R.id.image_delete);
            convertView.setTag(holder);
        } else {
            holder = (TestKindExAdapter.Holder) convertView.getTag();
        }
        holder.tvTestKindName.setText(((TestKind)getItem(position)).getTestKindName());
        holder.ivDelete.setTag(position);
        holder.ivDelete.setOnClickListener(this);
        return convertView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.image_delete:
                if (null != itemButtonListener){
                    itemButtonListener.itemButtOnClick(v);
                }
                break;
            default:
                break;
        }
    }

    public void setItemButtonOnClickListener(InnerItemOnclickListener listener){
        this.itemButtonListener = listener;
    }

    private class Holder{
        private TextView tvTestKindName;
        private ImageView ivDelete;
    }
}
