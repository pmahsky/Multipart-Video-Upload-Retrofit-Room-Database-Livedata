package com.app.sampleapp.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.sampleapp.R;
import com.app.sampleapp.database.Entity.VideoModelEntity;
import com.app.sampleapp.utils.AppConstants;

import java.util.ArrayList;
import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    ArrayList<VideoModelEntity> videoModelList = new ArrayList<>();

    public void setData(List<VideoModelEntity> videoList){

        this.videoModelList.clear();
        this.videoModelList.addAll(videoList);
    }

    public void updateVideoUploadStatus(VideoModelEntity _videoModelEntity){

        for (int i = 0; i < videoModelList.size(); i++) {

            VideoModelEntity videoModelEntity = videoModelList.get(i);

            if(videoModelEntity.getId() == _videoModelEntity.getId()){

                videoModelEntity.setStatus(_videoModelEntity.getStatus());

                videoModelList.set(videoModelList.indexOf(videoModelEntity),videoModelEntity);
            }
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new VideoViewHolder(LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.video_item,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder videoViewHolder, int i) {

        videoViewHolder.filePathTextView.setText(videoModelList.get(videoViewHolder.getAdapterPosition())
                .getFilePath());

        videoViewHolder.statusTextView.setText(videoModelList.get(videoViewHolder.getAdapterPosition())
                .getStatus());

        switch (videoModelList.get(videoViewHolder.getAdapterPosition()).getStatus()){

            case AppConstants.VIDEO_UPLOAD_STATUS_PENDING:
                videoViewHolder.progressBar.setVisibility(View.GONE);
                break;

            case AppConstants.VIDEO_UPLOAD_STATUS_UPLOADING:
                videoViewHolder.progressBar.setVisibility(View.VISIBLE);
                break;

            case AppConstants.VIDEO_UPLOAD_STATUS_SUCCESS:
                videoViewHolder.progressBar.setVisibility(View.GONE);
                break;

            case AppConstants.VIDEO_UPLOAD_STATUS_FAILED:
                videoViewHolder.progressBar.setVisibility(View.GONE);
                break;
        }

    }

    @Override
    public int getItemCount() {
        return videoModelList.size();
    }

    class VideoViewHolder extends RecyclerView.ViewHolder{

        private final TextView filePathTextView;
        private final TextView statusTextView;
        private final ProgressBar progressBar;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);

            filePathTextView = itemView.findViewById(R.id.filePathTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
}
