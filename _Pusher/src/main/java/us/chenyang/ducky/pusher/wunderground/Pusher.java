package us.chenyang.ducky.pusher.wunderground;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

public final class Pusher {

    public static void main(String[] args) {
        String url = "";

        while (true) {
            System.out.println(LocalDateTime.now());
            String temp = WUndergroundAPI.getURL(args[0], args[1], args[2], args[3], args[4], args[5]);
            
            try {
                if (StringUtils.equals(temp, url)) {
                    System.out.println("No Change - Wait for one minute and retry");
                    Thread.sleep(1000 * 30);
                    continue;
                }
                
                url = temp;
                
                System.out.println(url);
                System.out.println(IOUtils.toString(new URL(url), "UTF-8"));
                Thread.sleep(1000 * 60 * 2);
                
            } catch (IOException | InterruptedException e) {
                System.err.println(e.getMessage());
            }
        }
    }
}
