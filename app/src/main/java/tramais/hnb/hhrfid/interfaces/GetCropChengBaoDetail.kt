package tramais.hnb.hhrfid.interfaces

import tramais.hnb.hhrfid.bean.CropCheckChengBaoBean

interface GetCropChengBaoDetail {
    fun getBillDetail(chanKanDetailBean: CropCheckChengBaoBean?)
}