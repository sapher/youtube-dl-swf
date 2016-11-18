package com.sapher.yousync;

import com.amazonaws.services.simpleworkflow.AmazonSimpleWorkflow;
import com.amazonaws.services.simpleworkflow.model.WorkflowExecution;
import com.sapher.yousync.common.ConfigHelper;
import com.sapher.yousync.common.Constants;

/**
 * Created by sapher on 10/11/2016.
 */
public class WorkflowExecutionStarter {

    public static void main(String[] args) {

        AmazonSimpleWorkflow swfService = ConfigHelper.createSWFClient();

        YousyncWorkflowClientExternalFactory clientFactory = new YousyncWorkflowClientExternalFactoryImpl(swfService, Constants.DOMAIN);
        YousyncWorkflowClientExternal workflow = clientFactory.getClient();

        String url = "https://www.youtube.com/watch?v=RZVyHH-voR8";
        String videoId = "RZVyHH-voR8";

        if(args.length > 0) {
            url = args[0];
            videoId = args[0].split("=")[1];
        }

        workflow.execute(url, videoId);

        WorkflowExecution workflowExecution = workflow.getWorkflowExecution();

        printInfo(videoId, workflowExecution);
    }

    private static void printInfo(String videoId, WorkflowExecution workflowExecution) {
        System.out.println("Started workflow for videoId : " + videoId + " with workflowId=\"" + workflowExecution.getWorkflowId() + "\" and runId=\"" + workflowExecution.getRunId() + "\"");
    }
}
