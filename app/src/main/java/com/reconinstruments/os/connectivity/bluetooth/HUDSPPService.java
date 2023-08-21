package com.reconinstruments.os.connectivity.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.util.SparseArray;

import com.reconinstruments.os.connectivity.IHUDConnectivity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by Recom3 on 15/05/2023.
 */

public class HUDSPPService extends HUDBTBaseService {

    private static final String[] g = new String[] { "3007e231-e2af-4742-bcc4-70648bf22599", "798e999d-5fe8-4199-bc03-ab87f8545f1a", "5ed5a87f-15af-44c4-affc-9cbb686486e5", "c88436a2-0526-47c3-b365-c8519a5ea4e1" };
    private static final int h = 4;
    private static int i = 4;
    private final UUID[] j;
    private final String k;

    private ConnectThread[] l;
    private SPPOutStreamWriter sppOutStreamWriter;
    private SparseArray<InStreamThread> n = new SparseArray();
    private AcceptThread[] acceptThread;

    public class AcceptThread extends Thread {

        private final BluetoothServerSocket btServerSocket;
        private final int c;

        public AcceptThread(int i) {
            this.c = i;
            this.btServerSocket = createSocket("H_HUDSPPService_" + i, HUDSPPService.this.j[i]);
            StringBuilder sb = new StringBuilder("created accept socket # ").append(String.valueOf(i)).append("/").append(HUDSPPService.i);
            Log.i("AcceptThread", sb.toString());
        }

        private BluetoothServerSocket createSocket(String str, UUID uuid) {
            BluetoothServerSocket bss = null;
            try {
                bss = HUDSPPService.this.b.listenUsingRfcommWithServiceRecord(str, uuid);
            } catch (IOException e) {
                //throw new IOException("AcceptThread listenUsingRfcommWithServiceRecord() failed", e);
            }
            return bss;
        }

        public final void a() {
            new StringBuilder("CANCEL ").append(this);
            try {
                this.btServerSocket.close();
            } catch (IOException e) {
            }
            if (HUDSPPService.this.d == IHUDConnectivity.ConnectionState.LISTENING) {
                HUDSPPService.this.setConnState("AcceptThread:cancel", IHUDConnectivity.ConnectionState.DISCONNECTED);
            }
        }

        public void run() {
            HUDSPPService e;
            setName("AcceptThread #" + this.c);
            StringBuilder sb = new StringBuilder("BEGIN AcceptThread").append(this);
            Log.i("AcceptThread", sb.toString());

            if (HUDSPPService.this.d != IHUDConnectivity.ConnectionState.CONNECTED) {
                HUDSPPService.this.setConnState("AcceptThread:run", IHUDConnectivity.ConnectionState.LISTENING);
                try {
                    new StringBuilder("Waiting to accept socket #").append(this.c);
                    BluetoothSocket accept = this.btServerSocket.accept();
                    new StringBuilder("Accepted socket #").append(this.c);
                    e = HUDSPPService.this;
                    synchronized (e) {
                        try {
                            try {
                                HUDSPPService.this.connect(accept.getRemoteDevice(), accept, this.c);
                                //} catch (IOException e2) {
                                //} catch (InterruptedException e3) {
                            } catch (Exception e2) {
                            }
                        } catch (Throwable th) {
                            //IOException iOException = e;
                            throw th;
                        }
                    }
                } catch (IOException e4) {
                    //e = e4;
                    a();
                }
            }
            new StringBuilder("END AcceptThread").append(this);
        }

    }

    public class ConnectThread extends Thread {

        /* renamed from: a  reason: collision with root package name */
        private BluetoothSocket bluetoothSocket = null;

        /* renamed from: b  reason: collision with root package name */
        private /* synthetic */ HUDSPPService f2739b = null;
        private BluetoothDevice c = null;
        private int d;//socket?

