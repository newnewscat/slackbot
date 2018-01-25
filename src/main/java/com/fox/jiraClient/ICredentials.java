package com.fox.jiraClient;

import org.apache.http.HttpRequest;


/**
 * Created by shuangf on 1/4/18.
 */
public interface ICredentials {

    void initialize(RestClient client) throws JiraException;

    void authenticate (HttpRequest req);

    String getLogonName();

    void logout(RestClient client) throws JiraException;
}
