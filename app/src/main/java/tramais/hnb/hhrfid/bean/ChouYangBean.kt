package tramais.hnb.hhrfid.bean

import com.alibaba.fastjson.annotation.JSONField

class ChouYangBean {
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

    @JSONField(name = "FAddr")
    var fAddr: String? = null

    /**
     * fNumber
     */
    @JSONField(name = "FNumber")
    var fNumber: String? = null

    /**
     * fSampleDate
     */
    @JSONField(name = "FSampleDate")
    var fSampleDate: String? = null

    /**
     * fAreaQty
     */
    @JSONField(name = "FAreaQty")
    var fAreaQty: String? = null

    /**
     * fRiskQty
     */
    @JSONField(name = "FRiskQty")
    var fRiskQty: String? = null

    /**
     * fLandCategory
     */
    @JSONField(name = "FLandCategory")
    var fLandCategory: String? = null

    /**
     * fGrowthStage
     */
    @JSONField(name = "FGrowthStage")
    var fGrowthStage: String? = null

    /**
     * fSampleType
     */
    @JSONField(name = "FSampleType")
    var fSampleType: String? = null

    /**
     * fRiskDetail
     */
    @JSONField(name = "FRiskDetail")
    var fRiskDetail: String? = null

    /**
     * fSampleResult
     */
    @JSONField(name = "FSampleResult")
    var fSampleResult: String? = null

    /**
     * fExpertSign
     */
    @JSONField(name = "FExpertSign")
    var fExpertSign: String? = null

    /**
     * fEmpSign
     */
    @JSONField(name = "FEmpSign")
    var fEmpSign: String? = null

    /**
     * fFarmerSign
     */
    @JSONField(name = "FFarmerSign")
    var fFarmerSign: String? = null

    class DataDTO {
        /**
         * fRowID
         */
        @JSONField(name = "FRowID")
        var fRowID: String? = null

        /**
         * fLandName
         */
        @JSONField(name = "FLandName")
        var fLandName: String? = null

        /**
         * fLandArea
         */
        @JSONField(name = "FLandArea")
        var fLandArea: String? = null

        /**
         * fActualQty
         */
        @JSONField(name = "FActualQty")
        var fActualQty: String? = null

        /**
         * fProductionPre
         */
        @JSONField(name = "FProductionPre")
        var fProductionPre: String? = null

        /**
         * fWeiDuName
         */
        @JSONField(name = "FWeiDuName")
        var fWeiDuName: String? = null

        /**
         * fWeiDuValue
         */
        @JSONField(name = "FWeiDuValue")
        var fWeiDuValue: String? = null
    }
}