package com.fox.slack;

import me.ramswaroop.jbot.core.slack.Bot;
import me.ramswaroop.jbot.core.slack.Controller;
import me.ramswaroop.jbot.core.slack.EventType;
import me.ramswaroop.jbot.core.slack.models.Event;
import me.ramswaroop.jbot.core.slack.models.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * Created by shuangf on 1/8/18.
 */

@Component
public class SlackBot extends Bot {

    private static final Logger logger = LoggerFactory.getLogger(SlackBot.class);



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

    @Controller(events = {EventType.DIRECT_MENTION, EventType.DIRECT_MESSAGE})
    public void onReceiveDM(WebSocketSession session, Event event) {
        reply(session, event, new Message("Hi, I am " + slackService.getCurrentUser().getName()));
    }

    @Controller(pattern = "(update status)", next = "confirmProject")
    public void updateStatus(WebSocketSession session, Event event) {
        startConversation(event, "confirmProject");   // start conversation
        System.out.println("++++++++++++++"+event.getUserId());
        reply(session, event, new Message("Cool! What project do you want to update?"));
    }

    @Controller(next = "askContentForUpdate")
    public void confirmProject(WebSocketSession session, Event event) {
        reply(session, event, new Message("Your project is " + event.getText() + ". Is it correct?"));
        //store the project name here
        System.out.println("Project name: "+event.getText());
        nextConversation(event);    // jump to next question in conversation
    }

    @Controller(next = "storeUpdateStatus")
    public void askContentForUpdate(WebSocketSession session, Event event) {
        if(event.getText().contains("yes")) {
            reply(session, event, new Message("What is your update?"));
            nextConversation(event);
        }else {
            reply(session, event, new Message("Sorry, I am lost. Could you type 'update status' and restart again?"));
            stopConversation(event);
        }
    }

    @Controller
    public void storeUpdateStatus(WebSocketSession session, Event event) {
        if(!event.getText().isEmpty()) {
            reply(session, event, new Message("Thanks for your update!"));
            //store the update here
            System.out.println("update: "+event.getText());

        }else {
            reply(session, event, new Message("Sorry, I am lost. Could you type 'update status' and restart again?"));
        }stopConversation(event);
    }
}
