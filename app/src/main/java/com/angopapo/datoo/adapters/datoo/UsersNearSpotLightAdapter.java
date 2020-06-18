package com.angopapo.datoo.adapters.datoo;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.angopapo.datoo.R;
import com.angopapo.datoo.helpers.QuickActions;
import com.angopapo.datoo.helpers.QuickHelp;
import com.angopapo.datoo.home.payments.PaymentsActivity;
import com.angopapo.datoo.models.datoo.User;
import com.angopapo.datoo.modules.circularimageview.RoundedImage;

import java.util.Date;
import java.util.List;

public class UsersNearSpotLightAdapter extends RecyclerView.Adapter<UsersNearSpotLightAdapter.ViewHolder> {

    private List<User> mUserList;
    private Activity mActivity;


    public UsersNearSpotLightAdapter(Activity activity, List<User> userList) {
        mUserList = userList;
        mActivity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view;

        view = inflater.inflate(R.layout.spotlight_add_user_view, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        User user = mUserList.get(position);

        QuickHelp.getAvatars(user, viewHolder.mImage);

        if (user.getObjectId().equals(User.getUser().getObjectId())){

            if (user.getVipMoreVisits() != null && new Date().before(user.getVipMoreVisits())){
                viewHolder.mPlusBtn.setVisibility(View.GONE);
            } else {
                viewHolder.mPlusBtn.setVisibility(View.VISIBLE);
            }
        } else {
            viewHolder.mPlusBtn.setVisibility(View.GONE);
        }

        viewHolder.mImage.setOnClickListener(v -> {

            if (user.getObjectId().equals(User.getUser().getObjectId())){

                if (user.getVipMoreVisits() != null && new Date().before(user.getVipMoreVisits())){

                    QuickActions.showProfile(mActivity, user, true);

                } else {

                    QuickHelp.goToActivityFeatureActivation(mActivity, PaymentsActivity.TYPE_GET_MORE_VISITS);
                }

            } else {
                QuickActions.showProfile(mActivity, user, false);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        RoundedImage mImage;
        FrameLayout mPlusBtn;

        ViewHolder(View v) {
            super(v);
            mImage = v.findViewById(R.id.spotlightProfilePhoto);
            mPlusBtn = v.findViewById(R.id.add_btn);

        }
    }

}