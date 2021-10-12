package tramais.hnb.hhrfid.bean;

import java.io.Serializable;

public class DingSunDetailResult implements Serializable {

    /**
     * Fid : 12
     * FInsureQty : 2.39
     * FRiskQty : 5.00
     * FLossQty : 6.39
     * FFarmerName : 李四
     * FFarmerNumber : 411327199104171132
     */

    private String Fid;
    private String FInsureQty;
    private String FRiskQty;
    private String FLossQty;
    private String FFarmerName;
    private String FFarmerNumber;
    private String FlandName;
    private String FLossLevel;

    public String getFLossLevel() {
        return FLossLevel;
    }

    public void setFLossLevel(String FLossLevel) {
        this.FLossLevel = FLossLevel;
    }

    public String getFlandName() {
        return FlandName;
    }

    public void setFlandName(String flandName) {
        FlandName = flandName;
    }

    public String getFid() {
        return Fid;
    }

    public void setFid(String fid) {
        Fid = fid;
    }

    public String getFInsureQty() {
        return FInsureQty;
    }

    public void setFInsureQty(String FInsureQty) {
        this.FInsureQty = FInsureQty;
    }

    public String getFRiskQty() {
        return FRiskQty;
    }

    public void setFRiskQty(String FRiskQty) {
        this.FRiskQty = FRiskQty;
    }

    public String getFLossQty() {
        return FLossQty;
    }

    public void setFLossQty(String FLossQty) {
        this.FLossQty = FLossQty;
    }

    public String getFFarmerName() {
        return FFarmerName;
    }

    public void setFFarmerName(String FFarmerName) {
        this.FFarmerName = FFarmerName;
    }

    public String getFFarmerNumber() {
        return FFarmerNumber;
    }

    public void setFFarmerNumber(String FFarmerNumber) {
        this.FFarmerNumber = FFarmerNumber;
    }
}
