package com.fox.jiraClient;

import net.sf.json.JSON;
import net.sf.json.JSONObject;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by shuangf on 1/5/18.
 */
public class Issue {

    public static int count(RestClient restclient, String jql) throws JiraException {
        final String j = jql;
        JSON result;
        try {
            Map<String, String> queryParams = new HashMap<>();
            queryParams.put("jql", j);
            queryParams.put("maxResults", "1");
            URI searchUri = restclient.buildURI("/rest/api/2/search", queryParams);
            result = restclient.get(searchUri);
        } catch (Exception ex) {
            throw new JiraException("Failed to search issues", ex);
        }

        if (!(result instanceof JSONObject)) {
            throw new JiraException("JSON payload is malformed");
        }
        Map map = (Map) result;
        return getInteger(map.get("total"));
    }




    public static int getInteger(Object i) {
        int result = 0;

        if (i instanceof Integer)
            result = ((Integer)i).intValue();

        return result;
    }

}


