package tramais.hnb.hhrfid.bean

import java.util.*

class BannerData {
    var imageRes: Int? = null
    var imageUrl: String? = null
    var title: String?
    var viewType: Int

    constructor(imageRes: Int?, title: String?, viewType: Int) {
        this.imageRes = imageRes
        this.title = title
        this.viewType = viewType
    }

    constructor(imageUrl: String?, title: String?, viewType: Int) {
        this.imageUrl = imageUrl
        this.title = title
        this.viewType = viewType
    }



    companion object {
        val testData3: List<BannerData>
            get() {
                val list: MutableList<BannerData> = ArrayList()
                list.add(BannerData("https://img.zcool.cn/community/013de756fb63036ac7257948747896.jpg", null, 1))
                list.add(BannerData("https://img.zcool.cn/community/01639a56fb62ff6ac725794891960d.jpg", null, 1))
                list.add(BannerData("https://img.zcool.cn/community/01270156fb62fd6ac72579485aa893.jpg", null, 1))
                return list
            }
    }
}