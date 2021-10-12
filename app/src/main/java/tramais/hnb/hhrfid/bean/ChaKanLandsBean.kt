package tramais.hnb.hhrfid.bean

import com.alibaba.fastjson.annotation.JSONField

class ChaKanLandsBean {
    /**
     * data1
     */
    @JSONField(name = "Data1")
    var data1: List<Data1DTO>? = null

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

    class Data1DTO {
        var isExpanded = false

        /**
         * data2
         */
        @JSONField(name = "Data2")
        var data2: List<Data2DTO>? = null

        /**
         * fInsureNumber
         */
        @JSONField(name = "FInsureNumber")
        var fInsureNumber: String? = null

        /**
         * fName
         */
        @JSONField(name = "FName")
        var fName: String? = null

        /**
         * fNumber
         */
        @JSONField(name = "FNumber")
        var fNumber: String? = null

        /**
         * public string FFarmerRiskQty { get; set; }   //受灾面积
        public string FFarmerLossQty { get; set; }   //损失面积
         */

        @JSONField(name = "FFarmerRiskQty")
        var fFarmerRiskQty: String? = null

        @JSONField(name = "FFarmerLossQty")
        var fFarmerLossQty: String? = null

        /**
         * fFarmerQuantity
         */
        @JSONField(name = "FFarmerQuantity")
        var fFarmerQuantity: String? = null

        class Data2DTO {
            //FIsChecked
            @JSONField(name = "FIsChecked")
            var fIsChecked: Boolean= false


            /**
             * fLandNumber
             */
            @JSONField(name = "FLandNumber")
            var fLandNumber: String? = null

            @JSONField(name = "FLandName")
            var fLandName: String? = null

            /**
             * fSquare
             */
            @JSONField(name = "FSquare")
            var fSquare: String? = null

            /**
             * fGISPicture
             */
            @JSONField(name = "FGISPicture")
            var fGISPicture: String? = null

            /**
             * fRiskQty
             */
            @JSONField(name = "FRiskQty")
            var fRiskQty: String? = null

            /**
             * fLossQty
             */
            @JSONField(name = "FLossQty")
            var fLossQty: String? = null
        }
    }
}