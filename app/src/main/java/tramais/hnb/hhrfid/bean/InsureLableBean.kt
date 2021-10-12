package tramais.hnb.hhrfid.bean

import com.alibaba.fastjson.annotation.JSONField

class InsureLableBean {
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
         * fLabelNumber
         */
        @JSONField(name = "FLabelNumber")
        var fLabelNumber: String? = null
    }
}