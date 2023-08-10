package com.recom3.snow3.pairing;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;

import com.recom3.snow3.R;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Recom3 on 17/07/2022.
 */

public class PairedListDeviceDialogFragment extends DialogFragment {
    private View.OnClickListener bluetoothSettingsButtonClickListener = new View.OnClickListener() {
        public void onClick(View param1View) {
            //Logcat.d("onViewCreated - onClick");
            PairedListDeviceDialogFragment.this.getListener().launchSubActivity("android.settings.BLUETOOTH_SETTINGS");
        }
    };

    private TitleDescriptionModel mNoDevicePairedTitleDescriptionModel;

    private ArrayList<TitleDescriptionModel> createTitleDescriptionModel() {
        ArrayList<TitleDescriptionModel> arrayList = new ArrayList();
        Iterator<BluetoothDevice> iterator = BluetoothHelper.getPairedBluetoothDevices().iterator();
        while (true) {
            if (!iterator.hasNext()) {
                if (arrayList.isEmpty())
                    arrayList.add(this.mNoDevicePairedTitleDescriptionModel);
                return arrayList;
            }
            BluetoothDevice bluetoothDevice = iterator.next();
            arrayList.add(new TitleDescriptionModel(String.valueOf(bluetoothDevice.getName()) + " " + bluetoothDevice.getAddress().substring(0, 5), bluetoothDevice.getAddress()));
        }
    }

    private PairedListDeviceDialogListener getListener() {
        return (PairedListDeviceDialogListener)getTargetFragment();
    }

    private View getPairingDeviceListDialogView() {
        View view = getActivity().getLayoutInflater().inflate(R.layout.pairing_device_list_dialog, null);
        ((Button)view.findViewById(R.id.pairing_device_list_button_bluetooth_settings)).setOnClickListener(this.bluetoothSettingsButtonClickListener);
        return view;
    }

    public static PairedListDeviceDialogFragment newInstance(PairedListDeviceDialogListener paramPairedListDeviceDialogListener) {
        PairedListDeviceDialogFragment pairedListDeviceDialogFragment = new PairedListDeviceDialogFragment();
        pairedListDeviceDialogFragment.setTargetFragment((Fragment)paramPairedListDeviceDialogListener, 0);
        return pairedListDeviceDialogFragment;
    }

    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        //
        this.mNoDevicePairedTitleDescriptionModel = new TitleDescriptionModel(getString(R.string.pairing_no_device_paired_title), getString(R.string.pairing_no_device_paired_message));
    }

    public Dialog onCreateDialog(Bundle paramBundle) {
        final ArrayList<TitleDescriptionModel> fetch = createTitleDescriptionModel();
        TitleDescriptionArrayAdapter titleDescriptionArrayAdapter = new TitleDescriptionArrayAdapter((Activity)getActivity(), R.id.pairing_device_list_dialog_list_device, fetch);
        AlertDialog.Builder builder = new AlertDialog.Builder((Context)getActivity());
        //!!!
        //builder.setTitle(2131230826);
        builder.setAdapter((ListAdapter)titleDescriptionArrayAdapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface param1DialogInterface, int param1Int) {
                //Logcat.d("ItemIdex: " + param1Int);
                TitleDescriptionModel titleDescriptionModel = fetch.get(param1Int);
                if (!titleDescriptionModel.equals(PairedListDeviceDialogFragment.this.mNoDevicePairedTitleDescriptionModel))
                    PairedListDeviceDialogFragment.this.getListener().onDialogSelectItem(PairedListDeviceDialogFragment.this, titleDescriptionModel);
            }
        });
        builder.setView(getPairingDeviceListDialogView());
        return (Dialog)builder.create();
    }

    public static interface PairedListDeviceDialogListener {
        void launchSubActivity(String param1String);

        void onDialogSelectItem(DialogFragment param1DialogFragment, TitleDescriptionModel param1TitleDescriptionModel);
    }
}
