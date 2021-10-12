package tramais.hnb.hhrfid.litePalBean;

import org.litepal.crud.LitePalSupport;

public class ChaKanCache extends LitePalSupport {
    /*    params.add("BaoAnNumber", fenPei.getNumber());
        params.add("INNumber", fenPei.getInsureNumber());
        params.add("CompanyNumber", fenPei.getCompanyNumber());
        params.add("FarmerNumber", fenPei.getFarmerNumber());
        params.add("EmployeeNumber", fenPei.getEmployeeNo());*/
    private String number, INNumber, CompanyNumber, FarmerNumber, EmployeeNumber;
    private String chakanDate, chakanAddress, chankandesc, chankansunshi, chakanAdvice, chankanphoto;
    private String chakanSign;
    private String creatTime, updateTime;
    private boolean isUpLoad_basic, isUpLoad_sign;

    public boolean isUpLoad_basic() {
        return isUpLoad_basic;
    }

    public void setUpLoad_basic(boolean upLoad_basic) {
        isUpLoad_basic = upLoad_basic;
    }

    public boolean isUpLoad_sign() {
        return isUpLoad_sign;
    }

    public void setUpLoad_sign(boolean upLoad_sign) {
        isUpLoad_sign = upLoad_sign;
    }

    public String getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(String creatTime) {
        this.creatTime = creatTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getINNumber() {
        return INNumber;
    }

    public void setINNumber(String INNumber) {
        this.INNumber = INNumber;
    }

    public String getCompanyNumber() {
        return CompanyNumber;
    }

    public void setCompanyNumber(String companyNumber) {
        CompanyNumber = companyNumber;
    }

    public String getFarmerNumber() {
        return FarmerNumber;
    }

    public void setFarmerNumber(String farmerNumber) {
        FarmerNumber = farmerNumber;
    }

    public String getEmployeeNumber() {
        return EmployeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber) {
        EmployeeNumber = employeeNumber;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getChakanDate() {
        return chakanDate;
    }

    public void setChakanDate(String chakanDate) {
        this.chakanDate = chakanDate;
    }

    public String getChakanAddress() {
        return chakanAddress;
    }

    public void setChakanAddress(String chakanAddress) {
        this.chakanAddress = chakanAddress;
    }

    public String getChankandesc() {
        return chankandesc;
    }

    public void setChankandesc(String chankandesc) {
        this.chankandesc = chankandesc;
    }

    public String getChankansunshi() {
        return chankansunshi;
    }

    public void setChankansunshi(String chankansunshi) {
        this.chankansunshi = chankansunshi;
    }

    public String getChakanAdvice() {
        return chakanAdvice;
    }

    public void setChakanAdvice(String chakanAdvice) {
        this.chakanAdvice = chakanAdvice;
    }

    public String getChankanphoto() {
        return chankanphoto;
    }

    public void setChankanphoto(String chankanphoto) {
        this.chankanphoto = chankanphoto;
    }

    public String getChakanSign() {
        return chakanSign;
    }

    public void setChakanSign(String chakanSign) {
        this.chakanSign = chakanSign;
    }
}
