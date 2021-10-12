package tramais.hnb.hhrfid.bean

class Region {
    var code = 0
    var msg: String? = null
    var fNumber: String? = null
    var fProvince: String? = null
    var fCity: String? = null
    var fCounty: String? = null
    var isExpanded = false

    /**
     * FTown :
     * FVillage :
     */
    var data: List<DataBean>? = null

    class DataBean {
        //{"FRegionNumber":"632823100200","FTown":"新源镇","FVillage":"天棚牧委会"}
        var fTown: String? = null
        var fVillage: String? = null
        var fRegionNumber: String? = null
        var fNumber_GY_SA: String? = null
    }
}