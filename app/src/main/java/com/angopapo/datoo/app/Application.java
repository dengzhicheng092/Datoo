package com.angopapo.datoo.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.angopapo.datoo.BuildConfig;
import com.angopapo.datoo.helpers.QuickHelp;
import com.angopapo.datoo.home.connections.IncomingActivity;
import com.angopapo.datoo.models.datoo.CallsModel;
import com.angopapo.datoo.models.datoo.FollowModel;
import com.angopapo.datoo.models.datoo.GiftModel;
import com.angopapo.datoo.models.datoo.LiveMessageModel;
import com.angopapo.datoo.models.datoo.LiveStreamModel;
import com.angopapo.datoo.utils.rtcUtils.AGEventHandler;
import com.angopapo.datoo.utils.rtcUtils.WorkerThread;
import com.angopapo.datoo.models.datoo.ConnectionListModel;
import com.angopapo.datoo.models.datoo.EncountersModel;
import com.angopapo.datoo.models.datoo.MessageModel;
import com.angopapo.datoo.models.datoo.ReportModel;
import com.angopapo.datoo.utils.FontsOverride;
import com.angopapo.datoo.models.datoo.User;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.facebook.ParseFacebookUtils;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.twitter.TwitterEmojiProvider;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtm.ErrorInfo;
import io.agora.rtm.LocalInvitation;
import io.agora.rtm.RemoteInvitation;

import static com.angopapo.datoo.app.Constants.LOG_TAG;

public class Application extends MultiDexApplication implements AGEventHandler, LifecycleObserver, android.app.Application.ActivityLifecycleCallbacks {

    @SuppressLint("StaticFieldLeak")
    protected static Application Instance;
    public Activity mActivity;
    Timer mTimer;

    FirebaseAnalytics mFirebaseAnalytics;

    private WorkerThread mWorkerThread;

    private RequestQueue mRequestQueue;

    public void setInstance(Application instance) {
        Application.Instance = instance;
    }

    public boolean isAppOpened;

    public boolean isChatOpened;
    public String chatObjectId;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setInstance(this);
        Instance = this;
        MultiDex.install(getBaseContext());

