package com.angopapo.datoo.adapters.others;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.angopapo.datoo.R;
import com.angopapo.datoo.app.Application;
import com.angopapo.datoo.home.connections.ChatActivity;
import com.angopapo.datoo.models.others.UploadModel;
import com.bumptech.glide.Glide;

import java.util.ArrayList;


public class PhotosChatUploadAdapter extends RecyclerView.Adapter {
    private ArrayList<UploadModel> uploadModelArrayList;
    private Activity mActivity;


    public PhotosChatUploadAdapter(Activity activity, ArrayList<UploadModel> uploadModels) {
        this.mActivity = activity;
        uploadModelArrayList = uploadModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.photos_item_chat, parent, false);

        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        ViewHolder holder = (ViewHolder) viewHolder;

        UploadModel selectableItem = uploadModelArrayList.get(position);

        ColorDrawable colorDrawable = new ColorDrawable(ContextCompat.getColor(Application.getInstance().getApplicationContext(), R.color.white_alpha_15));

        if (position == 0 && selectableItem.getImageUrl().equals("camera")){

            holder.ItemLayout.setOnClickListener(v -> ((ChatActivity) mActivity).takeFromCamera());

            holder.ItemLayout.setBackgroundColor(Application.getInstance().getResources().getColor(R.color.colorPrimary));

            int padding = 120;

            holder.Photo.setImageResource(R.drawable.ic_chat_control_action_multimedia);
            holder.Photo.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
            holder.Photo.setPadding(padding, padding, padding, padding);

        } else if (position == 1 && selectableItem.getImageUrl().equals("gallery")){

            holder.ItemLayout.setOnClickListener(v -> ((ChatActivity) mActivity).pickFromGallery());

            holder.ItemLayout.setBackgroundColor(Application.getInstance().getResources().getColor(R.color.colorPrimary));

            int padding = 120;

            holder.Photo.setImageResource(R.drawable.ic_generic_photo_gallery);
            holder.Photo.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
            holder.Photo.setPadding(padding, padding, padding, padding);

        } else {

            holder.ItemLayout.setOnClickListener(v -> ((ChatActivity) mActivity).sendMessageFile(selectableItem.getImageUrl()));

            Glide.with(Application.getInstance().getApplicationContext())
                    .load(selectableItem.getImageUrl())
                    .error(colorDrawable)
                    .centerCrop()
                    .circleCrop()
                    .fitCenter()
                    .placeholder(colorDrawable)
                    .into(holder.Photo);

            holder.ItemLayout.setBackgroundColor(Application.getInstance().getResources().getColor(R.color.white_alpha_15));
        }
    }

    @Override
    public int getItemCount() {
        return uploadModelArrayList.size();
    }


    public static class  ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout ItemLayout;
        ImageView Photo;

        ViewHolder(View v) {
            super(v);

            ItemLayout = v.findViewById(R.id.layout_photo_item);
            Photo = v.findViewById(R.id.photo);
        }
    }

}