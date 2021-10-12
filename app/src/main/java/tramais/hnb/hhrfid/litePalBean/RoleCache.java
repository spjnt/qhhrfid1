package tramais.hnb.hhrfid.litePalBean;

import org.litepal.crud.LitePalSupport;

public class RoleCache extends LitePalSupport {
    private String FObjSubGroup;
    private String FObjGroup;
    private String FObjectName;
    private String FObjectNO;
    private String json;

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public String getFObjSubGroup() {
        return FObjSubGroup;
    }

    public void setFObjSubGroup(String FObjSubGroup) {
        this.FObjSubGroup = FObjSubGroup;
    }

    public String getFObjGroup() {
        return FObjGroup;
    }

    public void setFObjGroup(String FObjGroup) {
        this.FObjGroup = FObjGroup;
    }

    public String getFObjectName() {
        return FObjectName;
    }

    public void setFObjectName(String FObjectName) {
        this.FObjectName = FObjectName;
    }

    public String getFObjectNO() {
        return FObjectNO;
    }

    public void setFObjectNO(String FObjectNO) {
        this.FObjectNO = FObjectNO;
    }
}
