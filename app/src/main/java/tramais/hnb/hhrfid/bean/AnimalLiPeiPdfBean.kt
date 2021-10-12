package tramais.hnb.hhrfid.bean

class AnimalLiPeiPdfBean {
    var code = 0
    var msg: String? = null
    var fLabelAddress: String? = null
    var fItemName: String? = null
    var fInsureNumber: String? = null
    var fEmployeeName: String? = null
    var fCreateTime: String? = null
    var data: List<DataDTO>? = null

    class DataDTO  {
        var rn: String? = null
        var fFarmerName: String? = null
        var fLabelAddress: String? = null
        var fEarNumber: String? = null
        var fRiskDate: String? = null
        var fReasonName: String? = null
        var fUnitAmount: String? = null
        var fLossAmount: String? = null
        var fSignPicture: String? = null
        var fAnimalAge: String? = null
        var fAnimalWeight: String? = null
    }
}