package com.angopapo.datoo.adapters.datoo;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.angopapo.datoo.R;
import com.angopapo.datoo.helpers.QuickHelp;
import com.angopapo.datoo.home.live.LiveStreamingActivity;
import com.angopapo.datoo.models.datoo.FollowModel;
import com.angopapo.datoo.models.datoo.LiveStreamModel;
import com.angopapo.datoo.models.datoo.User;
import com.parse.ParseException;

import java.util.List;

public class UsersLiveAdapter extends RecyclerView.Adapter<UsersLiveAdapter.ViewHolder> {
    private List<LiveStreamModel> mStreaming;
    private Activity mActivity;


    public UsersLiveAdapter(Activity activity, List<LiveStreamModel> streamModels) {
        mStreaming = streamModels;
        mActivity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view;

        view = inflater.inflate(R.layout.list_item_live_broadcast_following_user, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        LiveStreamModel liveStreamModel = mStreaming.get(position);

        QuickHelp.getAvatars(liveStreamModel.getAuthor(), viewHolder.mUserPhoto);

        viewHolder.mFirstName.setText(liveStreamModel.getAuthor().getColFirstName());
        viewHolder.mViewerCount.setText(String.valueOf(liveStreamModel.getViewsLive()));

        FollowModel.queryFollowSingleUser(liveStreamModel.getAuthor(), new FollowModel.QueryFollowListener() {
            @Override
            public void onQueryFollowSuccess(boolean isFollowing) {

                if (isFollowing) {
                    viewHolder.mAddFavorite.setVisibility(View.GONE);
                    viewHolder.mAddFavorite.setEnabled(false);
                    viewHolder.mAddFavorite.setImageResource(R.drawable.ic_live_action_already_following);
                } else {
                    viewHolder.mAddFavorite.setVisibility(View.VISIBLE);
                    viewHolder.mAddFavorite.setEnabled(true);
                    viewHolder.mAddFavorite.setImageResource(R.drawable.ic_live_action_follow);
                }
            }

            @Override
            public void onQueryFollowError(ParseException error) {

                viewHolder.mAddFavorite.setVisibility(View.GONE);
            }
        });

        viewHolder.mUserPhoto.setOnClickListener(v -> {

            if (QuickHelp.isInternetAvailable(mActivity)){
                QuickHelp.goToActivityStreaming(mActivity, LiveStreamingActivity.LIVE_STREAMING_VIEWER, liveStreamModel, liveStreamModel.getObjectId());
            } else {
                QuickHelp.showNotification(mActivity, mActivity.getString(R.string.not_internet_connection), true);
            }
        });

        viewHolder.mAddFavorite.setOnClickListener(v1 -> {

            viewHolder.mAddFavorite.setVisibility(View.VISIBLE);
            viewHolder.mAddFavorite.setEnabled(false);
            viewHolder.mAddFavorite.setImageResource(R.drawable.ic_live_action_already_following);

            if (liveStreamModel.getObjectId() != null){

                FollowModel followModel = new FollowModel();
                followModel.setFromAuthor(User.getUser());
                followModel.setToAuthor(liveStreamModel.getAuthor());
                followModel.saveInBackground(e -> {

                    if (e != null){

                        viewHolder.mAddFavorite.setVisibility(View.VISIBLE);
                        viewHolder.mAddFavorite.setEnabled(true);
                        viewHolder.mAddFavorite.setImageResource(R.drawable.ic_live_action_follow);

                    }
                });

            }

        });

    }

    @Override
    public int getItemCount() {
        return mStreaming.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView mUserPhoto;
        TextView mFirstName, mViewerCount;
        ImageView mAddFavorite, mGoalIcon;

        ViewHolder(View v) {
            super(v);

            mUserPhoto = v.findViewById(R.id.liveBroadcastItem_userPhoto);
            mFirstName = v.findViewById(R.id.liveBroadcastItem_userName);
            mViewerCount = v.findViewById(R.id.liveBroadcastItem_viewersCount);
            mAddFavorite = v.findViewById(R.id.liveBroadcastItem_favouriteButton);
            mGoalIcon = v.findViewById(R.id.goalIcon);
        }
    }

}