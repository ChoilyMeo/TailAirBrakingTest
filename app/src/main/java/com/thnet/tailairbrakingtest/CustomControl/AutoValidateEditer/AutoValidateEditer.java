package com.thnet.tailairbrakingtest.CustomControl.AutoValidateEditer;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.View;

import com.thnet.tailairbrakingtest.R;

public class AutoValidateEditer extends AppCompatEditText {

    private static final String INPUT_WRONG = "input_wrong";
    private static final String GET_WRONG = "get_wrong";
    private String regexLine;
    private boolean canNullLine = true;

    public AutoValidateEditer(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public AutoValidateEditer(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }


    @Override
    public void setError(CharSequence error) {

        if (error == null) {
            setBackground(null);
        } else if (error.equals(INPUT_WRONG)) {
            setBackgroundResource(R.drawable.edittext_input_false);
        } else {
            super.setError(error);
        }
    }

    public void setSmartInputListener(boolean canNull, String regex) {

        canNullLine = canNull;
        regexLine = regex;
        if (!canNullLine) {
            setHint("此选项为必填选项!");
        }

        this.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                if (!hasFocus) {
                    String inputContent = ((AppCompatEditText) v).getText().toString().trim();
                    if (inputContent.length() > 0) {
                        if (regexLine == null) {
                            setBackground(null);
                        } else {
                            if (inputContent.matches(regexLine)) {
                                setBackground(null);
                            } else {
                                setError(INPUT_WRONG);
                            }
                        }
                    } else {
                        if (canNullLine) {
                            setBackground(null);
                        } else {
                            setError("此选项为必填选项!");
                        }
                    }
                } else if (hasFocus) {
                    setBackground(null);
                }
            }
        });
    }

    public boolean addInputFinishListener(final InputFinishListener listener){
        if(listener == null){
            return false;
        }
        this.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String inputContent = ((AppCompatEditText) v).getText().toString().trim();
                    if (inputContent.length() > 0) {
                        if (regexLine == null) {
                            setBackground(null);
                            listener.inputFinish(AutoValidateEditer.this);
                        } else {
                            if (inputContent.matches(regexLine)) {
                                setBackground(null);
                                listener.inputFinish(AutoValidateEditer.this);
                            } else {
                                setError(INPUT_WRONG);
                            }
                        }
                    } else {
                        if (canNullLine) {
                            setBackground(null);
                            listener.inputFinish(AutoValidateEditer.this);
                        } else {
                            setError("此选项为必填选项!");
                        }
                    }
                } else if (hasFocus) {
                    setBackground(null);
                }
            }
        });
        return true;
    }

    public boolean validate() {
        String inputContent = getText().toString().trim();
        if(inputContent.length() > 0){
            if(regexLine != null){
                if(inputContent.matches(regexLine)){
                    setBackground(null);
                    return true;
                }else{
                    setError(INPUT_WRONG);
                    return false;
                }
            }
            return true;
        }else{
            if(canNullLine){
                setBackground(null);
                return true;
            }else{
                setError("此选项为必填选项!");
                return false;
            }
        }
    }
}
