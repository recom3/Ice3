package com.reconinstruments.bluetooth.chat;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.util.Log;

import com.recom3.connect.util.XMLUtils;
import com.reconinstruments.bluetooth.BluetoothService;
import com.reconinstruments.bluetooth.ConnectedThread;
import com.reconinstruments.bluetooth.ConnectionManager;
import com.reconinstruments.connect.util.ReconMessageValidator;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Created by recom3 on 21/08/2023.
 */

public class ChatConnectedThread extends ConnectedThread {
    ReconMessageValidator xmlValidator;

    public ChatConnectedThread(BluetoothSocket socket, ConnectionManager service) {
        super(service);
        this.xmlValidator = new ReconMessageValidator();
        Log.i(this.TAG, getName() + "(" + socket + ")");
        this.socket = socket;
        getStreams();
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        Log.i(this.TAG, getName() + ".run()");
        while (!this.cancelled) {
            this.connMgr.setState(ConnectionManager.ConnectState.CONNECTED);
            try {
                byte[] buffer = new byte[BluetoothService.BT_BUFF_LEN];
                int bytesRead = this.btInStream.read(buffer);
                this.xmlValidator.appendString(buffer, bytesRead);
                String xmlMessage = this.xmlValidator.validate();
                if (xmlMessage != null) {
                    broadcastReceivedMessage(xmlMessage);
                    this.xmlValidator.reset();
                }
            } catch (IOException e) {
                Log.i(this.TAG, getName() + " read failed " + e.getMessage());
            }
        }
        connectionEnded();
        closeSocket();
        threadFinished();
    }

    public void broadcastReceivedMessage(String xml) {
        String intent = XMLUtils.getMessageIntent(xml);
        Intent msg = new Intent(intent).putExtra("message", xml);
        this.service.sendBroadcast(msg);
        Log.i(this.TAG, "received message! " + xml);
    }

    /* JADX WARN: Code restructure failed: missing block: B:17:0x0030, code lost:
        android.util.Log.i(r8.TAG, "sent message! " + r9);
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public synchronized void write(String message) {
        byte[] send = message.getBytes();
        if (send.length > 40960) {
            ByteArrayInputStream in = new ByteArrayInputStream(send);
            while (true) {
                try {
                    if (in.available() > 0) {
                        byte[] buffer = new byte[BluetoothService.BT_BUFF_LEN];
                        int bytesRead = in.read(buffer);
                        if (bytesRead <= 0) {
                            break;
                        }
                        write(buffer, 0, bytesRead);
                    } else {
                        break;
                    }
                } catch (IOException e) {
                    Log.e(this.TAG, "Error sending message!", e);
                }
            }
        } else if (send.length > 0) {
            write(send, 0, send.length);
            Log.i(this.TAG, "sent message! " + message);
        }
        try {
            Thread.sleep(50L);
        } catch (InterruptedException e2) {
        }
    }
}