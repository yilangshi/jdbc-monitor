package org.jdbc.monitor.excutor;

import org.jdbc.monitor.exceptoin.TaskRejectedException;

import java.util.concurrent.*;

/**
 * @author: shi rui
 * @create: 2018-12-13 16:26
 */
public class TaskExecutor implements Executor{

    private ThreadPoolExecutor threadPoolExecutor;

    private static final int CORE_POOL_SIZE = 1,MAX_POOLSIZE = 10, KEE_ALIVE_SECONDS = 60, QUEUE_CAPACITY = 10;

    public TaskExecutor(){
        init();
    }

    private void init() {
        BlockingQueue<Runnable> queue = createQueue(QUEUE_CAPACITY);
        threadPoolExecutor= new ThreadPoolExecutor(
                CORE_POOL_SIZE, MAX_POOLSIZE, KEE_ALIVE_SECONDS, TimeUnit.SECONDS,
                queue, Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
        threadPoolExecutor.allowCoreThreadTimeOut(true);
    }

    protected BlockingQueue<Runnable> createQueue(int queueCapacity) {
        if (queueCapacity > 0) {
            return new LinkedBlockingQueue<>(queueCapacity);
        }
        else {
            return new SynchronousQueue<>();
        }
    }

    public ThreadPoolExecutor getThreadPoolExecutor() {
        return this.threadPoolExecutor;
    }

    @Override
    public void execute(Runnable task) {
        Executor executor = getThreadPoolExecutor();
        try {
            executor.execute(task);
        }
        catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + executor + "] did not accept task: " + task, ex);
        }
    }
}
