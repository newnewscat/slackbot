package com.fox.utils;

import com.fox.jiraClient.BasicCredentials;
import com.fox.jiraClient.JiraClient;
import com.fox.jiraClient.JiraException;

/**
 * Created by shuangf on 1/23/18.
 */
public class JiraTicketHelper {

    private static String jiraResult;

    public static String getJiraTicketCount() {

        BasicCredentials creds = new BasicCredentials("", "");
        try {
            JiraClient jiraClient = new JiraClient("https://jira.foxemf.com/", creds);
            String fmcTicketCreate = "Bug created by FMC = "+jiraClient.countIssues("project='Fox Media Cloud' AND Created >= startOfDay() AND issuetype=Bug");
            String fmcTicketClose = "Bug closed by FMC = "+jiraClient.countIssues("project='Fox Media Cloud'  AND status changed to Closed after -1d  AND issuetype=Bug");
            String screenersTicketCreate = "Bug created by Screeners = "+jiraClient.countIssues("project='FMC Screeners'  AND Created >= startOfDay()  AND issuetype=Bug");
            String screenersTicketClose ="Bug Closed by Screeners = "+jiraClient.countIssues("project='FMC Screeners'  AND status changed to Closed after -1d  AND issuetype=Bug");
            StringBuilder stringBuilder = new StringBuilder();
            jiraResult = stringBuilder.append(fmcTicketCreate).append("\n").append(fmcTicketClose).append("\n").append(screenersTicketCreate).append("\n").append(screenersTicketClose).append("\n").toString();

        } catch (JiraException e) {
            e.printStackTrace();
        }
        return jiraResult;
    }
}
