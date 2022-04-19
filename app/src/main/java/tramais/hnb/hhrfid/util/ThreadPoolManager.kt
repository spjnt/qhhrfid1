package tramais.hnb.hhrfid.util

import java.util.concurrent.*


/**
 * Created  on 2019/1/3
 * @author lyj
 * Function
 */
class ThreadPoolManager {

    companion object {
        @Volatile
        private var mLongPool: ThreadPoolProxy? = null
        private val mLongLock = Any()

        fun getPool(mCoreSize: Int, mMaxSize: Int, mKeepLive: Long): ThreadPoolProxy? {
            if (mLongPool == null) {
                synchronized(mLongLock) {
                    if (mLongPool == null) {
                        mLongPool = ThreadPoolProxy(mCoreSize, mMaxSize, mKeepLive)
                    }
                }
            }

            return mLongPool
        }

    }

    fun newFixedThreadPool(nThreads: Int): ExecutorService {
        return ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, LinkedBlockingQueue<Runnable>());
    }

    class ThreadPoolProxy(private val mCoreSize: Int, private val mMaxSize: Int, private val mKeepLive: Long) {
        private var mExecutor: ThreadPoolExecutor? = null

        private fun checkPool() {
            if (mExecutor == null || mExecutor!!.isShutdown) {

                val unit = TimeUnit.MILLISECONDS
                val mWorkQueue = LinkedBlockingDeque<Runnable>()
                val mFactory = Executors.defaultThreadFactory()
                val mHandler = ThreadPoolExecutor.AbortPolicy()

                mExecutor = ThreadPoolExecutor(mCoreSize, mMaxSize, mKeepLive, unit, mWorkQueue, mFactory,
                        mHandler)
            }

        }

        fun getExcutor(): ThreadPoolExecutor? {

            return mExecutor
        }

        fun exec(task: Runnable?) {
            if (task == null) {
                return
            }

            checkPool()

            mExecutor!!.execute(task)
        }

        fun getComTask(): Long {
            return mExecutor!!.completedTaskCount
        }

        fun submit(task: Runnable?): Future<*>? {
            if (task == null) {
                return null
            }

            checkPool()
            return mExecutor!!.submit(task)
        }

        fun remove(task: Runnable?) {
            if (task == null) {
                return
            }
            checkPool()
            mExecutor!!.remove(task)
        }
    }
}
