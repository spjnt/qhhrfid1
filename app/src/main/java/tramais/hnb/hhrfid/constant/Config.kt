package tramais.hnb.hhrfid.constant


object Config {
    var img_quality_small = 70
    var img_quality_common =80

    @JvmField
    var REQUEST_SING = 1 shl 111

    /*生产ip:139.170.248.162:6051
测试ip：111.44.133.34:6050*/
    @JvmField

    var BASE_URL = "http://111.44.133.34:6051/"

    /*http://111.44.133.34:6030/SystemAdmin/News/ShowNews.htm?id=32
生产的是：http://139.170.248.162:6031/SystemAdmin/News/ShowNews.htm?id=32*/
    var NEWS_BASE_URL = "http://111.44.133.34:6030/"
    var GetNewsListDetail = NEWS_BASE_URL + "SystemAdmin/News/ShowNews.htm?id="
    var AppDescription = "http://111.44.133.34:6031/AppOtherPage/AppDescription.htm"

    @JvmField
    var PHOTO_URL = BASE_URL + "photo/"

    @JvmField
    var PDF_LOGO_URL = BASE_URL + "logo/picc_log1.png"
    var checklogin = BASE_URL + "employee/checklogin"

    @JvmField
    var nmg = "nmg"

    /*投保人列表*/
    @JvmField
    var getlist = BASE_URL + "farmer/getlist"

    /*创建投保人*/
    @JvmField
    var savefarmer = BASE_URL + "Farmer/SaveFarmer"

    //查勘照片增加
    @JvmField
    var SaveChaKanEarImg = BASE_URL + "LiPeiChaKan/SaveChaKanEarImg"

    @JvmField
    var savefarmer2 = BASE_URL + "Farmer/SaveFarmer2"

    /*牲畜品种*/
    @JvmField
    var getanimalcategory = "system/getanimalcategory"

    /*证件类型*/
    @JvmField
    var getidentifycategory = BASE_URL + "system/getidentifycategory"

    /*版本更新*/
    @JvmField
    var getversioninfo = BASE_URL + "system/getversioninfo"

    /*身份证识别*/
    var indentifyocr = BASE_URL + "Farmer/IdentifyOcr"

    //IdentifyBackOcr
    var IdentifyBackOcr = BASE_URL + "Farmer/IdentifyBackOcr"

    //Farmer/IdentifyBusinessLicense 营业执照
    var IdentifyBusinessLicense = BASE_URL + "Farmer/IdentifyBusinessLicense"

    /*银行卡识别*/
    @JvmField
    var bankocr = BASE_URL + "farmer/bankocr"

    // Farmer/SaveFarmerSign
    @JvmField
    var SaveFarmerSign = BASE_URL + "Farmer/SaveFarmerSign"

    @JvmField
    var SaveAnimal = BASE_URL + "Animal/SaveAnimal"

    /*按耳标号查询*/
    @JvmField
    var SearchByLabel = BASE_URL + "Animal/SearchByLabel"

    /*按农户查询*/
    @JvmField
    var SearchByFarmer = BASE_URL + "Animal/SearchByFarmer"
    var LandSearchByFarmer = BASE_URL + "Land/SearchByFarmer"

    //上传图片
    var UploadPicture = BASE_URL + "System/UploadPicture"

    //制作投保清单
    @JvmField
    var SaveBill = BASE_URL + "InsureBill/SaveBill"

    //获取投保清单
    var GetBill = BASE_URL + "InsureBill/GetBill"

    // 保存或更新报案
    @JvmField
    var SaveBaoAn = BASE_URL + "LiPeiBaoAn/SaveBaoAn"

    //出险原因列表
    @JvmField
    var GetRiskReason = BASE_URL + "System/GetRiskReason"

    @JvmField
    var GetLandGrowthStage = BASE_URL + "System/GetLandGrowthStage"

    // 报案列表
    @JvmField
    var GetBaoAn = BASE_URL + "LiPeiBaoAn/GetBaoAn"

    // 报案分配接口
    @JvmField
    var AssignBaoAn = BASE_URL + "LiPeiBaoAn/AssignBaoAn"

    //查勘员
    @JvmField
    var GetList = BASE_URL + "Employee/GetList"

    //保存查勘
    @JvmField
    var SaveChaKan = BASE_URL + "LiPeiChaKan/SaveChaKan"

