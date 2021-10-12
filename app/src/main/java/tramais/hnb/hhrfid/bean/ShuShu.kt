package tramais.hnb.hhrfid.bean

import com.alibaba.fastjson.annotation.JSONField

 class ShuShu {
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
     * count
     */
    var count = 0

    /**
     * picturename
     */
    var picturename: String? = null
}