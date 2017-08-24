package us.chenyang.ducky.shared.model;

import java.util.Objects;

public class WUndergroundConfig {
    private final String stationId;
    private final String wuPassword;
    private final String wuAPIKey;
    
    public WUndergroundConfig(final String stationId, final String wuPassword, final String wuAPIKey) {
        Objects.requireNonNull(stationId);
        Objects.requireNonNull(wuPassword);
        Objects.requireNonNull(wuAPIKey);
        
        this.wuAPIKey=wuAPIKey;
        this.stationId = stationId;
        this.wuPassword = wuPassword;
    }

    public String getStationId() {
        return stationId;
    }

    public String getWuPassword() {
        return wuPassword;
    }

    public String getWuAPIKey() {
        return wuAPIKey;
    }

}
