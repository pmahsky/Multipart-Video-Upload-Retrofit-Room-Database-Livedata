package com.app.sampleapp.database.Dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.app.sampleapp.database.Entity.VideoModelEntity;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface VideoDao {

    @Insert(onConflict = REPLACE)
    Long insertVideoModel(VideoModelEntity videoModelEntity);

    @Insert(onConflict = REPLACE)
    Long[] insertVideoModel(List<VideoModelEntity> videoModelEntityList);

    @Query("SELECT * FROM table_video")
    LiveData<List<VideoModelEntity>> getAllVideoModels();

    @Query("DELETE FROM table_video")
    void deleteAllVideoModels();
}
