package com.sapher.yousync;

import com.amazonaws.services.simpleworkflow.flow.annotations.Activities;
import com.amazonaws.services.simpleworkflow.flow.annotations.ActivityRegistrationOptions;
import com.amazonaws.services.simpleworkflow.flow.annotations.ExponentialRetry;
import com.sapher.youtubedl.YoutubeDLException;
import com.sapher.youtubedl.mapper.VideoInfo;

/**
 * Created by sapher on 10/11/2016.
 */
@Activities(version = "1.82")
@ActivityRegistrationOptions(defaultTaskScheduleToStartTimeoutSeconds = 60, defaultTaskStartToCloseTimeoutSeconds = 300)
public interface YousyncActivities {

    @ExponentialRetry(initialRetryIntervalSeconds = 10, maximumAttempts = 3)
    public VideoInfo getVideoInfo(String url) throws YoutubeDLException;

    @ExponentialRetry(initialRetryIntervalSeconds = 10, maximumAttempts = 10)
    public String download(String url, String fileName) throws YoutubeDLException;

    @ExponentialRetry(initialRetryIntervalSeconds = 10, maximumAttempts = 10)
    public void upload(String bucketName, String fileName) throws InterruptedException;

    @ExponentialRetry(initialRetryIntervalSeconds = 10)
    public void deleteLocalFile(String fileName);
}
