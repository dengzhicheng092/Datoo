package com.angopapo.datoo.home.live;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import com.angopapo.datoo.R;
import com.angopapo.datoo.helpers.QuickHelp;
import com.angopapo.datoo.models.datoo.FollowModel;
import com.angopapo.datoo.models.datoo.User;
import com.angopapo.datoo.utils.Tools;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Objects;

public class WalletActivity extends AppCompatActivity {


    Toolbar toolbar;

    TextView mTokens, mFollowers, mViewers, mLiveStreaming;
    AppCompatButton mStartLive;

    User mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);


        toolbar = findViewById(R.id.toolbar);

        mTokens = findViewById(R.id.tokens);
        mFollowers = findViewById(R.id.stats_followers);
        mViewers = findViewById(R.id.stats_viewers);
        mLiveStreaming = findViewById(R.id.stats_streams);

        mStartLive = findViewById(R.id.streamingList_goLiveButton);

        mCurrentUser = (User) ParseUser.getCurrentUser();

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Tools.setSystemBarColor(this, R.color.white);
        Tools.setSystemBarLight(this);

        mFollowers.setText(String.valueOf(mCurrentUser.getFollowersCount()));
        if (mCurrentUser.getLiveStreamsViewersUid() != null){
            mViewers.setText(String.valueOf(mCurrentUser.getLiveStreamsViewersUid().size()));
        } else {
            mViewers.setText("0");
        }

        mTokens.setText(String.valueOf(mCurrentUser.getTokens()));

        mLiveStreaming.setText(String.valueOf(mCurrentUser.getLiveSteamsCount()));

        mLiveStreaming.setOnClickListener(v -> QuickHelp.goToActivityWithNoClean(this, LiveActivity.class));

        mStartLive.setOnClickListener(v -> {

            if (QuickHelp.isInternetAvailable(this)){
                QuickHelp.goToActivityStreaming(this, LiveStreamingActivity.LIVE_STREAMING_STREAMER);
            } else {
                QuickHelp.showNotification(this, getString(R.string.not_internet_connection), true);
            }

        });

        init();

    }

    public void init(){

        ParseQuery<FollowModel> followModelParseQuery = FollowModel.getQuery();
        followModelParseQuery.whereEqualTo(FollowModel.TO_AUTHOR, mCurrentUser);
        followModelParseQuery.countInBackground((count, e) -> {

            if (e == null){

                mCurrentUser.setFollowersCount(count);
                mCurrentUser.saveInBackground();

                runOnUiThread(() -> mFollowers.setText(String.valueOf(mCurrentUser.getFollowersCount())));


            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //finish();
    }
}