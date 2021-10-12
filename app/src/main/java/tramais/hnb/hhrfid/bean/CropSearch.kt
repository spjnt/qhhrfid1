package tramais.hnb.hhrfid.bean

import com.alibaba.fastjson.annotation.JSONField

 class CropSearch {
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
     * farmerName
     */
    @JSONField(name = "FarmerName")
    var farmerName: String? = null

    /**
     * zjNumber
     */
    @JSONField(name = "ZjNumber")
    var zjNumber: String? = null

    /**
     * qty
     */
    @JSONField(name = "Qty")
    var qty: String? = null

    /**
     * msg
     */
    @JSONField(name = "Msg")
    var msg: String? = null

    class DataDTO {
        /**
         * iD
         */
        @JSONField(name = "ID")
        var iD = 0

        /**
         * sFZNumber
         */
        @JSONField(name = "SFZNumber")
        var sFZNumber: Any? = null

        /**
         * name
         */
        @JSONField(name = "Name")
        var name: Any? = null

        /**
         * landName
         */
        @JSONField(name = "LandName")
        var landName: String? = null

        /**
         * landNumber
         */
        @JSONField(name = "LandNumber")
        var landNumber: String? = null

        /**
         * category
         */
        @JSONField(name = "Category")
        var category: String? = null

        /**
         * bornDate
         */
        @JSONField(name = "BornDate")
        var bornDate: Any? = null

        /**
         * checkTime
         */
        @JSONField(name = "CheckTime")
        var checkTime: String? = null

        /**
         * insureTime
         */
        @JSONField(name = "InsureTime")
        var insureTime: Any? = null

        /**
         * status
         */
        @JSONField(name = "Status")
        var status: Any? = null

        /**
         * rowCount
         */
        @JSONField(name = "RowCount")
        var rowCount: Any? = null

        /**
         * fCheckSquare
         */
        @JSONField(name = "FCheckSquare")
        var fCheckSquare: String? = null

        /**
         * fCirLength
         */
        @JSONField(name = "FCirLength")
        var fCirLength: String? = null

        /**
         * fGISPicture
         */
        @JSONField(name = "FGISPicture")
        var fGISPicture: String? = null
    }
}