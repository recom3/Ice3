package com.reconinstruments.bluetooth.filetransfer;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.util.Log;

import com.recom3.connect.messages.MusicMessage;
import com.recom3.connect.messages.TransferResponseMessage;
import com.recom3.connect.util.FileUtils;
import com.recom3.snow3.mobilesdk.hudconnectivity.ConnectHelper;
import com.reconinstruments.bluetooth.ConnectedThread;
import com.reconinstruments.bluetooth.ConnectionManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Stack;

/**
 * Created by recom3 on 20/08/2023.
 */

public class FileTransferThread extends ConnectedThread {
    final int bufferSize;
    FileTransfer currentTransfer;
    FileInputStream fileInput;
    FileOutputStream fileOutput;
    Stack<FileTransfer> transfers;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public enum TransferCode {
        NOTHING,
        REQUEST_DATA,
        RECEIVE_COMPLETE
    }

    /* loaded from: classes.dex */
    public static class FileTransfer implements Serializable {
        private static final long serialVersionUID = 1;
        public int fileSize;
        public FileUtils.FilePath localPath;
        public FileUtils.FilePath remotePath;
        public boolean upload;

        public FileTransfer(FileUtils.FilePath localPath, FileUtils.FilePath remotePath) {
            this.localPath = localPath;
            this.remotePath = remotePath;
            this.upload = false;
        }

        public FileTransfer(FileUtils.FilePath localPath) {
            this.localPath = localPath;
            this.upload = true;
        }

        public FileTransfer(boolean upload) {
            this.upload = upload;
            this.localPath = new FileUtils.FilePath("test", FileUtils.FilePath.PathType.ROOT);
        }
    }

    public FileTransferThread(BluetoothSocket socket, ConnectionManager service) {
        super(service);
        this.bufferSize = 25600;
        Log.i(this.TAG, getName() + "(" + socket + ")");
        this.socket = socket;
        this.transfers = new Stack<>();
    }

    public void addTransfer(FileTransfer transfer) {
        try {
            Log.i(this.TAG, "queue " + (transfer.upload ? "upload" : "download") + " " + transfer.localPath.path);
        } catch (NullPointerException n) {
            Log.i(this.TAG, "transfer missing path ", n);
        }
        this.transfers.add(transfer);
        interrupt();
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        Log.i(this.TAG, getName() + ": FileTransferThread.run()");
        while (!this.cancelled) {
            this.connMgr.setState(ConnectionManager.ConnectState.CONNECTED);
            if (this.transfers.size() == 0) {
                synchronized (this) {
                    try {
                        Thread.sleep(60000L);
                    } catch (InterruptedException e) {
                        Log.i(this.TAG, getName() + ": sleep interrupted");
                    }
                }
            }
            if (this.transfers.size() != 0) {
                this.currentTransfer = this.transfers.pop();
                if (this.currentTransfer.upload) {
                    upload();
                } else {
                    download();
                }
            }
        }
        closeSocket();
        threadFinished();
    }

    private void download() {
        Log.i(this.TAG, getName() + " download(" + this.currentTransfer.localPath.getFilePath(this.service) + "," + this.currentTransfer.fileSize + ")");
        try {
            File saveFile = new File(this.currentTransfer.localPath.getFilePath(this.service));
            saveFile.getParentFile().mkdirs();
            if (saveFile.exists()) {
                saveFile.delete();
            }
            this.fileOutput = new FileOutputStream(saveFile);
            getStreams();
            byte[] dataBuffer = new byte[25600];
            int totalReadCount = 0;
            int chunkReadCount = 0;
            write(TransferCode.REQUEST_DATA);
            while (!this.cancelled) {
                try {
                    if (this.btInStream.available() > 0) {
                        int bytesRead = this.btInStream.read(dataBuffer);
                        chunkReadCount += bytesRead;
                        totalReadCount += bytesRead;
                        Log.v(this.TAG, getName() + ": Completed: " + totalReadCount + "/" + this.currentTransfer.fileSize + " " + ((totalReadCount / this.currentTransfer.fileSize) * 100.0f) + "%");
                        Log.v(this.TAG, getName() + ": chunkReadCount: " + chunkReadCount + ", totalReadCount: " + totalReadCount);
                        broadcastProgress(totalReadCount / this.currentTransfer.fileSize);
                        this.fileOutput.write(dataBuffer, 0, bytesRead);
                        if (totalReadCount >= this.currentTransfer.fileSize) {
                            sleepThread(150);
                            write(TransferCode.RECEIVE_COMPLETE);
                            finishedDownload();
                            return;
                        }
                        if (chunkReadCount >= 25599) {
                            write(TransferCode.REQUEST_DATA);
                            chunkReadCount = 0;
                        }
                        sleepThread(50);
                    } else {
                        continue;
                    }
                } catch (IOException e) {
                    Log.i(this.TAG, getName() + ": Failed to read", e);
                    cancel();
                }
            }
        } catch (FileNotFoundException e2) {
            Log.i(this.TAG, "couldn't open file for download!", e2);
        }
    }

