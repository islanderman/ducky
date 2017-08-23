package us.chenyang.ducky.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class NetatmoAPI implements INetatmoClientConstant {

    private static final ObjectMapper mapper = new ObjectMapper();

    private NetatmoAPI() {
    }

    public static JsonNode getNode(String url, String user, String password, String clientKey, String clientValue) {
        
        try (final CloseableHttpClient client = HttpClients.createDefault();
                final CloseableHttpResponse response = client
                        .execute(new HttpGet(API_GETDATA + "?access_token=" + getAccessToken(user, password, clientKey, clientValue)));) {

            String str = EntityUtils.toString(response.getEntity(), UTF8);

            return mapper.readTree(str);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getAccessToken(final String user, final String passwd, String clientKey, String clientSecret) {
        HttpClientContext context = HttpClientContext.create();

        try (final CloseableHttpClient client = HttpClients.createDefault();
                final CloseableHttpResponse response = client.execute(getAuthKeyHttpPost(user, passwd, clientKey,clientSecret),
                        context)) {

            String str = EntityUtils.toString(response.getEntity(), UTF8);

            return URLEncoder.encode(mapper.readTree(str).get("access_token").textValue(), UTF8);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static HttpPost getAuthKeyHttpPost(String user, String password, String key, String value) {
        HttpPost post = new HttpPost(AUTH_TOKEN);
        
        List<NameValuePair> params = new ArrayList<NameValuePair>(3);
        params.add(new BasicNameValuePair("grant_type", "password"));
        params.add(new BasicNameValuePair("client_id", key));
        params.add(new BasicNameValuePair("client_secret", value));
        params.add(new BasicNameValuePair("username", user));
        params.add(new BasicNameValuePair("password", password));
        
        try {
            post.setEntity(new UrlEncodedFormEntity(params, UTF8));
            return post;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static HttpPost getAuthHttpPost(String user, String password, String sessionId) {
        HttpPost post = new HttpPost(URL_LOGIN);

        List<NameValuePair> params = new ArrayList<NameValuePair>(4);
        params.add(new BasicNameValuePair(SESSION_ID_KEY, sessionId));
        params.add(new BasicNameValuePair("mail", user));
        params.add(new BasicNameValuePair("pass", password));
        params.add(new BasicNameValuePair("log_submit", "LOGIN"));

        try {
            post.setEntity(new UrlEncodedFormEntity(params, UTF8));

            return post;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }
}
