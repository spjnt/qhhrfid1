package tramais.hnb.hhrfid.bean;

import java.io.Serializable;
import java.util.List;


public class DkInfoDetail implements Serializable {


    /**
     * areacode : 410922100202
     * areaname : 西后街
     * bdmc : 制种玉米
     * bdmccode : 64
     * bxfl : 0.0
     * bxje : 6.7
     * bxsl : 67.0
     * cpid : 01
     * cpname : 制种玉米险种1
     * dw : 亩
     * dwbe : 0.1
     * fhbbxr : 测试
     * fl : 0.0
     * gisdk : [{"dkbm":"2020094109221002020012","dkmc":"地块三","dkmj":19.94,"dkzc":638.4220229114965,"gisdk":"MULTIPOLYGON(((115.1187422 35.9128761,115.1186048 35.912884,115.1185447 35.9126271,115.1186486 35.9126074,115.1186353 35.9125317,115.1182569 35.9125866,115.1180098 35.9117699,115.1181339 35.9117486,115.1180797 35.9115242,115.118356 35.9115074,115.1190414 35.9114842,115.1193603 35.9127977,115.1187422 35.9128761)))","ltzp":"[\"https://ram-agriinsurance-test.oss-cn-zhangjiakou.aliyuncs.com/mobile/2020/10/23/53142bb567a146e29f523d0e112b9a83/2020094109221002020012_ZLT.jpg?Expires=1606370376&OSSAccessKeyId=LTAIP816Jan0Nvjq&Signature=sS9gCgCScsAMsr0157iv2qY1CJs%3D\"]","ybmj":18},{"dkbm":"2020094109221002020011","dkmc":"地块二","dkmj":24.26,"dkzc":696.200357627597,"gisdk":"MULTIPOLYGON(((115.1203952 35.9131671,115.1203519 35.9126887,115.1202626 35.9118433,115.1202171 35.9113779,115.1206606 35.9113156,115.1210317 35.9112531,115.1211255 35.9113115,115.1212433 35.9131236,115.1208453 35.913144,115.1203952 35.9131671)))","ltzp":"[\"https://ram-agriinsurance-test.oss-cn-zhangjiakou.aliyuncs.com/mobile/2020/10/23/53142bb567a146e29f523d0e112b9a83/2020094109221002020011_ZLT.jpg?Expires=1606370376&OSSAccessKeyId=LTAIP816Jan0Nvjq&Signature=rPbRVyyYJ1WhJvTUemGHQhL2gTg%3D\"]","ybmj":22},{"dkbm":"2020094109221002020010","dkmc":"地块一","dkmj":29.75,"dkzc":696.2733384763123,"gisdk":"MULTIPOLYGON(((115.1213749 35.9143731,115.1212433 35.9131236,115.122019 35.9130837,115.1228504 35.913041,115.1229314 35.9142829,115.1213749 35.9143731)))","ltzp":"[\"https://ram-agriinsurance-test.oss-cn-zhangjiakou.aliyuncs.com/mobile/2020/10/23/53142bb567a146e29f523d0e112b9a83/2020094109221002020010_ZLT.jpg?Expires=1606370376&OSSAccessKeyId=LTAIP816Jan0Nvjq&Signature=NtGYTVgemTCsSxnLKnfDqyVPn8o%3D\"]","ybmj":27}]
     * sfpkh : 0
     * signpiclist : ["https://ram-agriinsurance-test.oss-cn-zhangjiakou.aliyuncs.com/mobile/2020/11/17/e7dbd0f240c14ad180d9f5c29616a84c/QM_FARMER.jpg?Expires=1606370376&OSSAccessKeyId=LTAIP816Jan0Nvjq&Signature=49CccaFUJ%2B7gE5Lw%2F1zLkKylJDE%3D"]
     * sum_dk : 73.95
     * tkid : 01
     * tkname : 制种玉米险种1
     * userid : dxqfx_test
     * zjhm : 698548856
     * zjlx : 1
     */
    private String sjh;
    private String areacode;
    private String areaname;
    private String bdmc;
    private String bdmccode;
    private Double bxfl;
    private Double bxje;
    private Double bxsl;
    private String cpid;
    private String cpname;
    private String dw;
    private Double dwbe;
    private String fhbbxr;
    private Double fl;
    private Integer sfpkh;
    private String signpiclist;
    private Double sumDk;
    private String tkid;
    private String tkname;
    private String userid;
    private String zjhm;

