package com.angopapo.datoo.home.calls;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.angopapo.datoo.app.Application;
import com.angopapo.datoo.utils.rtcUtils.ConstantApp;
import com.angopapo.datoo.utils.rtcUtils.EngineConfig;
import com.angopapo.datoo.utils.rtcUtils.MyEngineEventHandler;
import com.angopapo.datoo.utils.rtcUtils.WorkerThread;

import java.util.Arrays;


import io.agora.rtc.RtcEngine;

import static com.angopapo.datoo.utils.rtcUtils.AGEventHandler.EVENT_TYPE_ON_RELOGIN_NEEDED;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final View layout = findViewById(Window.ID_ANDROID_CONTENT);
        ViewTreeObserver vto = layout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    layout.getViewTreeObserver().addOnGlobalLayoutListener(this);
                }
                initUIandEvent();
            }
        });
    }

    protected abstract void initUIandEvent();

    protected abstract void deInitUIandEvent();

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        new Handler().postDelayed(() -> {
            if (isFinishing()) {
                return;
            }

            boolean checkPermissionResult = checkSelfPermissions();

            // so far we do not use OnRequestPermissionsResultCallback
        }, 500);
    }

    private boolean checkSelfPermissions() {
        return checkSelfPermission(Manifest.permission.RECORD_AUDIO, ConstantApp.PERMISSION_REQ_ID_RECORD_AUDIO) &&
                checkSelfPermission(Manifest.permission.CAMERA, ConstantApp.PERMISSION_REQ_ID_CAMERA) &&
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, ConstantApp.PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE) &&
                checkSelfPermission(Manifest.permission.PROCESS_OUTGOING_CALLS, ConstantApp.PERMISSION_REQ_ID_PROCESS_OUTGOING_CALL) &&
                checkSelfPermission(Manifest.permission.READ_CALL_LOG, ConstantApp.PERMISSION_REQ_ID_READ_CALL_LOG) &&
                checkSelfPermission(Manifest.permission.WRITE_CALL_LOG, ConstantApp.PERMISSION_REQ_ID_WRITE_CALL_LOG);
    }

    @Override
    protected void onDestroy() {
        deInitUIandEvent();
        super.onDestroy();
    }


    public boolean checkSelfPermission(String permission, int requestCode) {
        Log.v("AGORA","checkSelfPermission " + permission + " " + requestCode);
        if (ContextCompat.checkSelfPermission(this,
                permission)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{permission},
                    requestCode);
            return false;
        }

        if (Manifest.permission.WRITE_CALL_LOG.equals(permission)) {
            ((Application) getApplication()).initWorkerThread();
        }
        return true;
    }

    protected RtcEngine rtcEngine() {
        return ((Application) getApplication()).getWorkerThread().getRtcEngine();
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

    public final void showLongToast(final String msg) {
        this.runOnUiThread(() -> Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.v("AGORA","onRequestPermissionsResult " + requestCode + " " + Arrays.toString(permissions) + " " + Arrays.toString(grantResults));
        switch (requestCode) {
            case ConstantApp.PERMISSION_REQ_ID_RECORD_AUDIO: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkSelfPermission(Manifest.permission.CAMERA, ConstantApp.PERMISSION_REQ_ID_CAMERA);
                } else {
                    finish();
                }
                break;
            }
            case ConstantApp.PERMISSION_REQ_ID_CAMERA: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, ConstantApp.PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE);
                } else {
                    finish();
                }
                break;
            }
            case ConstantApp.PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkSelfPermission(Manifest.permission.PROCESS_OUTGOING_CALLS, ConstantApp.PERMISSION_REQ_ID_PROCESS_OUTGOING_CALL);
                } else {
                    finish();
                }
                break;
            }
            case ConstantApp.PERMISSION_REQ_ID_PROCESS_OUTGOING_CALL: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkSelfPermission(Manifest.permission.READ_CALL_LOG, ConstantApp.PERMISSION_REQ_ID_READ_CALL_LOG);
                } else {
                    finish();
                }
                break;

            }
            case ConstantApp.PERMISSION_REQ_ID_READ_CALL_LOG: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkSelfPermission(Manifest.permission.WRITE_CALL_LOG, ConstantApp.PERMISSION_REQ_ID_WRITE_CALL_LOG);

                } else {
                    finish();
                }
                break;
            }
            case ConstantApp.PERMISSION_REQ_ID_WRITE_CALL_LOG: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ((Application) getApplication()).initWorkerThread();
                } else {
                    finish();
                }
                break;

            }
        }
    }

    final void needReLogin(int type, Object... data) {
        if (EVENT_TYPE_ON_RELOGIN_NEEDED == type) {
            final boolean banned = (boolean) data[0];

            runOnUiThread(() -> {
                showLongToast("Logout, " + (banned ? "try again later" : "banned by server"));

                Intent intent = new Intent();
                intent.putExtra("result", "finish");
                setResult(RESULT_OK, intent);

                finish();
            });
        }
    }
}
