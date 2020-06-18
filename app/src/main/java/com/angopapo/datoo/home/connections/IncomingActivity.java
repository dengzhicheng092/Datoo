package com.angopapo.datoo.home.connections;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.util.Log;
import android.view.Window;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import com.angopapo.datoo.R;
import com.angopapo.datoo.app.Application;
import com.angopapo.datoo.helpers.QuickHelp;
import com.angopapo.datoo.utils.rtcUtils.AGEventHandler;
import com.angopapo.datoo.utils.rtcUtils.ConstantApp;
import com.angopapo.datoo.utils.rtcUtils.EngineConfig;
import com.angopapo.datoo.utils.rtcUtils.MyEngineEventHandler;
import com.angopapo.datoo.utils.rtcUtils.WorkerThread;
import com.angopapo.datoo.home.calls.CallActivity;
import com.angopapo.datoo.models.datoo.User;
import com.angopapo.datoo.modules.circularimageview.CircleImageView;

import java.util.Timer;
import java.util.TimerTask;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtm.ErrorInfo;
import io.agora.rtm.LocalInvitation;
import io.agora.rtm.RemoteInvitation;
import io.agora.rtm.RtmStatusCode;

public class IncomingActivity extends AppCompatActivity implements AGEventHandler {

    public static String EXTRA_USER_TO_ID = "user_to_id";
    public static String EXTRA_CALL_CHANNEL = "call_channel";

    User mUser;
    String mChannel;

    MediaPlayer mPlayer;

    CircleImageView mAvatarImage;
    AppCompatTextView mCallerName;
    ImageButton mReject, mAccept;

    // Timer
    private Vibrator vibrator;
    long autoRejectDelay = 27 * 1000;
    Timer timer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_video_chat_confirmation);

        mChannel = getIntent().getStringExtra(EXTRA_CALL_CHANNEL);
        mUser = getIntent().getParcelableExtra(EXTRA_USER_TO_ID);

        mAvatarImage = findViewById(R.id.videoChatConfirmation_avatar);
        mCallerName = findViewById(R.id.videoChatConfirmation_title);
        mReject = findViewById(R.id.videoChatConfirmation_rejectButton);
        mAccept = findViewById(R.id.videoChatConfirmation_acceptVideoButton);

        QuickHelp.getAvatars(mUser, mAvatarImage);
        mCallerName.setText(mUser.getColFullName());

        Application.getInstance().getWorkerThread().eventHandler().addEventHandler(this);

        try {
            mPlayer = MediaPlayer.create(this, R.raw.video_chat_incoming_call);
            mPlayer.setLooping(true);
            mPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mAccept.setOnClickListener(v -> {

            if (mPlayer != null && mPlayer.isPlaying()) {
                mPlayer.stop();
            }

            answerTheCall();
        });

        mReject.setOnClickListener(v -> callInRefuse());

        timer = new Timer();
        timer.schedule(new TimerTask() {

            public void run() {
                callInRefuse();
            }

        }, autoRejectDelay);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] vibrate = new long[]{2000, 2000, 2000, 2000, 2000};
        if (vibrator != null) {
            vibrator.vibrate(vibrate, 1);
        }
    }

    private void answerTheCall() {

        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        if (vibrator != null) {
            vibrator.cancel();
            vibrator = null;
        }

        runOnUiThread(() -> {
            Intent intent = new Intent(this, CallActivity.class);
            intent.putExtra(ConstantApp.ACTION_KEY_SUBSCRIBER_OBJECT, mUser);
            intent.putExtra(ConstantApp.ACTION_KEY_CHANNEL_NAME, mChannel);
            intent.putExtra(ConstantApp.ACTION_KEY_MakeOrReceive, ConstantApp.CALL_IN);

            startActivity(intent);
        });

        this.finish();
    }

    private void callInRefuse() {

        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        if (vibrator != null) {
            vibrator.cancel();
            vibrator = null;
        }

        // "status": 0 // Default
        // "status": 1 // Busy
        config().mRemoteInvitation.setResponse("{\"status\":0}");

        worker().hangupTheCall(config().mRemoteInvitation);

        new Handler(Looper.getMainLooper()).postDelayed(this::finish, 2000);
    }

    @Override
    public void onLoginSuccess(String uid) {

        Log.v("AGORA", "Login success");
        //forwardToDialerActivity(uid);
    }

    @Override
    public void onLoginFailed(String uid, ErrorInfo error) {

        Log.v("AGORA", "Login Failed");

        if (error.getErrorCode() == RtmStatusCode.LoginError.LOGIN_ERR_ALREADY_LOGIN) {
            Log.v("AGORA", "Login Failed");
        } else {
            Log.v("AGORA", error.getErrorDescription());
        }
    }

    @Override
    public void onPeerOnlineStatusQueried(String uid, boolean online) {

        Log.v("AGORA", "onPeerOnlineStatusQueried");
    }

    @Override
    public void onInvitationReceivedByPeer(LocalInvitation invitation) {

        Log.v("AGORA", "onInvitationReceivedByPeer");
    }

    @Override
    public void onLocalInvitationAccepted(LocalInvitation invitation, String response) {

        Log.v("AGORA", "onLocalInvitationAccepted");
    }

    @Override
    public void onLocalInvitationRefused(LocalInvitation invitation, String response) {

        Log.v("AGORA", "onLocalInvitationRefused");
    }

    @Override
    public void onLocalInvitationCanceled(LocalInvitation invitation) {

        Log.v("AGORA", "onLocalInvitationCanceled");
    }

    @Override
    public void onInvitationReceived(RemoteInvitation invitation) {

    }

    @Override
    public void onInvitationRefused(RemoteInvitation invitation) {

        Log.v("AGORA", "onInvitationRefused");
    }

    @Override
    public void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed) {

        Log.v("AGORA", "onFirstRemoteVideoDecoded");
    }

    @Override
    public void onJoinChannelSuccess(String channel, int uid, int elapsed) {

        Log.v("AGORA", "onJoinChannelSuccess");
    }

    @Override
    public void onUserOffline(int uid, int reason) {

        Log.v("AGORA", "onUserOffline");
    }

    @Override
    public void onExtraCallback(int type, Object... data) {

        Log.v("AGORA", "onExtraCallback");
    }

    @Override
    public void onLastmileQuality(int quality) {

        Log.v("AGORA", "onLastmileQuality");
    }

    @Override
    public void onLastmileProbeResult(IRtcEngineEventHandler.LastmileProbeResult result) {

        Log.v("AGORA", "onLastmileProbeResult");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        if (vibrator != null) {
            vibrator.cancel();
            vibrator = null;
        }

        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.stop();
        }

        this.event().removeEventHandler(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        callInRefuse();
    }

    protected final WorkerThread worker() {
        return ((Application) getApplication()).getWorkerThread();
    }

    protected final EngineConfig config() {
        return ((Application) getApplication()).getWorkerThread().getEngineConfig();
    }

    protected final MyEngineEventHandler event() {
        return ((Application) getApplication()).getWorkerThread().eventHandler();
    }
}
