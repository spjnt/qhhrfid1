package tramais.hnb.hhrfid.bean;

import java.util.ArrayList;
import java.util.List;

import tramais.hnb.hhrfid.util.GsonUtil;

public class GrowthStageBean {

    List<String> growth = new ArrayList<>();
    /**
     * FCategory : 小麦
     * FStage : 春小麦：苗期-拔节期
     * FRiskPre : 0.4
     */

    private String FCategory;
    private String FStage;
    private String FRiskPre;

    public String getFCategory() {
        List<GrowthStageBean> growthStageBeans = GsonUtil.getInstant().parseCommonUseArr(null, GrowthStageBean.class);
        for (GrowthStageBean item : growthStageBeans) {
            item.getFStage();
            growth.add(item.getFStage());
        }
        return FCategory;
    }

    public void setFCategory(String FCategory) {
        this.FCategory = FCategory;
    }

    public String getFStage() {
        return FStage;
    }

    public void setFStage(String FStage) {
        this.FStage = FStage;
    }

    public String getFRiskPre() {
        return FRiskPre;
    }

    public void setFRiskPre(String FRiskPre) {
        this.FRiskPre = FRiskPre;
    }
}
