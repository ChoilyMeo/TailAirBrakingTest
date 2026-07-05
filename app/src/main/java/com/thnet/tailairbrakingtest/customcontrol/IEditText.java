package com.thnet.tailairbrakingtest.customcontrol;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatEditText;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;


public class IEditText extends AppCompatEditText {
    private IKeyboardView mIKeyboardView;
    public IEditText(@NonNull Context context) {
        super(context);
        init();
    }

    public IEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public IEditText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setInputType(InputType.TYPE_NULL);
    }

    //绑定键盘
    public void setmIKeyboardView(IKeyboardView mIKeyboardView) {
        this.mIKeyboardView = mIKeyboardView;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //按下时绑定当前的EditText
        if (event.getAction() == MotionEvent.ACTION_DOWN){
            if (mIKeyboardView!=null){
                mIKeyboardView.setEditText(this);
            }
        }
        return super.onTouchEvent(event);
    }

}
