package tramais.hnb.hhrfid.litePalBean;

import org.litepal.crud.LitePalSupport;

import java.util.List;

public class RegionCache extends LitePalSupport {


    /**
     * Data : [{"FTown":"","FVillage":""},{"FTown":"新源镇","FVillage":""},{"FTown":"新源镇","FVillage":"天棚牧委会"},{"FTown":"新源镇","FVillage":"莫合拉牧委会"},{"FTown":"新源镇","FVillage":"查木康牧委会"},{"FTown":"新源镇","FVillage":"第一牧委会"},{"FTown":"新源镇","FVillage":"第八牧委会"},{"FTown":"新源镇","FVillage":"曲陇牧委会"},{"FTown":"新源镇","FVillage":"拉拢牧委会"},{"FTown":"新源镇","FVillage":"达尔角合牧委会"},{"FTown":"新源镇","FVillage":"日许尔牧委会"},{"FTown":"新源镇","FVillage":"扎德勒牧委会"},{"FTown":"新源镇","FVillage":"第十牧委会"},{"FTown":"新源镇","FVillage":"塞尔雄牧委会"},{"FTown":"江河镇","FVillage":""},{"FTown":"江河镇","FVillage":"莫合拉牧委会"},{"FTown":"江河镇","FVillage":"茶果儿牧委会"},{"FTown":"江河镇","FVillage":"赛尔创牧委会"},{"FTown":"江河镇","FVillage":"第一牧委会"},{"FTown":"江河镇","FVillage":"索德牧委会"},{"FTown":"江河镇","FVillage":"织合干木牧委会"},{"FTown":"江河镇","FVillage":"第五牧委会"},{"FTown":"江河镇","FVillage":"周东牧委会"},{"FTown":"木里镇","FVillage":""},{"FTown":"木里镇","FVillage":"角合根牧委会"},{"FTown":"木里镇","FVillage":"赛纳让牧委会"},{"FTown":"木里镇","FVillage":"佐陇牧委会"},{"FTown":"木里镇","FVillage":"唐莫日牧委会"},{"FTown":"阳康乡","FVillage":""},{"FTown":"阳康乡","FVillage":"第三牧委会"},{"FTown":"阳康乡","FVillage":"果当牧委会"},{"FTown":"阳康乡","FVillage":"赛尔娘牧委会"},{"FTown":"阳康乡","FVillage":"曲陇牧委会"},{"FTown":"织合玛乡","FVillage":""},{"FTown":"织合玛乡","FVillage":"第六牧委会"},{"FTown":"织合玛乡","FVillage":"多玉牧委会"},{"FTown":"织合玛乡","FVillage":"加吉牧委会"},{"FTown":"织合玛乡","FVillage":"曲陇牧委会"},{"FTown":"织合玛乡","FVillage":"加陇牧委会"},{"FTown":"织合玛乡","FVillage":"吉刚牧委会"},{"FTown":"龙门乡","FVillage":""},{"FTown":"龙门乡","FVillage":"那扎牧委会"},{"FTown":"龙门乡","FVillage":"措茫牧委会"},{"FTown":"龙门乡","FVillage":"第二牧委会"},{"FTown":"龙门乡","FVillage":"龙门牧委会"},{"FTown":"快尔玛乡","FVillage":""},{"FTown":"快尔玛乡","FVillage":"参康牧委会"},{"FTown":"快尔玛乡","FVillage":"莫日通牧委会"},{"FTown":"快尔玛乡","FVillage":"恰通牧委会"},{"FTown":"快尔玛乡","FVillage":"第二牧委会"},{"FTown":"快尔玛乡","FVillage":"第三牧委会"},{"FTown":"快尔玛乡","FVillage":"德陇牧委会"},{"FTown":"快尔玛乡","FVillage":"多尔责牧委会"},{"FTown":"快尔玛乡","FVillage":"阳陇牧委会"},{"FTown":"快尔玛乡","FVillage":"第一牧委会"},{"FTown":"苏里乡","FVillage":""},{"FTown":"苏里乡","FVillage":"豆库尔牧委会"},{"FTown":"苏里乡","FVillage":"登陇牧委会"},{"FTown":"苏里乡","FVillage":"措岗牧委会"},{"FTown":"苏里乡","FVillage":"尕河牧委会"},{"FTown":"苏里乡","FVillage":"曲尕追牧委会"},{"FTown":"生格乡","FVillage":""},{"FTown":"生格乡","FVillage":"阿吾嘎尔牧委会"},{"FTown":"生格乡","FVillage":"第四牧委会"},{"FTown":"生格乡","FVillage":"奥陇牧委会"},{"FTown":"生格乡","FVillage":"织合纳合牧委会"},{"FTown":"舟群乡","FVillage":""},{"FTown":"舟群乡","FVillage":"吉陇牧委会"},{"FTown":"舟群乡","FVillage":"桑毛牧委会"},{"FTown":"舟群乡","FVillage":"芒扎牧委会"},{"FTown":"舟群乡","FVillage":"第三牧委会"},{"FTown":"舟群乡","FVillage":"第六牧委会"},{"FTown":"舟群乡","FVillage":"迪尔恩牧委会"}]
     * Code : 0
     * Msg : 获取成功
     * FNumber : 632823
     * FProvince : 青海省
     * FCity : 海西蒙古族藏族自治州
     * FCounty : 天峻县
     */

    private int Code;
    private String Msg;
    private String FNumber;
    private String FProvince;
    private String FCity;
    private String FCounty;
    private String data_arr;

    public String getData_arr() {
        return data_arr;
    }

    public void setData_arr(String data_arr) {
        this.data_arr = data_arr;
    }
    //    private JSONArray jsonArray;
//
//    public JSONArray getJsonArray() {
//        return jsonArray;
//    }
//
//    public void setJsonArray(JSONArray jsonArray) {
//        this.jsonArray = jsonArray;
//    }

    /**
     * FTown :
     * FVillage :
     */

    private List<DataBean> Data;

    public int getCode() {
        return Code;
    }

    public void setCode(int Code) {
        this.Code = Code;
    }

    public String getMsg() {
        return Msg;
    }

    public void setMsg(String Msg) {
        this.Msg = Msg;
    }

    public String getFNumber() {
        return FNumber;
    }

    public void setFNumber(String FNumber) {
        this.FNumber = FNumber;
    }

    public String getFProvince() {
        return FProvince;
    }

    public void setFProvince(String FProvince) {
        this.FProvince = FProvince;
    }

    public String getFCity() {
        return FCity;
    }

    public void setFCity(String FCity) {
        this.FCity = FCity;
    }

    public String getFCounty() {
        return FCounty;
    }

    public void setFCounty(String FCounty) {
        this.FCounty = FCounty;
    }

    public List<DataBean> getData() {
        return Data;
    }

    public void setData(List<DataBean> Data) {
        this.Data = Data;
    }

    public static class DataBean {
        private String FTown;
        private String FVillage;
        private String FRegionNumber;

        public String getFRegionNumber() {
            return FRegionNumber;
        }

        public void setFRegionNumber(String FRegionNumber) {
            this.FRegionNumber = FRegionNumber;
        }

        public String getFTown() {
            return FTown;
        }

        public void setFTown(String FTown) {
            this.FTown = FTown;
        }

        public String getFVillage() {
            return FVillage;
        }

        public void setFVillage(String FVillage) {
            this.FVillage = FVillage;
        }
    }
}

