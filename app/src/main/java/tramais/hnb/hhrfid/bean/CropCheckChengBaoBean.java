package tramais.hnb.hhrfid.bean;

import java.util.List;

public class CropCheckChengBaoBean {


    /**
     * {"GetAllLandList":
     * [{"FSFZNumber":"632221196706040012","FCompanyNumber":null,"FName":"包文元","FRiskCategory":null,"FArea":null,"FLandName":"包文元","FLandNumber":"2020026322211002070004","FCropName":null,"FCropCode":null,"FSquare":"208.45","FCheckSquare":"208.45","FGISPicture":"http://10.252.252.238:6050/photo/2021-05-20/202105201648071912585.jpg","sum_FSquare":null,"sum_FCheckSquare":null,"block_count":null},
     * {"FSFZNumber":"632221196706040012","FCompanyNumber":null,"FName":"包文元","FRiskCategory":null,"FArea":null,"FLandName":"包文元","FLandNumber":"2020026322211002070005","FCropName":null,"FCropCode":null,"FSquare":"39.46","FCheckSquare":"39.46","FGISPicture":"http://10.252.252.238:6050/photo/2021-05-20/202105201648073821461.jpg","sum_FSquare":null,"sum_FCheckSquare":null,"block_count":null},
     * {"FSFZNumber":"632221196706040012","FCompanyNumber":null,"FName":"包文元","FRiskCategory":null,"FArea":null,"FLandName":"包文元","FLandNumber":"2020026322211002070006","FCropName":null,"FCropCode":null,"FSquare":"7.99","FCheckSquare":"7.99","FGISPicture":"http://10.252.252.238:6050/photo/2021-05-20/202105201648071381448.jpg","sum_FSquare":null,"sum_FCheckSquare":null,"block_count":null}],
     * "Code":0,"Msg":"获取数据成功","FNumber":"IN2021052000148","FDate":"2021-05-20","FFarmerNumber":"632221196706040012","FShowTime":"2021-05-20","FBeginDate":"2021-05-27","FEndDate":"2021-08-18","FLandCategory":"蚕豆","FLandCategoryId":"990016","FLandAddress":"中国广东省深圳市宝安区西乡街道宝源路","FSubsidies":"5527.44","FOwnAmount":"614.16","FSumAmount":"6141.6","FUnitAmount":"300","fproductCode":"HMW","FSignature":"http://10.252.252.238:6050/photo/2021-05-20/tbdsign20210520165021639.jpg","FSealPicture":""}
     */
    public String FSumAmount;  //保费金额
    public String FUnitAmount; //单位保额
    private int Code;
    private String Msg;
    private String FNumber;
    private String FDate;
    private String FFarmerNumber;
    private String FShowTime;
    private String FBeginDate;
    private String FEndDate;
    private String FLandCategory;
    private String FLandAddress;
    private String FSubsidies;
    private String FOwnAmount;
    private String dwbe;
    private String FSignature;
    private String FSealPicture;
    private String FLandCategoryId;
    private String fproductCode;
    private String FSquare;
    private String FCheckSquare;
    private String sum_FCheckSquare;
    private String block_count;
    private String sum_FSquare;
//"FNationbdwf":"","FProvincedwbf":"","FCitydwbf":"","FCountydwbf":""

    private String FNationbdwf;
    private String FProvincedwbf;
    private String FCitydwbf;
    private String FCountydwbf;
    private String FCompanyDeptCode;
    private String FCompanyDept;

    private String FAddress1;
    private String FAddress2;

    public String getFAddress1() {
        return this.FAddress1;
    }

    public void setFAddress1(final String FAddress1) {
        this.FAddress1 = FAddress1;
    }

    public String getFAddress2() {
        return this.FAddress2;
    }

    public void setFAddress2(final String FAddress2) {
        this.FAddress2 = FAddress2;
    }

    public String getFCompanyDeptCode() {
        return this.FCompanyDeptCode;
    }

    public void setFCompanyDeptCode(final String FCompanyDeptCode) {
        this.FCompanyDeptCode = FCompanyDeptCode;
    }

    public String getFCompanyDept() {
        return this.FCompanyDept;
    }

    public void setFCompanyDept(final String FCompanyDept) {
        this.FCompanyDept = FCompanyDept;
    }

    public String getFNationbdwf() {
        return FNationbdwf;
    }

    public void setFNationbdwf(String FNationbdwf) {
        this.FNationbdwf = FNationbdwf;
    }

