package tramais.hnb.hhrfid.litePalBean;

import org.litepal.crud.LitePalSupport;

public class RiskReasonCache extends LitePalSupport {

    /**
     * ReasonCode : 0001
     * ReasonName : 自然灾害
     */

    private String ReasonCode;
    private String ReasonName;
    private String Fcategory;

    public String getFcategory() {
        return this.Fcategory;
    }

    public void setFcategory(final String fcategory) {
        this.Fcategory = fcategory;
    }

    public String getReasonCode() {
        return ReasonCode;
    }

    public void setReasonCode(String ReasonCode) {
        this.ReasonCode = ReasonCode;
    }

    public String getReasonName() {
        return ReasonName;
    }

    public void setReasonName(String ReasonName) {
        this.ReasonName = ReasonName;
    }
}
