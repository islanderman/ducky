package us.chenyang.ducky.pusher.wunderground;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;

import us.chenyang.ducky.client.INetatmoClientConstant;
import us.chenyang.ducky.client.NetatmoAPI;
import us.chenyang.ducky.shared.utils.Utils;

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
        JsonNode devices = node.get("body").get("devices");

        for (JsonNode n : devices.get(0).get("modules")) {
            String type = n.get("type").textValue();

            switch (type) {
            case "NAModule3":
                map.put("dailyrainin", n.get("dashboard_data").get("sum_rain_24").asLong());
                map.put("rainin", n.get("dashboard_data").get("sum_rain_1").asLong());
                break;
            case "NAModule1":
                double humidity = Utils.doubleOf(n.get("dashboard_data").get("Humidity").asDouble(), 2);
                double tempature = Utils.doubleOf(getFahernheit(n.get("dashboard_data").get("Temperature").asDouble()),
                        2);
                map.put("humidity", humidity);
                map.put("tempf", tempature);

                map.put("dewptf", getDewPoint(tempature, humidity));
                map.put("dateutc", getUTC(n.get("last_message").asLong()));
                break;
            case "NAMain":
                map.put("baromin",
                        Utils.doubleOf(
                                node.get("body").get("devices").get(0).get("dashboard_data").get("Pressure").asDouble()
                                        * 0.0295299830714,
                                2));
                break;
            default:
                break;
            }
        }
        if (StringUtils.equalsAnyIgnoreCase("NAMain", node.get("body").get("devices").get(0).get("type").asText())) {
            map.put("baromin",
                    Utils.doubleOf(
                            node.get("body").get("devices").get(0).get("dashboard_data").get("Pressure").asDouble()
                                    * 0.0295299830714,
                            2));
        }

        return map;
    }

    private static DateTimeFormatter CUSTOMIZED_FORMATTER = new DateTimeFormatterBuilder().parseCaseInsensitive()
            .append(DateTimeFormatter.ISO_LOCAL_DATE).appendLiteral('+').append(DateTimeFormatter.ISO_LOCAL_TIME)
            .toFormatter();

    private static String getUTC(final long utc) {
        return ZonedDateTime.ofInstant(Instant.ofEpochSecond(utc), ZoneOffset.UTC)
                .format(CUSTOMIZED_FORMATTER);
    }

    private static double getFahernheit(final double celsius) {
        return 32 + celsius * 9 / 5;
    }

    private static double getDewPoint(final double temp, final double humidity) {
        return Utils.doubleOf(243.04 * (Math.log(humidity / 100) + ((17.625 * temp) / (243.04 + temp)))
                / (17.625 - Math.log(humidity / 100) - ((17.625 * temp) / (243.04 + temp))), 2);
    }

    private static String constructUrl(final String stationId, final String password, final Map<String, Object> map) {
        StringBuilder builder = new StringBuilder(WUnderground_UPLOAD);
        builder.append("?ID=").append(stationId).append("&PASSWORD=").append(password);

        for (Entry<String, Object> entry : map.entrySet()) {
            try {
                builder.append('&').append(URLEncoder.encode(entry.getKey(), "UTF-8")).append('=').append(URLEncoder.encode(Objects.toString(entry.getValue()), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        return builder.toString();
    }
}
