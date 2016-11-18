package com.sapher.yousync;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.simpleworkflow.AmazonSimpleWorkflow;
import com.amazonaws.services.simpleworkflow.flow.ActivityWorker;
import com.sapher.yousync.common.ConfigHelper;
import com.sapher.yousync.common.Constants;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

/**
 * Created by sapher on 10/11/2016.
 */
public class ActivityHost {

    public static void main(String[] args) throws Exception {

        // AWS
        AmazonSimpleWorkflow swfService = ConfigHelper.createSWFClient();
        AmazonS3 s3Client = ConfigHelper.createS3Client();

        // Share activities
        YousyncActivitiesImpl activitiesImpl = new YousyncActivitiesImpl(s3Client, getHostName());

        // Common task list
        final ActivityWorker workerForCommonTaskList = new ActivityWorker(swfService, Constants.DOMAIN, Constants.ACTIVITIES_TASK_LIST);
        workerForCommonTaskList.addActivitiesImplementation(activitiesImpl);
        workerForCommonTaskList.start();
        System.out.println("Host Service Started for Task List: " + Constants.ACTIVITIES_TASK_LIST);

        // Host task list
        final ActivityWorker workerForHostTaskList = new ActivityWorker(swfService, Constants.DOMAIN, getHostName());
        workerForHostTaskList.addActivitiesImplementation(activitiesImpl);
        workerForHostTaskList.start();
        System.out.println("Worker Started for Activity Task List: " + getHostName());

        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                try {
                    workerForCommonTaskList.shutdown();
                    workerForCommonTaskList.awaitTermination(1, TimeUnit.MINUTES);
                    workerForHostTaskList.shutdown();
                    workerForHostTaskList.awaitTermination(1, TimeUnit.MINUTES);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        System.out.println("Press key to terminate service...");

        try {
            System.in.read();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        System.exit(0);
    }

    static String getHostName() {
        try {
            InetAddress address = InetAddress.getLocalHost();
            return address.getHostName();
        }
        catch (UnknownHostException e) {
            throw new Error(e);
        }
    }
}
