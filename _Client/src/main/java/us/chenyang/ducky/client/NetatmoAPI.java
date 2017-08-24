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

import us.chenyang.ducky.shared.model.NetatmoConfig;

public class NetatmoAPI implements INetatmoClientConstant {

    private static final ObjectMapper mapper = new ObjectMapper();

    private NetatmoAPI() {
    }

    public static JsonNode getNode(final String url, final NetatmoConfig config) {

        try (final CloseableHttpClient client = HttpClients.createDefault();
                final CloseableHttpResponse response = client
                        .execute(new HttpGet(API_GETDATA + "?access_token=" + getAccessToken(config)));) {

            String str = EntityUtils.toString(response.getEntity(), UTF8);

            return mapper.readTree(str);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getAccessToken(final NetatmoConfig config) {

        HttpClientContext context = HttpClientContext.create();

        try (final CloseableHttpClient client = HttpClients.createDefault();
                final CloseableHttpResponse response = client.execute(getAuthKeyHttpPost(config), context)) {

            String str = EntityUtils.toString(response.getEntity(), UTF8);

            return URLEncoder.encode(mapper.readTree(str).get("access_token").textValue(), UTF8);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static HttpPost getAuthKeyHttpPost(final NetatmoConfig config) {
        HttpPost post = new HttpPost(AUTH_TOKEN);

        List<NameValuePair> params = new ArrayList<NameValuePair>(3);
        params.add(new BasicNameValuePair("grant_type", "password"));
        params.addAll(config.getPairs());

        try {
            post.setEntity(new UrlEncodedFormEntity(params, UTF8));
            return post;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static HttpPost getAuthHttpPost(final String user, final String password, final String sessionId) {
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
