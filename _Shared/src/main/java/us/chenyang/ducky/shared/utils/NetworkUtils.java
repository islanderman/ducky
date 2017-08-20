package us.chenyang.ducky.shared.utils;

import java.io.IOException;

import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public final class NetworkUtils {

    private NetworkUtils() {
    }

    public static CookieStore getSessionCookies(final String url) {
        HttpClientContext context = HttpClientContext.create();

        try (final CloseableHttpClient client = HttpClients.createDefault();
                final CloseableHttpResponse response = client.execute(new HttpGet(url), context)) {

            return context.getCookieStore();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



}
