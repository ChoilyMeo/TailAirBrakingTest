package com.thnet.tailairbrakingtest.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.thnet.tailairbrakingtest.DAO.UserInfo;
import com.thnet.tailairbrakingtest.R;

import java.util.List;

public class UserAdapter extends BaseAdapter {
    Context context;
    private List<UserInfo> userInfoList;

    public UserAdapter(Context context) {
        this.context = context;
    }

    public UserAdapter(Context context, List<UserInfo> userInfos){
        this.context = context;
        this.userInfoList = userInfos;
    }

    @Override
    public int getCount() {
        return userInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return userInfoList.get(position);
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
            convertView = View.inflate(context,R.layout.item_user,null);
            holder.tvUserName = convertView.findViewById(R.id.itemUserName);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.tvUserName.setText(((UserInfo)getItem(position)).getUserName());
        return convertView;
    }

    private class Holder{
        private TextView tvUserName;
    }
}
