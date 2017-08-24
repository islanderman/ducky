package us.chenyang.ducky.shared.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class NetatmoConfig {
    private final String netatmoUser;
    private final String netatmoPass;
    private final String netatmoKey;
    private final String netatmoSecret;

    public NetatmoConfig(final String netatmoUser, final String netatmoPass, final String netatmoKey,
            final String netatmoSecret) {
        Objects.requireNonNull(netatmoUser);
        Objects.requireNonNull(netatmoPass);
        Objects.requireNonNull(netatmoKey);
        Objects.requireNonNull(netatmoSecret);
        
        this.netatmoUser = netatmoUser;
        this.netatmoPass = netatmoPass;
        this.netatmoKey = netatmoKey;
        this.netatmoSecret = netatmoSecret;
    }

    public String getNetatmoUser() {
        return netatmoUser;
    }

    public String getNetatmoPass() {
        return netatmoPass;
    }

    public String getNetatmoKey() {
        return netatmoKey;
    }

    public String getNetatmoSecret() {
        return netatmoSecret;
    }

    public Collection<? extends NameValuePair> getPairs(){
        return Arrays.asList(
                new BasicNameValuePair("username", netatmoUser),
                new BasicNameValuePair("password", netatmoPass),
                new BasicNameValuePair("client_id", netatmoKey),
                new BasicNameValuePair("client_secret", netatmoSecret));
    }
    
}
