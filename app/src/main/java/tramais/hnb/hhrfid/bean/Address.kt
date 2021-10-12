package tramais.hnb.hhrfid.bean

import com.alibaba.fastjson.annotation.JSONField

 class Address {
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
     * companyAddress
     */
    var companyAddress: String? = null
}