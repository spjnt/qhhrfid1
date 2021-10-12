package tramais.hnb.hhrfid.litePalBean;

import org.litepal.crud.LitePalSupport;

public class IdCategoryCache extends LitePalSupport {
    private String IdentifyCode;
    private String IdentifyName;

    public String getIdentifyCode() {
        return IdentifyCode;
    }

    public void setIdentifyCode(String IdentifyCode) {
        this.IdentifyCode = IdentifyCode;
    }

    public String getIdentifyName() {
        return IdentifyName;
    }

    public void setIdentifyName(String IdentifyName) {
        this.IdentifyName = IdentifyName;
    }
}
