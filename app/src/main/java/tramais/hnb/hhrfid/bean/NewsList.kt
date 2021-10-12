package tramais.hnb.hhrfid.bean

class NewsList {
    var pictureurl: String? = null

    /**
     * id : null
     * title : 青海人保财险迅速启动应急机制应对果洛冰雹灾害
     * headindex : 1
     * pictureurl : http://piccqh.tramais.com/SystemAdmin/Content/net/upload/image/20201127/6374209410203133534023386.png
     * updator : 系统管理员
     * updatetime : 2020-11-27 17:17:22
     */
    var id: String? = null
    var title: String? = null
    var headindex = 0
    var updator: String? = null
    var updatetime: String? = null
    var pictureurlIndex = 0

    companion object {
        const val TEXT = 1
        const val IMG = 2
        const val IMG_TEXT = 3
    }
}