package com.sapher.yousync;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.sapher.youtubedl.YoutubeDL;
import com.sapher.youtubedl.YoutubeDLException;
import com.sapher.youtubedl.YoutubeDLRequest;
import com.sapher.youtubedl.YoutubeDLResponse;
import com.sapher.youtubedl.mapper.VideoInfo;

import java.io.File;
import java.nio.file.Paths;

/**
 * Created by sapher on 10/11/2016.
 */
public class YousyncActivitiesImpl implements YousyncActivities {

    private final AmazonS3 s3Client;
    private final String hostSpecificTaskList;
    private final String dir = System.getProperty("user.home");

    public YousyncActivitiesImpl(AmazonS3 s3Client, String taskList) {
        this.s3Client = s3Client;
        this.hostSpecificTaskList = taskList;
    }

    @Override
    public VideoInfo getVideoInfo(String url) throws YoutubeDLException {

        System.out.println("get video info");

        return YoutubeDL.getVideoInfo(url);
    }

    @Override
    public String download(String url, String fileName) throws YoutubeDLException {

        System.out.println("download");

        YoutubeDLRequest request = new YoutubeDLRequest(url, dir);
        request.setOption("output", "%(id)s.%(ext)s");
        request.setOption("extract-audio");
        request.setOption("audio-quality", "0");
        request.setOption("audio-format", "mp3");
        YoutubeDLResponse response = YoutubeDL.execute(request);

        System.out.println("download ended");

        return hostSpecificTaskList;
    }

    @Override
    public void upload(String bucketName, String fileName) throws InterruptedException {

        System.out.println("upload");

        // S3 - TransferManager
        TransferManager tx = new TransferManager(s3Client);
        Upload upload = tx.upload(bucketName, fileName, new File(getFilePath(dir, fileName + ".mp3")));
        upload.waitForCompletion();
        tx.shutdownNow(false);

        System.out.println("upload ended");
    }

    @Override
    public void deleteLocalFile(String fileName) {

        System.out.println("delete file begin fileName=" + fileName);

        File file = new File(getFilePath(dir, fileName));
        file.delete();

        System.out.println("delete file done");
    }

    private String getFilePath(String path, String fileName) {
        return Paths.get(path, fileName).toString();
    }
}
