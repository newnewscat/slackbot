package com.fox.slack;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fox.dynamodb.model.QAStatusInfo;
import com.fox.utils.DynamodbHelper;
import com.fox.utils.JiraTicketHelper;
import me.ramswaroop.jbot.core.slack.models.Attachment;
import me.ramswaroop.jbot.core.slack.models.RichMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;


/**
 * Created by shuangf on 1/8/18.
 */
@Component
public class SlackWebhooks {

    private static final Logger logger = LoggerFactory.getLogger(SlackWebhooks.class);
    private static final DateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");

    @Autowired
    private DynamodbHelper dynamodbHelper;

    @Value("${slackIncomingWebhookUrl}")
    private String slackIncomingWebhookUrl;

//    @Scheduled(cron = "0 0 0 * * ?")
@Scheduled(cron = "0 25 10 * * ?")
//    @PostConstruct
    public void invokeSlackWebhook() {
    Date date = new Date();
    RestTemplate restTemplate = new RestTemplate();
    RichMessage richMessage = new RichMessage("QA Report " + sdf.format(date));
    // set attachments
    Attachment[] attachments = new Attachment[8];
    setAttachment(attachments, 0, "16.2");
    setAttachment(attachments, 1, "CloudNext");
    setAttachment(attachments, 2, "DeadPool");
    setAttachment(attachments, 3, "Falcon");
    setAttachment(attachments, 4, "Screeners");
    setAttachment(attachments, 5, "Serenity");
    setAttachment(attachments, 6, "UX-Men");
    attachments[7] = new Attachment();
        attachments[7].setTitle("Daily Defects:");
        attachments[7].setText("Total Summary:\n"+ JiraTicketHelper.getJiraTicketCount());

//        attachments[0] = new Attachment();
//        attachments[0].setTitle("16.2:");
//        attachments[0].setText(dynamodbHelper.retrieveItem("16.2"));
//        attachments[1] = new Attachment();
//        attachments[1].setTitle("Screeners:");
//        attachments[1].setText(dynamodbHelper.retrieveItem("Screeners"));
//        attachments[2] = new Attachment();
//        attachments[2].setTitle("Daily Defects:");
//        attachments[2].setText("Total Summary:\n"+ JiraTicketHelper.getJiraTicketCount());
    richMessage.setAttachments(attachments);

    // For debugging purpose only
    try {
        logger.debug("Reply (RichMessage): {}", new ObjectMapper().writeValueAsString(richMessage));
    } catch (JsonProcessingException e) {
        logger.debug("Error parsing RichMessage: ", e);
    }

    // Always remember to send the encoded message to Slack
    try {
        restTemplate.postForEntity(slackIncomingWebhookUrl, richMessage.encodedMessage(), String.class);
    } catch (RestClientException e) {
        logger.error("Error posting to Slack Incoming Webhook: ", e);
    }
}


    public void setAttachment(Attachment[] attachments, int i, String projectName) {
        attachments[i] = new Attachment();
        attachments[i].setTitle(projectName+": ");
        attachments[i].setText(dynamodbHelper.retrieveItem(projectName));

    }



}
