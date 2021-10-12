package tramais.hnb.hhrfid.litePalBean

import org.litepal.crud.LitePalSupport

class AllBillDetailCache : LitePalSupport() {

    //FNationbdwf: String?, FProvincedwbf: String?, FCitydwbf: String?, FCountydwbf: String?,

    var FNationbdwf: String? = null
    var FProvincedwbf: String? = null
    var FCitydwbf: String? = null
    var FCountydwbf: String? = null

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
    var raiseQty = 0
    var insureQty = 0
    var earStartNo: String? = null
    var earEndNo: String? = null
    var unitAmount = 0.0
    var sumAmount = 0.0
    var signature: String? = null
    var labels: List<LabelsBean>? = null
    var lable_str: String? = null
    var creatTime: String? = null
    var updateTime: String? = null
    var isUpLoad: String? = null
    var fSubsidies = 0.0
    var fOwnAmount = 0.0
    var publicDate: String? = null
    var farmer_obj: String? = null
    var lable_obj: String? = null
    var fRiskRate: String? = null
    var updator: String? = null

    class LabelsBean {
        /**
         * LabelNumber : 31600491160149C
         */
        var labelNumber: String? = null
    }
}