package com.thnet.tailairbrakingtest.testwind;

import com.thnet.tailairbrakingtest.dao.PressureValue;

public interface OnDoTestBegin {
    /**
     * 试验开始处理
     * @param stime
     * @param pd
     */
    void DoTestBegin(String stime, PressureValue pd);
}
