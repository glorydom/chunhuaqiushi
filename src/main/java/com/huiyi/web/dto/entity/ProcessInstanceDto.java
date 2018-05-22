package com.huiyi.web.dto.entity;

import java.io.Serializable;

public class ProcessInstanceDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String processInstanceId;
    private String businessKey;

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public ProcessInstanceDto(String processInstanceId, String businessKey) {
        this.processInstanceId = processInstanceId;
        this.businessKey = businessKey;
    }

    public ProcessInstanceDto() {
    }


}
