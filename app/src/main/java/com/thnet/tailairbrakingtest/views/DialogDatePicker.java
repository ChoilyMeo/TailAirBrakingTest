package com.thnet.tailairbrakingtest.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialog;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.TextView;

import com.thnet.tailairbrakingtest.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class DialogDatePicker extends AppCompatDialog {
    Context context;
    boolean isStartDate = false;
    String date = "";
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    public DialogDatePicker(@NonNull Context context,boolean isStartDate) {
        super(context);
        this.context = context;
        this.isStartDate = isStartDate;
    }

    public DialogDatePicker(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
    }

    protected DialogDatePicker(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setCanceledOnTouchOutside(false);
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_time);
        DatePicker datePicker = findViewById(R.id.datePicker);
        Calendar calendar = Calendar.getInstance();
        //年
        int year = calendar.get(Calendar.YEAR);
        //月
        int month = calendar.get(Calendar.MONTH);
        //日
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        date = format.format(calendar.getTime());
        datePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // 获取一个日历对象，并初始化为当前选中的时间
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, monthOfYear, dayOfMonth);
                date = format.format(calendar.getTime());
//                Toast.makeText(context, format.format(calendar.getTime()), Toast.LENGTH_SHORT).show();
            }
        });
        TextView cancelTv = findViewById(R.id.dialog_no);
        cancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (onNoListener != null){
                    onNoListener.onCancel();
                }
            }
        });
        TextView okTv = findViewById(R.id.dialog_yes);
        okTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    dismiss();
                    if (onYesListener != null) {
                        onYesListener.onYes(date);
                    }
            }
        });
    }

    public interface OnYesListener{
        void onYes(String date);
    }

    OnYesListener onYesListener;
    public void setOnYesListener(OnYesListener listener){
        this.onYesListener = listener;
    }

    public interface OnNoListener{
        void onCancel();
    }

    OnNoListener onNoListener;
    public void setOnCancelListener(OnNoListener listener){
        this.onNoListener = listener;
    }
}
