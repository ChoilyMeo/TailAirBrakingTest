package com.thnet.tailairbrakingtest.testwind;

import java.util.ArrayList;
import java.util.List;

public class CEstimate {
    public List<Integer> lstValue = new ArrayList<Integer>(0);
    private int max = 0;
    private int min = 0;
    private int avg = 0;
    private int listMaxLen = 100;
    public CEstimate(int maxLen){
        listMaxLen = maxLen;
    }
    public synchronized void add(int pressureValue){
        if (lstValue.size() >= listMaxLen){
            lstValue.remove(0);
        }
        lstValue.add(pressureValue);
        int sum = 0;
        max = pressureValue;
        min = pressureValue;
        for (int item : lstValue){
            sum += item;
            if (item > max){
                max = item;
            }
            if (item < min){
                min = item;
            }
        }
        avg = (int)((sum * 1.0) / lstValue.size() + 0.5);
    }
    public synchronized void clear(){
        lstValue.clear();
        max = 0;
        min = 0;
        avg = 0;
    }
    public int getAtPosition(int position){
        if (position >=0 && position < lstValue.size()){
            return lstValue.get(position);
        } else {
            return 0;
        }
    }
    public int getListLen(){
        return lstValue.size();
    }

    public int getMax() {
        return max;
    }

    public int getMin() {
        return min;
    }

    public int getAvg() {
        return avg;
    }
}
