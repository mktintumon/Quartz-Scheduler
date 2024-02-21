package com.quartz.QuartzScheduler.payload;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmailResponse {

    private boolean success;
    private String jobId;
    private String groupId;
    private String message;

    
    public EmailResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }


    public EmailResponse(boolean success, String jobId, String groupId, String message) {
        this.success = success;
        this.jobId = jobId;
        this.groupId = groupId;
        this.message = message;
    }


    public boolean isSuccess() {
        return success;
    }


    public void setSuccess(boolean success) {
        this.success = success;
    }


    public String getJobId() {
        return jobId;
    }


    public void setJobId(String jobId) {
        this.jobId = jobId;
    }


    public String getGroupId() {
        return groupId;
    }


    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }


    public String getMessage() {
        return message;
    }


    public void setMessage(String message) {
        this.message = message;
    }

    
    
}
