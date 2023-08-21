package com.reconinstruments.bluetooth.filetransfer;

import android.util.Log;

import com.recom3.connect.messages.TransferResponseMessage;
import com.recom3.connect.util.FileUtils;
import com.recom3.snow3.mobilesdk.hudconnectivity.ConnectHelper;
import com.reconinstruments.bluetooth.BluetoothService;
import com.reconinstruments.connect.messages.TransferRequestMessage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Chus on 20/08/2023.
 */

public class SyncHelper {
    protected String TAG = "BluetoothSyncHelper";
    private List<SyncRequest> pendingChecksums = new ArrayList();
    private BluetoothService service;

    public SyncHelper(BluetoothService service) {
        this.service = service;
    }

    /* loaded from: classes.dex */
    public static class SyncRequest implements Serializable {
        private static final long serialVersionUID = 1;
        public Action action;
        public FileUtils.FilePath checkPath;
        public FileUtils.FilePath localPath;

        /* loaded from: classes.dex */
        public enum Action {
            PUSH,
            PULL
        }

        public SyncRequest(FileUtils.FilePath checkPath, FileUtils.FilePath localPath, Action action) {
            this.checkPath = checkPath;
            this.localPath = localPath;
            this.action = action;
        }

        public SyncRequest(FileUtils.FilePath checkPath, Action action) {
            this.checkPath = checkPath;
            this.localPath = checkPath;
            this.action = action;
        }
    }

    public void receivedChecksum(TransferResponseMessage.ResponseBundle bundle) {
        FileUtils.FilePath path = bundle.getPath();
        synchronized (this.pendingChecksums) {
            Iterator i$ = this.pendingChecksums.iterator();
            while (true) {
                if (!i$.hasNext()) {
                    break;
                }
                SyncRequest ca = (SyncRequest)i$.next();
                if (ca.checkPath.fileEquals(path)) {
                    String remoteSum = bundle.getSum();
                    String localSum = getChecksum(ca.localPath);
                    Log.d(this.TAG, "remote db checksum: " + remoteSum);
                    Log.d(this.TAG, "local  db checksum: " + localSum);
                    if (!remoteSum.equals(localSum)) {
                        if (ca.action == SyncRequest.Action.PUSH) {
                            TransferRequestMessage.RequestBundle reqBundle = new TransferRequestMessage.RequestBundle(TransferRequestMessage.RequestType.FILE_PUSH, ca.localPath, path);
                            String pushMessage = TransferRequestMessage.compose(reqBundle);
                            this.service.writeXMLMessage(pushMessage);
                        } else if (ca.action == SyncRequest.Action.PULL) {
                            ConnectHelper.requestFile(this.service, path, ca.localPath);
                        }
                    }
                    this.pendingChecksums.remove(ca);
                }
            }
        }
    }

    public void sendChecksum(FileUtils.FilePath filePath) {
        String sum = getChecksum(filePath);
        TransferResponseMessage.ResponseBundle bundle = new TransferResponseMessage.ResponseBundle(TransferResponseMessage.TransferResponse.CHECK, filePath, sum);
        String chkMessage = TransferResponseMessage.compose(bundle);
        this.service.writeXMLMessage(chkMessage);
    }

    public String getChecksum(FileUtils.FilePath path) {
        String pathString = path.getFilePath(this.service);
        Log.d(this.TAG, "checking local music db checksum to send locally " + pathString);
        boolean db = false;
        if (pathString.endsWith(".db")) {
            Log.d(this.TAG, "offsetting md5 to ignore header");
            db = true;
        }
        return FileUtils.md5(pathString, db);
    }

    public void addSyncRequest(SyncRequest request) {
        synchronized (this.pendingChecksums) {
            this.pendingChecksums.add(request);
        }
    }
}