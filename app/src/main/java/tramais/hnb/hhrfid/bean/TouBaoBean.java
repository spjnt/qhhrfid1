package tramais.hnb.hhrfid.bean;

import java.io.Serializable;

public class TouBaoBean implements Serializable {

    public boolean isCheck;
    /**
     * Number : IN2020072000001
     * Date : 2020-07-20
     * Status : 新建
     * CompanyNumber : null
     * Farmer :
     * Category :
     * SumAmount : 884.0
     * Creator : 测试账号
     */

    private String Number;
    private String Date;
    private String Status;
    private String CompanyNumber;
    private String Farmer;
    private String Category;
    private double SumAmount;
    private String Creator;
    private String FarmerNumber;
    private String Area;
    private String FItemName;
    private String AreaCode;

    public boolean isCheck() {
        return this.isCheck;
    }

    public void setCheck(final boolean check) {
        this.isCheck = check;
    }

    public String getAreaCode() {
        return this.AreaCode;
    }

    public void setAreaCode(final String areaCode) {
        this.AreaCode = areaCode;
    }

    public String getFItemName() {
        return this.FItemName;
    }

    public void setFItemName(final String FItemName) {
        this.FItemName = FItemName;
    }

    public String getArea() {
        return Area;
    }

    public void setArea(String area) {
        Area = area;
    }

    public String getFarmerNumber() {
        return FarmerNumber;
    }

    public void setFarmerNumber(String farmerNumber) {
        FarmerNumber = farmerNumber;
    }

    public String getNumber() {
        return Number;
    }

    public void setNumber(String Number) {
        this.Number = Number;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String Date) {
        this.Date = Date;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String Status) {
        this.Status = Status;
    }

    public String getCompanyNumber() {
        return CompanyNumber;
    }

    public void setCompanyNumber(String CompanyNumber) {
        this.CompanyNumber = CompanyNumber;
    }

    public String getFarmer() {
        return Farmer;
    }

    public void setFarmer(String Farmer) {
        this.Farmer = Farmer;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String Category) {
        this.Category = Category;
    }

    public double getSumAmount() {
        return SumAmount;
    }

    public void setSumAmount(double SumAmount) {
        this.SumAmount = SumAmount;
    }

    public String getCreator() {
        return Creator;
    }

    public void setCreator(String Creator) {
        this.Creator = Creator;
    }
}
