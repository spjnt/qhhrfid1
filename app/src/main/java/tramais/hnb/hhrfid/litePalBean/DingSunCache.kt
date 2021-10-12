package tramais.hnb.hhrfid.litePalBean

import org.litepal.crud.LitePalSupport

class DingSunCache : LitePalSupport() {
    var INNumber: String? = null
    var CompanyNumber: String? = null
    var FarmerNumber: String? = null
    var EmployeeNumber: String? = null
    var EmployeeName: String? = null
    var number: String? = null
    var sheep_snow: String? = null
    var sheep_ill: String? = null
    var sheep_harm: String? = null
    var sheep_other: String? = null
    var cow_snow: String? = null
    var cow_ill: String? = null
    var cow_harm: String? = null
    var cow_other: String? = null
    var isTruthTrue = 0
    var isTagTruth = 0
    var isWeather = 0
    var isIll = 0
    var isOther = 0
    var sheep_slavge: String? = null
    var sheep_market: String? = null
    var sheep_ratio: String? = null
    var sheep_lpr: String? = null
    var cow_slavge: String? = null
    var cow_market: String? = null
    var cow_ratio: String? = null
    var cow_lpr: String? = null
    var repet_sign: String? = null
    var farmer_sign: String? = null
    var farmer_mobile: String? = null
    var check_sign: String? = null
    var check_mobile: String? = null
    var status: String? = null
    var creatTime: String? = null
    var updateTime: String? = null
    var isup_load_sunshi: Boolean = false
    var isup_load_dingsun: Boolean = false
    var isup_load_sign: Boolean = false
}