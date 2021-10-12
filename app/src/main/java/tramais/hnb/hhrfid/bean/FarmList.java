package tramais.hnb.hhrfid.bean;

import java.io.Serializable;

public class FarmList implements Serializable {


    /**
     * Number : 468799845878965478
     * Name : 泡泡
     * Category : 集体投保
     * Area : 测试集体
     * ZjCategory : 身份证
     * ZjPicture : http://qnbapi.tramaisdb.com/photo/
     * ZjBackPicture : http://qnbapi.tramaisdb.com/photo/
     * ZjNumber : 468799845878965478
     * Mobile : 13111546798
     * IsPoor : 0
     * SFZAddress : 里
     * RaiseAddress : 中国广东省深圳市宝安区西乡街道宝源路1009
     * BankName : 葫芦屯
     * AccountNumber : 488557688
     * AccountName : 葫芦头
     * Remark :
     * CreateTime : 2020-8-18 14:08:12
     * UpdateTime : 2020-8-19 10:44:07
     */

    private String Number;
    private String Name;
    private String Category;
    private String Area;
    private String ZjCategory;
    private String ZjPicture;
    private String ZjBackPicture;
    private String ZjNumber;
    private String Mobile;
    private int IsPoor;
    private String SFZAddress;
    private String RaiseAddress;
    private String BankName;
    private String AccountNumber;
    private String AccountName;
    private String Remark;
    private String CreateTime;
    private String UpdateTime;
    private String BankPicture;
    private String SignPicture;
    private String FValidate;
    private String FBankCode;
    private String FBankRelatedCode;
    private String AreaCode;
    private String FStartTime;
    private String FEnterpriseNature;
    private String FEnterpriseNatureName;

    public String getFEnterpriseNatureName() {
        return this.FEnterpriseNatureName;
    }

    public void setFEnterpriseNatureName(final String FEnterpriseNatureName) {
        this.FEnterpriseNatureName = FEnterpriseNatureName;
    }

    public String getFEnterpriseNature() {
        return this.FEnterpriseNature;
    }

    public void setFEnterpriseNature(final String FEnterpriseNature) {
        this.FEnterpriseNature = FEnterpriseNature;
    }

    public String getFStartTime() {
        return this.FStartTime;
    }

    public void setFStartTime(final String FStartTime) {
        this.FStartTime = FStartTime;
    }

    public String getAreaCode() {
        return this.AreaCode;
    }

    public void setAreaCode(final String areaCode) {
        this.AreaCode = areaCode;
    }

    public String getFBankCode() {
        return this.FBankCode;
    }

    public void setFBankCode(final String FBankCode) {
        this.FBankCode = FBankCode;
    }

    public String getFBankRelatedCode() {
        return this.FBankRelatedCode;
    }

    public void setFBankRelatedCode(final String FBankRelatedCode) {
        this.FBankRelatedCode = FBankRelatedCode;
    }

    public String getFValidate() {
        return FValidate;
    }

    public void setFValidate(String FValidate) {
        this.FValidate = FValidate;
    }

    public String getSignPicture() {
        return SignPicture;
    }

    public void setSignPicture(String signPicture) {
        SignPicture = signPicture;
    }

    public String getBankPicture() {
        return BankPicture;
    }

    public void setBankPicture(String bankPicture) {
        BankPicture = bankPicture;
    }

    public String getNumber() {
        return Number;
    }

    public void setNumber(String Number) {
        this.Number = Number;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String Category) {
        this.Category = Category;
    }

    public String getArea() {
        return Area;
    }

    public void setArea(String Area) {
        this.Area = Area;
    }

    public String getZjCategory() {
        return ZjCategory;
    }

    public void setZjCategory(String ZjCategory) {
        this.ZjCategory = ZjCategory;
    }

    public String getZjPicture() {
        return ZjPicture;
    }

    public void setZjPicture(String ZjPicture) {
        this.ZjPicture = ZjPicture;
    }

    public String getZjBackPicture() {
        return ZjBackPicture;
    }

    public void setZjBackPicture(String ZjBackPicture) {
        this.ZjBackPicture = ZjBackPicture;
    }

    public String getZjNumber() {
        return ZjNumber;
    }

    public void setZjNumber(String ZjNumber) {
        this.ZjNumber = ZjNumber;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String Mobile) {
        this.Mobile = Mobile;
    }

    public int getIsPoor() {
        return IsPoor;
    }

    public void setIsPoor(int IsPoor) {
        this.IsPoor = IsPoor;
    }

    public String getSFZAddress() {
        return SFZAddress;
    }

    public void setSFZAddress(String SFZAddress) {
        this.SFZAddress = SFZAddress;
    }

    public String getRaiseAddress() {
        return RaiseAddress;
    }

    public void setRaiseAddress(String RaiseAddress) {
        this.RaiseAddress = RaiseAddress;
    }

    public String getBankName() {
        return BankName;
    }

    public void setBankName(String BankName) {
        this.BankName = BankName;
    }

    public String getAccountNumber() {
        return AccountNumber;
    }

    public void setAccountNumber(String AccountNumber) {
        this.AccountNumber = AccountNumber;
    }

    public String getAccountName() {
        return AccountName;
    }

    public void setAccountName(String AccountName) {
        this.AccountName = AccountName;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String Remark) {
        this.Remark = Remark;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String CreateTime) {
        this.CreateTime = CreateTime;
    }

    public String getUpdateTime() {
        return UpdateTime;
    }

    public void setUpdateTime(String UpdateTime) {
        this.UpdateTime = UpdateTime;
    }
}
