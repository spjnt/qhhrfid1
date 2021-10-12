package tramais.hnb.hhrfid.bean

import com.alibaba.fastjson.annotation.JSONField

 class LableBean {
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
     * farmerCount
     */
    @JSONField(name = "FarmerCount")
    var farmerCount: String? = null

    /**
     * labelCount
     */
    @JSONField(name = "LabelCount")
    var labelCount: String? = null

    class DataDTO {
        /**
         * data
         */
        @JSONField(name = "Data")
        var data: List<DataDTO.DataDTO1>? = null

        /**
         * fFarmerNumber
         */
        @JSONField(name = "FFarmerNumber")
        var fFarmerNumber: String? = null

        /**
         * fFarmerName
         */
        @JSONField(name = "FFarmerName")
        var fFarmerName: String? = null

        class DataDTO1{
            /**
             * fLabelNumber
             */
            @JSONField(name = "FLabelNumber")
            var fLabelNumber: String? = null
        }
    }
}