package com.setruth.timemanager.ui.screen.countdown

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.SystemClock
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.isActive
import timber.log.Timber
import java.util.PriorityQueue
import java.util.concurrent.atomic.AtomicReference
import kotlin.math.abs

/**
 * [一起设计一个Android倒计时组件](https://juejin.cn/post/6984725689257689101)
 * [复刻快手电商团队文章Android倒计时组件设计](https://juejin.cn/post/7219847645731668025)
 */
object CountDownTimerManager {

    private const val DEFAULT_INITIAL_CAPACITY = 5
    private const val MSG = 1

    private val mTaskQueue = PriorityQueue(
        DEFAULT_INITIAL_CAPACITY,
        run {
            Comparator { o1: Task, o2: Task -> o1.mExecuteTimeInNext.compareTo(o2.mExecuteTimeInNext) }
        }
    )

    private var mIsCancelled = false

    private val mHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            synchronized(this) {
                if (mIsCancelled) {
                    return
                }
                val task = mTaskQueue.poll() ?: return
                if (!task.isDisposed) return

                if (task.mMillisUntilFinished < task.mCountdownInterval || task.mCountdownInterval == 0L) {
                    task.dispose()
                    task.mEmitter.trySend(0)
                } else {
                    task.mEmitter.trySend(task.mMillisUntilFinished)
                    // 更新剩余时间
                    task.mMillisUntilFinished = task.mMillisUntilFinished - task.mCountdownInterval
                    // 更新下一次执行时间
                    task.mExecuteTimeInNext += task.mCountdownInterval
                    // 下一个 MSG 执行时间
                    var mNextMsgCountDownInterval = task.mExecuteTimeInNext - SystemClock.elapsedRealtime()
                    while (mNextMsgCountDownInterval < 0) {
                        mNextMsgCountDownInterval += task.mCountdownInterval
                    }
                    val diff = abs(mNextMsgCountDownInterval - task.mCountdownInterval)
                    if (diff >= 10) {
                        Timber.tag("CountDownTimerManager").d("下一次触发时间 偏差大于10：$mNextMsgCountDownInterval")
                    }
                    mTaskQueue.offer(task)
                    val nextMsg = this.obtainMessage(MSG)
                    sendMessageDelayed(nextMsg, 10L.coerceAtLeast(mNextMsgCountDownInterval))
                }
            }
        }
    }

    fun countdown(millisInFuture: Long, countDownInterval: Long, delayMillis: Long): Flow<Long> {
        require(millisInFuture > 0) { "millisInFuture must be greater than 0" }
        require(countDownInterval >= 0) { "countDownInterval must be greater than or equal to 0" }
        val taskAtomicReference = AtomicReference<Task>()
        return channelFlow {
            val newTask = Task(millisInFuture, countDownInterval, delayMillis, this)
            val topTask = mTaskQueue.peek()
            if (topTask == null || newTask.mExecuteTimeInNext < topTask.mExecuteTimeInNext) {
                cancel()
            }
            taskAtomicReference.set(newTask)
            mTaskQueue.offer(newTask)
            mIsCancelled = false
            mHandler.sendMessage(mHandler.obtainMessage(MSG))
            awaitClose { taskAtomicReference.get()?.dispose() }
        }
    }

    @Synchronized
    fun timer(delayMillis: Long): Flow<Long> {
        return countdown(0, 0, delayMillis)
    }

    @Synchronized
    fun cancel() {
        mIsCancelled = true
        mHandler.removeMessages(MSG)
    }

    private class Task(
        // 剩余时间
        var mMillisUntilFinished: Long,
        // 间隔时间
        val mCountdownInterval: Long,
        delayMillis: Long,
        val mEmitter: ProducerScope<Long>
    ) {
        // 是否结束
        var mDisposed = false

        // 下次执行时间
        var mExecuteTimeInNext: Long

        // 结束时间
        var mStopTimeInFuture: Long

        init {
            mExecuteTimeInNext = SystemClock.elapsedRealtime() + if (mCountdownInterval == 0L) {
                0L
            } else (mMillisUntilFinished % mCountdownInterval) + delayMillis
            mStopTimeInFuture = SystemClock.elapsedRealtime() + mMillisUntilFinished + delayMillis
        }

        fun dispose() {
            if (!mDisposed) {
                mEmitter.trySend(0)
                mDisposed = true
            }
        }

        val isDisposed: Boolean
            get() = mDisposed || mEmitter.isActive
    }
}
