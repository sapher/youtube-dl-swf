package com.sapher.yousync;

import com.amazonaws.services.simpleworkflow.flow.ActivitySchedulingOptions;
import com.amazonaws.services.simpleworkflow.flow.DecisionContextProviderImpl;
import com.amazonaws.services.simpleworkflow.flow.WorkflowContext;
import com.amazonaws.services.simpleworkflow.flow.annotations.Asynchronous;
import com.amazonaws.services.simpleworkflow.flow.core.Promise;
import com.amazonaws.services.simpleworkflow.flow.core.Settable;
import com.amazonaws.services.simpleworkflow.flow.core.TryCatchFinally;
import com.sapher.yousync.common.Constants;
import com.sapher.youtubedl.mapper.VideoInfo;

import java.nio.file.Paths;

/**
 * Created by sapher on 10/11/2016.
 */
public class YousyncWorkflowImpl implements YousyncWorkflow {

    private String state = "Started";

    private YousyncActivitiesClient client;

    private WorkflowContext workflowContext;

    public YousyncWorkflowImpl() {
        client = new YousyncActivitiesClientImpl();
        workflowContext = (new DecisionContextProviderImpl()).getDecisionContext().getWorkflowContext();
    }

    @Override
    public void execute(String url, String videoId) {

        final Settable<String> taskList = new Settable<>();

        //final String workflowRunId = workflowContext.getWorkflowExecution().getRunId();

        new TryCatchFinally() {

            @Override
            protected void doTry() throws Throwable {

                // Get Video Info
                Promise<VideoInfo> videoInfo = getVideoInfo(url);

                // Download file to local machine
                Promise<String> activityWorkerTaskList = download(url, videoInfo);
                taskList.chain(activityWorkerTaskList);

                // Upload file to S3
                Promise<Void> uploadProcessed = upload(Constants.S3_BUCKET_STORE, videoInfo, activityWorkerTaskList);
            }

            @Override
            protected void doCatch(Throwable e) throws Throwable {
                state = "Failed: " + e.getMessage();
                throw e;
            }

            @Override
            protected void doFinally() throws Throwable {
                if(taskList.isReady()) {

                    // Set option to schedule activity in worker specific task list
                    ActivitySchedulingOptions options = new ActivitySchedulingOptions().withTaskList(taskList.get());

                    client.deleteLocalFile(videoId + ".mp3", options);
                    //client.deleteLocalFile("x", options);
                }

                if(!state.startsWith("Failed:")) {
                    state = "Completed";
                }
            }
        };
    }

    @Asynchronous
    private Promise<VideoInfo> getVideoInfo(String url) {
        state = "GetInfo";
        return client.getVideoInfo(url);
    }

    @Asynchronous
    private Promise<String> download(String url, Promise<VideoInfo> videoInfo) {
        state = "Download";
        String fileName = videoInfo.get().id;
        return client.download(url, fileName);
    }

    @Asynchronous
    private Promise<Void> upload(String bucketName, Promise<VideoInfo> videoInfo, Promise<String> taskList) {
        state = "Upload";
        String fileName = videoInfo.get().id;
        ActivitySchedulingOptions opts = new ActivitySchedulingOptions().withTaskList(taskList.get());
        return client.upload(bucketName, fileName, opts);
    }

    @Override
    public String getState() {
        return state;
    }
}
