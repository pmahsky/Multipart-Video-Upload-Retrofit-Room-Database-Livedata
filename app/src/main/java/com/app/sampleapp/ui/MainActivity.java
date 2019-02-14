package com.app.sampleapp.ui;

import android.Manifest;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.app.sampleapp.R;
import com.app.sampleapp.database.Entity.VideoModelEntity;
import com.app.sampleapp.events.DataInsertionEventModel;
import com.app.sampleapp.service.VideoUploadService;
import com.app.sampleapp.ui.adapter.VideoAdapter;
import com.app.sampleapp.utils.AppConstants;
import com.app.sampleapp.utils.AppUtils;
import com.app.sampleapp.utils.LogHelper;
import com.app.sampleapp.utils.PermissionUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PermissionUtils.PermissionAskListener, View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private MainActivity context;
    private VideoAdapter videoAdapter;
    private Button uploadButton;
    final LinkedList<VideoModelEntity> videoModelLinkedList = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
      /*  if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, StartFragment.newInstance())
                    .commitNow();
        }*/

      context = this;

      initViews();

      updateUI();

      PermissionUtils.checkPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE, context);

    }

    private void initViews() {

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);

        videoAdapter = new VideoAdapter();

        recyclerView.setAdapter(videoAdapter);

        uploadButton = findViewById(R.id.uploadButton);
        uploadButton.setOnClickListener(context);
    }

    private void updateUI() {

        VideoViewModel videoViewModel = ViewModelProviders.of(context).get(VideoViewModel.class);

        List<VideoModelEntity> videoModelEntityList = videoViewModel.getVideoModelList();

        if(videoModelEntityList.isEmpty()) {

            final LiveData<List<VideoModelEntity>> videoModelListLiveData = videoViewModel.getAllVideoModels();

            videoModelListLiveData.observe(context, new Observer<List<VideoModelEntity>>() {
                @Override
                public void onChanged(@Nullable List<VideoModelEntity> videoModelEntities) {

                    videoModelListLiveData.removeObserver(this);

                    if (videoModelEntities != null && !videoModelEntities.isEmpty()) {

                        videoModelLinkedList.clear();

                        for (VideoModelEntity videoModel :
                                videoModelEntities) {

                            if(!videoModel.getStatus().equalsIgnoreCase(AppConstants.VIDEO_UPLOAD_STATUS_UPLOADING)){

                                videoModelLinkedList.add(videoModel);
                            }
                        }
                        updateAdapter(videoModelEntities);


                    }

                }
            });
        }else {

            videoModelLinkedList.clear();

            for (VideoModelEntity videoModel :
                    videoModelEntityList) {

                if(!videoModel.getStatus().equalsIgnoreCase(AppConstants.VIDEO_UPLOAD_STATUS_UPLOADING)){

                    videoModelLinkedList.add(videoModel);
                }
            }

            updateAdapter(videoViewModel.getVideoModelList());

        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        EventBus.getDefault().register(context);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main,menu);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        AppUtils.launchGalleryForVideoSelection(context);

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){

            if(data != null) {
                List<String> videosDataList = AppUtils.getSelectedVideos(data, context);

                List<VideoModelEntity> videoModelEntityList = new ArrayList<>();

                VideoViewModel videoViewModel = ViewModelProviders.of(context).get(VideoViewModel.class);

                for (int i = 0; i < videosDataList.size(); i++) {

                    LogHelper.log(TAG, "Video path === "+videosDataList.get(i));

                    VideoModelEntity videoModelEntity = new VideoModelEntity();
                    videoModelEntity.setFilePath(videosDataList.get(i));
                    videoModelEntity.setId(i);
                    videoModelEntity.setStatus(AppConstants.VIDEO_UPLOAD_STATUS_PENDING);

                    videoModelEntityList.add(videoModelEntity);
                }

                videoViewModel.deleteAllVideoModels();
                videoViewModel.insertVideoModel(videoModelEntityList);
            }

        }else{

            LogHelper.log(TAG, "Error occurred while getting video file ===");

        }
    }

    @Override
    public void onPermissionAsk() {

        ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}
                ,PermissionUtils.READ_EXTERNAL_STORAGE_REQUEST_CODE);

    }

    @Override
    public void onPermissionPreviouslyDenied() {
        PermissionUtils.showPermissionDialog(getString(R.string.permission_previously_denied), false,context);

    }

    @Override
    public void onPermissionDisabled() {

        PermissionUtils.showPermissionDialog(getString(R.string.permission_disabled), true,context);

    }

    @Override
    public void onPermissionGranted() {

//        AppUtils.launchGalleryForVideoSelection(context);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {

//            AppUtils.launchGalleryForVideoSelection(context);
            Toast.makeText(context,getString(R.string.permission_required),Toast.LENGTH_SHORT).show();
            finish();

        }
    }

    @Subscribe
    public void onDataInsertionCompleted(DataInsertionEventModel dataInsertionEventModel){

        if(dataInsertionEventModel.getInsertStatus() == AppConstants.DATABASE_INSERTION_SUCCESS) {

            LogHelper.log(TAG,"Data Insertion Successful === ");

            final VideoViewModel videoViewModel = ViewModelProviders.of(context).get(VideoViewModel.class);

            final LiveData<List<VideoModelEntity>> videosModelListLiveData = videoViewModel.getAllVideoModels();

            videosModelListLiveData.observe(context, new Observer<List<VideoModelEntity>>() {
                @Override
                public void onChanged(@Nullable List<VideoModelEntity> videoModelEntities) {

                    videosModelListLiveData.removeObserver(this);

                    if(videoModelEntities != null && !videoModelEntities.isEmpty()) {
                        updateAdapter(videoModelEntities);
                        videoViewModel.setVideoModelList(videoModelEntities);
                    }
                }
            });

        }else {

            LogHelper.log(TAG,"Data Insertion Failed === ");

        }

    }

    @Subscribe
    public void videoUploadStatus(VideoModelEntity videoModelEntity){

        VideoViewModel videoViewModel = ViewModelProviders.of(context).get(VideoViewModel.class);
        videoViewModel.insertVideoModel(videoModelEntity);

        if(videoAdapter != null) {

            videoAdapter.updateVideoUploadStatus(videoModelEntity);

        }else {

            LogHelper.log(TAG,"videoUploadStatus(), videoAdapter is NULL === ");
        }

        if(videoModelEntity.getStatus().equalsIgnoreCase(AppConstants.VIDEO_UPLOAD_STATUS_SUCCESS)) {

            initiateUpload();

        }else {

            stopService( new Intent(context, VideoUploadService.class));

        }

    }

    private void updateAdapter(List<VideoModelEntity> videoModelEntities){

        if (videoAdapter != null) {

            videoAdapter.setData(videoModelEntities);
            videoAdapter.notifyDataSetChanged();
            uploadButton.setEnabled(true);

        }else {

            LogHelper.log(TAG,"updateAdapter(), videoAdapter is NULL === ");

        }
    }

    @Override
    public void onClick(View view) {

        if(AppUtils.isInternetConnected(context)) {

            uploadButton.setEnabled(false);

            VideoViewModel videoViewModel = ViewModelProviders.of(context).get(VideoViewModel.class);

            final List<VideoModelEntity> videoModelEntityList = videoViewModel.getVideoModelList();

            if (videoModelEntityList.isEmpty()) {

                final LiveData<List<VideoModelEntity>> videoModelListLiveData = videoViewModel.getAllVideoModels();

                videoModelListLiveData.observe(context, new Observer<List<VideoModelEntity>>() {
                    @Override
                    public void onChanged(@Nullable List<VideoModelEntity> videoModelEntities) {

                        videoModelListLiveData.removeObserver(this);

                        if (videoModelEntities != null && !videoModelEntities.isEmpty()) {

                            videoModelLinkedList.clear();
                            videoModelLinkedList.addAll(videoModelEntities);

                            initiateUpload();

                        } else {

                            uploadButton.setEnabled(true);
                        }

                    }
                });
            } else {

                videoModelLinkedList.clear();
                videoModelLinkedList.addAll(videoModelEntityList);
                initiateUpload();
            }

        }else {

            Toast.makeText(context,getResources().getString(R.string.no_internet_message),Toast.LENGTH_SHORT).show();
        }

    }

    private void initiateUpload(){

        if(!videoModelLinkedList.isEmpty()) {

            VideoModelEntity videoModelEntity = videoModelLinkedList.remove();
            videoModelEntity.setStatus(AppConstants.VIDEO_UPLOAD_STATUS_UPLOADING);

            VideoViewModel videoViewModel = ViewModelProviders.of(context).get(VideoViewModel.class);
            videoViewModel.insertVideoModel(videoModelEntity);

            videoAdapter.updateVideoUploadStatus(videoModelEntity);

            if(videoModelEntity.getFilePath() !=null && !videoModelEntity.getFilePath().isEmpty()) {

                Intent intent = new Intent(context, VideoUploadService.class);
//        intent.putExtra(AppConstants.FILE_PATH,"/storage/emulated/0/DCIM/Camera/VID_20190214_232124.mp4");
                intent.putExtra(AppConstants.FILE_PATH, videoModelEntity.getFilePath());
                intent.putExtra(AppConstants.VIDEO_MODEL_ID, videoModelEntity.getId());

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    startForegroundService(intent);

                } else {

                    startService(intent);
                }
            }else {

                Toast.makeText(context,getResources().getString(R.string.error_message),Toast.LENGTH_SHORT).show();
            }

        }else {

            stopService( new Intent(context, VideoUploadService.class));

            uploadButton.setEnabled(true);

            Toast.makeText(context,getResources().getString(R.string.upload_complete),Toast.LENGTH_SHORT).show();
            LogHelper.log(TAG,"initiateUpload() videoModelLinkedList is empty ===");
        }

    }

    @Override
    protected void onStop() {
        super.onStop();

        EventBus.getDefault().unregister(context);
    }
}
