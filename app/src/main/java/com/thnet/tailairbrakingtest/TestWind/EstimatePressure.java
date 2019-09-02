package com.thnet.tailairbrakingtest.TestWind;

import java.util.ArrayList;
import java.util.List;

public class EstimatePressure {
    public List<Integer> lstValue = new ArrayList<Integer>();
    public int max = 0;
    public int min = 0;
    public int avg = 0;
    public int listLen;

    public EstimatePressure(){
        listLen = 100;
    }
    public EstimatePressure(int len){
        listLen = len;
    }

    public void Add(int PressureValue)
    {
        if (lstValue.size()>=listLen)
        {
            lstValue.remove(0);
        }
        int sum = 0;
        lstValue.add(PressureValue);
        max = PressureValue;
        min = PressureValue;
        for(int item : lstValue)
        {
            sum += item;
            if (item > max)
            {
                max = item;
            }
            if (item<min)
            {
                min = item;
            }
        }
        avg = (int)((sum * 1.0) / lstValue.size() + 0.5);
    }
}
