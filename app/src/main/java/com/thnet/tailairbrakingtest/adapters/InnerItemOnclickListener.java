package com.thnet.tailairbrakingtest.adapters;

import android.view.View;
import android.widget.CompoundButton;

public interface InnerItemOnclickListener {
    void itemButtOnClick(View v);
    void itemCheckBoxOnCheckedChanged(CompoundButton buttonView, boolean isChecked);
}