    private List<GisdkDTO> gisdk;
    private String qszh;
    private String qspiclist;
    private String jdlkhh;
    private String fypiclist;
    private String sfzjPicList;
    private String yhkPicList;
    private String khh;
    private String yhzh;
    private String zjmc, zjhzq, zjhqq;

    private String zhmc;
    private String jc;
    private String lhh;

    private String zjlx;

    private String qyxz;
    private String qyxzCode;

    public String getQyxz() {
        return this.qyxz;
    }

    public void setQyxz(final String qyxz) {
        this.qyxz = qyxz;
    }

    public String getQyxzCode() {
        return this.qyxzCode;
    }

    public void setQyxzCode(final String qyxzCode) {
        this.qyxzCode = qyxzCode;
    }

    public String getJc() {
        return this.jc;
    }

    public void setJc(final String jc) {
        this.jc = jc;
    }

    public String getLhh() {
        return this.lhh;
    }

    public void setLhh(final String lhh) {
        this.lhh = lhh;
    }

    public String getZhmc() {
        return this.zhmc;
    }

    public void setZhmc(final String zhmc) {
        this.zhmc = zhmc;
    }

    public String getZjhzq() {
        return zjhzq;
    }

    public void setZjhzq(String zjhzq) {
        this.zjhzq = zjhzq;
    }

    public String getZjhqq() {
        return zjhqq;
    }

    public void setZjhqq(String zjhqq) {
        this.zjhqq = zjhqq;
    }

    public String getYhkPicList() {
        return yhkPicList;
    }

    public void setYhkPicList(String yhkPicList) {
        this.yhkPicList = yhkPicList;
    }

    public String getZjmc() {
        return zjmc;
    }

    public void setZjmc(String zjmc) {
        this.zjmc = zjmc;
    }

    public String getYhzh() {
        return yhzh;
    }

    public void setYhzh(String yhzh) {
        this.yhzh = yhzh;
    }

    public String getKhh() {
        return khh;
    }

    public void setKhh(String khh) {
        this.khh = khh;
    }

    public String getSfzjPicList() {
        return sfzjPicList;
    }

    public void setSfzjPicList(String sfzjPicList) {
        this.sfzjPicList = sfzjPicList;
    }

    public String getSjh() {
        return sjh;
    }

    public void setSjh(String sjh) {
        this.sjh = sjh;
    }

    public String getJdlkhh() {
        return jdlkhh;
    }

    public void setJdlkhh(String jdlkhh) {
        this.jdlkhh = jdlkhh;
    }

    public String getQszh() {
        return qszh;
    }

    public void setQszh(String qszh) {
        this.qszh = qszh;
    }

    public String getQspiclist() {
        return qspiclist;
    }

    public void setQspiclist(String qspiclist) {
        this.qspiclist = qspiclist;
    }


    public String getFypiclist() {
        return fypiclist;
    }

    public void setFypiclist(String fypiclist) {
        this.fypiclist = fypiclist;
    }

    public String getAreacode() {
        return areacode;
    }

    public void setAreacode(String areacode) {
        this.areacode = areacode;
    }

    public String getAreaname() {
        return areaname;
    }

    public void setAreaname(String areaname) {
        this.areaname = areaname;
    }

    public String getBdmc() {
        return bdmc;
    }

    public void setBdmc(String bdmc) {
        this.bdmc = bdmc;
    }

    public String getBdmccode() {
        return bdmccode;
    }

    public void setBdmccode(String bdmccode) {
        this.bdmccode = bdmccode;
    }

    public Double getBxfl() {
        return bxfl;
    }

    public void setBxfl(Double bxfl) {
        this.bxfl = bxfl;
    }

    public Double getBxje() {
        return bxje;
    }

    public void setBxje(Double bxje) {
        this.bxje = bxje;
    }

    public Double getBxsl() {
        return bxsl;
    }

    public void setBxsl(Double bxsl) {
        this.bxsl = bxsl;
    }

    public String getCpid() {
        return cpid;
    }

    public void setCpid(String cpid) {
        this.cpid = cpid;
    }