    //保存定损损失  LiPeiLoss/SaveLoss
    @JvmField
    var SaveLoss = BASE_URL + "LiPeiLoss/SaveLoss"

    //保存损失情况  LiPeiLoss/SaveLossValue
    @JvmField
    var SaveLossValue = BASE_URL + "LiPeiLoss/SaveLossValue"

    //投保清单号 InsureBill/GetBillDetail
    @JvmField
    var GetBillDetail = BASE_URL + "InsureBill/GetBillDetail"

    //InsureBill/GetPiccInsureBillDetail
    @JvmField
    var GetPiccInsureBillDetail = BASE_URL + "InsureBill/GetPiccInsureBillDetail"

    //定损签名 LiPeiLoss/SaveLossSign
    @JvmField
    var SaveLossSign = BASE_URL + "LiPeiLoss/SaveLossSign"

    //
    @JvmField
    var SaveChaKanSign = BASE_URL + "LiPeiChaKan/SaveChaKanSign"

    // 查勘详情 LiPeiChaKan/GetChaKanDetail
    @JvmField
    var GetChaKanDetail = BASE_URL + "LiPeiChaKan/GetChaKanDetail"

    //定损详情  LiPeiLoss/GetChaKanDetail
    @JvmField
    var LossDetail = BASE_URL + "LiPeiLoss/GetChaKanDetail"

    //Employee/GetCompanyList
    @JvmField
    var GetCompanyList = BASE_URL + "Employee/GetCompanyList"

    //System/GetList
    @JvmField
    var System_GetList = BASE_URL + "System/GetList"

    //InsureBill/GetAllBillDetail
    @JvmField
    var GetAllBillDetail = BASE_URL + "InsureBill/GetAllBillDetail"

    //Farmer/GetGYArea
    @JvmField
    var GetGYArea = BASE_URL + "Farmer/GetGYArea"

    //    生产端
    //    sdk: implementation('com.grandtech.common:standard_sdk_prod:0.0.0.1.3:@aar') {  transitive = true }
    //    地块接口：http://api.agribigdata.com.cn/insurance-server-business-app/api/v1/gspicc/getstands/refer/res
    //    key: b0a0cef2e5684b28ad143ce9e9aebc2a
    //    secreat  054caca5e2d942ea85936817fc544b42
    //
    //    测试端：
    //    sdk：implementation('com.grandtech.common:standard_sdk:0.0.0.6.9:@aar') {  transitive = true }
    //    返回地块数据地址：http://gykj123.cn:8849/insurance-server-business-app/api/v1/gspicc/getstands/refer/res
    //    etKey：d9b91a2f18ea40f6afd42e4e79ada484
    //    etSecret：6abdbb7cd2a14d439683423222b92a48
    @JvmField
    var GUOYUAN = "http://api.agribigdata.com.cn/insurance-server-business-app/api/v1/gspicc/getstands/refer/res"

    @JvmField
    var KEY = "b0a0cef2e5684b28ad143ce9e9aebc2a"

    @JvmField
    var SECRET = "054caca5e2d942ea85936817fc544b42"

    //updatetime
    @JvmField
    var SaveLand = BASE_URL + "Land/SaveLand"

    @JvmField
    var SaveLand2 = BASE_URL + "Land/SaveLand2"

    //
    @JvmField
    var SaveLandBill = BASE_URL + "InsureBill/SaveLandBill"

    @JvmField
    var GetAllLandDetail = BASE_URL + "InsureBill/GetAllLandDetail"

    @JvmField
    var GetCrop = BASE_URL + "InsureBill/GetCrop"

    //7、提交初审和承保公示列表
    @JvmField
    var GetLandInsureBillList = BASE_URL + "InsureBill/GetLandInsureBillList"

    //8、提交初审和公示状态更改接口
    @JvmField
    var SetBillStatus = BASE_URL + "InsureBill/SetBillStatus"

    //9、提交初审和公示状态更改接口
    @JvmField
    var GetLandBillDetail = BASE_URL + "InsureBill/GetLandBillDetail"

    //13、新增报案(报案详情)
    @JvmField
    var SaveLandBaoAn = BASE_URL + "LiPeiBaoAn/SaveLandBaoAn"

    //11、待查勘任务列表
    @JvmField
    var GetLandChaKanList = BASE_URL + "LiPeiChaKan/GetLandChaKanList"

