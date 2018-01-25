package com.fox.utils;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.*;
import com.fox.dynamodb.model.QAStatusInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by shuangf on 1/23/18.
 */
@Component
public class DynamodbHelper {

    @Autowired
    private AmazonDynamoDB amazonDynamoDB;

    public void saveInDB(Map<String, AttributeValue> attributeValueMap) {
        PutItemRequest putItemRequest = new PutItemRequest()
                .withTableName("QAStatusInfo")
                .withItem(attributeValueMap);
       amazonDynamoDB.putItem(putItemRequest);
    }

    private DynamoDBMapper dynamoDBMapper;
    private String startTime = String.valueOf((new Date(System.currentTimeMillis()-24*60*60*1000)).getTime());
    private StringBuilder stringBuilder;
    private String finalUpdate;



    public String retrieveItem(String project) {
        dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);

        QAStatusInfo qaStatusInfo = new QAStatusInfo();
        qaStatusInfo.setProject(project);
        Condition rangeKeyConditon = new Condition().withComparisonOperator(ComparisonOperator.GT)
                .withAttributeValueList(new AttributeValue().withS(startTime));
        DynamoDBQueryExpression<QAStatusInfo> queryExpression1 = new DynamoDBQueryExpression<QAStatusInfo>()
                .withHashKeyValues(qaStatusInfo)
                .withRangeKeyCondition("TimeStamp",rangeKeyConditon);
        List<QAStatusInfo> itemList = dynamoDBMapper.query(QAStatusInfo.class, queryExpression1);

        stringBuilder = new StringBuilder();
        for (int i = 0; i < itemList.size(); i++) {
            System.out.println("User for project "+project+" "+itemList.get(i).getUpdate());
            String update = String.join(",", itemList.get(i).getUpdate());
            stringBuilder.append(update+"\n");
        }
        finalUpdate = String.valueOf(stringBuilder).replace(",", "\n");
        return finalUpdate;
    }
}
