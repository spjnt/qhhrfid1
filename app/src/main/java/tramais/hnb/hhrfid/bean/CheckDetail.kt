package tramais.hnb.hhrfid.bean

import com.alibaba.fastjson.annotation.JSONField

class CheckDetail { /* */
    /**
     * entrys
     */
    @JSONField(name = "Entrys")
    var entrys: List<*>? = null

    /**
     * liPeiAnimalData
     */
    @JSONField(name = "LiPeiAnimalData")
    var liPeiAnimalData: List<LiPeiAnimalDataDTO>? = null

    /**
     * code
     */
    @JSONField(name = "Code")
    var code = 0
    private val isExpanded = false
    /**
     * msg
     */
    @JSONField(name = "Msg")
    var msg: String? = null

    /**
     * baoAnNumber
     */
    @JSONField(name = "BaoAnNumber")
    var baoAnNumber: String? = null

    /**
     * companyNumber
     */
    @JSONField(name = "CompanyNumber")
    var companyNumber: String? = null

    /**
     * farmerNumber
     */
    @JSONField(name = "FarmerNumber")
    var farmerNumber: String? = null

    /**
     * employeeNumber
     */
    @JSONField(name = "EmployeeNumber")
    var employeeNumber: String? = null

    /**
     * chaKanDate
     */
    @JSONField(name = "ChaKanDate")
    var chaKanDate: String? = null

    /**
     * riskAddress
     */
    @JSONField(name = "RiskAddress")
    var riskAddress: String? = null

    /**
     * lostRemark
     */
    @JSONField(name = "LostRemark")
    var lostRemark: String? = null

    /**
     * lostAssess
     */
    @JSONField(name = "LostAssess")
    var lostAssess: String? = null

    /**
     * chaKanResult
     */
    @JSONField(name = "ChaKanResult")
    var chaKanResult: String? = null

    /**
     * bankName
     */
    @JSONField(name = "BankName")
    var bankName: String? = null

    /**
     * bankAccount
     */
    @JSONField(name = "BankAccount")
    var bankAccount: String? = null

    /**
     * accountName
     */
    @JSONField(name = "AccountName")
    var accountName: String? = null

    /**
     * bankPicture
     */
    @JSONField(name = "BankPicture")
    var bankPicture: String? = null

    /**
     * fFarmerSign
     */
    @JSONField(name = "FFarmerSign")
    var fFarmerSign: String? = null

    /**
     * fRiskPre
     */
    @JSONField(name = "FRiskPre")
    var fRiskPre: String? = null

    /**
     * fRiskQty
     */
    @JSONField(name = "FRiskQty")
    var fRiskQty: String? = null

    /**
     * fRiskAmount
     */
    @JSONField(name = "FRiskAmount")
    var fRiskAmount: String? = null

    /**
     * fReasonCode
     */
    @JSONField(name = "FReasonCode")
    var fReasonCode: String? = null

    /**
     * fReasonName
     */
    @JSONField(name = "FReasonName")
    var fReasonName: String? = null

    /**
     * fIsTrue
     */
    @JSONField(name = "FIsTrue")
    var fIsTrue: String? = null

    /**
     * fIsLabel
     */
    @JSONField(name = "FIsLabel")
    var fIsLabel: String? = null

    /**
     * fMartketPrice
     */
    @JSONField(name = "FMartketPrice")
    var fMartketPrice: String? = null

    /**
     * fSubCanZhi
     */
    @JSONField(name = "FSubCanZhi")
    var fSubCanZhi: String? = null

    /**
     * creator
     */
    @JSONField(name = "Creator")
    var creator: Any? = null

    /**
     * updator
     */
    @JSONField(name = "Updator")
    var updator: Any? = null

    /**
     * createTime
     */
    @JSONField(name = "FBankRelatedCode")
    var fBankRelatedCode: String? = null
    @JSONField(name = "FBankCode")
    var fBankCode: String? = null
    @JSONField(name = "CreateTime")
    var createTime: String? = null

    /**
     * updateTime
     */
    @JSONField(name = "UpdateTime")
    var updateTime: String? = null

    class LiPeiAnimalDataDTO {
        /**
         * liPeiAnimalPicData
         */
        @JSONField(name = "LiPeiAnimalPicData")
        var liPeiAnimalPicData: MutableList<LiPeiAnimalPicDataDTO>? = null
         var isExpanded = false

        /**
         * fEarNumber
         */
        @JSONField(name = "FEarNumber")
        var fEarNumber: String? = null

        /**
         * fEntryID
         */
        @JSONField(name = "FEntryID")
        var fEntryID: String? = null

        /**
         * fRiskPre
         */
        @JSONField(name = "FRiskPre")
        var fRiskPre: String? = null

        /**
         * fLossAmount
         */
        @JSONField(name = "FLossAmount")
        var fLossAmount: String? = null

        class LiPeiAnimalPicDataDTO {
            /**
             * fEntryID
             */
            @JSONField(name = "FEntryID")
            var fEntryID: String? = null

            /**
             * picUrl
             */
            @JSONField(name = "PicUrl")
            var picUrl: String? = null
        }
    }
}