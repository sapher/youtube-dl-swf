package com.sapher.yousync.common;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.simpleworkflow.AmazonSimpleWorkflow;
import com.amazonaws.services.simpleworkflow.AmazonSimpleWorkflowClient;

/**
 * Created by sapher on 10/11/2016.
 */
public abstract class ConfigHelper {

    public static AmazonSimpleWorkflow createSWFClient() {
        AWSCredentials credentials = new DefaultAWSCredentialsProviderChain().getCredentials();
        AmazonSimpleWorkflow swfClient = new AmazonSimpleWorkflowClient(credentials);
        swfClient.setEndpoint(Constants.URL_ENDPOINT);
        return swfClient;
    }

    public static AmazonS3 createS3Client() {
        AWSCredentials credentials = new DefaultAWSCredentialsProviderChain().getCredentials();
        AmazonS3 s3Client = new AmazonS3Client(credentials);
        s3Client.setRegion(Region.getRegion(Regions.US_WEST_2));
        return s3Client;
    }
}
