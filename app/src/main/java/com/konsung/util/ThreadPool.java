package com.konsung.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Cmad on 2015/11/13.
 * 线程池
 */
public class ThreadPool {
    private static final int MAX_THREAD_SIZE = 15;
    private static ExecutorService mFixedThreadPool = Executors.newFixedThreadPool(MAX_THREAD_SIZE);


    public static void execute(Runnable runnable){
        mFixedThreadPool.execute(runnable);
    }

}
