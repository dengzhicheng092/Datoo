package com.angopapo.datoo.app;


import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.angopapo.datoo.R;
import com.angopapo.datoo.auth.WelcomeActivity;
import com.angopapo.datoo.helpers.QuickHelp;
import com.angopapo.datoo.home.HomeActivity;
import com.angopapo.datoo.models.datoo.User;
import com.angopapo.datoo.utils.StatusBarUtil;
import com.angopapo.datoo.utils.Tools;


public class DispatchActivity extends AppCompatActivity {

    User mCurrentUser;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

      requestWindowFeature(Window.FEATURE_NO_TITLE);
      getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

    mCurrentUser = (User)User.getCurrentUser();

      StatusBarUtil.useTransparentBarWithCurrentBackground(this);
      StatusBarUtil.setLightMode(this);

      Tools.setSystemBarColor(this, R.color.white);
      Tools.setSystemBarLight(this);


    // Check if there is a valid user session or not

    if (mCurrentUser != null )  {

        QuickHelp.goToActivityAndFinish(this, HomeActivity.class);

    } else {

        QuickHelp.goToActivityAndFinish(this, WelcomeActivity.class);
    }


  }

}
