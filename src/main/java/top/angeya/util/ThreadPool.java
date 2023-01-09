package top.angeya.util;

import java.util.concurrent.*;

/**
 * 通用线程池
 * @author: Angeya
 * @date: 2022/12/31 17:04
 **/
public class ThreadPool {

    private ThreadPool() {
    }

    /**
     * 恶汉单例
     */
    private static final ExecutorService SERVICE = new ThreadPoolExecutor(15, 200,
            60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy());

    public static ExecutorService getThreadPool() {
        return SERVICE;
    }
}
