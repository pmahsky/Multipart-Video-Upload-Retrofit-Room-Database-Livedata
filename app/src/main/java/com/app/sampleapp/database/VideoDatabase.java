package com.app.sampleapp.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.app.sampleapp.database.Dao.VideoDao;
import com.app.sampleapp.database.Entity.VideoModelEntity;

@Database(entities = VideoModelEntity.class,version = 1,exportSchema = false)
public abstract class VideoDatabase extends RoomDatabase {

    private static final String VIDEO_DATABASE = "video_database";
    private static volatile VideoDatabase INSTANCE;

    public abstract VideoDao mVideoDao();

    public static VideoDatabase getAppDatabase(final Context context){

        if(INSTANCE == null){

            synchronized (VideoDatabase.class){

                if(INSTANCE == null){

                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            VideoDatabase.class,VIDEO_DATABASE).build();
                }

            }
        }

        return INSTANCE;

    }
}
