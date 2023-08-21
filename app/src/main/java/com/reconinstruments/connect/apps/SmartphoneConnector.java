package com.reconinstruments.connect.apps;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.TransitionDrawable;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.recom3.snow3.mobilesdk.hudconnectivity.ConnectHelper;

/**
 * Created by recom3 on 21/08/2023.
 */

public class SmartphoneConnector {
    static final String TAG = "SmartphoneConnector";
    SmartphoneInterface context;
    ConnectedDevice.ConnectionState currentState;
    FrameLayout mainView;
    View overlay;
    int CONNECT_REQUEST_CODE = 0;
    int CONNECT_SUCCESS_CODE = 44;
    BroadcastReceiver phoneConnectionReceiver = new BroadcastReceiver() { // from class: com.reconinstruments.connect.apps.SmartphoneConnector.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ConnectHelper.MSG_STATE_UPDATED)) {
                boolean connected = intent.getBooleanExtra(ConnectedDevice.COL_CONN_STATE, false);
                Log.i(SmartphoneConnector.TAG, "connected " + connected);
                DeviceInfo.DeviceType type = DeviceInfo.DeviceType.ANDROID;
                if (intent.hasExtra("device")) {
                    Log.i(SmartphoneConnector.TAG, "device: " + intent.getStringExtra("device"));
                    type = intent.getStringExtra("device").equalsIgnoreCase("ios") ? DeviceInfo.DeviceType.IOS : DeviceInfo.DeviceType.ANDROID;
                }
                ConnectedDevice.ConnectionState state = new ConnectedDevice.ConnectionState(connected, type);
                SmartphoneConnector.this.updateConnectionState(state);
            }
        }
    };

    public Activity getActivity() {
        return (Activity) this.context;
    }

    public SmartphoneConnector(SmartphoneInterface context) {
        this.context = context;
        getActivity().registerReceiver(this.phoneConnectionReceiver, new IntentFilter(ConnectHelper.MSG_STATE_UPDATED));
        this.currentState = ConnectedDevice.getConnectionState(getActivity());
        if (this.currentState.connected) {
            onConnect();
        } else {
            onDisconnect();
        }
    }

    public void onDestroy() {
        getActivity().unregisterReceiver(this.phoneConnectionReceiver);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateConnectionState(ConnectedDevice.ConnectionState state) {
        if (state.connected != this.currentState.connected) {
            this.currentState = state;
            if (this.currentState.connected) {
                onConnect();
            } else {
                onDisconnect();
            }
        }
    }

    private void onConnect() {
        if (this.mainView != null && this.overlay != null) {
            this.mainView.removeView(this.overlay);
            this.mainView.setFocusable(true);
        }
        this.context.onConnect();
    }

    private void onDisconnect() {
        switch (this.currentState.lastDeviceType) {
            case NONE:
                showNewConnectOverlay();
                break;
            case ANDROID:
                showAndroidOverlay();
                break;
            case IOS:
                if (this.context.requiresAndroid()) {
                    showNewConnectOverlay();
                    break;
                } else {
                    showIOSOverlay();
                    break;
                }
        }
        this.context.onDisconnect();
    }

    public void showAndroidOverlay() {
        this.mainView = (FrameLayout) getActivity().getWindow().getDecorView().findViewById(16908290);
        this.overlay = this.context.getAndroidOverlay();
        this.mainView.addView(this.overlay, new LinearLayout.LayoutParams(this.mainView.getLayoutParams().width, this.mainView.getLayoutParams().height));
    }

    private void showIOSOverlay() {
        this.mainView = (FrameLayout) getActivity().getWindow().getDecorView().findViewById(16908290);
        this.overlay = this.context.getIOSOverlay();
        this.mainView.addView(this.overlay, new LinearLayout.LayoutParams(this.mainView.getLayoutParams().width, this.mainView.getLayoutParams().height));
        View connectButton = this.context.getIOSConnectButton(this.overlay);
        connectButton.setOnClickListener(new View.OnClickListener() { // from class: com.reconinstruments.connect.apps.SmartphoneConnector.2
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                SmartphoneConnector.this.getActivity().startActivityForResult(new Intent("com.reconinstruments.connectdevice.CONNECT_IOS"), SmartphoneConnector.this.CONNECT_REQUEST_CODE);
            }
        });
    }

    private void showNewConnectOverlay() {
        this.mainView = (FrameLayout) getActivity().getWindow().getDecorView().findViewById(16908290);
        this.overlay = this.context.getNoConnectOverlay();
        this.mainView.addView(this.overlay, new LinearLayout.LayoutParams(this.mainView.getLayoutParams().width, this.mainView.getLayoutParams().height));
        View setupItem = this.context.getNoConnectSetupButton(this.overlay);
        setupItem.setOnClickListener(new View.OnClickListener() { // from class: com.reconinstruments.connect.apps.SmartphoneConnector.3
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                Settings.System.putString(SmartphoneConnector.this.getActivity().getContentResolver(), "DisableSmartphone", "false");
                SmartphoneConnector.this.getActivity().startActivityForResult(new Intent("com.reconinstruments.connectdevice.CONNECT"), SmartphoneConnector.this.CONNECT_REQUEST_CODE);
            }
        });
        setupItem.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: com.reconinstruments.connect.apps.SmartphoneConnector.4
            @Override // android.view.View.OnFocusChangeListener
            public void onFocusChange(View v, boolean hasFocus) {
                TransitionDrawable transition = (TransitionDrawable) v.getBackground();
                if (hasFocus) {
                    transition.startTransition(300);
                } else {
                    transition.resetTransition();
                }
            }
        });
        setupItem.setSelected(true);
        setupItem.requestFocus();
        View noshowItem = this.context.getNoConnectNoShowButton(this.overlay);
        if (noshowItem != null) {
            noshowItem.setOnClickListener(new View.OnClickListener() { // from class: com.reconinstruments.connect.apps.SmartphoneConnector.5
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    Settings.System.putString(SmartphoneConnector.this.getActivity().getContentResolver(), "DisableSmartphone", "true");
                    SmartphoneConnector.this.getActivity().finish();
                }
            });
            noshowItem.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: com.reconinstruments.connect.apps.SmartphoneConnector.6
                @Override // android.view.View.OnFocusChangeListener
                public void onFocusChange(View v, boolean hasFocus) {
                    TransitionDrawable transition = (TransitionDrawable) v.getBackground();
                    if (hasFocus) {
                        transition.startTransition(300);
                    } else {
                        transition.resetTransition();
                    }
                }
            });
        }
    }
}