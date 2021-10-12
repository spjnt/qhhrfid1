package tramais.hnb.hhrfid.bean;

import java.util.List;


public class ProductDetailBean {



    private List<Data1DTO> data1;

    private List<Data2DTO> data2;

    private Integer code;

    private String msg;

    private String fCode;
    private String fName;
    private String fProductCode;
    private String fUnitPremium;
    private String fPremiumNation;
    private String fPremiumProvince;
    private String fPremiumCity;
    private String fPremiumCounty;
    private String fPremiumPrivate;
    private String fClauseCode;

    public List<Data1DTO> getData1() {
        return data1;
    }

    public void setData1(List<Data1DTO> data1) {
        this.data1 = data1;
    }

    public List<Data2DTO> getData2() {
        return data2;
    }

    public void setData2(List<Data2DTO> data2) {
        this.data2 = data2;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getfCode() {
        return fCode;
    }

    public void setfCode(String fCode) {
        this.fCode = fCode;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getfProductCode() {
        return fProductCode;
    }

    public void setfProductCode(String fProductCode) {
        this.fProductCode = fProductCode;
    }

    public String getfUnitPremium() {
        return fUnitPremium;
    }

    public void setfUnitPremium(String fUnitPremium) {
        this.fUnitPremium = fUnitPremium;
    }

    public String getfPremiumNation() {
        return fPremiumNation;
    }

    public void setfPremiumNation(String fPremiumNation) {
        this.fPremiumNation = fPremiumNation;
    }

    public String getfPremiumProvince() {
        return fPremiumProvince;
    }

    public void setfPremiumProvince(String fPremiumProvince) {
        this.fPremiumProvince = fPremiumProvince;
    }

    public String getfPremiumCity() {
        return fPremiumCity;
    }

    public void setfPremiumCity(String fPremiumCity) {
        this.fPremiumCity = fPremiumCity;
    }

    public String getfPremiumCounty() {
        return fPremiumCounty;
    }

    public void setfPremiumCounty(String fPremiumCounty) {
        this.fPremiumCounty = fPremiumCounty;
    }

    public String getfPremiumPrivate() {
        return fPremiumPrivate;
    }

    public void setfPremiumPrivate(String fPremiumPrivate) {
        this.fPremiumPrivate = fPremiumPrivate;
    }

    public String getfClauseCode() {
        return fClauseCode;
    }

    public void setfClauseCode(String fClauseCode) {
        this.fClauseCode = fClauseCode;
    }

    public static class Data1DTO {
        private String fUnitAmount;

        public String getfUnitAmount() {
            return fUnitAmount;
        }

        public void setfUnitAmount(String fUnitAmount) {
            this.fUnitAmount = fUnitAmount;
        }
    }


    public static class Data2DTO {
        private String FRiskAmountRate;
private String FRiskRowNum;

        public String getFRiskRowNum() {
            return FRiskRowNum;
        }

        public void setFRiskRowNum(String FRiskRowNum) {
            this.FRiskRowNum = FRiskRowNum;
        }

        public String getFRiskAmountRate() {
            return FRiskAmountRate;
        }

        public void setFRiskAmountRate(String FRiskAmountRate) {
            this.FRiskAmountRate = FRiskAmountRate;
        }
    }
}
