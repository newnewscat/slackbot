package com.fox.jiraClient;

import net.sf.json.JSON;
import net.sf.json.JSONSerializer;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * Created by shuangf on 1/4/18.
 */
public class RestClient {


    private CloseableHttpClient httpClient;
    private ICredentials creds;
    private URI uri;

    public RestClient(CloseableHttpClient httpClient, ICredentials creds, URI uri) {
        this.httpClient = httpClient;
        this.creds = creds;
        this.uri = uri;
    }

    public JSON get(URI uri) throws RestException, IOException {
        return request(new HttpGet(uri));
    }


    private JSON request(HttpRequestBase req) throws RestException, IOException {
        httpClient = HttpClients.createDefault();
        req.addHeader("Accept", "application/json");

        if (creds != null)
            creds.authenticate(req);

        CloseableHttpResponse resp = httpClient.execute(req);
        HttpEntity ent = resp.getEntity();
        StringBuilder result = new StringBuilder();

        if (ent != null) {
            String encoding = null;
            if (ent.getContentEncoding() != null) {
                encoding = ent.getContentEncoding().getValue();
            }

            if (encoding == null) {
                Header contentTypeHeader = resp.getFirstHeader("Content-Type");
                HeaderElement[] contentTypeElements = contentTypeHeader.getElements();
                for (HeaderElement he : contentTypeElements) {
                    NameValuePair nvp = he.getParameterByName("charset");
                    if (nvp != null) {
                        encoding = nvp.getValue();
                    }
                }
            }

            InputStreamReader isr =  encoding != null ?
                    new InputStreamReader(ent.getContent(), encoding) :
                    new InputStreamReader(ent.getContent());
            BufferedReader br = new BufferedReader(isr);
            String line = "";

            while ((line = br.readLine()) != null) {
                result.append(line);
            }

            isr.close();
            br.close();
            isr=null;
            br=null;
        }
        EntityUtils.consumeQuietly(ent);

        StatusLine sl = resp.getStatusLine();

        if (sl.getStatusCode() >= 300)
            throw new RestException(sl.getReasonPhrase(), sl.getStatusCode(), result.toString(), resp.getAllHeaders());
        httpClient.close();

        return result.length() > 0 ? JSONSerializer.toJSON(result.toString()): null;
    }


    public URI buildURI(String path, Map<String, String> params) throws URISyntaxException {
        URIBuilder ub = new URIBuilder(uri);
        ub.setPath(ub.getPath() + path);

        if (params != null) {
            for (Map.Entry<String, String> ent : params.entrySet())
                ub.addParameter(ent.getKey(), ent.getValue());
        }

        return ub.build();
    }

}
