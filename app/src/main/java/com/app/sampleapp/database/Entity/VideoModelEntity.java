package com.app.sampleapp.database.Entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.app.sampleapp.utils.AppConstants;

@Entity(tableName = AppConstants.DATABASE_TABLE_NAME)
public class VideoModelEntity {

    @PrimaryKey
    @NonNull
    int id;

    String status;

    String title;

    String fileName;

    String filePath;

    @NonNull
    public int getId() {
        return id;
    }

    public void setId(int id){
        this.id = id;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
