package us.chenyang.ducky.shared.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import us.chenyang.ducky.shared.IConstant;

public final class NetworkUtils {
    private static final String UTF8 = "UTF-8";
    private static final ObjectMapper mapper = new ObjectMapper();

    private NetworkUtils() {
    }

    private static CookieStore getSessionCookies() {
        HttpClientContext context = HttpClientContext.create();

        try (final CloseableHttpClient client = HttpClients.createDefault();
                final CloseableHttpResponse response = client.execute(new HttpGet(IConstant.URL_LOGIN), context)) {

            return context.getCookieStore();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getAccessToken(final String user, final String passwd) {
        final CookieStore sessionCookie = getSessionCookies();
        if (sessionCookie == null) {
            return null;
        }

        Optional<Cookie> optional = sessionCookie.getCookies().stream()
                .filter(cookie -> StringUtils.equalsAnyIgnoreCase("netatmocomci_csrf_cookie_na", cookie.getName()))
                .findFirst();

        String sessionId = optional.get().getValue();

        if (StringUtils.isEmpty(sessionId)) {
            return null;
        }

        HttpClientContext context = HttpClientContext.create();

        try (final CloseableHttpClient client = HttpClientBuilder.create().setDefaultCookieStore(sessionCookie).build();
                final CloseableHttpResponse response = client.execute(getAuthHttpPost(user, passwd, sessionId),
                        context)) {
            Optional<Cookie> token = context.getCookieStore().getCookies().stream()
                    .filter(authCookie -> StringUtils.equalsIgnoreCase("netatmocomaccess_token", authCookie.getName()))
                    .findFirst();

            if (token.isPresent()) {
                return token.get().getValue();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static JsonNode getNode(String url, String user, String password) {
        try (final CloseableHttpClient client = HttpClients.createDefault();
                final CloseableHttpResponse response = client.execute(
                        new HttpGet(IConstant.API_GETDATA + "?access_token=" + getAccessToken(user, password)));) {

            String str = EntityUtils.toString(response.getEntity(), UTF8);

            return mapper.readTree(str);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static HttpPost getAuthHttpPost(String user, String password, String sessionId) {
        HttpPost post = new HttpPost(IConstant.URL_LOGIN);

        List<NameValuePair> params = new ArrayList<NameValuePair>(4);
        params.add(new BasicNameValuePair("ci_csrf_netatmo", sessionId));
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
