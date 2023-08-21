package com.reconinstruments.bluetooth.filetransfer;

import android.os.Bundle;
import android.util.Log;

import com.recom3.connect.messages.MusicMessage;
import com.recom3.connect.messages.TransferResponseMessage;
import com.recom3.connect.util.FileUtils;
import com.recom3.snow3.mobilesdk.messages.XMLMessage;
import com.reconinstruments.bluetooth.BluetoothHelper;
import com.reconinstruments.bluetooth.BluetoothService;
import com.reconinstruments.bluetooth.ConnectionManager;
import com.reconinstruments.connect.messages.TransferRequestMessage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by recom3 on 20/08/2023.
 */

public class FileManager extends ConnectionManager {
    protected String TAG;
    private List<FileTransferThread.FileTransfer> pendingDownloads;
    private SyncHelper syncHelper;

    public FileManager(BluetoothService service) {
        super(service, ConnectionManager.BTType.BT_FILETRANSFER);
        this.TAG = "BluetoothFileManager";
        this.syncHelper = new SyncHelper(service);
        this.pendingDownloads = new ArrayList();
    }

    public void addPendingDownload(FileTransferThread.FileTransfer transfer) {
        Log.i(this.TAG, "new download pending: " + transfer.remotePath.path);
        synchronized (this.pendingDownloads) {
            this.pendingDownloads.add(transfer);
        }
    }

    public void startReceiving(TransferResponseMessage.ResponseBundle data) {
        synchronized (this.pendingDownloads) {
            for (FileTransferThread.FileTransfer transfer : this.pendingDownloads) {
                if (transfer.remotePath.equals(data.getPath())) {
                    transfer.fileSize = data.getSize();
                    queueTransfer(transfer);
                    this.pendingDownloads.remove(transfer);
                    return;
                }
            }
            Log.i(this.TAG, "Failed to receive unexpected file");
        }
    }

    public void queueTransfer(FileTransferThread.FileTransfer transfer) {
        ((FileTransferThread) this.currentThread).addTransfer(transfer);
    }

    public void handleAppMessage(BluetoothHelper.AppMSG what, Bundle bundle) {
        switch (what) {
            case START_DOWNLOAD:
                FileUtils.FilePath savePath = (FileUtils.FilePath) bundle.getSerializable("savePath");
                FileUtils.FilePath reqPath = (FileUtils.FilePath) bundle.getSerializable("requestPath");
                addPendingDownload(new FileTransferThread.FileTransfer(savePath, reqPath));
                return;
            case SYNC_FILE:
                this.syncHelper.addSyncRequest((SyncHelper.SyncRequest) bundle.getSerializable(MusicMessage.ATTR_ACTION));
                return;
            default:
                return;
        }
    }

    public void handleFileTransferMessage(String action, String message) {
        if (action.equals(XMLMessage.TRANSFER_REQUEST_MESSAGE)) {
            TransferRequestMessage.RequestBundle reqBundle = TransferRequestMessage.parseRequest(message);
            Log.i(this.TAG, "received file transfer message type: " + reqBundle.type);
            if (reqBundle.type == TransferRequestMessage.RequestType.DIR_REQUEST) {
                String dirMessage = TransferResponseMessage.compose(new TransferResponseMessage.ResponseBundle(TransferResponseMessage.TransferResponse.DIR, null));
                this.service.writeXMLMessage(dirMessage);
            } else if (reqBundle.type == TransferRequestMessage.RequestType.FILE_REQUEST) {
                Log.i(this.TAG, "queuing upload: " + reqBundle.filePath.getFilePath(this.service));
                queueTransfer(new FileTransferThread.FileTransfer(reqBundle.filePath));
            } else if (reqBundle.type == TransferRequestMessage.RequestType.FILE_CHECK) {
                this.syncHelper.sendChecksum(reqBundle.filePath);
            } else if (reqBundle.type == TransferRequestMessage.RequestType.FILE_PUSH) {
                String reqMessage = TransferRequestMessage.compose(new TransferRequestMessage.RequestBundle(TransferRequestMessage.RequestType.FILE_REQUEST, reqBundle.filePath));
                this.service.writeXMLMessage(reqMessage);
                addPendingDownload(new FileTransferThread.FileTransfer(reqBundle.destPath, reqBundle.filePath));
            } else if (reqBundle.type == TransferRequestMessage.RequestType.FILE_DELETE && reqBundle.filePath.type == FileUtils.FilePath.PathType.TRIPS && reqBundle.filePath.path.endsWith(".RIB")) {
                String path = reqBundle.filePath.getFilePath(this.service);
                new File(path).delete();
                new File(path.replace("DAY", "EVENT")).delete();
            }
        }
        if (action.equals(XMLMessage.TRANSFER_RESPONSE_MESSAGE)) {
            TransferResponseMessage.ResponseBundle bundle = TransferResponseMessage.parseResponse(message);
            if (bundle.type == TransferResponseMessage.TransferResponse.FILE) {
                startReceiving(bundle);
            } else if (bundle.type == TransferResponseMessage.TransferResponse.CHECK) {
                this.syncHelper.receivedChecksum(bundle);
            }
        }
    }
}
