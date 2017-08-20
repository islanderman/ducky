package us.chenyang.ducky.client;

import us.chenyang.ducky.shared.IConstant;

public interface INetatmoClientConstant extends IConstant {
    public static final String URL_LOGIN = "https://auth.netatmo.com/en-US/access/login";
    public static final String AUTH_TOKEN = "https://api.netatmo.com/oauth2/token";
    
    public static final String API_GETMEASURECSV = "https://my.netatmo.com/api/devicelist";
    public static final String API_GETMEASURE = "https://api.netatmo.com/api/getmeasure";
    public static final String API_GETDATA = "https://api.netatmo.com/api/getstationsdata";

    public static final String SESSION_ID_KEY = "ci_csrf_netatmo";
    public static final String ACCESS_KEY = "netatmocomaccess_token";

}