    //14、获取种植险报案信息的详情
    @JvmField
    var GetLandChaKanDetail = BASE_URL + "LiPeiChaKan/GetLandChaKanDetail"

    //15获取员工列表
    var GetEmployeeList = BASE_URL + "Employee/GetList"

    //16  任务分配接口（任务分配给某员工）
    @JvmField
    var LandChaKanFenPei = BASE_URL + "LiPeiChaKan/LandChaKanFenPei"

    //22 查勘信息概览列表 LiPeiChaKan/GetLandChaKanDetailList
    @JvmField
    var GetLandChaKanDetailList = BASE_URL + "LiPeiChaKan/GetLandChaKanDetailList"

    //LiPeiChaKan/GetLandChaKanFidDetail
    @JvmField
    var GetLandChaKanFidDetail = BASE_URL + "LiPeiChaKan/GetLandChaKanFidDetail"

    //26  新增察勘信息
    @JvmField
    var SaveLandChaKan = BASE_URL + "LiPeiChaKan/SaveLandChaKan"

    //新闻
    @JvmField
    var GetNewsList = BASE_URL + "news/GetNewsList"

    @JvmField
    var GetLandBill = BASE_URL + "InsureBill/GetLandBill"

    //31  抽样结论保存
    @JvmField
    var SaveChouYangSign = BASE_URL + "LiPeiChaKan/SaveChouYangSign"

    //31  抽样结论保存
    @JvmField
    var SaveChouYang = BASE_URL + "LiPeiChaKan/SaveChouYang"

    ///获取农作物维度
    @JvmField
    var getItem = BASE_URL + "System/getItem"

    //  定损信息获取
    @JvmField
    var getDingSun = BASE_URL + "LiPeiChaKan/getDingSun"

    //LiPeiChaKan/SaveDingSun
    @JvmField
    var SaveDingSun = BASE_URL + "LiPeiChaKan/SaveDingSun"

    //LiPeiChaKan/getChouYang
    @JvmField
    var getChouYang = BASE_URL + "LiPeiChaKan/getChouYang"

    //InsureBill/getLandBillPDFDetail
    @JvmField
    var getLandBillPDFDetail = BASE_URL + "InsureBill/getLandBillPDFDetail"

    ///LiPeiChaKan/GetDingSunlandDetailResult
    @JvmField
    var GetDingSunlandDetailResult = BASE_URL + "LiPeiChaKan/GetDingSunlandDetailResult"

    //LiPeiChaKan/GetDingSunlandResult
    @JvmField
    var GetDingSunlandResult = BASE_URL + "LiPeiChaKan/GetDingSunlandResult"

    ///获取农作物维度
    @JvmField
    var getRegion = BASE_URL + "System/getRegion"

    //GetGongShiPDFDetail
    @JvmField
    var GetGongShiPDFDetail = BASE_URL + "InsureBill/GetGongShiPDFDetail"

    //InsureBill/GetLandInsureBillList
    @JvmField
    var GetAnimalInsureBillList = BASE_URL + "InsureBill/GetAnimalInsureBillList"

    //InsureBill/SetAnimalBillStatus
    @JvmField
    var SetAnimalBillStatus = BASE_URL + "InsureBill/SetAnimalBillStatus"

    //UpdateAnimalBaoAnStatus
    @JvmField
    var UpdateAnimalBaoAnStatus = BASE_URL + "LiPeiChaKan/UpdateAnimalBaoAnStatus"

    //删除照片增加
    @JvmField
    var DeleteChaKanEarImg = BASE_URL + "LiPeiChaKan/DeleteChaKanEarImg"

    //LiPeiChaKan/LandChaKanSunShi
    @JvmField
    var LandChaKanSunShi = BASE_URL + "LiPeiChaKan/LandChaKanSunShi"

    //InsureBill/GetAnimalInsureGongShiPDF
    @JvmField
    var GetAnimalInsureGongShiPDF = BASE_URL + "InsureBill/GetAnimalInsureGongShiPDF"

    @JvmField
    var GetNMGAnimalInsureGongShiPDF = BASE_URL + "InsureBill/GetNMGAnimalInsureGongShiPDF"

    ///System/getRole
    @JvmField
    var getRole = BASE_URL + "System/getRole"

