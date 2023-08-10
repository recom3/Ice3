package com.recom3.snow3;

import android.content.Intent;

/**
 * Created by Recom3 on 06/03/2022.
 */

public interface IResultCallbackActivity {
    void onResultCancel(Intent paramIntent);

    void onResultOk(Intent paramIntent);
}
