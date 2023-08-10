package com.reconinstruments.os.connectivity.bluetooth;

import com.reconinstruments.os.connectivity.IHUDConnectivity;

/**
 * Created by Recom3 on 15/05/2023.
 */

public interface IHUDBTService {
    IHUDConnectivity.ConnectionState getConnectionState();

    void a(HUDBTBaseService.OutputStreamContainer paramHUDBTBaseService$OutputStreamContainer);

    void a(HUDBTBaseService.OutputStreamContainer paramHUDBTBaseService$OutputStreamContainer, byte[] paramArrayOfbyte);

    void a(IHUDBTConsumer paramIHUDBTConsumer);

    HUDBTBaseService.OutputStreamContainer getOutputStreamCont();
}
