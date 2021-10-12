package tramais.hnb.hhrfid.bean

class AllBillDetal {
    /**
     * Labels : [{"LabelNumber":"31600491160149C"}]
     * Code : 0
     * Msg : null
     * BillNumber : IN2020080300015
     * BillDate : 2020-08-03
     * Status : 新建
     * Category : 自行投保
     * BeginDate :
     * EndDate : 2020-08-03
     * LabelAddress :
     * RiskType :
     * FarmerNumber : 411327199104171132
     * FarmerName : 阿尔山
     * ZJCategory : 身份证
     * SFZNumber : 411327199104171132
     * Area :
     * RaiseQty : 1
     * InsureQty : 1
     * EarStartNo : 001
     * EarEndNo : 001
     * UnitAmount : 10.0
     * SumAmount : 10.0
     * Signature : http://qnbapi.tramaisdb.com/Photo/2020-08-03\\sign411327199104171132-19-00-03.jpg
     */
    var code = 0
    var msg: Any? = null
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
    var farmer: List<FarmerDTO>? = null
    var FNationbdwf: String? = null
    var FProvincedwbf: String? = null
    var FCitydwbf: String? = null
    var FCountydwbf: String? = null
    var fSubsidies= 0.0
    var fOwnAmount= 0.0
    var fRiskRate :String? =null
    class LabelsBean {
        /**
         * LabelNumber : 31600491160149C
         */
        var labelNumber: String? = null
    }
    class FarmerDTO {
        var fname: String? = null
        var fSfzNumber: String? = null
    }
}