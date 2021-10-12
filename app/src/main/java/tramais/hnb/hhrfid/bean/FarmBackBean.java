package tramais.hnb.hhrfid.bean;


public class FarmBackBean {

    /**
     * areaCode : 410922100202
     * areaName : 后街村
     * bxgsdm : 9
     * gyKey : 4d3a69e6879a437abdbff9d46e8dafa8
     * gySecret : adb39bd3ef3c4677936aa739229dc69f
     * gydms : {}
     * module : 验标
     * nf : 2020
     * picLocalPaths : {}
     * token : eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJSZXNvdXJjZXMgQ2VydGlmaWNhdGlvbiIsImNvbnRleHRLZXkiOiI0ZDNhNjllNjg3OWE0MzdhYmRiZmY5ZDQ2ZThkYWZhOCIsImNvbnRleHRTZWNyZXQiOiJhZGIzOWJkM2VmM2M0Njc3OTM2YWE3MzkyMjlkYzY5ZiIsImV4cCI6MTYwNjIwMTIzN30.0Qno8TfOhkyfLAfieV_mNa7Her5A8MpHDq1SQHWlJhg
     */

    private String areaCode;

    private String areaName;

    private String bxgsdm;

    private String gyKey;

    private String gySecret;


    private String module;

    private String nf;


    private String token;

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getBxgsdm() {
        return bxgsdm;
    }

    public void setBxgsdm(String bxgsdm) {
        this.bxgsdm = bxgsdm;
    }

    public String getGyKey() {
        return gyKey;
    }

    public void setGyKey(String gyKey) {
        this.gyKey = gyKey;
    }

    public String getGySecret() {
        return gySecret;
    }

    public void setGySecret(String gySecret) {
        this.gySecret = gySecret;
    }


    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getNf() {
        return nf;
    }

    public void setNf(String nf) {
        this.nf = nf;
    }


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
