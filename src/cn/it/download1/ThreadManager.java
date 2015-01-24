package cn.it.download1;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;
import java.util.concurrent.TimeUnit;

public class ThreadManager {

    private static ThreadPoolProxy mDownloadPool = null;
    private static Object mDownloadLock = new Object();
    
    public static ThreadPoolProxy getDownloadPool() {
        synchronized (mDownloadLock) {
            if (mDownloadPool == null) {
                mDownloadPool = new ThreadPoolProxy(1, 3, 5L);
            }
            return mDownloadPool;
        }
    }
    
    public static class ThreadPoolProxy {
        private ThreadPoolExecutor mPool;
        private int mCorePoolSize;
        private int mMaximumPoolSize;
        private long mKeepAliveTime;

        private ThreadPoolProxy(int corePoolSize, int maximumPoolSize, long keepAliveTime) {
            mCorePoolSize = corePoolSize;
            mMaximumPoolSize = maximumPoolSize;
            mKeepAliveTime = keepAliveTime;
        }

        /**
         *  执行任务，当线程池处于关闭，将会重新创建新的线程�?
         * @param run
         */
        public synchronized void execute(Runnable run) {
            if (run == null) {
                return;
            }
            if (mPool == null || mPool.isShutdown()) {
                mPool = new ThreadPoolExecutor(mCorePoolSize, mMaximumPoolSize, mKeepAliveTime, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), Executors.defaultThreadFactory(), new AbortPolicy());
            }
            mPool.execute(run);
        }

        /**
         *  取消线程池中某个还未执行的任�?
         * @param run
         */
        public synchronized void cancel(Runnable run) {
            if (mPool != null && (!mPool.isShutdown() || mPool.isTerminating())) {
                mPool.getQueue().remove(run);
            }
        }

        /**
         *  判断线程池中是否包含有某个任�?
         * @param run
         * @return
         */
        public synchronized boolean contains(Runnable run) {
            if (mPool != null && (!mPool.isShutdown() || mPool.isTerminating())) {
                return mPool.getQueue().contains(run);
            } else {
                return false;
            }
        }

        /**
         * 立刻关闭线程池，并且正在执行的任务也将会被中�?
         */
        public void stop() {
            if (mPool != null && (!mPool.isShutdown() || mPool.isTerminating())) {
                mPool.shutdownNow();
            }
        }

        /**
         *  平缓关闭单任务线程池，但是会确保�?��已经加入的任务都将会被执行完毕才关闭 
         */
        public synchronized void shutdown() {
            if (mPool != null && (!mPool.isShutdown() || mPool.isTerminating())) {
                mPool.shutdownNow();
            }
        }
    }
}
