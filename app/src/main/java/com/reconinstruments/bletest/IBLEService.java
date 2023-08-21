package com.reconinstruments.bletest;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/**
 * Created by recom3 on 21/08/2023.
 */

public interface IBLEService extends IInterface {
    boolean getInMusicApp() throws RemoteException;

    boolean getIsMaster() throws RemoteException;

    boolean getIsMasterBeforeonCreate() throws RemoteException;

    byte[] getOwnMacAddress() throws RemoteException;

    int getRemoteControlVersionNumber() throws RemoteException;

    int getTemperature() throws RemoteException;

    String getiOSDeviceName() throws RemoteException;

    int getiOSRemoteStatus() throws RemoteException;

    boolean hasEverConnectedAsSlave() throws RemoteException;

    boolean ifCanSendMusicXml() throws RemoteException;

    boolean ifCanSendXml() throws RemoteException;

    int incrementFailedPushCounter() throws RemoteException;

    boolean isConnected() throws RemoteException;

    int pushIncrementalRib(String str) throws RemoteException;

    int pushXml(String str) throws RemoteException;

    int sendControlByte(byte b) throws RemoteException;

    void setInMusicApp(boolean z) throws RemoteException;

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IBLEService {
        private static final String DESCRIPTOR = "com.reconinstruments.bletest.IBLEService";
        static final int TRANSACTION_getInMusicApp = 9;
        static final int TRANSACTION_getIsMaster = 2;
        static final int TRANSACTION_getIsMasterBeforeonCreate = 3;
        static final int TRANSACTION_getOwnMacAddress = 5;
        static final int TRANSACTION_getRemoteControlVersionNumber = 6;
        static final int TRANSACTION_getTemperature = 1;
        static final int TRANSACTION_getiOSDeviceName = 15;
        static final int TRANSACTION_getiOSRemoteStatus = 17;
        static final int TRANSACTION_hasEverConnectedAsSlave = 7;
        static final int TRANSACTION_ifCanSendMusicXml = 11;
        static final int TRANSACTION_ifCanSendXml = 10;
        static final int TRANSACTION_incrementFailedPushCounter = 12;
        static final int TRANSACTION_isConnected = 4;
        static final int TRANSACTION_pushIncrementalRib = 14;
        static final int TRANSACTION_pushXml = 16;
        static final int TRANSACTION_sendControlByte = 13;
        static final int TRANSACTION_setInMusicApp = 8;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IBLEService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IBLEService)) {
                return (IBLEService) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    int _result = getTemperature();
                    reply.writeNoException();
                    reply.writeInt(_result);
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result2 = getIsMaster();
                    reply.writeNoException();
                    reply.writeInt(_result2 ? 1 : 0);
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result3 = getIsMasterBeforeonCreate();
                    reply.writeNoException();
                    reply.writeInt(_result3 ? 1 : 0);
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result4 = isConnected();
                    reply.writeNoException();
                    reply.writeInt(_result4 ? 1 : 0);
                    return true;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    byte[] _result5 = getOwnMacAddress();
                    reply.writeNoException();
                    reply.writeByteArray(_result5);
                    return true;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    int _result6 = getRemoteControlVersionNumber();
                    reply.writeNoException();
                    reply.writeInt(_result6);
                    return true;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result7 = hasEverConnectedAsSlave();
                    reply.writeNoException();
                    reply.writeInt(_result7 ? 1 : 0);
                    return true;
                case 8:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _arg0 = data.readInt() != 0;
                    setInMusicApp(_arg0);
                    reply.writeNoException();
                    return true;
                case 9:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result8 = getInMusicApp();
                    reply.writeNoException();
                    reply.writeInt(_result8 ? 1 : 0);
                    return true;
                case 10:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result9 = ifCanSendXml();
                    reply.writeNoException();
                    reply.writeInt(_result9 ? 1 : 0);
                    return true;
                case 11:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result10 = ifCanSendMusicXml();
                    reply.writeNoException();
                    reply.writeInt(_result10 ? 1 : 0);
                    return true;
                case 12:
                    data.enforceInterface(DESCRIPTOR);
                    int _result11 = incrementFailedPushCounter();
                    reply.writeNoException();
                    reply.writeInt(_result11);
                    return true;
                case 13:
                    data.enforceInterface(DESCRIPTOR);
                    byte _arg02 = data.readByte();
                    int _result12 = sendControlByte(_arg02);
                    reply.writeNoException();
                    reply.writeInt(_result12);
                    return true;
                case TRANSACTION_pushIncrementalRib /* 14 */:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg03 = data.readString();
                    int _result13 = pushIncrementalRib(_arg03);
                    reply.writeNoException();
                    reply.writeInt(_result13);
                    return true;
                case 15:
                    data.enforceInterface(DESCRIPTOR);
                    String _result14 = getiOSDeviceName();
                    reply.writeNoException();
                    reply.writeString(_result14);
                    return true;
                case 16:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg04 = data.readString();
                    int _result15 = pushXml(_arg04);
                    reply.writeNoException();
                    reply.writeInt(_result15);
                    return true;
                case TRANSACTION_getiOSRemoteStatus /* 17 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _result16 = getiOSRemoteStatus();
                    reply.writeNoException();
                    reply.writeInt(_result16);
                    return true;
                case 1598968902:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements IBLEService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // com.reconinstruments.bletest.IBLEService
            public int getTemperature() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.reconinstruments.bletest.IBLEService
            public boolean getIsMaster() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readInt() != 0;
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.reconinstruments.bletest.IBLEService
            public boolean getIsMasterBeforeonCreate() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readInt() != 0;
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.reconinstruments.bletest.IBLEService
            public boolean isConnected() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readInt() != 0;
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.reconinstruments.bletest.IBLEService
            public byte[] getOwnMacAddress() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    byte[] _result = _reply.createByteArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.reconinstruments.bletest.IBLEService
            public int getRemoteControlVersionNumber() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.reconinstruments.bletest.IBLEService
            public boolean hasEverConnectedAsSlave() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readInt() != 0;
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.reconinstruments.bletest.IBLEService
            public void setInMusicApp(boolean flag) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(flag ? 1 : 0);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.reconinstruments.bletest.IBLEService
            public boolean getInMusicApp() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readInt() != 0;
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.reconinstruments.bletest.IBLEService
            public boolean ifCanSendXml() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readInt() != 0;
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.reconinstruments.bletest.IBLEService
            public boolean ifCanSendMusicXml() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readInt() != 0;
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.reconinstruments.bletest.IBLEService
            public int incrementFailedPushCounter() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.reconinstruments.bletest.IBLEService
            public int sendControlByte(byte ctrl) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByte(ctrl);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.reconinstruments.bletest.IBLEService
            public int pushIncrementalRib(String bytesString) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(bytesString);
                    this.mRemote.transact(Stub.TRANSACTION_pushIncrementalRib, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.reconinstruments.bletest.IBLEService
            public String getiOSDeviceName() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.reconinstruments.bletest.IBLEService
            public int pushXml(String xmlString) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(xmlString);
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.reconinstruments.bletest.IBLEService
            public int getiOSRemoteStatus() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getiOSRemoteStatus, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }
    }
}
