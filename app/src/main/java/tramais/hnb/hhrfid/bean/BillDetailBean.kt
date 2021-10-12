package tramais.hnb.hhrfid.bean

class BillDetailBean {
    var labels: List<LabelsDTO>? = null
    var farmer: List<FarmerDTO>? = null
    var code: Int? = null
    var msg: String? = null
    var billNumber: String? = null
    var billDate: String? = null
    var status: String? = null
    var category: String? = null
    var beginDate: String? = null
    var endDate: String? = null
    var labelAddress: String? = null
    var riskType: String? = null
    var farmerNumber: String? = null
    var farmerName: String? = null
    var zJCategory: String? = null
    var sFZNumber: String? = null
    var area: String? = null
    var raiseQty: Int? = null
    var insureQty: Int? = null
    var earStartNo: String? = null
    var earEndNo: String? = null
    var unitAmount: Double? = null
    var sumAmount: Double? = null
    var fSubsidies: Double? = null
    var fOwnAmount: Double? = null
    var fRiskRate: String? = null
    var fNationbdwf: String? = null
    var fProvincedwbf: String? = null
    var fCitydwbf: String? = null
    var fCountydwbf: String? = null
    var signature: String? = null
    var fShowTime: String? = null
    var createTime: String? = null
    var updateTime: String? = null
    var FCompanyDeptCode: String? = null
    var FCompanyDept: String? = null

    var FAddress1: String? = null
    var FAddress2: String? = null

    class LabelsDTO {
        var labelNumber: String? = null
        var farmerName: String? = null
    }

    class FarmerDTO {
        var fname: String? = null
        var fSfzNumber: String? = null
    }
}