package tramais.hnb.hhrfid.litePalBean

import org.litepal.crud.LitePalSupport
import java.io.Serializable

class AnimalInsureBillListCache : LitePalSupport(), Serializable {
    var fCreateTime: String? = null
    var fCreator: String? = null
    var fFarmerNumber: String? = null
    var fItemName: String? = null
    var fLabelAddress: String? = null
    var fNumber: String? = null
    var fRiskCode: String? = null
    var fShowTime: String? = null
    var fStatus: String? = null
    var fUpdateTime: String? = null
    var fname: String? = null
    var fproductCode: String? = null
    var isUpLoad = false
    var statusCode: String? = null
}