package tramais.hnb.hhrfid.litePalBean;

import org.litepal.crud.LitePalSupport;

public class EarTagCache extends LitePalSupport {
    String earTag;

    public String getEarTag() {
        return earTag;
    }

    public void setEarTag(String earTag) {
        this.earTag = earTag;
    }
}
