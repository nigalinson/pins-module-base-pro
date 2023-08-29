package com.sloth.utils.test;


import java.util.concurrent.atomic.AtomicReference;

public class AsyncTest {

    public static void test(TestRunnable runnable) throws InterruptedException {
         new AsyncTest().run(runnable);
    }

    private final Object lock = new Object();

    private AsyncTest() { }

    private void run(TestRunnable runnable) throws InterruptedException {
        AtomicReference<Throwable> err = new AtomicReference<Throwable>();
        runnable.setLock(lock);
        Thread testThread = new Thread(runnable);
        testThread.setUncaughtExceptionHandler((t, e) -> {
            err.set(e);
            //测试线程中断
            synchronized (lock){
                lock.notifyAll();
            }
        });
        testThread.start();
        synchronized (lock){
            lock.wait();
        }
        if(err.get() != null){
            //测试结束，但是存在错误
            throw new RuntimeException(err.get());
        }
    }

    public abstract static class TestRunnable implements Runnable {
        private Object lock;


        public TestRunnable() { }

        public void setLock(Object lock) {
            this.lock = lock;
        }

        @Override
        public void run() {
            try {
                runTest();
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("test interrupted:" + e.getMessage());
            }
        }

        protected abstract void runTest() throws Exception;

        public void endTest(){
            synchronized (lock){
                lock.notifyAll();
            }
        }

        public void testFailed(){
            endTest();
            throw new RuntimeException("test failed");
        }
    }

}
