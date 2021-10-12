package tramais.hnb.hhrfid.litePalBean

import org.litepal.crud.LitePalSupport

/*
 * 制作投保清单
 * */
class MakeTouBaoDealBean : LitePalSupport() {
    /**
     * Labels : [{"LabelNumber":"014014904307B9E"},{"LabelNumber":"00050093CB45DCD"},{"LabelNumber":"0005005F041514A"},{"LabelNumber":"68300F113656C03"},{"LabelNumber":"000400E73A484BE"},{"LabelNumber":"000020DED59FB09"},{"LabelNumber":"00040093CB3E9CD"},{"LabelNumber":"0005005F0445018"},{"LabelNumber":"00040093CB47DCD"},{"LabelNumber":"702012826205D08"},{"LabelNumber":"315013912407411"},{"LabelNumber":"8402B89ECC19CD3"},{"LabelNumber":"000020DED5B45EB"},{"LabelNumber":"21801991330913D"}]
     * Code : 0
     * Msg : 成功返回投保清单
     * BillNumber : IN2020080700029
     * BillDate : 2020-08-07
     * Status : 新建
     * Category : 自行投保
     * BeginDate : 2020-08-07
     * EndDate : 2021-08-07
     * LabelAddress :
     * RiskType : 养殖险
     * FarmerNumber : 430381198808100100
     * FarmerName : 易昆100
     * ZJCategory : 身份证
     * SFZNumber : 430381198808100100
     * Area :
     * RaiseQty : 14
     * InsureQty : 14
     * EarStartNo : 014014904307B9E
     * EarEndNo : 21801991330913D
     * UnitAmount : 100.0
     * SumAmount : 20000.0
     * Signature : http://qnbapi.tramaisdb.com/Photo/2020-08-07\\sign430381198808100100-16-43-47-1.jpg
     */
    var code = 0
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
    var raiseQty = 0
    var insureQty = 0
    var earStartNo: String? = null
    var earEndNo: String? = null
    var unitAmount = 0.0
    var sumAmount = 0.0
    var signature: String? = null
    var labels: String? = null
    var creatTime: String? = null
    var upDateTime: String? = null
}