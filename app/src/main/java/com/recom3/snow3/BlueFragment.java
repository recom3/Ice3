package com.recom3.snow3;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.recom3.mobilesdk.buddytracking.BuddyAirwaveService;
import com.recom3.snow3.mobilesdk.EngageSdkService;
import com.recom3.snow3.mobilesdk.HUDConnectivityService;
import com.recom3.snow3.mobilesdk.hudconnectivity.HUDStateUpdateListener;
import com.recom3.snow3.model.PairingConnectionState;
import com.recom3.snow3.model.PairingConnectionStateEnum;
import com.recom3.snow3.pairing.PairedListDeviceDialogFragment;
import com.recom3.snow3.pairing.TitleDescriptionModel;
import com.recom3.snow3.service.MediaPlayerHudService;
import com.reconinstruments.bluetooth.BluetoothProvider;
import com.reconinstruments.bluetooth.BluetoothService;
import com.reconinstruments.modlive.bluetooth.MLBluetoothService;

import static com.reconinstruments.bluetooth.ConnectionManager.BTType.BT_FILETRANSFER;

/**
 * Created by Recom3 on 05/07/2022.
 */

public class BlueFragment extends Fragment implements PairedListDeviceDialogFragment.PairedListDeviceDialogListener {

    public static final String TAG = BlueFragment.class.getSimpleName();

    //private IStartActivityDelegate mStartActivityDelegate;

    private static MediaPlayerHudService mMediaPlayerHudService = null;

    private ImageButton mGgoggleImageButton;
    private ImageView mConnectedIcon;
    private TextView mConnectionStatusTextView;

    private HUDStateUpdateListener hudStateUpdateListener = new HUDStateUpdateListener() {
        public void onHUDStateUpdate(HUDStateUpdateListener.HUD_STATE param1HUD_STATE) {
            BlueFragment.this.setConnectionState(param1HUD_STATE);
        }
    };

    private boolean mediaPlayerBound = false;

    private PairingConnectionState mCurrentPairingConnectionState;

    private boolean mWasDisconnectedByTimeOut = false;

    //private AlertDialogBuilder mAlertDialogBuilder;

    protected BluetoothService btService;

    private String macAddress;

