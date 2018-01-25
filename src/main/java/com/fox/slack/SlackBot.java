package com.fox.slack;


import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemResult;
import com.fox.utils.DynamodbHelper;
import com.fox.utils.TimeHelper;
import me.ramswaroop.jbot.core.slack.Bot;
import me.ramswaroop.jbot.core.slack.Controller;
import me.ramswaroop.jbot.core.slack.EventType;
import me.ramswaroop.jbot.core.slack.models.Event;
import me.ramswaroop.jbot.core.slack.models.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;

/**
 * Created by shuangf on 1/8/18.
 */

@Component
public class SlackBot extends Bot {

    @Autowired
    private DynamodbHelper dynamodbHelper;

    private static final Logger logger = LoggerFactory.getLogger(SlackBot.class);

//    private Map<String, Object> statusMap = new HashMap<>();

    private Map<String, AttributeValue> attributeValueMap = new HashMap<>();

    private List<String> updates = new ArrayList<>();


    @Value("${slackBotToken}")
    private String slackToken;

    @Override
    public String getSlackToken() {
        return slackToken;
    }

    @Override
    public Bot getSlackBot() {
        return this;
    }


    @Controller(events = {EventType.DIRECT_MENTION, EventType.DIRECT_MESSAGE},pattern = "(update status)", next = "askContentForUpdate")
    public void updateStatus(WebSocketSession session, Event event) {
        startConversation(event, "askContentForUpdate");
        System.out.println("++++++++++++++"+event.getUserId());
        if(!attributeValueMap.isEmpty()) {

            attributeValueMap = new HashMap<>();
        }

        attributeValueMap.put("userID", new AttributeValue().withS(event.getUserId()));
        reply(session, event, new Message("Cool! What project do you want to update?"));
    }

    @Controller(events = {EventType.DIRECT_MENTION, EventType.DIRECT_MESSAGE}, next = "storeUpdateStatus")
    public void askContentForUpdate(WebSocketSession session, Event event) {
        if(event.getText().contains("16.2")||event.getText().contains("Screeners")||event.getText().contains("CloudNext")||event.getText().contains("UX-Men")||event.getText().contains("Serenity")||event.getText().contains("DeadPool")||event.getText().contains("Falcon")){
            reply(session, event, new Message("What is your update?"));
            System.out.println("Project name: "+event.getText());
            attributeValueMap.put("Project", new AttributeValue().withS(event.getText()));
            attributeValueMap.put("TimeStamp", new AttributeValue().withS(TimeHelper.getCurrentMiliTime()));
            nextConversation(event);    // jump to next question in conversation
        }else{
            reply(session, event, new Message("Sorry, it is not a valid project name. The valid project name are: \"16.2\", \"CloudNext\", \"DeadPool\", \"Falcon\", \"Screeners\", \"Serenity\", and \"UX-Men\". Could you type 'update status' and restart again?"));
            stopConversation(event);
        }
    }


    @Controller(events = {EventType.DIRECT_MENTION, EventType.DIRECT_MESSAGE}, next = "confirmUpdateDone")
    public void storeUpdateStatus(WebSocketSession session, Event event) {
        if(!event.getText().isEmpty()) {
            reply(session, event, new Message("I got it. If you have more update, please type in, otherwise, please reply \"no\"."));
            //store the update here
            System.out.println("update: "+event.getText());
            if(updates.size()>0) {
                updates.clear();
            }
            updates.add(event.getText());
            attributeValueMap.put("Update", new AttributeValue().withSS(updates));
            nextConversation(event);
        }else {
            reply(session, event, new Message("Sorry, I am lost. Could you type 'update status' and restart again?"));
            stopConversation(event);
        }
    }

    @Controller(events = {EventType.DIRECT_MENTION, EventType.DIRECT_MESSAGE})
    public void confirmUpdateDone(WebSocketSession session, Event event) {

        if(event.getText().equalsIgnoreCase("no")) {
            reply(session, event, new Message("Thanks for your update. Bye!"));
            stopConversation(event);
        }else {
                reply(session, event, new Message("I have it recorded. Thanks for your update. Bye!"));
                //store the update here
                System.out.println("update: " + event.getText());
                if (attributeValueMap.containsKey("Update")) {
                    updates.add(event.getText());
                    attributeValueMap.put("Update", new AttributeValue().withSS(updates));
                }
            }stopConversation(event);

            System.out.println("Print attributeValueMap: "+Arrays.asList(attributeValueMap));

            dynamodbHelper.saveInDB(attributeValueMap);

        }


}
