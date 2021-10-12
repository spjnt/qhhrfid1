package tramais.hnb.hhrfid.bean

import com.alibaba.fastjson.annotation.JSONField
import java.io.Serializable

class FidDetail : Serializable {
    /**
     * data
     */
    @JSONField(name = "Data")
    var data: List<DataDTO>? = null

    /**
     * code
     */
    @JSONField(name = "Code")
    var code = 0

    /**
     * msg
     */
    @JSONField(name = "Msg")
    var msg: String? = null

    /**
     * fBaoAnNumber
     */
    @JSONField(name = "FBaoAnNumber")
    var fBaoAnNumber: String? = null

    /**
     * fid
     */
    @JSONField(name = "Fid")
    var fid: String? = null

    /**
     * fChaKanDate
     */
    @JSONField(name = "FChaKanDate")
    var fChaKanDate: String? = null

    /**
     * fCreator
     */
    @JSONField(name = "FCreator")
    var fCreator: String? = null

    /**
     * fAddress
     */
    @JSONField(name = "FAddress")
    var fAddress: String? = null

    /**
     * fFarmerqty
     */
    @JSONField(name = "FFarmerqty")
    var fFarmerqty: Any? = null

    /**
     * fRiskReason
     */
    @JSONField(name = "FRiskReason")
    var fRiskReason: String? = null

    /**
     * fShouZaiqty
     */
    @JSONField(name = "FShouZaiqty")
    var fShouZaiqty: String? = null
/*
* FShouZaiqty */
@JSONField(name = "FSunShiqty")
var fSunShiqty: String? = null
    /**
     * fLossRate
     */
    @JSONField(name = "FLossRate")
    var fLossRate: Any? = null

    /**
     * fGrowthStage
     */
    @JSONField(name = "FGrowthStage")
    var fGrowthStage: String? = null

    /**
     * fLossqty
     */
    @JSONField(name = "FLossqty")
    var fLossqty: String? = null

    /**
     * fRiskProcess
     */
    @JSONField(name = "FRiskProcess")
    var fRiskProcess: String? = null

    /**
     * fOpintion
     */
    @JSONField(name = "FOpintion")
    var fOpintion: String? = null

    /**
     * fFarmerSign
     */
    @JSONField(name = "FFarmerSign")
    var fFarmerSign: Any? = null

    /**
     * fEmployeeSign
     */
    @JSONField(name = "FEmployeeSign")
    var fEmployeeSign: String? = null

    class DataDTO {
        /**
         * fPicture
         */
        @JSONField(name = "FPicture")
        var fPicture: String? = null
    }
}