    public String getCpname() {
        return cpname;
    }

    public void setCpname(String cpname) {
        this.cpname = cpname;
    }

    public String getDw() {
        return dw;
    }

    public void setDw(String dw) {
        this.dw = dw;
    }

    public Double getDwbe() {
        return dwbe;
    }

    public void setDwbe(Double dwbe) {
        this.dwbe = dwbe;
    }

    public String getFhbbxr() {
        return fhbbxr;
    }

    public void setFhbbxr(String fhbbxr) {
        this.fhbbxr = fhbbxr;
    }

    public Double getFl() {
        return fl;
    }

    public void setFl(Double fl) {
        this.fl = fl;
    }

    public Integer getSfpkh() {
        return sfpkh;
    }

    public void setSfpkh(Integer sfpkh) {
        this.sfpkh = sfpkh;
    }

    public String getSignpiclist() {
        return signpiclist;
    }

    public void setSignpiclist(String signpiclist) {
        this.signpiclist = signpiclist;
    }

    public Double getSumDk() {
        return sumDk;
    }

    public void setSumDk(Double sumDk) {
        this.sumDk = sumDk;
    }

    public String getTkid() {
        return tkid;
    }

    public void setTkid(String tkid) {
        this.tkid = tkid;
    }

    public String getTkname() {
        return tkname;
    }

    public void setTkname(String tkname) {
        this.tkname = tkname;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getZjhm() {
        return zjhm;
    }

    public void setZjhm(String zjhm) {
        this.zjhm = zjhm;
    }

    public String getZjlx() {
        return zjlx;
    }

    public void setZjlx(String zjlx) {
        this.zjlx = zjlx;
    }

    public List<GisdkDTO> getGisdk() {
        return gisdk;
    }

    public void setGisdk(List<GisdkDTO> gisdk) {
        this.gisdk = gisdk;
    }

    public static class GisdkDTO {
        /**
         * dkbm : 2020094109221002020012
         * dkmc : 地块三
         * dkmj : 19.94
         * dkzc : 638.4220229114965
         * gisdk : MULTIPOLYGON(((115.1187422 35.9128761,115.1186048 35.912884,115.1185447 35.9126271,115.1186486 35.9126074,115.1186353 35.9125317,115.1182569 35.9125866,115.1180098 35.9117699,115.1181339 35.9117486,115.1180797 35.9115242,115.118356 35.9115074,115.1190414 35.9114842,115.1193603 35.9127977,115.1187422 35.9128761)))
         * ltzp : ["https://ram-agriinsurance-test.oss-cn-zhangjiakou.aliyuncs.com/mobile/2020/10/23/53142bb567a146e29f523d0e112b9a83/2020094109221002020012_ZLT.jpg?Expires=1606370376&OSSAccessKeyId=LTAIP816Jan0Nvjq&Signature=sS9gCgCScsAMsr0157iv2qY1CJs%3D"]
         * ybmj : 18.0
         */
        private String xczp;
        private String dkbm;
        private String dkmc;
        private Double dkmj;
        private Double dkzc;
        private String gisdk;
        private String ltzp;
        private Double ybmj;

        public String getXczp() {
            return xczp;
        }

        public void setXczp(String xczp) {
            this.xczp = xczp;
        }

        public String getDkbm() {
            return dkbm;
        }

        public void setDkbm(String dkbm) {
            this.dkbm = dkbm;
        }

        public String getDkmc() {
            return dkmc;
        }

        public void setDkmc(String dkmc) {
            this.dkmc = dkmc;
        }

        public Double getDkmj() {
            return dkmj;
        }

        public void setDkmj(Double dkmj) {
            this.dkmj = dkmj;
        }

        public Double getDkzc() {
            return dkzc;
        }

        public void setDkzc(Double dkzc) {
            this.dkzc = dkzc;
        }

        public String getGisdk() {
            return gisdk;
        }

        public void setGisdk(String gisdk) {
            this.gisdk = gisdk;
        }

        public String getLtzp() {
            return ltzp;
        }

        public void setLtzp(String ltzp) {
            this.ltzp = ltzp;
        }

        public Double getYbmj() {
            return ybmj;
        }

        public void setYbmj(Double ybmj) {
            this.ybmj = ybmj;
        }
    }
}
