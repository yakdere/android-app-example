package com.yaprakakdere.myapplication.service;

import android.os.Bundle;
import android.os.ResultReceiver;

/**
 * Created by yaprakakdere on 5/4/17.
 */

public interface UINotifier {

    public static final int STATUS_SUCCESS = 0;
    public static final int STATUS_FAILED = 1;
    public static final int RESULT_TYPE_DISCOVER = 1;
    public static final int RESULT_TYPE_DISCOVER_DETAILS = 2;


    void notifyUI(int status, int typeResult, Bundle bundle, ResultReceiver receiver);
    void notifyUI(int status, int typeResult, String result, ResultReceiver receiver);
    void notifyUI(int status, int typeResult, ResultReceiver receiver);

}
