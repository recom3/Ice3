package com.recom3.snow3.mobilesdk.hudconnectivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.recom3.connect.util.FileUtils;

import java.io.Serializable;

/**
 * Created by Recom3 on 29/03/2022.
 */

public class ConnectHelper {

    public static void a(Context paramContext, String paramString) {
        Intent intent = new Intent("RECON_SMARTPHONE_CONNECTION_MESSAGE");
        intent.setAction("RECON_SMARTPHONE_CONNECTION_MESSAGE");
        intent.putExtra("message", paramString);
        paramContext.sendBroadcast(intent);
    }

    public static final String GEN_MSG = "RECON_SMARTPHONE_CONNECTION_MESSAGE";

    public static final String MSG_STATE_UPDATED = "CONNECT_STATE_UPDATED";

    public static final String MSG_TRANSFER_PROGRESS = "FILETRANSFER_PROGRESS";

    public static final String MSG_TRANSFER_STATE = "FILETRANSFER_STATUS";

    public static final String TAG = "ConnectHelper";

    public static void broadcastXML(Context paramContext, String paramString) {
        Intent intent = new Intent("RECON_SMARTPHONE_CONNECTION_MESSAGE");
        intent.setAction("RECON_SMARTPHONE_CONNECTION_MESSAGE");
        intent.putExtra("message", paramString);
        paramContext.sendBroadcast(intent);
    }

    public static void pushFile(Context paramContext, FileUtils.FilePath paramFilePath1, FileUtils.FilePath paramFilePath2) {
        FileUtils.FilePath filePath = paramFilePath2;
        if (paramFilePath2 == null)
            filePath = paramFilePath1;
        //!
        //broadcastXML(paramContext, TransferRequestMessage.compose(new TransferRequestMessage.RequestBundle(TransferRequestMessage.RequestType.FILE_PUSH, paramFilePath1, filePath)));
    }

    public static void requestFile(Context paramContext, FileUtils.FilePath paramFilePath1, FileUtils.FilePath paramFilePath2) {
        Log.d("ConnectHelper", "File requested: " + paramFilePath1.path);
        FileUtils.FilePath filePath = paramFilePath2;
        if (paramFilePath2 == null)
            filePath = paramFilePath1;
        Bundle bundle = new Bundle();
        bundle.putSerializable("savePath", (Serializable)filePath);
        bundle.putSerializable("requestPath", (Serializable)paramFilePath1);
        //!
        //BluetoothHelper.messageFrontEnd(paramContext, BluetoothHelper.AppMSG.START_DOWNLOAD, ConnectionManager.BTType.BT_FILETRANSFER, bundle);
        //broadcastXML(paramContext, TransferRequestMessage.compose(new TransferRequestMessage.RequestBundle(TransferRequestMessage.RequestType.FILE_REQUEST, paramFilePath1)));
    }

    public static void requestTripList(Context paramContext) {
        Log.d("ConnectHelper", "File List requested!");
        //!
        //broadcastXML(paramContext, TransferRequestMessage.compose(new TransferRequestMessage.RequestBundle(TransferRequestMessage.RequestType.DIR_REQUEST, null)));
    }

    //!
    /*
    public static void syncFile(Context paramContext, SyncHelper.SyncRequest paramSyncRequest) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("action", (Serializable)paramSyncRequest);
        //!
        //BluetoothHelper.messageFrontEnd(paramContext, BluetoothHelper.AppMSG.SYNC_FILE, ConnectionManager.BTType.BT_FILETRANSFER, bundle);
        //broadcastXML(paramContext, TransferRequestMessage.compose(new TransferRequestMessage.RequestBundle(TransferRequestMessage.RequestType.FILE_CHECK, paramSyncRequest.checkPath)));
    }
    */

    public enum TransferState {
        FINISHED_DOWNLOAD, FINISHED_UPLOAD, START_DOWNLOAD, START_UPLOAD;

        //static {
        //    $VALUES = new TransferState[] { START_DOWNLOAD, START_UPLOAD, FINISHED_DOWNLOAD, FINISHED_UPLOAD };
        //}
    }
}
