package com.angopapo.datoo.adapters.datoo;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.angopapo.datoo.R;
import com.angopapo.datoo.app.Config;
import com.angopapo.datoo.app.Constants;
import com.angopapo.datoo.helpers.QuickActions;
import com.angopapo.datoo.helpers.QuickHelp;
import com.angopapo.datoo.models.datoo.User;
import com.angopapo.datoo.modules.circularimageview.CircleImageView;

import java.util.List;
import java.util.Locale;

public class UsersNearAdapterNormal extends RecyclerView.Adapter<UsersNearAdapterNormal.ViewHolder> {
    private List<User> mUsers;
    private Activity mActivity;


    public UsersNearAdapterNormal(Activity activity, List<User> users) {
        mUsers = users;
        mActivity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view;

        view = inflater.inflate(R.layout.item_users_near, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        User user = mUsers.get(position);

        QuickHelp.getAvatars(user, viewHolder.userPhoto);

        viewHolder.firstName.setText(user.getColFirstName());

        if (user.getGeoPoint() != null && User.getUser().getGeoPoint() != null){

            if (user.getGeoPoint().distanceInKilometersTo(User.getUser().getGeoPoint()) <= Config.DistanceForRealBadge){

                viewHolder.userNearBadge.setVisibility(View.VISIBLE);
                viewHolder.mUserDistance.setVisibility(View.GONE);

            } else  if (user.getGeoPoint().distanceInKilometersTo(User.getUser().getGeoPoint()) <= Config.DistanceForRealKm){

                viewHolder.userNearBadge.setVisibility(View.GONE);

                if (!User.getUser().getPrivacyDistanceEnabled()){

                    viewHolder.mUserDistance.setVisibility(View.GONE);

                } else if (!user.getPrivacyDistanceEnabled()){

                    viewHolder.mUserDistance.setVisibility(View.GONE);

                } else {

                    viewHolder.mUserDistance.setVisibility(View.VISIBLE);
                    viewHolder.mUserDistance.setText(String.format(Locale.US, "%.2f km", user.getGeoPoint().distanceInKilometersTo(User.getUser().getGeoPoint())));
                }

            } else {

                viewHolder.mUserDistance.setVisibility(View.GONE);
                viewHolder.userNearBadge.setVisibility(View.GONE);
            }

        } else {

            viewHolder.mUserDistance.setVisibility(View.GONE);
            viewHolder.userNearBadge.setVisibility(View.GONE);
        }

        viewHolder.userPhoto.setOnClickListener(v1 -> {

            if (user.getObjectId() != null){
                // Go to user profile

                QuickActions.showProfile(mActivity, user, false);

            }

        });

        if (user.getLastOnline() != null){


            if (!User.getUser().getPrivacyOnlineStatusEnabled()){

                viewHolder.userStatus.setVisibility(View.GONE);

            } else if (!user.getPrivacyOnlineStatusEnabled()){

                viewHolder.userStatus.setVisibility(View.GONE);

            } else {

                if (System.currentTimeMillis() - user.getUpdatedAt().getTime() > Constants.TIME_TO_OFFLINE) {

                    viewHolder.userStatus.setVisibility(View.GONE);

                } else if (System.currentTimeMillis() - user.getUpdatedAt().getTime() > Constants.TIME_TO_SOON) {

                    viewHolder.userStatus.setVisibility(View.VISIBLE);
                    viewHolder.userStatus.setImageResource(R.color.orange_500);

                } else {

                    viewHolder.userStatus.setVisibility(View.VISIBLE);
                    viewHolder.userStatus.setImageResource(R.color.green_500);
                }
            }

        } else {

            viewHolder.userStatus.setVisibility(View.GONE);
        }

        if (getItemCount() > 2 && position == 1) {

            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) viewHolder.userItemLayout.getLayoutParams();
            int height = viewHolder.userItemLayout.getMeasuredHeight();

            if (height <= 150){
                params.topMargin = 150;
                viewHolder.userItemLayout.setLayoutParams(params);
            }

        }

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout userItemLayout;
        CircleImageView userPhoto, userStatus;
        ImageView userNearBadge;
        TextView firstName, mUserDistance;

        ViewHolder(View v) {
            super(v);

            userItemLayout = v.findViewById(R.id.users_item_layout);
            userPhoto = v.findViewById(R.id.peopleNearby_personImage);
            firstName = v.findViewById(R.id.peopleNearby_personName);
            userNearBadge = v.findViewById(R.id.peopleNearby_personBadge);
            userStatus = v.findViewById(R.id.peopleNearby_personStatus);
            mUserDistance = v.findViewById(R.id.peopleNearby_distance);
        }
    }

}