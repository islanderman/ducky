package us.chenyang.ducky.pusher.wunderground;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;

import org.apache.commons.io.IOUtils;

public final class Pusher {

    public static void main(String[] args) {
        System.out.println(LocalDateTime.now());
        String url = WUndergroundAPI.getURL(args[0], args[1], args[2], args[3], args[4], args[5]);

        System.out.println(url);
        try {
            System.out.println(IOUtils.toString(new URL(url), "UTF-8"));

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
