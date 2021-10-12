package tramais.hnb.hhrfid.litePalBean


import com.google.gson.annotations.SerializedName
import org.litepal.LitePal
import org.litepal.crud.LitePalSupport


data class NatureanCache (
        @SerializedName("FCode")
        var fCode: String, // 100
        @SerializedName("FName")
        var fName: String, // 机关、团体
): LitePalSupport()
