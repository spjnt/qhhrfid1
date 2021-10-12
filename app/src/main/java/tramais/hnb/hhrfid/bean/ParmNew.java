package tramais.hnb.hhrfid.bean;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.grandtech.access.ParamIn;
import com.grandtech.standard.bean.FarmerLocalPicPaths;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ParmNew implements Serializable {
    private String areaCode;
    private String areaName;
    private String loginName = "username";
    private String module;
    private String bxgsdm;
    private String bdh;
    private String nhdm;
    private String psw;
    private String nf;
    private String zwdm;
    private Integer sfpkh;
    private String nhxm;
    private String gyKey;
    private String gySecret;
    private String alterMessage;
    private FarmerLocalPicPaths picPaths;
    private List<String> nhdms;
private String zwmc;

    public String getZwmc() {
        return zwmc;
    }

    public void setZwmc(String zwmc) {
        this.zwmc = zwmc;
    }

    public ParmNew() {
    }

    public FarmerLocalPicPaths getPicPaths() {
        return this.picPaths;
    }

    public void setPicPaths(FarmerLocalPicPaths picPaths) {
        this.picPaths = picPaths;
    }

    public String getAlterMessage() {
        return this.alterMessage;
    }

    public void setAlterMessage(String alterMessage) {
        this.alterMessage = alterMessage;
    }

    public String getGyKey() {
        return this.gyKey;
    }

    public void setGyKey(String gyKey) {
        this.gyKey = gyKey;
    }

    public String getGySecret() {
        return this.gySecret;
    }

    public void setGySecret(String gySecret) {
        this.gySecret = gySecret;
    }

    public String getNhxm() {
        return this.nhxm;
    }

    public void setNhxm(String nhxm) {
        this.nhxm = nhxm;
    }

    public Integer getSfpkh() {
        return this.sfpkh;
    }

    public void setSfpkh(Integer sfpkh) {
        this.sfpkh = sfpkh;
    }

    public String getNf() {
        return this.nf;
    }

    public void setNf(String nf) {
        this.nf = nf;
    }

    public String getZwdm() {
        return this.zwdm;
    }

    public void setZwdm(String zwdm) {
        this.zwdm = zwdm;
    }

    public String getPsw() {
        return this.psw;
    }

    public void setPsw(String psw) {
        this.psw = psw;
    }

    public String getNhdm() {
        return this.nhdm;
    }

    public void setNhdm(String nhdm) {
        this.nhdm = nhdm;
    }

    public String getBxgsdm() {
        return this.bxgsdm;
    }

    public void setBxgsdm(String bxgsdm) {
        this.bxgsdm = bxgsdm;
    }

    public List<String> getNhdms() {
        return this.nhdms;
    }

    public void setNhdms(List<String> nhdms) {
        this.nhdms = nhdms;
    }

    public String getAreaCode() {
        return this.areaCode;
    }

    public String getBdh() {
        return this.bdh;
    }

    public void setBdh(String bdh) {
        this.bdh = bdh;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getAreaName() {
        return this.areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getLoginName() {
        return this.loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getModule() {
        return this.module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String toJson() {
        Gson gson = (new GsonBuilder()).setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        gson.serializeNulls();
        return gson.toJson(this);
    }

    public static ParamIn fromJson(String json) {
        Gson gson = (new GsonBuilder()).setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        gson.serializeNulls();
        return (ParamIn) gson.fromJson(json, ParamIn.class);
    }

    public ParmNew putNh(String nhdm) {
        if (this.nhdms == null) {
            this.nhdms = new ArrayList();
        }

        if (this.nhdms.contains(nhdm)) {
            return this;
        } else {
            this.nhdms.add(nhdm);
            return this;
        }
    }
}