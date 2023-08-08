package com.recom3.snow3.mobilesdk;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * It is related mainly to bindDependentServices.
 * So most propername should be:
 * ParentService or
 * DependendServiceBinder
 */

public abstract class EngageSdkService extends Service {

    private static final String TAG = EngageSdkService.class.getSimpleName();

    private Handler.Callback allServicesConnected;

    List<ServiceConnection> boundServiceConnections = new ArrayList<ServiceConnection>();

    private LocalBinder mBinder = new LocalBinder();

    private int numServicesConnected = -1;

    private int numTotalServices = 0;

    private void bindDependentService(boolean paramBoolean, Class<? extends Service> paramClass, ServiceConnection paramServiceConnection) {
        if (!paramBoolean) {
            Log.v(getClass().getSimpleName(), "Binding dependent service: " + paramClass.getSimpleName());
            bindService(new Intent((Context)this, paramClass), paramServiceConnection, BIND_AUTO_CREATE);
            return;
        }
        serviceConnected();
    }

    private void serviceConnected() {
        int i = this.numServicesConnected + 1;
        this.numServicesConnected = i;
        if (i == this.numTotalServices)
            this.allServicesConnected.handleMessage(null);
    }

    public void bindDependentServices(Handler.Callback paramCallback) {
        this.allServicesConnected = paramCallback;
        List<ServiceDependency> list = getDependentServices();
        if (list == null || list.isEmpty()) {
            serviceConnected();
            return;
        }
        this.numServicesConnected = 0;
        this.numTotalServices = list.size();
        Iterator<ServiceDependency> iterator = list.iterator();
        while (true) {
            if (iterator.hasNext()) {
                ServiceDependency serviceDependency = iterator.next();
                WrapperServiceConnection wrapperServiceConnection = new WrapperServiceConnection(serviceDependency.serviceConnection);
                this.boundServiceConnections.add(wrapperServiceConnection);
                bindDependentService(serviceDependency.isServiceConnected, serviceDependency.serviceClass, wrapperServiceConnection);
                continue;
            }
            return;
        }
    }

    protected abstract List<ServiceDependency> getDependentServices();

    public IBinder onBind(Intent paramIntent) {
        return (IBinder)this.mBinder;
    }

    public void unbindDependentServices() {
        for (ServiceConnection serviceConnection : this.boundServiceConnections) {
            Log.v(TAG, "Unbinding dependent service...");
            unbindService(serviceConnection);
        }
    }

    public class LocalBinder extends Binder {
        public EngageSdkService getService() {
            return EngageSdkService.this;
        }
    }

    public static class ServiceDependency {
        boolean isServiceConnected;

        Class<? extends Service> serviceClass;

        ServiceConnection serviceConnection;

        public ServiceDependency(Class<? extends Service> param1Class, ServiceConnection param1ServiceConnection, boolean param1Boolean) {
            this.serviceClass = param1Class;
            this.serviceConnection = param1ServiceConnection;
            this.isServiceConnected = param1Boolean;
        }
    }

    private class WrapperServiceConnection implements ServiceConnection {
        private ServiceConnection userConnection;

        public WrapperServiceConnection(ServiceConnection param1ServiceConnection) {
            this.userConnection = param1ServiceConnection;
        }

        public void onServiceConnected(ComponentName param1ComponentName, IBinder param1IBinder) {
            Log.v(EngageSdkService.TAG, getClass().getSimpleName() + " connected to dependent service " + param1ComponentName.getShortClassName());
            this.userConnection.onServiceConnected(param1ComponentName, param1IBinder);
            EngageSdkService.this.serviceConnected();
        }

        public void onServiceDisconnected(ComponentName param1ComponentName) {
            Log.v(EngageSdkService.TAG, getClass().getSimpleName() + " disconnected from dependent service " + param1ComponentName.getShortClassName());
            this.userConnection.onServiceDisconnected(param1ComponentName);
        }
    }
}