    private void upload() {
        Log.i(this.TAG, getName() + " upload(" + this.currentTransfer.localPath.getFilePath(this.service) + "," + this.currentTransfer.fileSize + ")");
        try {
            File sendFile = new File(this.currentTransfer.localPath.getFilePath(this.service));
            this.fileInput = new FileInputStream(sendFile);
            TransferResponseMessage.ResponseBundle bundle = new TransferResponseMessage.ResponseBundle(TransferResponseMessage.TransferResponse.FILE, this.currentTransfer.localPath, this.fileInput.available());
            String message = TransferResponseMessage.compose(bundle);
            this.service.writeXMLMessage(message);
            getStreams();
            byte[] dataBuffer = new byte[25600];
            int sent = 0;
            while (!this.cancelled) {
                try {
                    Log.i(this.TAG, getName() + ": input stream.read()");
                    int type = this.btInStream.read();
                    Log.i(this.TAG, getName() + ": read byte " + type);
                    if (type == TransferCode.REQUEST_DATA.ordinal()) {
                        int readCount = this.fileInput.read(dataBuffer, 0, 25600);
                        write(dataBuffer, 0, readCount);
                        sent += readCount;
                        Log.i(this.TAG, getName() + ": sent bytes " + sent);
                        if (sent == this.currentTransfer.fileSize) {
                            while (this.btInStream.read() != TransferCode.RECEIVE_COMPLETE.ordinal()) {
                                try {
                                    sleepThread(50);
                                //} catch (IOException e) {
                                } catch (Exception e) {
                                    Log.i(this.TAG, getName() + ": failed to read", e);
                                }
                            }
                            finishedUpload();
                            return;
                        }
                    } else {
                        Log.e(this.TAG, "expected REQUEST_DATA got " + TransferCode.values()[type]);
                        return;
                    }
                } catch (IOException e2) {
                    Log.e(this.TAG, "FileTransferThread: disconnected", e2);
                    cancel();
                    return;
                }
            }
        } catch (Exception e3) {
            e3.printStackTrace();
        }
    }

    private void broadcastProgress(float progress) {
        Intent fileTransferProgress = new Intent(ConnectHelper.MSG_TRANSFER_PROGRESS);
        fileTransferProgress.putExtra(MusicMessage.ATTR_PROGRESS, progress);
        this.service.sendBroadcast(fileTransferProgress);
    }

    private void startUpload() {
        Intent fileTransferComplete = new Intent(ConnectHelper.MSG_TRANSFER_STATE);
        fileTransferComplete.putExtra(MusicMessage.ATTR_STATE, ConnectHelper.TransferState.START_UPLOAD);
        fileTransferComplete.putExtra("uploadingFile", this.currentTransfer.localPath);
        this.service.sendBroadcast(fileTransferComplete);
    }

    private void startDownload() {
        Intent fileTransferComplete = new Intent(ConnectHelper.MSG_TRANSFER_STATE);
        fileTransferComplete.putExtra(MusicMessage.ATTR_STATE, ConnectHelper.TransferState.START_DOWNLOAD);
        fileTransferComplete.putExtra("savingFile", this.currentTransfer.localPath);
        this.service.sendBroadcast(fileTransferComplete);
    }

    private void finishedUpload() {
        Intent fileTransferComplete = new Intent(ConnectHelper.MSG_TRANSFER_STATE);
        fileTransferComplete.putExtra(MusicMessage.ATTR_STATE, ConnectHelper.TransferState.FINISHED_UPLOAD);
        fileTransferComplete.putExtra("uploadedFile", this.currentTransfer.localPath);
        this.service.sendBroadcast(fileTransferComplete);
        Log.i(this.TAG, "finished upload: " + this.currentTransfer.localPath.getFilePath(this.service) + " size: " + this.currentTransfer.fileSize);
    }

    private void finishedDownload() {
        Intent fileTransferComplete = new Intent(ConnectHelper.MSG_TRANSFER_STATE);
        fileTransferComplete.putExtra(MusicMessage.ATTR_STATE, ConnectHelper.TransferState.FINISHED_DOWNLOAD);
        fileTransferComplete.putExtra("savedFile", this.currentTransfer.localPath);
        fileTransferComplete.putExtra("requestedFile", this.currentTransfer.remotePath);
        this.service.sendBroadcast(fileTransferComplete);
        Log.i(this.TAG, "finished download: " + this.currentTransfer.localPath.getFilePath(this.service) + " size: " + this.currentTransfer.fileSize);
    }

    private void write(TransferCode type) {
        Log.v(this.TAG, getName() + ".write(" + type.name() + "(" + type.ordinal() + "))");
        write(type.ordinal());
    }
}



