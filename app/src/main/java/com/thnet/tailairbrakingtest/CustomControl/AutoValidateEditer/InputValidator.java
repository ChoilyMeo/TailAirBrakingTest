package com.thnet.tailairbrakingtest.CustomControl.AutoValidateEditer;

import android.app.Activity;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.thnet.tailairbrakingtest.CustomControl.AutoValidateEditer.Annotations.Validated;

public class InputValidator {

    public static List<AutoValidateEditer> inputList;
    public static boolean isChecked;

    public static void init(Activity activity) {
        if (inputList != null) {
            inputList.clear();
        } else {
            inputList = new ArrayList<AutoValidateEditer>();
        }

        Class<? extends Activity> clazz = activity.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(Annotations.Inject.class)) {
                Annotations.Inject annotation = field.getAnnotation(Annotations.Inject.class);
                int id = annotation.value();
                if (id >= 0) {
                    try {
                        field.set(activity, activity.findViewById(id));
                    } catch (IllegalAccessException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IllegalArgumentException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            if (field.isAnnotationPresent(Annotations.Validated.class)) {
                Validated annotation = field.getAnnotation(Validated.class);
                boolean canNull = annotation.canNull();
                String regex = annotation.regex().equals(Annotations.NO_REGEX) ? null : annotation.regex();
                Log.d("mzlei", canNull + regex);
                AutoValidateEditer editText;
                try {
                    editText = (AutoValidateEditer) (field.get(activity));
                    editText.setSmartInputListener(canNull, regex);
                    inputList.add(editText);
                } catch (IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }
    }

    public static boolean validate() {
        isChecked = true;
        if (inputList == null) {
            return false;
        } else {
            Log.d("mzlei", inputList.toString());
            for (AutoValidateEditer editText : inputList) {
                if (!editText.validate()) {

                    isChecked = false;
                }
            }
            return isChecked;
        }
    }
}
