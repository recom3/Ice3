Liftie...

I/HUDWebService: onServiceConnected
I/Choreographer: Skipped 200 frames!  The application may be doing too much work on its main thread.
I/com.reconinstruments.os.connectivity.bluetooth.HUDSPPService$AcceptThread: Accepted socket #0
I/com.reconinstruments.os.connectivity.bluetooth.HUDSPPService: setConnState connected
I/com.reconinstruments.os.connectivity.bluetooth.HUDSPPService$AcceptThread: END AcceptThreadThread[AcceptThread #0,5,main]
I/com.reconinstruments.os.connectivity.bluetooth.HUDSPPService$AcceptThread: CANCEL Thread[AcceptThread #0,5,main]
I/com.reconinstruments.os.connectivity.bluetooth.HUDSPPService$AcceptThread: created accept socket # 0/1
I/com.reconinstruments.os.connectivity.bluetooth.HUDSPPService$AcceptThread: BEGIN AcceptThreadThread[AcceptThread #0,5,main]
I/com.reconinstruments.os.connectivity.bluetooth.HUDSPPService$AcceptThread: Waiting to accept socket #0
I/Helper: Relaunching GetMetaData...

Snow3...

I/com.reconinstruments.os.connectivity.bluetooth.HUDSPPService$ConnectThread: connectSocket: socket #0 attempt 1
W/BluetoothAdapter: getBluetoothService() called with no BluetoothManagerCallback
E/AndroidRuntime: FATAL EXCEPTION: InStreamThread #0
                  Process: com.recom3.snow3, PID: 1596
                  java.lang.NullPointerException: Attempt to invoke virtual method 'boolean com.reconinstruments.os.connectivity.bluetooth.HUDBTMessage.b()' on a null object reference
                      at com.reconinstruments.os.connectivity.bluetooth.HUDBTMessageCollectionManager.a(HUDBTMessageCollectionManager.java:173)
                      at com.reconinstruments.os.connectivity.bluetooth.HUDSPPService$InStreamThread.run(HUDSPPService.java:236)
I/Process: Sending signal. PID: 1596 SIG: 9

           [ 05-19 00:01:18.995  1766: 1766 I/         ]
           power log dlsym ok
Disconnected from the target VM, address: 'localhost:8635', transport: 'socket'

Data received:
(https://codebeautify.org/ascii-to-text?utm_content=cmp-true)
123 34 100 111 73 110 112 117 116 34 58 116 114 117 101 44 34 114 101 113 117 101 115 116 77 101 116 104 111 100 34 58 49 44 34 116 105 109 101 111 117 116 34 58 49 53 48 48 48 44 34 117 114 108 34 58 34 104 116 116 112 58 92 47 92 47 108 105 102 116 105 101 46 105 110 102 111 92 47 97 112 105 92 47 109 101 116 97 34 125
{"doInput":true,"requestMethod":1,"timeout":15000,"url":"http:\/\/liftie.info\/api\/meta"}