        public ConnectThread(int i, BluetoothDevice bluetoothDevice, HUDSPPService hudsppService) {
            //this.c = i;
            this.c = bluetoothDevice;
            this.f2739b = hudsppService;
            try {
                this.bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(HUDSPPService.this.j[i]);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            new StringBuilder("created connect socket # ").append(String.valueOf(i)).append("/").append(HUDSPPService.i);
        }

        private void connect(BluetoothSocket bluetoothSocket, int i) {
            while (true) {
                i++;
                try {
                    StringBuilder sb = new StringBuilder("connectSocket: socket #").append(this.d).append(" attempt ").append(i);
                    Log.i(this.getClass().getName(), sb.toString());
                    bluetoothSocket.connect();
                    return;
                } catch (IOException e) {
                    if (i > 3) {
                        //throw e;
                    }
                    try {
                        Thread.sleep(300L);
                    }
                    catch (InterruptedException e2) {
                    }
                }
            }
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            setName("ConnectThread socket #" + this.d);
            try {
                StringBuilder sb=new StringBuilder("connecting to socket #").append(this.d);
                Log.i(this.getClass().getName(), sb.toString());
                connect(this.bluetoothSocket, 0);
                synchronized (this.f2739b) {
                    this.f2739b.l = null;
                }
                try {
                    this.f2739b.connect(this.c, this.bluetoothSocket, this.d);
                }
                catch (Exception e) {

                }
            } catch (Exception e3) {
                StringBuilder sb=new StringBuilder("Connect Failed socket #").append(this.d);
                Log.i(this.getClass().getName(), sb.toString());
                try {
                    this.bluetoothSocket.close();
                }
                catch (IOException e4) {
                }
                this.f2739b.a("ConnectThread::run");
            }
        }
    }

    public class InStreamThread extends Thread {

        /* renamed from: b  reason: collision with root package name */
        private BluetoothSocket f2741b;
        private InputStream inputStream = null;
        private final int d;

        public InStreamThread(BluetoothSocket bluetoothSocket, int i) {
            this.f2741b = bluetoothSocket;
            try {
                this.inputStream = bluetoothSocket.getInputStream();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            this.d = i;
        }

        public void a() {
            interrupt();
            new Thread(new Runnable() { // from class: com.reconinstruments.os.connectivity.bluetooth.HUDSPPService.InStreamThread.1
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        InStreamThread.this.inputStream.close();
                        InStreamThread.this.f2741b.close();
                    } catch (Exception e) {
                        new StringBuilder().append(InStreamThread.this.getName()).append(" failed to close socket");
                    }
                }
            }).start();
            HUDSPPService.this.n.delete(this.d);
            HUDSPPService.this.sppOutStreamWriter.b(this.f2741b);
        }

        /**
         *
         * @param hUDBTMessage
         */
        private void sendBTMessage(HUDBTMessage hUDBTMessage) {
            if (hUDBTMessage != null) {
                synchronized (HUDSPPService.this.f) {
                    for (int i = 0;
                         i < HUDSPPService.this.f.size() && !HUDSPPService.this.f.get(i).sendHttpRequest(hUDBTMessage.header, hUDBTMessage.payload, hUDBTMessage.d);
                         i++) {
                    }
                }
            }
        }

        @Override // java.lang.Thread, java.lang.Runnable
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public void run() {
            boolean z = false;
            setName("InStreamThread #" + this.d);
            new StringBuilder("BEGIN ").append(getName());
            byte[] bArr = new byte[1024];
            ExcessDataAgent excessDataAgent = new ExcessDataAgent();
            while (!isInterrupted()) {
                try {
                    int read = this.inputStream.read(bArr);

                    //Message deserialize
                    HUDBTMessage a2 = read > 0 ? HUDBTMessageCollectionManager.a(this.d, bArr, read, excessDataAgent) : null;

                    while (true) {
                        sendBTMessage(a2);

                        int a3 = excessDataAgent.a();
                        if (a3 > 0) {
                            if (a3 < 32) {
                                z = false;
                            } else if (HUDBTHeaderFactory.checkHeaderIsValid(excessDataAgent.buffer)) {
                                z = true;
                            } else {
                                excessDataAgent.buffer = null;
                            }
                            if (!z) {
                                a2 = HUDBTMessageCollectionManager.a(this.d, excessDataAgent.buffer, excessDataAgent.a(), excessDataAgent);
                            }
                        }
                        z = false;
                        if (!z) {
                            break;
                        }
                    }
                } catch (IOException e) {
                    a();
                    HUDSPPService.this.a("InStreamThread::run");
                    return;
                }
            }
        }
    }

