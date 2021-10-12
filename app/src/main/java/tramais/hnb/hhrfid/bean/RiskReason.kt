package tramais.hnb.hhrfid.bean

import com.alibaba.fastjson.annotation.JSONField

 class RiskReason {
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

    class DataDTO {
        /**
         * reasonCode
         */
        @JSONField(name = "ReasonCode")
        var reasonCode: String? = null

        /**
         * reasonName
         */
        @JSONField(name = "ReasonName")
        var reasonName: String? = null
        //FCategory
        @JSONField(name = "FCategory")
        var fCategory: String? = null
    }
}