package com.thnet.tailairbrakingtest.TestWind;

import com.thnet.tailairbrakingtest.DAO.PressureValue;

public interface OnDoTestBegin {
    /**
     * 试验开始处理
     * @param stime
     * @param pd
     */
    void DoTestBegin(String stime, PressureValue pd);
}
