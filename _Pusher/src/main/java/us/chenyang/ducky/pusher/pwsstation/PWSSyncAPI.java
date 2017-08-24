package us.chenyang.ducky.pusher.pwsstation;

import us.chenyang.ducky.shared.model.PWSConfig;
import us.chenyang.ducky.shared.model.WUndergroundConfig;

public class PWSSyncAPI {
    private PWSSyncAPI() {
    }
    
    public static final String getSyncURL(final WUndergroundConfig wuConfig, final PWSConfig pwsConfig) {
        StringBuilder builder = new StringBuilder("http://wufyi.com/?");
        builder.append("wuAPI=").append(wuConfig.getWuAPIKey()).append('&')
               .append("wuID=").append(wuConfig.getStationId()).append('&')
               .append("pwsID=").append(pwsConfig.getPwsId()).append('&')
               .append("psw=").append(pwsConfig.getPsw());
        
        return builder.toString();
    }
}
