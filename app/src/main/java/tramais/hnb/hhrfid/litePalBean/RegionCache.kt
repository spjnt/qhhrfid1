package tramais.hnb.hhrfid.litePalBean

import org.litepal.crud.LitePalSupport

class RegionCache : LitePalSupport() {
    var code = 0
    var msg: String? = null
    var fNumber: String? = null
    var fProvince: String? = null
    var fCity: String? = null
    var fCounty: String? = null
    var data_arr: String? = null
    var data: List<DataBean>? = null

    class DataBean {
        var fTown: String? = null
        var fVillage: String? = null
        var fRegionNumber: String? = null
    }
}