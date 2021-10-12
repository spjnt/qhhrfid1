package tramais.hnb.hhrfid.bean;

import java.util.List;

public class PDFDetailBean {

    /**
     * Data : [{"FFarmerNumber":"2222211111","fname":"赵六","FSquare":"68.55","FContactWay":"13115687894","FBankAccount":"154789545625478965","FBankName":"深圳","ZBXF":"1645.20","farmerBX":"164.52"},{"FFarmerNumber":"222221111199999999","fname":"牛人","FSquare":"268.53","FContactWay":"1367647273","FBankAccount":"888888888","FBankName":"双非银行","ZBXF":"6444.72","farmerBX":"644.47"}]
     * Code : 0
     * Msg : 获取数据成功
     * fnumber : IN2020121800064
     * ZBXF : 1645.20
     * farmerBX : 164.52
     * FCreator : 测试5
     * fmobile : null
     * FLandCategoryId : 1012
     * FLandCategory : 大白菜
     * DWBXJE : 300
     * DWFL : 0.08
     * BXFL : 2.40
     * FLandAddress : 中国广东省深圳市宝安区西乡街道宝源路1009
     */

    private Integer Code;
    private String Msg;
    private String fnumber;
    private String ZBXF;
    private String farmerBX;
    private String FCreator;
    private String fmobile;
    private String FLandCategoryId;
    private String FLandCategory;
    private String DWBXJE;
    private String DWFL;
    private String BXFL;
    private String FLandAddress;
    private List<DataDTO> Data;

    public Integer getCode() {
        return Code;
    }

    public void setCode(Integer code) {
        Code = code;
    }

    public String getMsg() {
        return Msg;
    }

    public void setMsg(String msg) {
        Msg = msg;
    }

    public String getFnumber() {
        return fnumber;
    }

    public void setFnumber(String fnumber) {
        this.fnumber = fnumber;
    }

    public String getZBXF() {
        return ZBXF;
    }

    public void setZBXF(String ZBXF) {
        this.ZBXF = ZBXF;
    }

    public String getFarmerBX() {
        return farmerBX;
    }

    public void setFarmerBX(String farmerBX) {
        this.farmerBX = farmerBX;
    }

    public String getFCreator() {
        return FCreator;
    }

    public void setFCreator(String FCreator) {
        this.FCreator = FCreator;
    }

    public String getFmobile() {
        return fmobile;
    }

    public void setFmobile(String fmobile) {
        this.fmobile = fmobile;
    }

    public String getFLandCategoryId() {
        return FLandCategoryId;
    }

    public void setFLandCategoryId(String FLandCategoryId) {
        this.FLandCategoryId = FLandCategoryId;
    }

    public String getFLandCategory() {
        return FLandCategory;
    }

    public void setFLandCategory(String FLandCategory) {
        this.FLandCategory = FLandCategory;
    }

    public String getDWBXJE() {
        return DWBXJE;
    }

    public void setDWBXJE(String DWBXJE) {
        this.DWBXJE = DWBXJE;
    }

    public String getDWFL() {
        return DWFL;
    }

    public void setDWFL(String DWFL) {
        this.DWFL = DWFL;
    }

    public String getBXFL() {
        return BXFL;
    }

    public void setBXFL(String BXFL) {
        this.BXFL = BXFL;
    }

    public String getFLandAddress() {
        return FLandAddress;
    }

    public void setFLandAddress(String FLandAddress) {
        this.FLandAddress = FLandAddress;
    }

    public List<DataDTO> getData() {
        return Data;
    }

    public void setData(List<DataDTO> data) {
        Data = data;
    }

    public static class DataDTO {
        /**
         * FFarmerNumber : 2222211111
         * fname : 赵六
         * FSquare : 68.55
         * FContactWay : 13115687894
         * FBankAccount : 154789545625478965
         * FBankName : 深圳
         * ZBXF : 1645.20
         * farmerBX : 164.52
         */

        private String FFarmerNumber;
        private String fname;
        private String FSquare;
        private String FContactWay;
        private String FBankAccount;
        private String FBankName;
        private String ZBXF;
        private String farmerBX;

        public String getFFarmerNumber() {
            return FFarmerNumber;
        }

        public void setFFarmerNumber(String FFarmerNumber) {
            this.FFarmerNumber = FFarmerNumber;
        }

        public String getFname() {
            return fname;
        }

        public void setFname(String fname) {
            this.fname = fname;
        }

        public String getFSquare() {
            return FSquare;
        }

        public void setFSquare(String FSquare) {
            this.FSquare = FSquare;
        }

        public String getFContactWay() {
            return FContactWay;
        }

        public void setFContactWay(String FContactWay) {
            this.FContactWay = FContactWay;
        }

        public String getFBankAccount() {
            return FBankAccount;
        }

        public void setFBankAccount(String FBankAccount) {
            this.FBankAccount = FBankAccount;
        }

        public String getFBankName() {
            return FBankName;
        }

        public void setFBankName(String FBankName) {
            this.FBankName = FBankName;
        }

        public String getZBXF() {
            return ZBXF;
        }

        public void setZBXF(String ZBXF) {
            this.ZBXF = ZBXF;
        }

        public String getFarmerBX() {
            return farmerBX;
        }

        public void setFarmerBX(String farmerBX) {
            this.farmerBX = farmerBX;
        }
    }
}
