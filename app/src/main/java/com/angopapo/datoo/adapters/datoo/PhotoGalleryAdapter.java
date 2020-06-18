package com.angopapo.datoo.adapters.datoo;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.angopapo.datoo.R;
import com.angopapo.datoo.app.Application;
import com.bumptech.glide.Glide;
import com.parse.ParseFile;

import java.util.List;


public class PhotoGalleryAdapter extends RecyclerView.Adapter<PhotoGalleryAdapter.ViewHolder> {

    private Activity mActivity;
    private List<ParseFile> mParseFiles;

    public PhotoGalleryAdapter(Activity activity, List<ParseFile> parseFiles) {
        this.mActivity = activity;
        this.mParseFiles = parseFiles;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_photo_viewer, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        ParseFile parseFile = mParseFiles.get(position);

        ColorDrawable colorDrawable = new ColorDrawable(ContextCompat.getColor(Application.getInstance().getApplicationContext(), R.color.highlight_light_ripple));

        Glide.with(mActivity)
                .load(parseFile.getUrl())
                .placeholder(colorDrawable)
                .into(holder.mPhoto);

        holder.mPhoto.setOnClickListener(v -> mActivity.onBackPressed());
    }

    @Override
    public int getItemCount() {
        return mParseFiles.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView mPhoto;

        public ViewHolder(View itemView) {
            super(itemView);
            mPhoto = itemView.findViewById(R.id.image_slider);
        }
    }
}
