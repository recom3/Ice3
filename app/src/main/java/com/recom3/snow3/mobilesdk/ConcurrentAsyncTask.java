package com.recom3.snow3.mobilesdk;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;

/**
 * Created by Recom3 on 24/01/2022.
 */

public abstract class ConcurrentAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
    @SuppressLint({"NewApi"})
    public AsyncTask<Params, Progress, Result> concurrentExecute(Params... paramVarArgs) {
        //return (Build.VERSION.SDK_INT <= 12) ? execute((Object[])paramVarArgs) : executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Object[])paramVarArgs);
        return executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Params[]) paramVarArgs);
    }
}
