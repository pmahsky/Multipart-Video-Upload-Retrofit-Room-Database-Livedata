package com.app.sampleapp.ui;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.app.sampleapp.database.Entity.VideoModelEntity;
import com.app.sampleapp.database.Repository.VideoRepository;

import java.util.ArrayList;
import java.util.List;

 public class VideoViewModel extends AndroidViewModel {

    private VideoRepository mVideoRepository;
    private List<VideoModelEntity> mVideoModelList = new ArrayList<>();

    public VideoViewModel(@NonNull Application application) {
        super(application);

        mVideoRepository = new VideoRepository(application);

    }

    void deleteAllVideoModels(){

        mVideoRepository.deleteAllVideoModels();
    }

    LiveData<List<VideoModelEntity>> getAllVideoModels(){

        return mVideoRepository.getAllVideoModels();
    }

    void insertVideoModel(List<VideoModelEntity> videoModelEntity){

        mVideoRepository.insertVideoModel(videoModelEntity);
    }

     void insertVideoModel(VideoModelEntity videoModelEntity){

         mVideoRepository.insertVideoModel(videoModelEntity);
     }


    void setVideoModelList(List<VideoModelEntity> videoModelList){

        this.mVideoModelList.clear();
        this.mVideoModelList.addAll(videoModelList);

    }

     public List<VideoModelEntity> getVideoModelList() {
         return mVideoModelList;
     }
 }
