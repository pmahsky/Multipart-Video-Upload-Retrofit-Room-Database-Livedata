package com.app.sampleapp.database.Repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import com.app.sampleapp.database.Dao.VideoDao;
import com.app.sampleapp.database.Entity.VideoModelEntity;
import com.app.sampleapp.database.VideoDatabase;
import com.app.sampleapp.database.background_operations.DeleteAsyncTask;
import com.app.sampleapp.database.background_operations.InsertAsyncTask;

import java.util.List;

public class VideoRepository {

    //Similar to Proxy Design Pattern, where data source need not to be known by accessing client

    private VideoDao mVideoDao;
    static final String TAG = VideoRepository.class.getSimpleName();

    public VideoRepository(Application application){

        VideoDatabase appDatabase = VideoDatabase.getAppDatabase(application);
        mVideoDao = appDatabase.mVideoDao();
    }

    public LiveData<List<VideoModelEntity>> getAllVideoModels(){

        return mVideoDao.getAllVideoModels();
    }

    public void deleteAllVideoModels(){

        new DeleteAsyncTask(mVideoDao).execute();
    }

    public void insertVideoModel(List<VideoModelEntity> videoModelEntity){

        new InsertAsyncTask(mVideoDao).execute(videoModelEntity);
    }

    public void insertVideoModel(VideoModelEntity videoModelEntity){

        new InsertAsyncTask(mVideoDao).execute(videoModelEntity);
    }
}