        registerActivityLifecycleCallbacks(this);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);

        EmojiManager.install(new TwitterEmojiProvider());

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);

        setupFonts();

        ParseObject.registerSubclass(User.class);
        ParseObject.registerSubclass(EncountersModel.class);
        ParseObject.registerSubclass(ConnectionListModel.class);
        ParseObject.registerSubclass(ReportModel.class);
        ParseObject.registerSubclass(MessageModel.class);
        ParseObject.registerSubclass(LiveStreamModel.class);
        ParseObject.registerSubclass(FollowModel.class);
        ParseObject.registerSubclass(LiveMessageModel.class);
        ParseObject.registerSubclass(GiftModel.class);
        ParseObject.registerSubclass(CallsModel.class);

        if (BuildConfig.DEBUG){
            Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);
        }

        Parse.Configuration config = new Parse.Configuration.Builder(getApplicationContext())
                .applicationId(Config.SERVER_API_KEY)
                .clientKey(Config.SERVER_CLIENT_KEY)
                .server(Config.SERVER_URL)
                .enableLocalDataStore()
                .build();
        Parse.initialize(config);

        ParseFacebookUtils.initialize(this);

        User account = User.getUser();

        Handler mHandler = new Handler();
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.post(() -> {
                    // run main thread code
                    updateTimeOnline(account);
                });
            }
        }, 1000, 30 * 1000);

        initWorkerThread();

        getWorkerThread().eventHandler().addEventHandler(this); // Move to User session if error

        if(isAccountValid(account)) {

            account.setInstallation(ParseInstallation.getCurrentInstallation());
            account.saveInBackground();

            QuickHelp.saveInstallation(account);

            mFirebaseAnalytics.setUserId(account.getObjectId());

            Crashlytics.setUserIdentifier(account.getObjectId());

            if (account.getColFullName() != null && !account.getColFullName().isEmpty()){
                Crashlytics.setUserName(account.getColFullName());
            }

            if (account.getEmail() != null && !account.getEmail().isEmpty()){
                Crashlytics.setUserEmail(account.getEmail());
            }


            getWorkerThread().connectToRtmService(String.valueOf(account.getUid()));

        } else {

            QuickHelp.removeUserToInstallation();
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onAppStared() {
        isAppOpened = true;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onAppResumed() {
        isAppOpened = true;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onAppPaused() {
        isAppOpened = false;
        setIsChatOpened(false, null);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onAppStoped() {
        isAppOpened = false;
        setIsChatOpened(false, null);
    }

    public void setIsChatOpened(boolean isOpened, String objectId){

        isChatOpened = isOpened;
        chatObjectId = objectId;
    }

    public boolean isChatOpened(){
        return isChatOpened;
    }

    public String getChatObjectId(){
        return chatObjectId;
    }


    @Override
    public void onTerminate() {
        super.onTerminate();

        RtcEngine.destroy();
        mTimer.cancel();
        setIsChatOpened(false, null);

        deInitWorkerThread();

        getWorkerThread().eventHandler().removeEventHandler(this);
    }

    public void updateTimeOnline(User user) {

        if (user != null){

            // Get Calendar Date and Time
            Calendar now = Calendar.getInstance();
            Date date = now.getTime();

            user.setLastOnline(date);
            if (user.getBirthDate() != null){
                user.setAge(QuickHelp.getAgeFromDate(user.getBirthDate()));
            }
            user.saveInBackground();

            getWorkerThread().connectToRtmService(String.valueOf(user.getUid()));

        }

    }

    public static boolean isAccountValid(ParseUser account) {
        return !(account == null || TextUtils.isEmpty(account.getObjectId()));
    }

    public static synchronized Application getInstance() {
        return Instance;
    }

    public void setupFonts(){

        FontsOverride.setDefaultFont(this, "DEFAULT", "fonts/mabry_regular_pro.ttf");
        FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/mabry_regular_pro.ttf");
        FontsOverride.setDefaultFont(this, "SERIF", "fonts/mabry_regular_pro.ttf");
        FontsOverride.setDefaultFont(this, "SANS_SERIF", "fonts/mabry_regular_pro.ttf");
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public synchronized void initWorkerThread() {
        if (mWorkerThread == null) {
            mWorkerThread = new WorkerThread(getApplicationContext());
            mWorkerThread.start();

            mWorkerThread.waitForReady();
        }
    }

    public synchronized WorkerThread getWorkerThread() {
        return mWorkerThread;
    }

    public synchronized void deInitWorkerThread() {

        if (mWorkerThread != null){

            mWorkerThread.exit();
            try {
                mWorkerThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mWorkerThread = null;
        }


    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }


    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(LOG_TAG);
        getRequestQueue().add(req);
    }

    public boolean isAppOpened(){
        return isAppOpened;
    }

    /////////////////////////// Calls ////////////////////////////

    @Override
    public void onLoginSuccess(String uid) {

    }

    @Override
    public void onLoginFailed(String uid, ErrorInfo error) {
    }

    @Override
    public void onPeerOnlineStatusQueried(String uid, boolean online) {
    }

    @Override
    public void onInvitationReceivedByPeer(LocalInvitation invitation) {
    }

    @Override
    public void onLocalInvitationAccepted(LocalInvitation invitation, String response) {
    }

    @Override
    public void onLocalInvitationRefused(LocalInvitation invitation, String response) {
    }

    @Override
    public void onLocalInvitationCanceled(LocalInvitation invitation) {
    }

    @Override
    public void onInvitationReceived(RemoteInvitation invitation) {

        getInstance().getWorkerThread().getEngineConfig().mRemoteInvitation = invitation;

        Log.v("AGORA", "onInvitationReceived");

        ParseQuery<User> parseQuery = User.getUserQuery();
        parseQuery.whereEqualTo(User.UID, Integer.valueOf(invitation.getCallerId()));
        parseQuery.getFirstInBackground((user, e) -> {

            if (e == null){
                QuickHelp.goToActivityInComing(this, IncomingActivity.class, user, invitation.getChannelId());
            }
        });
    }

    @Override
    public void onInvitationRefused(RemoteInvitation invitation) {
    }

    @Override
    public void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed) {
    }

    @Override
    public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
    }

    @Override
    public void onUserOffline(int uid, int reason) {
    }

    @Override
    public void onExtraCallback(int type, Object... data) {
    }

    @Override
    public void onLastmileQuality(int quality) {
    }

    @Override
    public void onLastmileProbeResult(IRtcEngineEventHandler.LastmileProbeResult result) {
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        mActivity = activity;
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        mActivity = null;
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }

    public Activity getCurrentActivity(){
        return mActivity;
    }
}