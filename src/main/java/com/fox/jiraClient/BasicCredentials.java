package com.fox.jiraClient;

import org.apache.http.HttpRequest;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.auth.BasicScheme;

/**
 * Created by shuangf on 1/5/18.
 */
public class BasicCredentials implements ICredentials{

    private String username;
    private String password;

    public BasicCredentials (String username, String password) {
        this.username = username;
        this.password = password;
    }


    @Override
    public void initialize(RestClient client) throws JiraException {

    }


    public void authenticate(HttpRequest req) {
        Credentials creds = new UsernamePasswordCredentials(username, password);
        req.addHeader(BasicScheme.authenticate(creds, "utf-8", false));

    }

    @Override
    public String getLogonName() {
        return null;
    }

    @Override
    public void logout(RestClient client) throws JiraException {

    }
}
