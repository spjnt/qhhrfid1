package tramais.hnb.hhrfid.util

import android.content.Context
import com.luck.picture.lib.engine.CompressEngine
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnCallbackListener
import tramais.hnb.hhrfid.util.ImageCompressEngine

class ImageCompressEngine private constructor() : CompressEngine {


    companion object {
        private var instance: ImageCompressEngine? = null
        fun createCompressEngine(): ImageCompressEngine? {
            if (null == instance) {
                synchronized(ImageCompressEngine::class.java) {
                    if (null == instance) {
                        instance = ImageCompressEngine()
                    }
                }
            }
            return instance
        }
    }
    override fun onCompress(context: Context?, compressData: MutableList<LocalMedia>?, listener: OnCallbackListener<MutableList<LocalMedia>>?) {
        listener!!.onCall(compressData)
    }
}