    public String getFProvincedwbf() {
        return FProvincedwbf;
    }

    public void setFProvincedwbf(String FProvincedwbf) {
        this.FProvincedwbf = FProvincedwbf;
    }

    public String getFCitydwbf() {
        return FCitydwbf;
    }

    public void setFCitydwbf(String FCitydwbf) {
        this.FCitydwbf = FCitydwbf;
    }

    public String getFCountydwbf() {
        return FCountydwbf;
    }

    public void setFCountydwbf(String FCountydwbf) {
        this.FCountydwbf = FCountydwbf;
    }

    public String getSum_FCheckSquare() {
        return sum_FCheckSquare;
    }

    public void setSum_FCheckSquare(String sum_FCheckSquare) {
        this.sum_FCheckSquare = sum_FCheckSquare;
    }

    public String getBlock_count() {
        return block_count;
    }

    public void setBlock_count(String block_count) {
        this.block_count = block_count;
    }

    public String getSum_FSquare() {
        return sum_FSquare;
    }

    public void setSum_FSquare(String sum_FSquare) {
        this.sum_FSquare = sum_FSquare;
    }

    public String getFSquare() {
        return FSquare;
    }

    public void setFSquare(String FSquare) {
        this.FSquare = FSquare;
    }

    public String getFCheckSquare() {
        return FCheckSquare;
    }

    public void setFCheckSquare(String FCheckSquare) {
        this.FCheckSquare = FCheckSquare;
    }

    public String getFLandCategoryId() {
        return FLandCategoryId;
    }

    public void setFLandCategoryId(String FLandCategoryId) {
        this.FLandCategoryId = FLandCategoryId;
    }

    public String getFproductCode() {
        return fproductCode;
    }

    public void setFproductCode(String fproductCode) {
        this.fproductCode = fproductCode;
    }

    public String getFSumAmount() {
        return FSumAmount;
    }

    public void setFSumAmount(String FSumAmount) {
        this.FSumAmount = FSumAmount;
    }

    public String getFUnitAmount() {
        return FUnitAmount;
    }

    public void setFUnitAmount(String FUnitAmount) {
        this.FUnitAmount = FUnitAmount;
    }

    /**
     * FSFZNumber : 411327199104171133
     * FCompanyNumber : null
     * FName : 测试用户
     * FRiskCategory : null
     * FArea : null
     * FLandName : 测试用户
     * FLandNumber : 2020094109221002020028
     * FCropName : null
     * FCropCode : null
     * FSquare : 22.85
     * FCheckSquare : 22.85
     */

    private List<GetAllLandListDTO> GetAllLandList;

    public String getFlandCategoryID() {
        return FLandCategoryId;
    }

    public void setFlandCategoryID(String flandCategoryID) {
        FLandCategoryId = flandCategoryID;
    }

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

    public String getFDate() {
        return FDate;
    }

    public void setFDate(String FDate) {
        this.FDate = FDate;
    }

    public String getFFarmerNumber() {
        return FFarmerNumber;
    }

    public void setFFarmerNumber(String FFarmerNumber) {
        this.FFarmerNumber = FFarmerNumber;
    }

    public String getFShowTime() {
        return FShowTime;
    }

    public void setFShowTime(String FShowTime) {
        this.FShowTime = FShowTime;
    }

    public String getFBeginDate() {
        return FBeginDate;
    }

    public void setFBeginDate(String FBeginDate) {
        this.FBeginDate = FBeginDate;
    }

    public String getFEndDate() {
        return FEndDate;
    }

    public void setFEndDate(String FEndDate) {
        this.FEndDate = FEndDate;
    }

    public String getFLandCategory() {
        return FLandCategory;
    }

    public void setFLandCategory(String FLandCategory) {
        this.FLandCategory = FLandCategory;
    }

    public String getFLandAddress() {
        return FLandAddress;
    }

    public void setFLandAddress(String FLandAddress) {
        this.FLandAddress = FLandAddress;
    }

    public String getFSubsidies() {
        return FSubsidies;
    }

    public void setFSubsidies(String FSubsidies) {
        this.FSubsidies = FSubsidies;
    }

    public String getFOwnAmount() {
        return FOwnAmount;
    }

    public void setFOwnAmount(String FOwnAmount) {
        this.FOwnAmount = FOwnAmount;
    }

    public String getDwbe() {
        return dwbe;
    }

