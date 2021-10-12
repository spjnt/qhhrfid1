package tramais.hnb.hhrfid.bean;

public class DingSunInfoBean {


    private String FFarmerName;
    private String FFarmerNumber;
    private String FFarmerqty;
    private String FInsureqty;
    private String FRiskqty;
    private boolean isAdd;

    public boolean isAdd() {
        return isAdd;
    }

    public void setAdd(boolean add) {
        isAdd = add;
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

    public String getFFarmerqty() {
        return FFarmerqty;
    }

    public void setFFarmerqty(String FFarmerqty) {
        this.FFarmerqty = FFarmerqty;
    }

    public String getFInsureqty() {
        return FInsureqty;
    }

    public void setFInsureqty(String FInsureqty) {
        this.FInsureqty = FInsureqty;
    }

    public String getFRiskqty() {
        return FRiskqty;
    }

    public void setFRiskqty(String FRiskqty) {
        this.FRiskqty = FRiskqty;
    }
}
