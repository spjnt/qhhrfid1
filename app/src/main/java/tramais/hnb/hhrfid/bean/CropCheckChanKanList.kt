package tramais.hnb.hhrfid.bean

import com.alibaba.fastjson.annotation.JSONField
import java.io.Serializable

class CropCheckChanKanList {
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
     * maxFid
     */
    var maxFid: String? = null

    class DataDTO : Serializable {
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
         * fGrowthStage
         */
        @JSONField(name = "FGrowthStage")
        var fGrowthStage: String? = null

        //FFarmerName
        @JSONField(name = "FFarmerName")
        var fFarmerName: String? = null

        /**
         * rn
         */
        var rn: String? = null
    }
}