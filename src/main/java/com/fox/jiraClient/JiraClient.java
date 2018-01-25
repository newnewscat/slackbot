package com.fox.jiraClient;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;


import java.net.URI;

/**
 * Created by shuangf on 1/5/18.
 */
public class JiraClient {

    private RestClient restClient;
    private String username;


    public JiraClient(String uri, ICredentials creds) throws JiraException{
        this( null, uri, creds);
    }

    public JiraClient(CloseableHttpClient httpClient, String uri, ICredentials creds) throws JiraException{

        restClient = new RestClient(httpClient, creds, URI.create(uri));
        username = creds.getLogonName();
        creds.initialize(restClient);

    }


    public int countIssues(String jql) throws JiraException {
        return Issue.count(restClient, jql);
    }
}
