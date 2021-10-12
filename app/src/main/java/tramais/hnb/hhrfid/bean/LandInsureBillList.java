package tramais.hnb.hhrfid.bean;

import java.io.Serializable;

public class LandInsureBillList implements Serializable {


    /**
     * FFarmerNumber : 632323199302091835
     * FNumber : IN2020111100063
     * FCreator : 测试账号
     * FCreateTime : 2020-11-11 0:00:00
     * FUpdateTime : 2020-11-11
     */
    private String FItemName;
    private String FFarmerNumber;
    private String FNumber;
    private String FCreator;
    private String FCreateTime;
    private String FUpdateTime;
    private String FLandAddress;
    private String FStatus;
    private String FLandCategory;
    private String FLandCategoryID;
    private String fname;
    private String FShowTime;

    public String getFItemName() {
        return this.FItemName;
    }

    public void setFItemName(final String FItemName) {
        this.FItemName = FItemName;
    }

    public String getFShowTime() {
        return FShowTime;
    }

    public void setFShowTime(String FShowTime) {
        this.FShowTime = FShowTime;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getFLandCategoryID() {
        return FLandCategoryID;
    }

    public void setFLandCategoryID(String FLandCategoryID) {
        this.FLandCategoryID = FLandCategoryID;
    }

    public String getFLandCategory() {
        return FLandCategory;
    }

    public void setFLandCategory(String FLandCategory) {
        this.FLandCategory = FLandCategory;
    }

    public String getFStatus() {
        return FStatus;
    }

    public void setFStatus(String FStatus) {
        this.FStatus = FStatus;
    }

    public String getFLandAddress() {
        return FLandAddress;
    }

    public void setFLandAddress(String FLandAddress) {
        this.FLandAddress = FLandAddress;
    }

    public String getFFarmerNumber() {
        return FFarmerNumber;
    }

    public void setFFarmerNumber(String FFarmerNumber) {
        this.FFarmerNumber = FFarmerNumber;
    }

    public String getFNumber() {
        return FNumber;
    }

    public void setFNumber(String FNumber) {
        this.FNumber = FNumber;
    }

    public String getFCreator() {
        return FCreator;
    }

    public void setFCreator(String FCreator) {
        this.FCreator = FCreator;
    }

    public String getFCreateTime() {
        return FCreateTime;
    }

    public void setFCreateTime(String FCreateTime) {
        this.FCreateTime = FCreateTime;
    }

    public String getFUpdateTime() {
        return FUpdateTime;
    }

    public void setFUpdateTime(String FUpdateTime) {
        this.FUpdateTime = FUpdateTime;
    }
}
