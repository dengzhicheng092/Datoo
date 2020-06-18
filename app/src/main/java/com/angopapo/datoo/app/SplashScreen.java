package com.angopapo.datoo.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.angopapo.datoo.R;
import com.angopapo.datoo.auth.WelcomeActivity;
import com.angopapo.datoo.home.HomeActivity;
import com.angopapo.datoo.models.datoo.User;
import com.angopapo.datoo.utils.StatusBarUtil;
import com.angopapo.datoo.utils.Tools;


public class SplashScreen extends AppCompatActivity {

    User mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);

        mCurrentUser = (User)User.getCurrentUser();

        StatusBarUtil.useTransparentBarWithCurrentBackground(this);
        StatusBarUtil.setLightMode(this);

        Tools.setSystemBarColor(this, R.color.white);
        Tools.setSystemBarLight(this);

        int splashInterval = 3000;
        new Handler().postDelayed(this::goToApp, splashInterval);
    }

    public void goToApp() {

        if (mCurrentUser != null )  {

            Intent mainIntent = new Intent(SplashScreen.this, HomeActivity.class);
            startActivity(mainIntent);
            finish();

        } else {

            Intent mainIntent = new Intent(SplashScreen.this, WelcomeActivity.class);
            startActivity(mainIntent);
            finish();
        }
    }
}