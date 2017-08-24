package us.chenyang.ducky.shared.model;

public class PWSConfig {

    private final String pwsId;
    private final String psw;

    public PWSConfig(final String pwsId, final String psw) {
        this.pwsId = pwsId;
        this.psw = psw;
    }

    public String getPwsId() {
        return pwsId;
    }

    public String getPsw() {
        return psw;
    }

}
