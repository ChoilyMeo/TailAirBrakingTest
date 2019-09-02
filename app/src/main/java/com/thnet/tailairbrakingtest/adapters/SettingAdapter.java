package com.thnet.tailairbrakingtest.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.thnet.tailairbrakingtest.DAO.SysParms;
import com.thnet.tailairbrakingtest.R;

import java.util.List;

public class SettingAdapter extends ArrayAdapter<SysParms> {
    Context context;
    private int resourceId;
    private List<SysParms> mParmsList;

    public SettingAdapter(Context context, int viewResourceId, List<SysParms> parmsListList) {
        super(context, viewResourceId, parmsListList);
        this.context = context;
        this.resourceId = viewResourceId;
        this.mParmsList = parmsListList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null || convertView.getTag() == null){
            holder = new Holder();
            convertView = View.inflate(context,R.layout.item_setting,null);
            holder.tvParamsName = convertView.findViewById(R.id.tvParamsName);
            holder.tvParamsValue = convertView.findViewById(R.id.tvParamsValue);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.tvParamsName.setText(getItem(position).getParamName());
        holder.tvParamsValue.setText(getItem(position).getParamValue());
        return convertView;
    }

    private class Holder{
        private TextView tvParamsName;
        private TextView tvParamsValue;
    }
}
