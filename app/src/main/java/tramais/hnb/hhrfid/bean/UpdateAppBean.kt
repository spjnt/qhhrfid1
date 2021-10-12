package tramais.hnb.hhrfid.bean

import com.alibaba.fastjson.annotation.JSONField

internal class UpdateAppBean {
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
     * data
     */
    @JSONField(name = "Data")
    var data: DataDTO? = null

    class DataDTO {
        /**
         * version
         */
        @JSONField(name = "Version")
        var version: String? = null

        /**
         * updateMsg
         */
        @JSONField(name = "UpdateMsg")
        var updateMsg: String? = null

        /**
         * updateURL
         */
        @JSONField(name = "UpdateURL")
        var updateURL: String? = null
    }
}