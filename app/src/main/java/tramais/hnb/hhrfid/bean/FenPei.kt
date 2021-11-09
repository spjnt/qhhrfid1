package tramais.hnb.hhrfid.bean

import java.io.Serializable

class FenPei : Serializable {

    var isCheck = false
    var number: String? = null
    var companyNumber: String? = null
    var farmerNumber: String? = null
    var farmerName: String? = null
    var mobile: String? = null
    var insureNumber: String? = null
    var riskAddress: String? = null
    var riskDate: String? = null
    var baoAnDate: String? = null
    var riskReason: String? = null
    var riskQty: String? = null
    var riskProcess: String? = null
    var status: String? = null
    var employeeNo: String? = null
    var employeeName: String? = null
    var fCoinsFlag: String? = null
    var fRemark: String? = null
    var handleInfo: String? = null
    var fRiskCode: String? = null
    var fItemDetailList: String? = null
    var fFlag: String? = null
    var createTime: String? = null
    var updateTime: String? = null
    var fUnitAmount: String? = null
    var fBank: String? = null
    var fAccountNo: String? = null
    var FFarmerSign: String? = null
    var FSignPicture: String? = null
    var EarTag: String? = null
    var Tag: String? = null

    // FShowTimeBegin
    var FBankCode: String? = null
    var FShowTimeBegin: String? = null
    var FBankRelatedCode: String? = null
    var FProductCode: String? = null

    //FLandCategoryId
    var FLandCategoryId: String? = null
    var FHandleInfoDetail: String? = null
    var lat: Double = 0.0
    var log: Double = 0.0
    var FClaimNo: String? = null
    var FCompensateNo: String? = null
}