package us.chenyang.ducky.pusher.wunderground;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;

import us.chenyang.ducky.client.INetatmoClientConstant;
import us.chenyang.ducky.client.NetatmoAPI;

public class WUndergroundAPI implements IWUndergroundClientConstant {
    private WUndergroundAPI() {
    }

    public static String getURL(final String netatmoUser, final String netatmoPass, final String netatmoKey,
            final String netatmoSecret, final String stationId, final String wuPassword) {
        JsonNode node = NetatmoAPI.getNode(INetatmoClientConstant.API_GETMEASURE, netatmoUser, netatmoPass, netatmoKey,
                netatmoSecret);
        return constructUrl(stationId, wuPassword, getMap(node));
    }

    public static Map<String, Object> getMap(final JsonNode node) {

        Map<String, Object> map = new HashMap<>();

        for (JsonNode n : node.get("body").get("devices").get(0).get("modules")) {
            String type = n.get("type").textValue();

            switch (type) {
            case "NAModule3":
                map.put("dailyrainin", n.get("dashboard_data").get("sum_rain_24").asLong());
                map.put("rainin", n.get("dashboard_data").get("sum_rain_1").asLong());
                break;
            case "NAModule1":
                double humidity = n.get("dashboard_data").get("Humidity").asDouble();
                double tempature = getFahernheit(n.get("dashboard_data").get("Temperature").asDouble());
                map.put("humidity", humidity);
                map.put("tempf", tempature);
                map.put("dewptf", getDewPoint(tempature, humidity));

                map.put("dateutc",
                        ZonedDateTime.ofInstant(Instant.ofEpochSecond(n.get("dashboard_data").get("time_utc").asLong()),
                                ZoneOffset.UTC).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

                break;
            default:
                break;
            }
        }
        if (StringUtils.equalsAnyIgnoreCase("NAMain", node.get("body").get("devices").get(0).get("type").asText())) {
            map.put("dateutc", ZonedDateTime.ofInstant(Instant.ofEpochSecond(node.get("body").get("devices").get(0).get("dashboard_data").get("time_utc").asLong()), ZoneOffset.UTC).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            map.put("baromin", node.get("body").get("devices").get(0).get("dashboard_data").get("Pressure").asDouble() * 0.0295299830714);
        
        }
        
        return map;
    }

    private static double getFahernheit(final double celsius) {
        return 32 + celsius * 9 / 5;
    }

    private static double getDewPoint(final double temp, final double humidity) {
        return temp - (14.55 + 0.114 * temp) * (1 - 0.01 * humidity)
                - Math.pow((2.5 + 0.007 * temp) * (1 - 0.01 * humidity), 3)
                - (15.9 + 0.117 * temp) * Math.pow(1 - 0.01 * humidity, 14);
    }

    private static String constructUrl(final String stationId, final String password, final Map<String, Object> map) {
        StringBuilder builder = new StringBuilder(WUnderground_UPLOAD);
        builder.append("?ID=").append(stationId).append("&PASSWORD=").append(password);

        for (Entry<String, Object> entry : map.entrySet()) {
            builder.append('&').append(entry.getKey()).append('=').append(entry.getValue());
        }

        return builder.toString();
    }
}
