package com.recom3.snow3.mobilesdk.btconnectivity;

import android.content.Context;
import android.util.Log;

import com.recom3.snow3.hudconnectivity.HUDConnectivityMessage;
import com.recom3.snow3.mobilesdk.HUDConnectivityService;
import com.recom3.snow3.mobilesdk.btttransport.BTTransportManager;

/**
 * Created by Recom3 on 03/04/2022.
 */

public abstract class BTConnector extends Thread {
    private static QueueMessage STOP_SIGN = new QueueMessage(new HUDConnectivityMessage());

    private static final String TAG = "BTConnector";

    public static boolean fileTransferDone = false;

    private boolean fileChannelError = false;

    private boolean fileChannelIdle = true;

    protected BTTransportManager mBTTransportManager;

    protected HUDConnectivityService.Channel mChannel;

    protected Context mContext;

    BTConnector(Context paramContext, HUDConnectivityService.Channel paramChannel, BTTransportManager paramBTTransportManager) {
        this.mContext = paramContext;
        this.mChannel = paramChannel;
        this.mBTTransportManager = paramBTTransportManager;
    }

    private void postResult(QueueMessage paramQueueMessage, boolean paramBoolean) {
        paramQueueMessage.getHUDConnectivityCallBack().onCompleted(paramBoolean);
    }

    private QueueMessage takeQueueMessage() {
        QueueMessage queueMessage = null;
        QueueMessage queueMessage1 = null;
        QueueMessage queueMessage2 = null;
        QueueMessage queueMessage3 = queueMessage1;
        try {
            switch (this.mChannel) {
                default:
                    return queueMessage2;
                case COMMAND_CHANNEL:
                    queueMessage3 = queueMessage1;
                    queueMessage1 = BTConnectivityManager.mCommandQueue.take();
                    queueMessage2 = queueMessage1;
                    queueMessage3 = queueMessage1;
                    if (!STOP_SIGN.equals(queueMessage1)) {
                        queueMessage3 = queueMessage1;
                        StringBuilder stringBuilder = new StringBuilder();
                        queueMessage3 = queueMessage1;
                        queueMessage3 = queueMessage1;
                        Log.i("BTConnector", stringBuilder.append("Taking the message from the command queue: ").append(queueMessage1.getIntentFilter()).toString());
                        queueMessage = queueMessage1;
                    }
                    break;
                case OBJECT_CHANNEL:
                    queueMessage3 = queueMessage1;
                    queueMessage1 = BTConnectivityManager.mObjectQueue.take();
                    queueMessage2 = queueMessage1;
                    queueMessage3 = queueMessage1;
                    if (!STOP_SIGN.equals(queueMessage1)) {
                        queueMessage3 = queueMessage1;
                        StringBuilder stringBuilder = new StringBuilder();
                        queueMessage3 = queueMessage1;
                        queueMessage3 = queueMessage1;
                        Log.i("BTConnector", stringBuilder.append("Taking the message from the object queue: ").append(queueMessage1.getIntentFilter()).toString());
                        queueMessage = queueMessage1;
                    }
                    break;
                case FILE_CHANNEL:
                    queueMessage3 = queueMessage1;
                    queueMessage1 = BTConnectivityManager.mFileQueue.take();
                    queueMessage2 = queueMessage1;
                    queueMessage3 = queueMessage1;
                    if (!STOP_SIGN.equals(queueMessage1)) {
                        queueMessage3 = queueMessage1;
                        StringBuilder stringBuilder = new StringBuilder();
                        queueMessage3 = queueMessage1;
                        queueMessage3 = queueMessage1;
                        Log.i("BTConnector", stringBuilder.append("Taking the message from the file queue: ").append(queueMessage1.getIntentFilter()).toString());
                        queueMessage = queueMessage1;
                    }
                    break;
            }
        } catch (InterruptedException interruptedException) {
            Log.w("BTConnector", "Stop taking the message from the queue...");
            interruptedException.printStackTrace();
            Thread.currentThread().interrupt();
            //QueueMessage queueMessage = queueMessage3;
        }

        /*
        queueMessage3 = queueMessage1;
        try {
            queueMessage1 = BTConnectivityManager.mFileQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        queueMessage2 = queueMessage1;
        queueMessage3 = queueMessage1;
        if (!STOP_SIGN.equals(queueMessage1)) {
            queueMessage3 = queueMessage1;
            StringBuilder stringBuilder = new StringBuilder();
            queueMessage3 = queueMessage1;
            queueMessage3 = queueMessage1;
            Log.i("BTConnector", stringBuilder.append("Taking the message from the file queue: ").append(queueMessage1.getIntentFilter()).toString());
            queueMessage = queueMessage1;
        }
        */

        //!!!
        return queueMessage;
    }

