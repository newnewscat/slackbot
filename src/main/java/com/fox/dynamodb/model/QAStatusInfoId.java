package com.fox.dynamodb.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by shuangf on 1/11/18.
 */
public class QAStatusInfoId implements Serializable {

    private static final long serialVersionUID = 1L;

    private String project;
    Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
    private String updateDate = String.valueOf(timeStamp.getTime());

    @DynamoDBHashKey
    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    @DynamoDBRangeKey
    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }
}
