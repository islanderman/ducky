package us.chenyang.ducky.pusher.wunderground;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;

import us.chenyang.ducky.client.INetatmoClientConstant;
import us.chenyang.ducky.client.NetatmoAPI;
import us.chenyang.ducky.pusher.pwsstation.PWSSyncAPI;
import us.chenyang.ducky.shared.model.NetatmoConfig;
import us.chenyang.ducky.shared.model.PWSConfig;
import us.chenyang.ducky.shared.model.WUndergroundConfig;

public final class Pusher {

    public static void main(String[] args) {
        final String netatmoUser = args[0];
        final String netatmoPass = args[1];
        final String netatmoKey = args[2];
        final String netatmoSecret = args[3];
        final String stationId = args[4];
        final String wuPassword = args[5];
        final String wuAPI = args[6];
        
        final NetatmoConfig netatnoConfig = new NetatmoConfig(netatmoUser, netatmoPass, netatmoKey, netatmoSecret);
        final WUndergroundConfig wuConfig = new WUndergroundConfig(stationId, wuPassword, wuAPI);

        PWSConfig pwsConfig = null;
        if (args.length == 9) {
            final String pwsId = args[7];
            final String psw = args[8];
            pwsConfig = new PWSConfig(pwsId, psw);
        }

        String url = "";
        Map<String, Object> map = Collections.emptyMap();
        while (true) {
            try {
                JsonNode node = NetatmoAPI.getNode(INetatmoClientConstant.API_GETMEASURE, netatnoConfig);
                map = WUndergroundAPI.getMap(node);
                
                String temp = WUndergroundAPI.constructUrl(wuConfig, map);

                if (StringUtils.equalsIgnoreCase(temp, url)) {
                    Thread.sleep(1000 * 30);
                    continue;
                }
                url = temp;

                System.out.println(url);
                if (StringUtils.contains(IOUtils.toString(new URL(url), "UTF-8"), "success")) {
                    System.out.println("!!PUSHED!!");
                    
                    if (pwsConfig != null) {
                        Thread.sleep(1000 * 10);
                        System.out.println(
                                IOUtils.toString(new URL(PWSSyncAPI.getSyncURL(wuConfig, pwsConfig)), "UTF-8"));
                        System.out.println("!!SYNC'D!!\n");
                    }
                    
                    Thread.sleep(1000 * 60);
                }
            } catch (IOException | InterruptedException | RuntimeException e) {
                System.err.println(map.toString());
                System.err.println(e.getMessage());
            }
        }
    }
}