    void cancel() {
        Log.i("BTConnector", "Stopping " + getName() + "...");
        switch (this.mChannel) {
            default:
                interrupt();
                return;
            case COMMAND_CHANNEL:
                BTConnectivityManager.mCommandQueue.clear();
            case OBJECT_CHANNEL:
                BTConnectivityManager.mObjectQueue.clear();
            case FILE_CHANNEL:
                BTConnectivityManager.mFileQueue.clear();
                break;
        }
    }

    abstract boolean processing(QueueMessage paramQueueMessage);

    public void run() {

        QueueMessage queueMessage = null;

        while(queueMessage != BTConnector.STOP_SIGN) {
            // Byte code:
            //   0: aload_0
            //   1: new java/lang/StringBuilder
            //   4: dup
            //   5: invokespecial <init> : ()V
            //   8: ldc 'BTConnector '
            //   10: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   13: aload_0
            //   14: getfield mChannel : Lcom/reconinstruments/mobilesdk/hudconnectivity/HUDConnectivityService$Channel;
            //   17: invokevirtual append : (Ljava/lang/Object;)Ljava/lang/StringBuilder;
            //   20: ldc ' '
            //   22: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   25: aload_0
            //   26: invokevirtual append : (Ljava/lang/Object;)Ljava/lang/StringBuilder;
            //   29: invokevirtual toString : ()Ljava/lang/String;
            //   32: invokevirtual setName : (Ljava/lang/String;)V
            //   35: aconst_null
            //   36: astore_1
            //   37: invokestatic currentThread : ()Ljava/lang/Thread;
            //   40: invokevirtual isInterrupted : ()Z
            //   43: ifne -> 312
            //   46: aload_0
            //   47: getfield mContext : Landroid/content/Context;
            //   50: invokestatic getInstance : (Landroid/content/Context;)Lcom/reconinstruments/mobilesdk/btmfi/BTMfiSessionManager;
            //   53: invokevirtual isInUse : ()Z
            //   56: ifeq -> 215
            //   59: getstatic com/reconinstruments/mobilesdk/hudconnectivity/HUDConnectivityService$Channel.FILE_CHANNEL : Lcom/reconinstruments/mobilesdk/hudconnectivity/HUDConnectivityService$Channel;
            //   62: aload_0
            //   63: getfield mChannel : Lcom/reconinstruments/mobilesdk/hudconnectivity/HUDConnectivityService$Channel;
            //   66: invokevirtual compareTo : (Ljava/lang/Enum;)I
            //   69: ifne -> 215
            //   72: aload_1
            //   73: astore_2
            //   74: aload_0
            //   75: getfield fileChannelIdle : Z
            //   78: ifeq -> 145
            //   81: aload_0
            //   82: invokespecial takeQueueMessage : ()Lcom/reconinstruments/mobilesdk/btconnectivity/QueueMessage;
            queueMessage = takeQueueMessage();

            if (queueMessage != BTConnector.STOP_SIGN && queueMessage != null) {

                this.processing(queueMessage);
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}

