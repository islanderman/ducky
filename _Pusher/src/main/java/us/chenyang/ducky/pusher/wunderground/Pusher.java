package us.chenyang.ducky.pusher.wunderground;

import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

public final class Pusher {

    public static void main(String[] args) {
        String url = null;

        while (true) {
            try {
                String temp = WUndergroundAPI.getURL(args[0], args[1], args[2], args[3], args[4], args[5]);
                if (StringUtils.equalsIgnoreCase(temp, url)) {
                    Thread.sleep(1000 * 30);
                    continue;
                }
                url = temp;

                System.out.println(url);
                if (StringUtils.contains(IOUtils.toString(new URL(url), "UTF-8"), "success")) {
                    System.out.println("OK");
                    Thread.sleep(1000 * 60);
                }
            } catch (IOException | InterruptedException | RuntimeException e) {
                System.err.println(e.getMessage());
            }
        }
    }
}
