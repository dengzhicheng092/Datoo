package com.angopapo.datoo.home.encounters;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.angopapo.datoo.R;
import com.angopapo.datoo.helpers.QuickActions;
import com.angopapo.datoo.helpers.QuickHelp;
import com.angopapo.datoo.models.datoo.ConnectionListModel;
import com.angopapo.datoo.models.datoo.User;
import com.angopapo.datoo.modules.circularimageview.RoundedImage;
import com.parse.ParseException;
import com.parse.ParseUser;

public class MutualAttractionActivity extends AppCompatActivity {

    public static final String OtherUserObject = "user_object";


    User mCurrentUser, mUser;

    TextView mMessage;
    ImageView mHeart, mClose;
    Button mSayHello;

    RoundedImage mUserPhoto, mOtherUserPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mutual_attraction);

        mClose = findViewById(R.id.matchScreen_close);
        mMessage = findViewById(R.id.matchScreen_message);
        mHeart = findViewById(R.id.matchScreen_heart);
        mSayHello = findViewById(R.id.matchScreen_sendSmile);

        mUserPhoto = findViewById(R.id.matchScreen_myImage);
        mOtherUserPhoto = findViewById(R.id.matchScreen_otherUserImage);

        mCurrentUser = (User) ParseUser.getCurrentUser();

        mUser = getIntent().getParcelableExtra(OtherUserObject);

        QuickHelp.getAvatars(mCurrentUser, mUserPhoto);
        QuickHelp.getAvatars(mUser.getProfilePhotos().get(mUser.getAvatarPhotoPosition()).getUrl(), mOtherUserPhoto);

        QuickHelp.setPulseAnimation(mHeart);

        mMessage.setText(String.format("%s %s %s", getString(R.string.you_and), mUser.getColFullName(), getString(R.string.match_popup_view_message_two)));

        mClose.setOnClickListener(v -> onBackPressed());

        ConnectionListModel.queryUserConversation(mUser, new ConnectionListModel.QueryUserConversationListener() {
            @Override
            public void onQueryExist(boolean exist) {

                if (exist){

                    mSayHello.setOnClickListener(v -> sendMessage());

                } else {

                    mSayHello.setOnClickListener(v -> sayHello());
                }
            }

            @Override
            public void onQueryError(ParseException error) {

                mSayHello.setOnClickListener(v -> sendMessage());
            }
        });

    }

    public void sendMessage(){

        QuickHelp.goToActivityChat(this, mUser);
    }

    public void sayHello(){

        QuickActions.sendMessage(this, mSayHello , mCurrentUser, mUser, getString(R.string.say_hello_message));
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}