    public class SPPOutStreamWriter extends HUDBTBaseService.OutStreamWriter {
        private final Collection<BluetoothSocket> d;

        public SPPOutStreamWriter(int i) {
            super(new ArrayBlockingQueue(i));
            this.d = new ArrayList();
        }

        public /*static*/ void a(HUDBTBaseService.OutputStreamContainer outputStreamContainer, byte[] bArr) {
            try {
                outputStreamContainer.a().write(bArr);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        public final void addBTSocket(BluetoothSocket bluetoothSocket) {
            OutputStreamContainer outputStreamContainer = null;
            try {
                outputStreamContainer = new OutputStreamContainer(bluetoothSocket.getOutputStream());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            this.d.add(bluetoothSocket);
            this.mBlockingQueue.offer(outputStreamContainer);
        }

        public final void closeBluetooth() {
            try {
                for (BluetoothSocket bluetoothSocket : this.d) {
                    bluetoothSocket.close();
                }
            } catch (IOException e) {
            }
            this.mBlockingQueue.clear();
            this.d.clear();
        }

        public final void b(BluetoothSocket bluetoothSocket) {
            this.d.remove(bluetoothSocket);
            this.mBlockingQueue.remove(bluetoothSocket);
        }
    }

    public HUDSPPService(IHUDConnectivity iHUDConnectivity) throws Exception {
        super(iHUDConnectivity);
        this.j = new UUID[i];
        this.k = "HUDSPPService";
        this.n = new SparseArray<>();
        createSppOutStreamWriter();
    }

    public void a(String str) {
        synchronized (this) {
            StringBuilder sb = new StringBuilder("btSocketFailed (called from ").append(str).append(")");
            if (this.n.size() == 0 && this.d != IHUDConnectivity.ConnectionState.LISTENING && this.d != IHUDConnectivity.ConnectionState.STOPPED) {
                setConnState(str, IHUDConnectivity.ConnectionState.DISCONNECTED);
                try {
                    startListening();
                } catch (Exception e) {
                    e.getMessage();
                }
            }
        }
    }

    private void createSppOutStreamWriter() {
        if (h <= 0) {
            throw new IllegalArgumentException("Count must be between 0 and" + h);
        }
        //int i = 1;
        for (int i2 = 0; i2 < i; i2++) {
            this.j[i2] = UUID.fromString(g[i2]);
        }
        this.n = new SparseArray<>();
        //Called with number of UUID
        this.sppOutStreamWriter = new SPPOutStreamWriter(i);
    }

    private void cancelStreams() {
        for (int i2 = 0; i2 < this.n.size(); i2++) {
            if (this.n.get(i2) != null) {
                this.n.get(i2).a();
            }
        }
        this.n.clear();
    }

    /*
    private void h() {
        if (this.l != null) {
            HUDSPPService$ConnectThread[] arrayOfHUDSPPService$ConnectThread = this.l;
            int i = arrayOfHUDSPPService$ConnectThread.length;
            byte b = 0;
            while (true) {
                if (b < i) {
                    HUDSPPService$ConnectThread hUDSPPService$ConnectThread = arrayOfHUDSPPService$ConnectThread[b];
                    if (hUDSPPService$ConnectThread != null)
                        try {
                            hUDSPPService$ConnectThread.a.close();
                        } catch (IOException iOException) {}
                    b++;
                    continue;
                }
                this.l = null;
                return;
            }
        }
    }
    */

    private void connectThreadClose() {
        ConnectThread[] connectThreadArr;
        if (this.l != null) {
            for (ConnectThread connectThread : this.l) {
                if (connectThread != null) {
                    try {
                        connectThread.bluetoothSocket.close();
                    } catch (IOException e) {
                    }
                }
            }
            this.l = null;
        }
    }

    private void acceptThreadCancel() {
        AcceptThread[] acceptThreadArr;
        if (this.acceptThread != null) {
            for (AcceptThread acceptThread : this.acceptThread) {
                if (acceptThread != null) {
                    acceptThread.a();
                }
            }
            this.acceptThread = null;
        }
    }

    public final void connect(BluetoothDevice bluetoothDevice, BluetoothSocket bluetoothSocket, int i2) {
        synchronized (this) {
            if (this.n.get(i2) != null) {
                this.n.get(i2).a();
                this.n.remove(i2);
            }
            InStreamThread inStreamThread = new InStreamThread(bluetoothSocket, i2);
            inStreamThread.start();
            this.n.put(i2, inStreamThread);
            this.sppOutStreamWriter.addBTSocket(bluetoothSocket);
            this.e = bluetoothDevice.getName();
            this.c.onDeviceName(this.e);
            if (this.d != IHUDConnectivity.ConnectionState.CONNECTED) {
                setConnState("connected", IHUDConnectivity.ConnectionState.CONNECTED);
            }
        }
    }

    //@Override // com.reconinstruments.os.connectivity.bluetooth.IHUDBTService
    public final void a(HUDBTBaseService.OutputStreamContainer outputStreamContainer) {
        try {
            this.sppOutStreamWriter.a(outputStreamContainer);
        } catch (InterruptedException e) {
            Log.wtf(this.f2726a, "Couldn't release OutputStreamContainer", e);
        }
    }

    //@Override // com.reconinstruments.os.connectivity.bluetooth.IHUDBTService
    public final void a(HUDBTBaseService.OutputStreamContainer outputStreamContainer, byte[] bArr) {
        synchronized (this) {
            if (this.d != IHUDConnectivity.ConnectionState.CONNECTED) {
                //throw new Exception("ConnectionState is not CONNECTED");
            }
            //SPPOutStreamWriter.a(outputStreamContainer, bArr);
            try {
                outputStreamContainer.a().write(bArr);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    @Override // com.reconinstruments.os.connectivity.bluetooth.HUDBTBaseService, com.reconinstruments.os.connectivity.bluetooth.IHUDBTService
    public final void a(IHUDBTConsumer iHUDBTConsumer) {
        super.a(iHUDBTConsumer);
    }

    public final void startListening() {
        synchronized (this) {
            connectThreadClose();
            cancelStreams();
            this.sppOutStreamWriter.closeBluetooth();
            acceptThreadCancel();
            this.acceptThread = new AcceptThread[i];
            for (int i2 = 0; i2 < this.acceptThread.length; i2++) {
                try {
                    this.acceptThread[i2] = new AcceptThread(i2);
                    this.acceptThread[i2].start();
                    //} catch (IOException e) {
                } catch (Exception e) {
                    String s=e.getMessage();
                }
            }
            setConnState("startListening", IHUDConnectivity.ConnectionState.LISTENING);
        }
    }

    public final void stopListening() {
        synchronized (this) {
            setConnState("stopListening", IHUDConnectivity.ConnectionState.STOPPED);
            connectThreadClose();
            acceptThreadCancel();
            cancelStreams();
            this.sppOutStreamWriter.closeBluetooth();
        }
    }

    //JC: 15.05.2023
    public final void connect(BluetoothAdapter paramBluetoothAdapter, BluetoothDevice bluetoothDevice) {
        synchronized (this) {
            connectThreadClose();
            cancelStreams();
            this.sppOutStreamWriter.closeBluetooth();
            acceptThreadCancel();

            //BluetoothSocket this.mSocket = bluetoothDevice.createRfcommSocketToServiceRecord(UUID.fromString(g[0]));

            int nConn = 1;
            this.l = new ConnectThread[nConn];
            for (int i2 = 0; i2 < this.l.length; i2++) {
                try {
                    this.l[i2] = new ConnectThread(i2, bluetoothDevice, this);
                    this.l[i2].start();
                } catch (Exception e) {
                }
            }
            setConnState("startConnecting", IHUDConnectivity.ConnectionState.CONNECTING);
        }
    }

    @Override // com.reconinstruments.os.connectivity.bluetooth.IHUDBTService
    public final HUDBTBaseService.OutputStreamContainer getOutputStreamCont() {
        HUDBTBaseService.OutputStreamContainer osc = null;
        try {
            osc =  this.sppOutStreamWriter.a();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        return osc;
    }
}
