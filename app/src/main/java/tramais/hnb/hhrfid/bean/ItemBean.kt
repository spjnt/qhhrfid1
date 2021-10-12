package tramais.hnb.hhrfid.bean

import com.alibaba.fastjson.annotation.JSONField

class ItemBean {
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
         * fType
         */
        @JSONField(name = "FType")
        var fType: String? = null

        /**
         * fCategory
         */
        @JSONField(name = "FCategory")
        var fCategory: String? = null

        /**
         * fCategoryID
         */
        @JSONField(name = "FCategoryID")
        var fCategoryID: String? = null

        /**
         * fItem
         */
        @JSONField(name = "FItem")
        var fItem: String? = null

        /**
         * fValues
         */
        @JSONField(name = "FValues")
        var fValues: String? = null
    }
}