    public void setDwbe(String dwbe) {
        this.dwbe = dwbe;
    }

    public String getFSignature() {
        return FSignature;
    }

    public void setFSignature(String FSignature) {
        this.FSignature = FSignature;
    }

    public String getFSealPicture() {
        return FSealPicture;
    }

    public void setFSealPicture(String FSealPicture) {
        this.FSealPicture = FSealPicture;
    }

    public List<GetAllLandListDTO> getGetAllLandList() {
        return GetAllLandList;
    }

    public void setGetAllLandList(List<GetAllLandListDTO> GetAllLandList) {
        this.GetAllLandList = GetAllLandList;
    }

    public static class GetAllLandListDTO {
        private String FSFZNumber;
        private String FCompanyNumber;
        private String FName;
        private String FRiskCategory;
        private String FArea;
        private String FLandName;
        private String FLandNumber;
        private String FCropName;
        private String FCropCode;
        private String FSquare;
        private String FCheckSquare;
        private String FGISPicture;
        private String FChengBaoArea, FSunShiArea, FShouZaiArea, FSunShiQty;

        private String sum_FCheckSquare;
        private String block_count;
        private String sum_FSquare;

        public String getSum_FCheckSquare() {
            return sum_FCheckSquare;
        }

        public void setSum_FCheckSquare(String sum_FCheckSquare) {
            this.sum_FCheckSquare = sum_FCheckSquare;
        }

        public String getBlock_count() {
            return block_count;
        }

        public void setBlock_count(String block_count) {
            this.block_count = block_count;
        }

        public String getSum_FSquare() {
            return sum_FSquare;
        }

        public void setSum_FSquare(String sum_FSquare) {
            this.sum_FSquare = sum_FSquare;
        }

        public String getFGISPicture() {
            return FGISPicture;
        }

        public void setFGISPicture(String FGISPicture) {
            this.FGISPicture = FGISPicture;
        }

        public String getFChengBaoArea() {
            return FChengBaoArea;
        }

        public void setFChengBaoArea(String FChengBaoArea) {
            this.FChengBaoArea = FChengBaoArea;
        }

        public String getFSunShiArea() {
            return FSunShiArea;
        }

        public void setFSunShiArea(String FSunShiArea) {
            this.FSunShiArea = FSunShiArea;
        }

        public String getFShouZaiArea() {
            return FShouZaiArea;
        }

        public void setFShouZaiArea(String FShouZaiArea) {
            this.FShouZaiArea = FShouZaiArea;
        }

        public String getFSunShiQty() {
            return FSunShiQty;
        }

        public void setFSunShiQty(String FSunShiQty) {
            this.FSunShiQty = FSunShiQty;
        }

        public String getFSFZNumber() {
            return FSFZNumber;
        }

        public void setFSFZNumber(String FSFZNumber) {
            this.FSFZNumber = FSFZNumber;
        }

        public String getFCompanyNumber() {
            return FCompanyNumber;
        }

        public void setFCompanyNumber(String FCompanyNumber) {
            this.FCompanyNumber = FCompanyNumber;
        }

        public String getFName() {
            return FName;
        }

        public void setFName(String FName) {
            this.FName = FName;
        }

        public String getFRiskCategory() {
            return FRiskCategory;
        }

        public void setFRiskCategory(String FRiskCategory) {
            this.FRiskCategory = FRiskCategory;
        }

        public String getFArea() {
            return FArea;
        }

        public void setFArea(String FArea) {
            this.FArea = FArea;
        }

        public String getFLandName() {
            return FLandName;
        }

        public void setFLandName(String FLandName) {
            this.FLandName = FLandName;
        }

        public String getFLandNumber() {
            return FLandNumber;
        }

        public void setFLandNumber(String FLandNumber) {
            this.FLandNumber = FLandNumber;
        }

        public String getFCropName() {
            return FCropName;
        }

        public void setFCropName(String FCropName) {
            this.FCropName = FCropName;
        }

        public String getFCropCode() {
            return FCropCode;
        }

        public void setFCropCode(String FCropCode) {
            this.FCropCode = FCropCode;
        }

        public String getFSquare() {
            return FSquare;
        }

        public void setFSquare(String FSquare) {
            this.FSquare = FSquare;
        }

        public String getFCheckSquare() {
            return FCheckSquare;
        }

        public void setFCheckSquare(String FCheckSquare) {
            this.FCheckSquare = FCheckSquare;
        }
    }
}
