package tramais.hnb.hhrfid.litePalBean

import org.litepal.crud.LitePalSupport

class ChanKanEntrysBean : LitePalSupport() {
    var baoAnNumber: String? = null
    var entryID = 0
    var category: String? = null
    var label: String? = null
    var weight: String? = null
    var length: String? = null
    var picture: String? = null
    var isUpLoad: Boolean? = false
}