package com.sapher.yousync;

import com.amazonaws.services.simpleworkflow.flow.annotations.Execute;
import com.amazonaws.services.simpleworkflow.flow.annotations.GetState;
import com.amazonaws.services.simpleworkflow.flow.annotations.Workflow;
import com.amazonaws.services.simpleworkflow.flow.annotations.WorkflowRegistrationOptions;

/**
 * Created by sapher on 10/11/2016.
 */
@Workflow
@WorkflowRegistrationOptions(defaultExecutionStartToCloseTimeoutSeconds = 300, defaultTaskStartToCloseTimeoutSeconds = 10)
public interface YousyncWorkflow {

    @Execute(version = "18")
    public void execute(String url, String videoId);

    @GetState
    public String getState();
}
