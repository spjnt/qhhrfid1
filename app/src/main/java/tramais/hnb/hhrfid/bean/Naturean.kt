package tramais.hnb.hhrfid.bean

import com.alibaba.fastjson.annotation.JSONField
import tramais.hnb.hhrfid.bean.Naturean.DATADTO

 class Naturean {
    /**
     * dATA
     */
    @JSONField(name = "DATA")
    var dATA: List<DATADTO>? = null

    /**
     * code
     */
    @JSONField(name = "Code")
    var code = 0

    /**
     * msg
     */
    @JSONField(name = "Msg")
    var msg: Any? = null

    class DATADTO {
        /**
         * fName
         */
        @JSONField(name = "FName")
        var fName: String? = null

        /**
         * fCode
         */
        @JSONField(name = "FCode")
        var fCode: String? = null
    }
}