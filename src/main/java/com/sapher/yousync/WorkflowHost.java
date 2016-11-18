package com.sapher.yousync;

import com.amazonaws.services.simpleworkflow.AmazonSimpleWorkflow;
import com.amazonaws.services.simpleworkflow.flow.WorkflowWorker;
import com.sapher.yousync.common.ConfigHelper;
import com.sapher.yousync.common.Constants;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by sapher on 10/11/2016.
 */
public class WorkflowHost {

    private static final String DECISION_TASK_LIST = "FileProcessing";

    public static void main(String[] args) throws Exception {

        // AWS
        AmazonSimpleWorkflow swfService = ConfigHelper.createSWFClient();

        // Worker
        final WorkflowWorker worker = new WorkflowWorker(swfService, Constants.DOMAIN, DECISION_TASK_LIST);
        worker.addWorkflowImplementationType(YousyncWorkflowImpl.class);
        worker.start();

        System.out.println("Workflow Host Service Started...");

        Runtime.getRuntime().addShutdownHook(new Thread() {

            public void run() {
                try {
                    worker.shutdownAndAwaitTermination(1, TimeUnit.MINUTES);
                    System.out.println("Workflow Host Service Terminated...");
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        System.out.println("Please press any key to terminate service.");

        try {
            System.in.read();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        System.exit(0);
    }
}
