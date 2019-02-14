package com.app.sampleapp.database.background_operations;

import android.os.AsyncTask;

import com.app.sampleapp.database.Dao.VideoDao;
import com.app.sampleapp.database.Entity.VideoModelEntity;
import com.app.sampleapp.events.DataInsertionEventModel;
import com.app.sampleapp.utils.AppConstants;
import com.app.sampleapp.utils.LogHelper;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

 public  class InsertAsyncTask extends AsyncTask<Object,Void, Integer> {

     private static final String TAG = InsertAsyncTask.class.getSimpleName();
     private final VideoDao mAsynctaskDao;

    public InsertAsyncTask(VideoDao dao){

        mAsynctaskDao = dao;
    }

    @Override
    protected Integer doInBackground(final Object... params) {

        if(params[0] instanceof List) {

            List dataList = (List) params[0];

            Long[] insertedRowIdArray = mAsynctaskDao.insertVideoModel(dataList);

            if (insertedRowIdArray != null && insertedRowIdArray.length > 0) {

                for (int i = 0; i < insertedRowIdArray.length; i++) {

                    LogHelper.log(TAG, "List item - Inserted Row Id === " + insertedRowIdArray[i]);

                    if (insertedRowIdArray.length == dataList.size() && insertedRowIdArray[i] >=0 ) {

                        return AppConstants.DATABASE_INSERTION_SUCCESS;
                    }
                }

            }

        }else{

            Long insertedRowId = mAsynctaskDao.insertVideoModel((VideoModelEntity) params[0]);

            if(insertedRowId != null && insertedRowId >= 0){

                LogHelper.log(TAG, "Single Item - Inserted Row Id === " + insertedRowId);

                return AppConstants.DATABASE_INSERTION_SUCCESS;

            }
        }

        return AppConstants.DATABASE_INSERTION_FAILURE;

    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);

        DataInsertionEventModel dataInsertionEventModel = new DataInsertionEventModel();

        if(result == AppConstants.DATABASE_INSERTION_SUCCESS){

            dataInsertionEventModel.setInsertStatus(AppConstants.DATABASE_INSERTION_SUCCESS);

        }else {

            dataInsertionEventModel.setInsertStatus(AppConstants.DATABASE_INSERTION_FAILURE);

        }

        EventBus.getDefault().post(dataInsertionEventModel);
    }
}

