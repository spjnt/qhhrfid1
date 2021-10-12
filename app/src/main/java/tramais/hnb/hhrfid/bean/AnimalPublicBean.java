package tramais.hnb.hhrfid.bean;

import java.util.List;

public class AnimalPublicBean {

    /**
     * Data : [{"farmername":"test111","FItemName":"藏系牦牛","FLabelAddress":"vvv","farmer_cou":"3","RiskAmount":"360.00","farmerRiskAmount":"54.00","FSignPicture":"http://qnbapitwo.tramaisdb.com/photo/2021-03-03/sign323334555566666778-15-47-45-1.jpg"}]
     * Code : 0
     * Msg : 取数成功
     * fnumber : IN2021030200072
     * FCompanyName : 青海人保有限公司
     * FAddress : vvv
     * FBeginDate : 2021-03-02
     * FEndDate : 2022-03-01
     * FUnitAmount : 2000.00
     * FShowTime : 2021-03-02
     * FEmployeeName : yikun
     * FShowTimeEnd : 2021-03-09
     * FMobile : yikun
     */

    private int Code;
    private String Msg;
    private String fnumber;
    private String FCompanyName;
    private String FAddress;
    private String FBeginDate;
    private String FEndDate;
    private String FUnitAmount;
    private String FShowTime;
    private String FEmployeeName;
    private String FShowTimeEnd;
    private String FMobile;
    private String FUnitRiskAmount;

    public String getFUnitRiskAmount() {
        return FUnitRiskAmount;
    }

    public void setFUnitRiskAmount(String FUnitRiskAmount) {
        this.FUnitRiskAmount = FUnitRiskAmount;
    }

    /**
     * farmername : test111
     * FItemName : 藏系牦牛
     * FLabelAddress : vvv
     * farmer_cou : 3
     * RiskAmount : 360.00
     * farmerRiskAmount : 54.00
     * FSignPicture : http://qnbapitwo.tramaisdb.com/photo/2021-03-03/sign323334555566666778-15-47-45-1.jpg
     */

    private List<DataBean> Data;

    public int getCode() {
        return Code;
    }

    public void setCode(int Code) {
        this.Code = Code;
    }

    public String getMsg() {
        return Msg;
    }

    public void setMsg(String Msg) {
        this.Msg = Msg;
    }

    public String getFnumber() {
        return fnumber;
    }

    public void setFnumber(String fnumber) {
        this.fnumber = fnumber;
    }

    public String getFCompanyName() {
        return FCompanyName;
    }

    public void setFCompanyName(String FCompanyName) {
        this.FCompanyName = FCompanyName;
    }

    public String getFAddress() {
        return FAddress;
    }

    public void setFAddress(String FAddress) {
        this.FAddress = FAddress;
    }

    public String getFBeginDate() {
        return FBeginDate;
    }

    public void setFBeginDate(String FBeginDate) {
        this.FBeginDate = FBeginDate;
    }

    public String getFEndDate() {
        return FEndDate;
    }

    public void setFEndDate(String FEndDate) {
        this.FEndDate = FEndDate;
    }

    public String getFUnitAmount() {
        return FUnitAmount;
    }

    public void setFUnitAmount(String FUnitAmount) {
        this.FUnitAmount = FUnitAmount;
    }

    public String getFShowTime() {
        return FShowTime;
    }

    public void setFShowTime(String FShowTime) {
        this.FShowTime = FShowTime;
    }

    public String getFEmployeeName() {
        return FEmployeeName;
    }

    public void setFEmployeeName(String FEmployeeName) {
        this.FEmployeeName = FEmployeeName;
    }

    public String getFShowTimeEnd() {
        return FShowTimeEnd;
    }

    public void setFShowTimeEnd(String FShowTimeEnd) {
        this.FShowTimeEnd = FShowTimeEnd;
    }

    public String getFMobile() {
        return FMobile;
    }

    public void setFMobile(String FMobile) {
        this.FMobile = FMobile;
    }

    public List<DataBean> getData() {
        return Data;
    }

    public void setData(List<DataBean> Data) {
        this.Data = Data;
    }

    public static class DataBean {
        private String farmername;
        private String FItemName;
        private String FLabelAddress;
        private String farmer_cou;
        private String RiskAmount;
        private String farmerRiskAmount;
        private String FSignPicture;

        public String getFarmername() {
            return farmername;
        }

        public void setFarmername(String farmername) {
            this.farmername = farmername;
        }

        public String getFItemName() {
            return FItemName;
        }

        public void setFItemName(String FItemName) {
            this.FItemName = FItemName;
        }

        public String getFLabelAddress() {
            return FLabelAddress;
        }

        public void setFLabelAddress(String FLabelAddress) {
            this.FLabelAddress = FLabelAddress;
        }

        public String getFarmer_cou() {
            return farmer_cou;
        }

        public void setFarmer_cou(String farmer_cou) {
            this.farmer_cou = farmer_cou;
        }

        public String getRiskAmount() {
            return RiskAmount;
        }

        public void setRiskAmount(String RiskAmount) {
            this.RiskAmount = RiskAmount;
        }

        public String getFarmerRiskAmount() {
            return farmerRiskAmount;
        }

        public void setFarmerRiskAmount(String farmerRiskAmount) {
            this.farmerRiskAmount = farmerRiskAmount;
        }

        public String getFSignPicture() {
            return FSignPicture;
        }

        public void setFSignPicture(String FSignPicture) {
            this.FSignPicture = FSignPicture;
        }
    }
}
