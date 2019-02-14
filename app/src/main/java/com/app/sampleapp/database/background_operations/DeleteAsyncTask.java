package com.app.sampleapp.database.background_operations;

import android.os.AsyncTask;

import com.app.sampleapp.database.Dao.VideoDao;

public  class DeleteAsyncTask extends AsyncTask<Void,Void, Void> {

    private static final String TAG = DeleteAsyncTask.class.getSimpleName();
    private final VideoDao mAsynctaskDao;

   public DeleteAsyncTask(VideoDao dao){

       mAsynctaskDao = dao;
   }

   @Override
   protected Void doInBackground(final Void... params) {

       mAsynctaskDao.deleteAllVideoModels();
       return null;

   }

}

