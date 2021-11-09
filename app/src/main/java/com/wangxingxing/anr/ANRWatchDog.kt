package com.wangxingxing.anr

import android.os.*
import android.util.Log
import java.lang.Exception
import java.lang.StringBuilder

/**
 * author : 王星星
 * date : 2021/11/8 19:49
 * email : 1099420259@qq.com
 * description : 一个监听 ANR 的线程
 */
object ANRWatchDog : Thread("ANR-WatchDog-Thread") {
    private const val TAG = "ANRWatchDog"

    // 超时即为ANR
    private const val mTimeOut = 5000L
    private const val mIgnoreDebugger = true
    private val mHandler = Handler(Looper.getMainLooper())

    // ANR 发生时执行的回调函数，没有指定时会使用默认实现
    private lateinit var mOnANRHappened: (stackTraceInfo: String) -> Unit
    private val mBlockChecker = BlockChecker(this)

    // Kotlin的超类为Any，不再提供 wait、notify等线程通信方法，把一个 Object 对象当做锁来用
    private val lock: Object = Object()

    /**
     * 一个检查主线程是否被阻塞的线程任务
     */
    private class BlockChecker(val mANRWatchDog: ANRWatchDog) : Runnable {
        private var mCompleted = false
        private var mStartTime = 0L
        private var mExecuteTime = 0L

        override fun run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND)
            // 执行这里代表消息被 MainLooper 从消息队列中取出，并执行 Runnable 任务
            mCompleted = true
            // 任务执行的时间
            mExecuteTime = SystemClock.uptimeMillis()
            Log.d(TAG, "BlockChecker run mCompleted is:$mCompleted")
        }

        /**
         * 把任务发送给MainLooper，准备执行任务，但是任务真正执行要等到主线程没有被阻塞
         */
        fun schedule() {
            mCompleted = false
            Log.d(TAG, "BlockChecker schedule mCompleted is:$mCompleted")
            mStartTime = SystemClock.uptimeMillis()
            // 立即发送Message到队列，而且是放在队列的最前面
            mHandler.postAtFrontOfQueue(this)
        }

        val notBlocked: Boolean
            get() {
                Log.d(TAG, "BlockChecker notBlocked get completed:$mCompleted, duration:${mExecuteTime - mStartTime}")
                return mCompleted && (mExecuteTime - mStartTime) in 0 until mANRWatchDog.mTimeOut
            }
    }

    override fun run() {
        // 设置当前线程为后台线程，执行优先级低于主线程，这样主线程会分配更多执行资源
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND)
        loop@ while (!isInterrupted) {
            synchronized(lock) {
                // mBlockChecker 发送消息给主线程
                mBlockChecker.schedule()
                try {
                    // 等待 mTimeout 毫秒时间
                    lock.wait(mTimeOut)
                    Log.d(TAG, "ANRWatchDog wait over")
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e(TAG, "ANRWatchDog: ", e)
                }
            }
            // 检查主线程是否被阻塞，没有则重新开始倒计时
            // 看 mBlockChecker 任务是否被执行，并且没有超时
            if (mBlockChecker.notBlocked) {
                Log.d(TAG, "notBlocked, ANRWatchDog count again")
                continue
            }
            // 执行到这里，说明已经超时
            // 由于调试模式比较耗时，会拖慢主线程执行速度，所以当处于调试模式下，就有可能会超时
            // 如果忽略调试带来的影响，则重新开始倒计时
            if (Debug.isDebuggerConnected() && !mIgnoreDebugger) {
                continue
            }
            // 执行回调
            mOnANRHappened(stackTraceInfo)
        }
    }

    fun start(onANRHappened: (stackTraceInfo: String) -> Unit = {}) {
        mOnANRHappened = onANRHappened
        // 启动线程
        start()
    }

    // 获取主线程的堆栈信息
    private val stackTraceInfo: String
        get() {
            val sb = StringBuilder()
            for (stackTraceElement in Looper.getMainLooper().thread.stackTrace) {
                sb.append("$stackTraceElement \r\n")
            }
            return sb.toString()
        }
}