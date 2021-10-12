package tramais.hnb.hhrfid.bean

import com.alibaba.fastjson.annotation.JSONField

 class BankInfo {
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
         * fBackCode
         */
        @JSONField(name = "FBackCode")
        var fBackCode: String? = null

        /**
         * fBackName
         */
        @JSONField(name = "FBackName")
        var fBackName: String? = null


    }
}