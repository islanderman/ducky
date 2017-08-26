package us.chenyang.ducky.pusher.wunderground;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;

import us.chenyang.ducky.client.INetatmoClientConstant;
import us.chenyang.ducky.client.NetatmoAPI;
import us.chenyang.ducky.shared.model.NetatmoConfig;
import us.chenyang.ducky.shared.model.WUndergroundConfig;
import us.chenyang.ducky.shared.utils.Utils;

public class WUndergroundAPI implements IWUndergroundClientConstant {
    private WUndergroundAPI() {
    }

    public static String getURL(NetatmoConfig netatMoconfig, final WUndergroundConfig wuConfig) {
        JsonNode node = NetatmoAPI.getNode(INetatmoClientConstant.API_GETMEASURE, netatMoconfig);
        return constructUrl(wuConfig, getMap(node));
    }

    public static Map<String, Object> getMap(final JsonNode node) {

        Map<String, Object> map = new LinkedHashMap<>();
        JsonNode devices = node.get("body").get("devices");

        if (StringUtils.equalsAnyIgnoreCase("NAMain", devices.get(0).get("type").asText())) {
            double baromhPa = devices.get(0).get("dashboard_data").get("Pressure").asDouble(); 
            double indoorC = devices.get(0).get("dashboard_data").get("Temperature").asDouble();
            double indoorHumidity= devices.get(0).get("dashboard_data").get("Humidity").asDouble();
            
            double indoorF = getFahernheit(indoorC);
            double baromin =  Utils.doubleOf(baromhPa * 0.0295299830714, 2);
            
            long utc = devices.get(0).get("dashboard_data").get("time_utc").asLong();
            
            String utcStr = getUTC(utc);
            map.put("dateutc", utcStr);

            map.put("baromin", baromin);
            map.put("indoortempf", indoorF);
            map.put("indoorhumidity", indoorHumidity);
        }

        
        for (JsonNode n : devices.get(0).get("modules")) {
            String type = n.get("type").textValue();

            switch (type) {
            case "NAModule3":
                long sumRain = n.get("dashboard_data").get("sum_rain_24").asLong();
                long rain = n.get("dashboard_data").get("sum_rain_1").asLong();
                map.put("dailyrainin", sumRain);
                map.put("rainin", rain);
                break;
            case "NAModule1":
                double humidity = Utils.doubleOf(n.get("dashboard_data").get("Humidity").asDouble(), 2);
                double tempatureC = n.get("dashboard_data").get("Temperature").asDouble();
                
                double tempatureF = getFahernheit(tempatureC);
                
                double dewF = getFahernheit(getDewPoint(tempatureC, humidity));

                
                map.put("humidity", humidity);
                map.put("tempf", tempatureF);
                map.put("dewptf", dewF);

                break;
            case "NAModule4":
                // nothing to know
                
                break;
            default:
                break;
            }
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
        return Utils.doubleOf(32 + celsius * 9 / 5, 2);
    }

    private static double getDewPoint(final double temp, final double humidity) {
        return Utils.doubleOf(temp - (14.55 + 0.114 * temp) * (1 - (0.01 * humidity))
                - Math.pow((2.5 + 0.007 * temp) * (1 - (0.01 * humidity)), 3)
                - (15.9 + 0.117 * temp) * Math.pow(1 - (0.01 * humidity), 14), 2);
        
    }

    public static String constructUrl(final WUndergroundConfig config, final Map<String, Object> map) {
        StringBuilder builder = new StringBuilder(WUnderground_UPLOAD);
        builder.append("?ID=").append(config.getStationId()).append("&PASSWORD=").append(config.getWuPassword());

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
