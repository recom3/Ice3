package com.reconinstruments.os.connectivity;

/**
 * Created by Recom3 on 15/05/2023.
 */

public interface IHUDConnectivityConnection {
    boolean hasNetworkAccess();

    void startListening();

    void stopListening();
    //New
    void sendWebRequest(com.reconinstruments.os.connectivity.http.HUDHttpRequest hudHttpRequest);

    void connect(String paramString, int paramInt);
}