    private ServiceConnection mediaPlayerHudServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName param1ComponentName, IBinder param1IBinder) {
            BlueFragment.this.startService(MediaPlayerHudService.class);
            BlueFragment.mMediaPlayerHudService = (MediaPlayerHudService)((EngageSdkService.LocalBinder)param1IBinder).getService();
            BlueFragment.mMediaPlayerHudService.bindDependentServices(new Handler.Callback() {
                public boolean handleMessage(Message param2Message) {
                    return false;
                }
            });
            BlueFragment.this.mediaPlayerBound = true;
        }

        public void onServiceDisconnected(ComponentName param1ComponentName) {
            BlueFragment.this.stopService(MediaPlayerHudService.class);
            BlueFragment.mMediaPlayerHudService = null;
            BlueFragment.this.mediaPlayerBound = false;
        }
    };

    private ServiceConnection BTServiceConnection = new ServiceConnection() { // from class: com.reconinstruments.bluetooth.BluetoothProvider.1
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName className, IBinder service) {
            BlueFragment.this.btService = ((BluetoothService.LocalBinder) service).getService();
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName className) {
            Log.d(BlueFragment.TAG, "bluetooth service disconnected");
            BlueFragment.this.btService = null;
        }
    };

    public void startService(Class<?> paramClass) {
        //this.mStartActivityDelegate.startService(paramClass);
    }

    public void stopService(Class<?> paramClass) {
        //this.mStartActivityDelegate.stopService(paramClass);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.view_blue, container, false);

        // Demonstration of a collection-browsing activity.

        mGgoggleImageButton = rootView.findViewById(R.id.pairing_layout_button_goggle);
        mConnectedIcon = rootView.findViewById(R.id.pairing_connection_status_icon_fragment_connected_icon);
        mConnectionStatusTextView = rootView.findViewById(R.id.pairing_layout_text_tap_to_pair_hud);

        rootView.findViewById(R.id.pairing_layout_button_goggle)
                .setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        PairingConnectionState pairingConnectionState = new PairingConnectionState(PairingConnectionStateEnum.DISCONNECTED);
                        if (pairingConnectionState.equals(BlueFragment.this.mCurrentPairingConnectionState)) {
                            BlueFragment.this.showPairedDevicesDialog();
                            return;
                        }
                        //BlueFragment.this.showDisconnectDeviceDialog();
                    }
                });

        bindBT();

        return rootView;
    }

    public void onResume() {
        super.onResume();

        this.hudStateUpdateListener.startListening(getActivity());
        //!!!
        if (MainActivityTest.mConnectivityHudService != null) {
            //Logcat.d("MainActivity.hudSrvc != null");
            setConnectionState(MainActivityTest.mConnectivityHudService.getConnectionState());

            //To test
            //setConnectionState(HUDStateUpdateListener.HUD_STATE.CONNECTED);
            //setConnectionState(HUDStateUpdateListener.HUD_STATE.DISCONNECTED);
        }
    }

    private void setConnectionState(HUDStateUpdateListener.HUD_STATE paramHUD_STATE) {
        //Logcat.d("State: " + paramHUD_STATE);
        this.mCurrentPairingConnectionState = new PairingConnectionState(paramHUD_STATE);
        if (this.mWasDisconnectedByTimeOut) {
            //this.mAlertDialogBuilder.showAlertDialog(2131230842, 2131230843);
            this.mWasDisconnectedByTimeOut = false;
        }
        if (this.mCurrentPairingConnectionState.isDisconnected())
            setStateDisconnected();
        if (this.mCurrentPairingConnectionState.isConnecting())
            setStateConnecting();
        if (this.mCurrentPairingConnectionState.isConnected()) {
            setStateConnected();
            //bindService((Service)mMediaPlayerHudService, MediaPlayerHudService.class, this.mediaPlayerHudServiceConnection);
            Intent intent = new Intent(getContext(), MediaPlayerHudService.class);
            getContext().bindService(intent, this.mediaPlayerHudServiceConnection, Context.BIND_AUTO_CREATE);

            Log.i(TAG, "Trying to connect bluetooth service #1");
            if(this.btService!=null)
            {
                Log.i(TAG, "Trying to connect bluetooth service #2");
                //this.btService.connect(BT_FILETRANSFER, this.macAddress);
            }
        }
    }

    private void setStateConnected() {
        Log.i(TAG, "setStateConnected");
        //this.mPairingHudConnectionStatusFragment.setStateConnected();
            //this.mStatusLabel.setText(getString(getConnectedString()));
            //this.mPairingConnectionStatusIconFragment.setStateConnected();
            //this.mDisconnectedIcon.setVisibility(8);
            this.mConnectedIcon.setVisibility(View.VISIBLE);
            //this.mFailConnectionIcon.setVisibility(8);
            //this.mLoadingIcon.setVisibility(8);
            //this.mWarningIcon.setVisibility(8);
        //this.mPairingRemoteConnectionStatusFragment.setStateUnsupported();
        this.mConnectionTimeHandler.removeCallbacks(this.mConnectionTimeTask);
        this.mGgoggleImageButton.setEnabled(true);
        this.mConnectionStatusTextView.setText(R.string.pairing_tap_to_disconnect);
    }

    private void setStateConnecting() {
        //this.mPairingHudConnectionStatusFragment.setStateConnecting();
        //this.mPairingRemoteConnectionStatusFragment.setStateConnecting();
        this.mGgoggleImageButton.setEnabled(false);
        this.mConnectionStatusTextView.setText(R.string.pairing_please_wait);
    }

    private void setStateDisconnected() {
        this.mConnectedIcon.setVisibility(View.INVISIBLE);
        //this.mPairingHudConnectionStatusFragment.setStateDisconnected();
        //this.mPairingRemoteConnectionStatusFragment.setStateDisconnected();
        this.mGgoggleImageButton.setEnabled(true);
        this.mConnectionStatusTextView.setText(R.string.pairing_hud_tap_to_pair);
    }

    private PairedListDeviceDialogFragment mPairedListDeviceDialogFragment;

    private Handler mConnectionTimeHandler = new Handler();

    private Runnable mConnectionTimeTask = new Runnable() {
        public void run() {
            BlueFragment.this.timeOutDisconnect();
            BlueFragment.this.mConnectionTimeHandler.removeCallbacks(this);
        }
    };

    private void timeOutDisconnect() {
        MainActivityTest.mConnectivityHudService.disconnect(HUDConnectivityService.DeviceType.ANDROID);
        this.mWasDisconnectedByTimeOut = true;
    }

    private void showPairedDevicesDialog() {
        this.mPairedListDeviceDialogFragment = PairedListDeviceDialogFragment.newInstance(this);
        this.mPairedListDeviceDialogFragment.show(getActivity().getSupportFragmentManager(), "PairedListDeviceDialogFragment");
    }

    @Override
    public void launchSubActivity(String param1String) {

        //Intent i = new Intent(this, BrowserReconLoginActivity.class);
        //startActivityForResult(i, 1);

        /*
        launchSubActivity(new Intent(param1String), new IResultCallbackActivity() {
            public void onResultCancel(Intent param1Intent) {
                onResultOk(param1Intent);
            }

            public void onResultOk(Intent param1Intent) {
                BlueFragment.this.mPairedListDeviceDialogFragment.dismiss();
                BlueFragment.this.showPairedDevicesDialog();
            }
        });
        */
    }

    @Override
    public void onDialogSelectItem(DialogFragment param1DialogFragment, TitleDescriptionModel param1TitleDescriptionModel) {

        boolean doHudSrvConnect = true;
        boolean doHudWebSrvConnect = false;

        if(doHudSrvConnect) {
            this.macAddress = param1TitleDescriptionModel.getDescription();

            MainActivityTest.mConnectivityHudService.connect(HUDConnectivityService.DeviceType.ANDROID, param1TitleDescriptionModel.getDescription());

            //!recom3: desconection time out task is commented, because we need to be connected in all moment
            this.mConnectionTimeHandler.postDelayed(this.mConnectionTimeTask, 15000L);
        }

        if(doHudWebSrvConnect)
        {
            MainActivityTest.mHUDWebService.connect("jet", 1);
        }
    }

    public synchronized void bindBT() {
        if (this.btService == null) {
            Intent intent = new Intent(getContext(), MLBluetoothService.class);
            getContext().bindService(intent, this.BTServiceConnection, Context.BIND_AUTO_CREATE);
            //getContext().bindService(new Intent("RECON_BLUETOOTH_SERVICE"), this.BTServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }
}
