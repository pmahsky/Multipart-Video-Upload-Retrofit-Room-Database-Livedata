package com.app.sampleapp.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.app.sampleapp.R;
import com.app.sampleapp.database.Entity.VideoModelEntity;
import com.app.sampleapp.network.ApiClient;
import com.app.sampleapp.network.ApiInterface;
import com.app.sampleapp.utils.AppConstants;
import com.app.sampleapp.utils.LogHelper;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoUploadService extends Service {
    private static final String TAG = VideoUploadService.class.getSimpleName();
    private NotificationManager notificationManager;


    @Override
    public void onCreate() {
        super.onCreate();

        notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        createForegroundNotification();
    }

    private void createForegroundNotification(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel notificationChannel = new NotificationChannel(AppConstants.ID_NOTIFICATION_CHANNEL,
                    getString(R.string.app_name), NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(notificationChannel);

            Notification.Builder builder = new Notification.Builder(this, AppConstants.ID_NOTIFICATION_CHANNEL)
                    .setContentTitle(getString(R.string.notification_title))
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentText(getString(R.string.notification_message))
                    .setAutoCancel(true);

            Notification notification = builder.build();

            startForeground(1, notification);

        }else {
            Notification.Builder builder = new Notification.Builder(this)
                    .setContentTitle(getString(R.string.app_name))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                    .setContentText(getString(R.string.notification_message))
                    .setAutoCancel(true);

            Notification notification = builder.build();
            startForeground(1, notification);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        uploadVideo(intent);
        return START_NOT_STICKY;
    }

    public void uploadVideo(Intent intent) {

        String filePath = intent.getStringExtra(AppConstants.FILE_PATH);

        final int videoModelId = intent.getIntExtra(AppConstants.VIDEO_MODEL_ID,-1);

        LogHelper.log(TAG,"uploadVideo() Video Model id === "+videoModelId);
        LogHelper.log(TAG,"uploadVideo() Video File path === "+filePath);

        File file = new File(filePath);

        ApiInterface service = ApiClient.getClient().create(ApiInterface.class);

        MultipartBody.Part filePart = MultipartBody.Part.createFormData("video", file.getName(),
                RequestBody.create(MediaType.parse("video/mp4"), file));

        Call<ResponseBody> call = service.uploadVideo(filePart);
        call.enqueue(new Callback<ResponseBody>(){
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                LogHelper.log(TAG,"onResponse === "+response.toString());

                VideoModelEntity videoModel = new VideoModelEntity();

                if(response.isSuccessful()) {

                    videoModel.setStatus(AppConstants.VIDEO_UPLOAD_STATUS_SUCCESS);
                    videoModel.setId(videoModelId);

                    EventBus.getDefault().post(videoModel);

                }else {

                    videoModel.setStatus(AppConstants.VIDEO_UPLOAD_STATUS_FAILED);
                    videoModel.setId(videoModelId);

                    EventBus.getDefault().post(videoModel);
                }

//                stopForeground(true);
//                stopSelf();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                LogHelper.log(TAG,"onFailure === ");

                VideoModelEntity videoModel = new VideoModelEntity();
                videoModel.setStatus(AppConstants.VIDEO_UPLOAD_STATUS_FAILED);
                videoModel.setId(videoModelId);

                EventBus.getDefault().post(videoModel);

//                stopForeground(true);
//                stopSelf();

            }

        });
    }
}

