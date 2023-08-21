package com.reconinstruments.connect.apps;

import android.view.View;

/**
 * Created by recom3 on 21/08/2023.
 */

public interface SmartphoneInterface {
    View getAndroidOverlay();

    View getIOSConnectButton(View view);

    View getIOSOverlay();

    View getNoConnectNoShowButton(View view);

    View getNoConnectOverlay();

    View getNoConnectSetupButton(View view);

    void onConnect();

    void onDisconnect();

    boolean requiresAndroid();
}