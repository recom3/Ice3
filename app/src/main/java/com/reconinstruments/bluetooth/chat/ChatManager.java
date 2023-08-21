package com.reconinstruments.bluetooth.chat;

import com.reconinstruments.bluetooth.BluetoothService;
import com.reconinstruments.bluetooth.ConnectionManager;

/**
 * Created by recom3 on 21/08/2023.
 */

public class ChatManager extends ConnectionManager {
    public ChatManager(BluetoothService service) {
        super(service, ConnectionManager.BTType.BT_CHAT);
    }
}
