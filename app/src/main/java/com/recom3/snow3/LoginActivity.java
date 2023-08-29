package com.recom3.snow3;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.recom3.connect.messages.MusicMessage;
import com.recom3.jetandroid.services.EngageHudConnectivityService;
import com.recom3.modlivemobile.utils.FileController;
import com.recom3.snow3.hudconnectivity.HUDConnectivityMessage;
import com.recom3.snow3.mobilesdk.EngageSdkService;
import com.recom3.snow3.mobilesdk.HUDConnectivityService;
import com.recom3.snow3.mobilesdk.engageweb.AuthResponse;
import com.recom3.snow3.mobilesdk.engageweb.IAuthenticationManagerCallback;
import com.recom3.snow3.mobilesdk.engageweb.UserInfo;
import com.recom3.snow3.mobilesdk.hudconnectivity.HUDStateUpdateListener;
import com.recom3.snow3.mobilesdk.messages.PhoneMessage;
import com.recom3.snow3.mobilesdk.phonecontrol.PhoneReceiver;
import com.recom3.snow3.model.User;
import com.recom3.snow3.repository.AirwaveRepository;
import com.recom3.snow3.repository.UserRepository;
import com.recom3.snow3.service.AirwaveService;
import com.recom3.snow3.service.AuthenticationAirwaveService;
import com.recom3.snow3.service.BasicModule;
import com.recom3.snow3.service.UserService;
import com.recom3.snow3.service.authentication.AuthenticationManagerHelper;
import com.recom3.snow3.util.validation.StringValidation;
import com.reconinstruments.applauncher.phone.PhoneRelayReceiver;
import com.reconinstruments.os.connectivity.HUDWebService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    public static final String LOG_TAG = "snow3_log";

    public static final String KEY_CODE_REQUEST_COMES_FROM_MENU_ITEM = "KEY_CODE_REQUEST_COMES_FROM_MENU_ITEM";

    public static final String KEY_CODE_TOKEN_URL = "code";

    private static AuthenticationAirwaveService authSrvc = null;

    private static HUDConnectivityService hudSrvc = null;

    private static NLService nlSrvc = null;

    private UserRepository mUserRepository;

    //@Inject
    private UserService mUserService;

    private AirwaveRepository mAirwaveRepository;

    //@Inject
    private AirwaveService mAirwaveService;

    PhoneRelayReceiver mPhoneRelayReceiver;

    private PhoneReceiver receiver = new PhoneReceiver();

    public static HUDWebService mHUDWebService;

    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

    @Override
    protected void onResume() {
        //IntentFilter filter = new IntentFilter();
        //filter.addAction("android.intent.action.PHONE_STATE");
        //registerReceiver(receiver, filter);
        super.onResume();
    }

    @Override
    protected void onPause() {
        //unregisterReceiver(receiver);
        super.onPause();
    }

    //----------------------------------------------------------------------------------

    private void doUnbindService() {
        if (authSrvc != null) {
            authSrvc.unbindDependentServices();
            //!recom3
            //stopService(AuthenticationAirwaveService.class);
            authSrvc = null;
        }
    }

    private void refreshSDK(String paramString) {
        if (authSrvc != null) {
            authSrvc.loginToEngage(this.authenticationManager, paramString);
            return;
        }
        //Log.e(TAG, "Authentication service is NULL");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        //BasicModule module = new BasicModule(this.getApplicationContext());
        //Injector injector = Guice.createInjector(module);
        //injector.injectMembers(this);
        this.mUserRepository = new UserRepository(this.getApplicationContext());
        this.mAirwaveRepository = new AirwaveRepository(this.getApplicationContext());

        this.mUserService = new UserService(this.mUserRepository);
        this.mAirwaveService = new AirwaveService();
        this.mAirwaveService.mAirwaveRepository = this.mAirwaveRepository;

        verifyIfUserRequestedLoginOrLogout();

        //Test for parsing trip file

        InputStream ins = getResources().openRawResource(
                getResources().getIdentifier("day13",
                        "raw", getPackageName()));

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[64000];

        try {
            while ((nRead = ins.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            //FileController.parseTripFile(buffer.toByteArray(), 0, buffer.toByteArray().length);
        } catch (IOException e) {
            e.printStackTrace();
        }

        startAuthService();

        startWebServices();

        //For incomming call testing
        //startHudService();

        //Try to bid to answer calls
        //startNLService();
        //startService(new Intent(this, NLService.class));

        //Test phone
        this.mPhoneRelayReceiver = new PhoneRelayReceiver(this);
        IntentFilter intentFilter = new IntentFilter();
        //intentFilter.addAction("RECON_PHONE_MESSAGE");
        //intentFilter.addAction("HFP_CALL_STATUS_CHANGED");

        intentFilter.addAction("INTENT_OLD_API_MESSAGE");
        Log.v("PhoneRelayService", "registering phonerelayfilter");
        registerReceiver(this.mPhoneRelayReceiver, intentFilter);

        //Test
        /*
        final Intent intent=new Intent();
        intent.setAction("com.recom3.ANSWER_CALL");
        //intent.setAction("com.recom3.ANSWERED");
        intent.putExtra("KeyName","code1id");
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.setComponent(new ComponentName("com.recom3.ice3svr","com.recom3.ice3svr.MyBroadcastReceiver"));
        //intent.setComponent(new ComponentName("com.recom3.ice3svr","com.recom3.ice3svr.MyBroadcastAnswered"));
        sendBroadcast(intent);
        */

        checkAndRequestPermissions();
        //https://stackoverflow.com/questions/47735140/securityexception-permission-denial-reading-com-android-providers-media-mediap
        //https://stackoverflow.com/questions/32431723/read-external-storage-permission-for-android
        requestRead();

        //Test avoiding login
        boolean isNotLogin = MainActivityTest.mDoAuthLogin;
        if(isNotLogin)
        {
            goToActivity(MainActivityTest.class);
        }
        
    }

    /**
     * permission code
     */
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    /**
     * requestPermissions and do something
     *
     */
    public void requestRead() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            /*
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSION);
            */

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            readFile();
        }
    }

    /**
     * onRequestPermissionsResult
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults!=null &&
                    grantResults.length>0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                readFile();
            } else {
                // Permission Denied
                //Toast.makeText(ToolbarActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                String msg = "Permission Denied";
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * do you want to do
     */
    public void readFile() {
        // do something
    }

    public  static final int PERMISSIONS_MULTIPLE_REQUEST = 123;

    private  boolean checkAndRequestPermissions() {

        //Check notifications permisions
        //Intent intent = new
        //        Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
        //startActivity(intent);

        //Phone permissions
        int readPhoneState = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        int read_call_log = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG);

        int process_outgoing_calls = ContextCompat.checkSelfPermission(this, Manifest.permission.PROCESS_OUTGOING_CALLS);
        int internet = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);

        int answer_phone_call = ContextCompat.checkSelfPermission(this, Manifest.permission.ANSWER_PHONE_CALLS);
        int call_priv = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PRIVILEGED);

        //14.08.2023
        //From Redmi 9
        /*
        08-14 22:36:11.841 1244-4055/? W/BroadcastQueue: Permission Denial: receiving Intent { act=android.intent.action.PHONE_STATE flg=0x1000010 (has extras) } to com.recom3.snow3/.mobilesdk.phonecontrol.PhoneReceiver requires android.permission.READ_CALL_LOG due to sender android (uid 1000)
        08-14 22:36:12.118 1244-3646/? W/BroadcastQueue: Permission Denial: receiving Intent { act=android.intent.action.PHONE_STATE flg=0x1000010 (has extras) } to com.recom3.snow3/.mobilesdk.phonecontrol.PhoneReceiver requires android.permission.READ_PRIVILEGED_PHONE_STATE due to sender android (uid 1000)
        */
        //Manifest.permission.READ_PRIVILEGED_PHONE_STATE
        //Manifest.permission.READ_CALL_LOG

        int read_storage = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int write_storage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        int fine_location = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int coarse_location = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        List listPermissionsNeeded = new ArrayList<>();

        if (readPhoneState != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
        }

        if (read_call_log != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_CALL_LOG);
        }

        if (process_outgoing_calls != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.PROCESS_OUTGOING_CALLS);
        }

        if (internet != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.INTERNET);
        }

        if (answer_phone_call != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ANSWER_PHONE_CALLS);
        }

        if (call_priv != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CALL_PRIVILEGED);
        }

        boolean isRequestExternal = true;
        if(isRequestExternal) {
            if (read_storage != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (write_storage != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        }

        if (fine_location != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (coarse_location != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    (String[]) listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()])
                    //,REQUEST_ID_MULTIPLE_PERMISSIONS
                    ,PERMISSIONS_MULTIPLE_REQUEST
                    );

            return false;
        }

        return true;
    }

    private ServiceConnection authConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName param1ComponentName, IBinder param1IBinder) {

            Log.i(LOG_TAG, "onServiceConnected");

            //Intent intent = new Intent(getApplicationContext(), AuthenticationAirwaveService.class);
            Intent intent = new Intent(LoginActivity.this, AuthenticationAirwaveService.class);

            //LoginActivity.this.startService(AuthenticationAirwaveService.class);
            LoginActivity.this.startService(intent);

            //LoginActivity.authSrvc = (AuthenticationAirwaveService)((EngageSdkService.LocalBinder)param1IBinder).getService();
            EngageSdkService.LocalBinder binder = (EngageSdkService.LocalBinder) param1IBinder;
            LoginActivity.authSrvc = (AuthenticationAirwaveService)binder.getService();
            LoginActivity.authSrvc.bindDependentServices(new Handler.Callback() {
                public boolean handleMessage(Message param2Message) {

                    LoginActivity.this.refreshLoginIfUserWasLoggedBefore();
                    return false;
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName param1ComponentName) {
            LoginActivity.authSrvc = null;
            //Logcat.d("Authentication Disconnected!");
        }
    };

    private ServiceConnection hudConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName param1ComponentName, IBinder param1IBinder) {

            Log.i(LOG_TAG, "onServiceConnected");

            HUDConnectivityService.LocalBinder binder = (HUDConnectivityService.LocalBinder) param1IBinder;
            LoginActivity.hudSrvc = (HUDConnectivityService) binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName param1ComponentName) {
            LoginActivity.hudSrvc = null;
        }
    };

    private ServiceConnection nlConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName param1ComponentName, IBinder param1IBinder) {
            Log.i(LOG_TAG, "onServiceConnected NLService");
        }

        @Override
        public void onServiceDisconnected(ComponentName param1ComponentName) {
            LoginActivity.nlSrvc = null;
        }
    };

    //This was implemented in AbstractRoboFragmentActivity
    private void goToActivity(Class<?> myClass)
    {
        Intent intent = new Intent(this, myClass);
        String message = "";
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    private IAuthenticationManagerCallback authenticationManager = (IAuthenticationManagerCallback)new AuthenticationManagerHelper() {
        private void saveUserAndGotoMainScreen() {
            UserInfo userInfo = LoginActivity.authSrvc.getUserInfo();
            LoginActivity.this.mUserService.save(new User(userInfo));

            LoginActivity.this.goToActivity(MainActivityTest.class);
            //LoginActivity.this.mAirwaveService.setBuddyTracking(Boolean.valueOf(false));
        }

        public void onAuthConnectionError(AuthResponse param1AuthResponse) {
            //!recom3
            //LoginActivity.this.mProgressDialog.dismiss();
            //LoginActivity.this.mUserService.delete();
            //(new AlertDialogBuilder((Activity)LoginActivity.this)).showErrorMessage(param1AuthResponse.getErrorMsg());
        }

        public void onLogin(AuthResponse param1AuthResponse) {
            //LoginActivity.this.mProgressDialog.dismiss();
            if (param1AuthResponse.isLoggedIn()) {
                saveUserAndGotoMainScreen();
                return;
            }
            //Logcat.e("failed updating SDK");
        }

        public void onLoginThirdParty(String param1String) {
            if (StringValidation.isNotEmpty(param1String))
                saveUserAndGotoMainScreen();
        }
    };

    int LAUNCH_SECOND_ACTIVITY = 1;

    public void onConnect(View param1View) {

        hudStateUpdateListener.startListening(this);

        //Testing code
        if(hudSrvc!=null) {
            hudSrvc.connect(HUDConnectivityService.DeviceType.ANDROID, "Snow2"/*paramTitleDescriptionModel.getDescription()*/);
        }
    }

    /**
     * Used to send an incoming task test to the Goggles
     * @param param1View
     */
    public void onTest(View param1View) {
        incommingCall();
    }

    public static void incommingCall()
    {
        Log.i("LoginAcivity", "Incommming call");

        if(hudSrvc!=null) {
            /*
            HUDConnectivityMessage hUDConnectivityMessage = new HUDConnectivityMessage();
            hUDConnectivityMessage.setIntentFilter("RECON_MUSIC_MESSAGE");
            hUDConnectivityMessage.setRequestKey(0);
            hUDConnectivityMessage.setSender("com.reconinstruments.mobilesdk.mediaplayer.MediaPlayerService");
            hUDConnectivityMessage.setData((new MusicMessage(paramPlayerInfo, MusicMessage.Type.STATUS)).toXML().getBytes());
            Log.i("MediaPlayerServiceSDK", "size of music status message: " + (hUDConnectivityMessage.toByteArray()).length);
            hudSrvc.push(hUDConnectivityMessage, HUDConnectivityService.Channel.OBJECT_CHANNEL);
            */

            HUDConnectivityMessage hUDConnectivityMessage = new HUDConnectivityMessage();

            //Error: sample trace
            /*
            04-13 08:35:46.804 854-915/? D/BTAcceptThread: mServerSocket.accept()
            04-13 08:35:46.804 854-1174/? D/BTConnectedThread: BEGIN BTConnectedThread OBJECT_CHANNEL Thread[Thread-75,5,main]
            04-13 08:35:56.453 854-854/? I/BTConnectivityManager: Start receiving new HUDConnectivityMessage data block, total size = 219
            04-13 08:35:56.453 854-854/? D/BTConnectivityManager: dataReceived= 219
            04-13 08:35:56.453 854-854/? I/BTConnectivityManager: Stop receiving data, constructing the HUDConnectivityMessage
            04-13 08:35:56.460 854-854/? I/BTConnectivityManager: HUDConnectivityMessage md5 = 175a68a176e560d53f51395df4b0684b
            04-13 08:35:56.460 854-854/? D/BTConnectivityManager: Sent out the broadcast to RECON_SMARTPHONE_CONNECTION_MESSAGE
            04-13 08:35:56.468 854-854/? W/Bundle: Key message expected String but value was a [B.  The default value <null> was returned.
            04-13 08:35:56.468 585-585/? W/Bundle: Key message expected String but value was a [B.  The default value <null> was returned.
            04-13 08:35:56.476 854-854/? W/Bundle: Attempt to cast generated internal exception:
                                                   java.lang.ClassCastException: byte[] cannot be cast to java.lang.String
                                                       at android.os.Bundle.getString(Bundle.java:1061)
                                                       at com.reconinstruments.hudservice.HUDService$1.onReceive(HUDService.java:92)

             */

            //hUDConnectivityMessage.setIntentFilter("RECON_SMARTPHONE_CONNECTION_MESSAGE");

            //Nothing happens
            //hUDConnectivityMessage.setIntentFilter("SMARTPHONE_STATE_UPDATED");

            //hudStateUpdateListener.startListening(this);

            hUDConnectivityMessage.setIntentFilter("RECON_PHONE_MESSAGE");
            hUDConnectivityMessage.setRequestKey(0);
            hUDConnectivityMessage.setSender("com.reconinstruments.mobilesdk.phonecontrol.PhoneControlService");
            String[] param = {"123456", "myfriend", "xx", "yy"};
            String testStr = new PhoneMessage(PhoneMessage.Status.RINGING, param).toXML().toString();

            byte[] bytes = (new PhoneMessage(PhoneMessage.Status.RINGING, param)).toXML().getBytes();
            //hUDConnectivityMessage.setData(bytes);
            hUDConnectivityMessage.setData((new PhoneMessage(PhoneMessage.Status.RINGING, param)).toXML().getBytes());

            Log.i("PhoneServiceSDK", "size of phone status message: " + (hUDConnectivityMessage.toByteArray()).length);

            MessageDigest md = null;
            try {
                md = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            md.update(hUDConnectivityMessage.getData());
            byte[] digest = md.digest();
            Log.i("PhoneServiceSDK", "md5: " + md.toString());

            //Test deserialize

            HUDConnectivityMessage hudConnectivityMessage = new HUDConnectivityMessage(hUDConnectivityMessage.toByteArray());

            PhoneMessage phnMessage = new PhoneMessage(new String(hudConnectivityMessage.getData()));

            hudSrvc.push(hUDConnectivityMessage, HUDConnectivityService.Channel.OBJECT_CHANNEL);

            //To compare with other messages...:
            /*
            HUDConnectivityMessage hUDConnectivityMessage = new HUDConnectivityMessage();
            hUDConnectivityMessage.setIntentFilter("RECON_MUSIC_MESSAGE");
            hUDConnectivityMessage.setRequestKey(0);
            hUDConnectivityMessage.setSender("com.reconinstruments.mobilesdk.mediaplayer.MediaPlayerService");
            hUDConnectivityMessage.setData((new MusicMessage(paramPlayerInfo, MusicMessage.Type.STATUS)).toXML().getBytes());
            Log.i("MediaPlayerServiceSDK", "size of music status message: " + (hUDConnectivityMessage.toByteArray()).length);
            hudSrvc.push(hUDConnectivityMessage, HUDConnectivityService.Channel.OBJECT_CHANNEL);
            */
        }
    }

    /*
    Try to login
     */
    //private View.OnClickListener mLoginOrSignupClickListener = new View.OnClickListener() {
    public void onClick(View param1View) {

        Intent i = new Intent(this, BrowserReconLoginActivity.class);
        startActivityForResult(i, LAUNCH_SECOND_ACTIVITY);

        /*
        LoginActivity.this.launchSubActivity(BrowserReconLoginActivity.class, new IResultCallbackActivity() {
            public void onResultCancel(Intent param2Intent) {
                //(new AlertDialogBuilder((Activity)LoginActivity.null.access$0(LoginActivity.null.this))).showErrorMessage(2131230863);
            }

            public void onResultOk(Intent param2Intent) {
                //(LoginActivity.null.access$0(LoginActivity.null.this)).mProgressDialog.setMessage(2131230864).show();
                String str = param2Intent.getStringExtra("code");
                //LoginActivity.authSrvc.loginToEngageThirdParty(
                //        (LoginActivity.null.access$0(LoginActivity.null.this)).authenticationManager, str);
            }
        });
        */
    }
    //};

    public void onExplore(View param1View) {

        boolean isGoToRecon = false;

        if(isGoToRecon) {
            String url = "https://www.recom3.com/web/#/market";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }
        else
        {
            Intent intent = new Intent(this, MainActivityTest.class);
            intent.putExtra(LoginActivity.KEY_CODE_REQUEST_COMES_FROM_MENU_ITEM, true);
            startActivity(intent);
            //goToActivity(MainActivityTest.class);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LAUNCH_SECOND_ACTIVITY) {
            if(resultCode == Activity.RESULT_OK){
                String result=data.getStringExtra("code");

                //This is the original implementation
                //LoginActivity.authSrvc.loginToEngageThirdParty(LoginActivity.this.authenticationManager, result);

                LoginActivity.authSrvc.loginToEngage(LoginActivity.this.authenticationManager, result);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                // Write your code if there's no result
                //(new AlertDialogBuilder((Activity)LoginActivity.null.access$0(LoginActivity.null.this))).showErrorMessage(2131230863);
                new AlertDialog.Builder(LoginActivity.this)
                        .setTitle("Delete entry")
                        .setMessage("Are you sure you want to delete this entry?")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Continue with delete operation
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        }
    } //onActivityResult

    //Some code samples:
    //https://developer.android.com/guide/components/bound-services
    //https://stackoverflow.com/questions/8341667/bind-unbind-service-example-android
    //https://stackoverflow.com/questions/10581827/service-not-being-created-or-connecting-after-bindservice
    //https://stackoverflow.com/questions/8341667/bind-unbind-service-example-android
    private void startAuthService() {
        if (authSrvc == null) {
            Intent intent = new Intent(this, AuthenticationAirwaveService.class);
            bindService(intent, this.authConn, Context.BIND_AUTO_CREATE);
        }
    }

    private void startHudService() {
        if (hudSrvc == null) {
            Intent intent = new Intent(this, HUDConnectivityService.class);
            //https://developer.android.com/guide/components/bound-services
            bindService(intent, this.hudConn, Context.BIND_AUTO_CREATE);
        }
    }

    private void startNLService() {
        if (nlSrvc == null) {
            Intent intent = new Intent(this, NLService.class);
            //https://developer.android.com/guide/components/bound-services
            bindService(intent, this.nlConn, Context.BIND_AUTO_CREATE);
        }
    }

    protected void refreshLoginIfUserWasLoggedBefore() {
        //mUserService come as injected and can be null
        if(mUserService!=null) {
            User user = this.mUserService.load();
            if (StringValidation.isNotEmpty(user.getToken())) {
                //this.mProgressDialog.setMessage(2131230864).show();
                refreshSDK(user.getToken());
            }
            //Only for testing
            else
            {
                //LoginActivity.this.goToActivity(MainActivity.class);
            }
        }
    }

    private HUDStateUpdateListener hudStateUpdateListener = new HUDStateUpdateListener() {
        public void onHUDStateUpdate(HUDStateUpdateListener.HUD_STATE param1HUD_STATE) {
            //PairingTabFragment.this.setConnectionState(param1HUD_STATE);
            TextView tv = (TextView)findViewById(R.id.textConnected);
            if(param1HUD_STATE==HUD_STATE.CONNECTED) {
                tv.setText("Connected");
            }
            else
            {
                tv.setText("Not connected");
            }
        }
    };

    public void startWebServices() {
        Intent intent = new Intent(this, HUDWebService.class);
        bindService(intent, this.hudWebSrvConn, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection hudWebSrvConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName param1ComponentName, IBinder param1IBinder) {

            Log.i("HUDWebService", "onServiceConnected");

            HUDWebService.LocalBinder binder = (HUDWebService.LocalBinder) param1IBinder;
            LoginActivity.this.mHUDWebService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName param1ComponentName) {
            LoginActivity.this.mHUDWebService = null;
        }
    };

    //Is called onCreate
    private void verifyIfUserRequestedLoginOrLogout() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            boolean requestedLoginOrLogout = extras.getBoolean(KEY_CODE_REQUEST_COMES_FROM_MENU_ITEM, false);
            if (requestedLoginOrLogout && this.mUserService!=null && this.mUserService.isLogged()) {
                this.mUserService.delete();
                //authSrvc.logout();
            }
        }
    }
}
