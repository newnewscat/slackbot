package com.fox.jiraClient;

/**
 * Created by shuangf on 1/4/18.
 */
public class JiraException extends Exception {

    public JiraException(String msg) {
        super(msg);
    }

    public JiraException(String msg, Throwable cause) {
        super(msg, cause);
    }


}
