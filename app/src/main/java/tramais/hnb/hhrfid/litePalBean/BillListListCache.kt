package tramais.hnb.hhrfid.litePalBean

import org.litepal.crud.LitePalSupport

class BillListListCache : LitePalSupport() {
    var number: String? = null
    var date: String? = null
    var status: String? = null
    var companyNumber: String? = null
    var farmer: String? = null
    var category: String? = null
    var sumAmount = 0.0
    var creator: String? = null
    var farmerNumber: String? = null
    var creatTime: String? = null
    var area: String? = null
}