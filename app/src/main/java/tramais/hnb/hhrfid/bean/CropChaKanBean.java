package tramais.hnb.hhrfid.bean;

import java.io.Serializable;

public class CropChaKanBean implements Serializable {

    public boolean isCheck = false;
    /**
     * FNumber : BA2020111200033
     * FFarmerNumber : 632323199302091835
     * FFarmerName : 索南加
     * FEmployeeNO : PICCTEST
     * FEmployeeName : 测试账号
     * FStatus : 已分配
     * FMobile : 17309738882
     */

    private String FNumber;
    private String FFarmerNumber;
    private String FFarmerName;
    private String FEmployeeNO;
    private String FEmployeeName;
    private String FStatus;
    private String FMobile;
    private String FRiskAddress;
    private String FLandCategoryid;
    private String FLandCategory;

    public String getFLandCategory() {
        return FLandCategory;
    }

    public void setFLandCategory(String FLandCategory) {
        this.FLandCategory = FLandCategory;
    }

    public String getFLandCategoryid() {
        return FLandCategoryid;
    }

    public void setFLandCategoryid(String FLandCategoryid) {
        this.FLandCategoryid = FLandCategoryid;
    }

    public String getFRiskAddress() {
        return FRiskAddress;
    }

    public void setFRiskAddress(String FRiskAddress) {
        this.FRiskAddress = FRiskAddress;
    }

    public String getFNumber() {
        return FNumber;
    }

    public void setFNumber(String FNumber) {
        this.FNumber = FNumber;
    }

    public String getFFarmerNumber() {
        return FFarmerNumber;
    }

    public void setFFarmerNumber(String FFarmerNumber) {
        this.FFarmerNumber = FFarmerNumber;
    }

    public String getFFarmerName() {
        return FFarmerName;
    }

    public void setFFarmerName(String FFarmerName) {
        this.FFarmerName = FFarmerName;
    }

    public String getFEmployeeNO() {
        return FEmployeeNO;
    }

    public void setFEmployeeNO(String FEmployeeNO) {
        this.FEmployeeNO = FEmployeeNO;
    }

    public String getFEmployeeName() {
        return FEmployeeName;
    }

    public void setFEmployeeName(String FEmployeeName) {
        this.FEmployeeName = FEmployeeName;
    }

    public String getFStatus() {
        return FStatus;
    }

    public void setFStatus(String FStatus) {
        this.FStatus = FStatus;
    }

    public String getFMobile() {
        return FMobile;
    }

    public void setFMobile(String FMobile) {
        this.FMobile = FMobile;
    }
}
