package com.thnet.tailairbrakingtest.Communication;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public interface OnDataReceiveSendListener {
    public static final int RECV_DATA = 0;
    public static final int RECV_COMMAND = 1;
    public static final int SEND_COMMAND = 2;
    @IntDef({
            RECV_DATA,
            RECV_COMMAND,
            SEND_COMMAND
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface Flags {}
    void OnDataReceiveSend(@Flags int flag);
}