    //LiPeiChaKan/GetAnimalLipeiPDF
    @JvmField
    var GetAnimalLipeiPDF = BASE_URL + "LiPeiChaKan/GetAnimalLipeiPDF"

    //PiccChengBao/ WapGsIListImportServlet
    @JvmField
    var WapGsIListImportServlet = BASE_URL + "PiccChengBao/WapGsIListImportServlet"

    //PiccChengBao/PayBill
    @JvmField
    var PayBill = BASE_URL + "PiccChengBao/PayBill"

    //PiccChengBao/PayBillStatus
    @JvmField
    var PayBillStatus = BASE_URL + "PiccChengBao/PayBillStatus"

    //PiccChengBao/PayBillCancel
    @JvmField
    var PayBillCancel = BASE_URL + "PiccChengBao/PayBillCancel"

    //PiccLandChengBao/ WapGsHListImportServlet
    @JvmField
    var CropWapGsHListImportServlet = BASE_URL + "PiccLandChengBao/WapGsHListImportServlet"

    //PiccChengBao/PayBill
    @JvmField
    var CropPayBill = BASE_URL + "PiccLandChengBao/PayBill"

    //PiccLandChengBao/PayBillStatus
    @JvmField
    var CropPayBillStatus = BASE_URL + "PiccLandChengBao/PayBillStatus"

    //  PiccLandChengBao/PayBillCancel
    @JvmField
    var CropPayBillCancel = BASE_URL + "PiccLandChengBao/PayBillCancel"

    //LiPeiChaKan/UpdateBaoAnStatus
    @JvmField
    var getBillCount = BASE_URL + "System/getBillCount"

    ///InsureBill/GetProductDetail
    @JvmField
    var GetProductDetail = BASE_URL + "InsureBill/GetProductDetail"

    //System/GetVerificationCode
    @JvmField
    var GetVerificationCode = BASE_URL + "System/GetVerificationCode"

    //System/JudgeVerificationCode
    @JvmField
    var JudgeVerificationCode = BASE_URL + "System/JudgeVerificationCode"

    //System/ResetPswd
    @JvmField
    var ResetPswd = BASE_URL + "System/ResetPswd"

    //InsureBill/GetPiccInsureBillDetail
//SaveAnimalDingSun
    @JvmField
    var SaveAnimalDingSun = BASE_URL + "LiPeiChaKan/SaveAnimalDingSun2"

    /*
    * System/getBankInfo*/
    @JvmField
    var getBankInfo = BASE_URL + "System/getBankInfo"

    //System/getBankInfo
    @JvmField
    var getBankInfoDetail = BASE_URL + "System/getBank"

    //LiPeiChaKan/SetLiPeiStatus
    var SetLiPeiStatus = BASE_URL + "LiPeiChaKan/SetLiPeiStatus"

    ///LiPeiChaKan/GetInsureLabels
    var GetInsureLabels = BASE_URL + "LiPeiChaKan/GetInsureLabels"

    //farmer/GetInsureFarmer
    var GetInsureFarmer = BASE_URL + "farmer/GetInsureFarmer"

    //LiPeiBaoAn/GetLandBaoAn
    var GetLandBaoAn = BASE_URL + "LiPeiBaoAn/GetLandBaoAn"

    //farmer/SaveFarmer3
    var SaveFarmer3 = BASE_URL + "farmer/SaveFarmer3"

    //LiPeiChaKan/GetChaKanLands
    var GetChaKanLands = BASE_URL + "LiPeiChaKan/GetChaKanLands"

    //Picc/Animal/LiPei/NXLPAppServlet
    var NXLPAppServlet = BASE_URL + "PiccAnimalLiPei/NXLPAppServlet"

    //AIShuoShuo/Shuoshuo
    var AIShuoShuo = BASE_URL + "AIShuoShuo/Shuoshuo"

    //AIShuoShuo/Shuoshuo
    var GetLabels = BASE_URL + "InsureBill/GetLabels"

    //DeleteBill
    var DeleteAnimalBill = BASE_URL + "InsureBill/DeleteAnimalBill"

    //System/getEnterpriseNature
    var getEnterpriseNature = BASE_URL + "System/getEnterpriseNature"
    var GetCompanyDept = BASE_URL + "InsureBill/GetCompanyDept"

    //System/address
    var address = BASE_URL + "System/address"
}