package tramais.hnb.hhrfid.bean

class GongShiPdfBean {
    /**
     * Data : [{"farmername":"牛人","FLandCategory":"大白菜","FLandAddress":"中国广东省深圳市宝安区西乡街道宝源路","FSquare":"268.53","dwbe":"300.00","bf":"6444.72","grbf":"644.47"},{"farmername":"赵六","FLandCategory":"大白菜","FLandAddress":"中国广东省深圳市宝安区西乡街道宝源路","FSquare":"68.55","dwbe":"300.00","bf":"1645.20","grbf":"164.52"}]
     * Code : 0
     * Msg : 取数成功
     * fnumber : IN2020121800064
     * FLandAddress : 中国广东省深圳市宝安区西乡街道宝源路
     * FBeginDate : 2020-12-31
     * FEndDate : 2021-03-24
     * FProductName : 小麦
     * dwbe : null
     * fcompanyname : 青海人保有限公司
     * femployeename :
     * FMobile :
     * showtimebegin : 2020-01-13
     * showtimeend : 2020-01-20
     */
    var code = 0
    var msg: String? = null
    var fnumber: String? = null
    var fLandAddress: String? = null
    var fBeginDate: String? = null
    var fEndDate: String? = null
    var fProductName: String? = null
    var dwbe: String? = null
    var fcompanyname: String? = null
    var femployeename: String? = null
    var fMobile: String? = null
    var showtimebegin: String? = null
    var showtimeend: String? = null

    /**
     * farmername : 牛人
     * FLandCategory : 大白菜
     * FLandAddress : 中国广东省深圳市宝安区西乡街道宝源路
     * FSquare : 268.53
     * dwbe : 300.00
     * bf : 6444.72
     * grbf : 644.47
     */
    var data: List<DataBean>? = null

    class DataBean {
        var farmername: String? = null
        var fLandCategory: String? = null
        var fLandAddress: String? = null
        var fSquare: String? = null
        var dwbe: String? = null
        var bf: String? = null
        var grbf: String? = null
        var fSignPicture: String? = null
        var ffarmernumber: String? = null
        var farmermobile: String? = null
    }
}