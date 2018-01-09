package com.fox.slack;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.ramswaroop.jbot.core.slack.models.Attachment;
import me.ramswaroop.jbot.core.slack.models.RichMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by shuangf on 1/8/18.
 */
@Component
public class SlackWebhooks {

    private static final Logger logger = LoggerFactory.getLogger(SlackWebhooks.class);
    private static final DateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");

    @Value("${slackIncomingWebhookUrl}")
    private String slackIncomingWebhookUrl;

    @PostConstruct
    public void invokeSlackWebhook() {
        Date date = new Date();
        RestTemplate restTemplate = new RestTemplate();
        RichMessage richMessage = new RichMessage("QA Report "+sdf.format(date));
        // set attachments
        Attachment[] attachments = new Attachment[2];
        attachments[0] = new Attachment();
        attachments[0].setTitle("16.2:");
        attachments[0].setText("QA updates!");
        attachments[1] = new Attachment();
        attachments[1].setTitle("Screnners:");
        attachments[1].setText("QA updates!\nQA updates!!");
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
}
