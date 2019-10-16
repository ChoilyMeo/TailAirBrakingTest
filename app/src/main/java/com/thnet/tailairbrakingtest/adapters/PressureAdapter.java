package com.thnet.tailairbrakingtest.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.thnet.tailairbrakingtest.dao.PressureValue;
import com.thnet.tailairbrakingtest.R;

import java.util.ArrayList;
import java.util.List;

public class PressureAdapter extends BaseAdapter {
    Context context;
    List<PressureValue> pressureValueList = new ArrayList<>();

    public PressureAdapter(Context context, List<PressureValue> pressureValues) {
        this.context = context;
        if (null != pressureValues){
            pressureValueList = pressureValues;
        }
    }

    @Override
    public int getCount() {
        return pressureValueList.size();
    }

    @Override
    public Object getItem(int position) {
        return pressureValueList.get(position);
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
            convertView = View.inflate(context,R.layout.item_pressure,null);
            holder.tvPressureValue = convertView.findViewById(R.id.item_pressureValue);
            holder.tvPressureTime = convertView.findViewById(R.id.item_time);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.tvPressureValue.setText(String.valueOf(((PressureValue)getItem(position)).getPressureValue()));
        holder.tvPressureTime.setText(((PressureValue)getItem(position)).getPressureTime());
        return convertView;
    }

    private class Holder{
        private TextView tvPressureValue;
        private TextView tvPressureTime;
    }
}
